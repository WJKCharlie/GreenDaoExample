package com.wjk.greendaoexample.util;

import android.database.Cursor;
import android.database.SQLException;
import android.support.annotation.NonNull;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.EncryptedDatabase;
import org.greenrobot.greendao.internal.DaoConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JKWANG-PC on 2016/12/30.
 */

public class EncryptedMigrationHelper {
    public static boolean DEBUG = true;
    private static String TAG = "UpgradeHelper";

    private static List<String> tablenames = new ArrayList<>();

    public static List<String> getTables(SQLiteDatabase db){
        List<String> tables = new ArrayList<>();

        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' order by name", null);
        while(cursor.moveToNext()){
            //遍历出表名
            tables.add(cursor.getString(0));
        }
        cursor.close();
        return tables;
    }

    public static void migrate(EncryptedDatabase db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        Database database = db;
        if (DEBUG) {
            Log.d(TAG, "【Database Version】" + db.getSQLiteDatabase().getVersion());
            Log.d(TAG, "【Generate temp table】start");
        }

        tablenames=getTables(db.getSQLiteDatabase());

        generateTempTables(database, daoClasses);
        if (DEBUG) {
            Log.d(TAG, "【Generate temp table】complete");
        }
        dropAllTables(database, true, daoClasses);
        createAllTables(database, false, daoClasses);

        if (DEBUG) {
            Log.d(TAG, "【Restore data】start");
        }
        restoreData(database, daoClasses);
        if (DEBUG) {
            Log.d(TAG, "【Restore data】complete");
        }
    }

    private static void generateTempTables(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        for (int i = 0; i < daoClasses.length; i++) {
            String tempTableName = null;

            try {
                DaoConfig daoConfig = new DaoConfig(db, daoClasses[i]);
                if(!tablenames.contains(daoConfig.tablename)){
                    continue;
                }
                String tableName = daoConfig.tablename;
                tempTableName = daoConfig.tablename.concat("_TEMP");

                StringBuilder dropTableStringBuilder = new StringBuilder();
                dropTableStringBuilder.append("DROP TABLE IF EXISTS ").append(tempTableName).append(";");
                db.execSQL(dropTableStringBuilder.toString());

                StringBuilder insertTableStringBuilder = new StringBuilder();
                insertTableStringBuilder.append("CREATE TEMPORARY TABLE ").append(tempTableName);
                insertTableStringBuilder.append(" AS SELECT * FROM ").append(tableName).append(";");
                db.execSQL(insertTableStringBuilder.toString());
                if (DEBUG) {
                    Log.d(TAG, "【Table】" + tableName +"\n ---Columns-->"+getColumnsStr(daoConfig));
                    Log.d(TAG, "【Generate temp table】" + tempTableName);
                }
            } catch (SQLException e) {
                Log.e(TAG, "【Failed to generate temp table】" + tempTableName, e);
            }
        }
    }

    private static String getColumnsStr(DaoConfig daoConfig) {
        if (daoConfig == null) {
            return "no columns";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < daoConfig.allColumns.length; i++) {
            builder.append(daoConfig.allColumns[i]);
            builder.append(",");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }


    private static void dropAllTables(Database db, boolean ifExists, @NonNull Class<? extends AbstractDao<?, ?>>... daoClasses) {
        reflectMethod(db, "dropTable", ifExists, daoClasses);
        if (DEBUG) {
            Log.d(TAG, "【Drop all table】");
        }
    }

    private static void createAllTables(Database db, boolean ifNotExists, @NonNull Class<? extends AbstractDao<?, ?>>... daoClasses) {
        reflectMethod(db, "createTable", ifNotExists, daoClasses);
        if (DEBUG) {
            Log.d(TAG, "【Create all table】");
        }
    }

    /**
     * dao class already define the sql exec method, so just invoke it
     */
    private static void reflectMethod(Database db, String methodName, boolean isExists, @NonNull Class<? extends AbstractDao<?, ?>>... daoClasses) {
        if (daoClasses.length < 1) {
            return;
        }
        try {
            for (Class cls : daoClasses) {
                Method method = cls.getDeclaredMethod(methodName, Database.class, boolean.class);
                method.invoke(null, db, isExists);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void restoreData(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        for (int i = 0; i < daoClasses.length; i++) {
            String tempTableName = null;

            try {
                DaoConfig daoConfig = new DaoConfig(db, daoClasses[i]);
                String tableName = daoConfig.tablename;

                if(!tablenames.contains(tableName)){
                    continue;
                }

                tempTableName = daoConfig.tablename.concat("_TEMP");
                StringBuilder insertTableStringBuilder = new StringBuilder();
                insertTableStringBuilder.append("INSERT INTO ").append(tableName).append(" SELECT * FROM ").append(tempTableName).append(";");
                db.execSQL(insertTableStringBuilder.toString());
                if (DEBUG) {
                    Log.d(TAG, "【Restore data】 to " + tableName);
                }

                StringBuilder dropTableStringBuilder = new StringBuilder();
                dropTableStringBuilder.append("DROP TABLE IF EXISTS ").append(tempTableName);
                db.execSQL(dropTableStringBuilder.toString());
                if (DEBUG) {
                    Log.d(TAG, "【Drop temp table】" + tempTableName);
                }
            } catch (SQLException e) {
                Log.e(TAG, "【Failed to restore data from temp table (probably new table)】" + tempTableName, e);
            }
        }
    }
}
