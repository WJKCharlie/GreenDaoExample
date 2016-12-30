# GreenDaoExample
Android GreenDao使用总结（包括模型生成、增删改查、修改存储路径、数据库更新升级和加解密数据库）。
<br>其中数据库更新升级参考了https://github.com/yuweiguocn/GreenDaoUpgradeHelper。<br>
详细介绍可参见博客：http://blog.csdn.net/wjk343977868/article/details/53943135。<br>

## 数据库模型生成及读取操作
   采用注释方式生成数据库模型，并对数据库进行读写操作。<br>

## 修改数据库文件路径
   默认情况下，新创建的数据存储在data的包名目录下，设备如果不root的话，是无法查看SQLite数据库文件的。而实际应用中，我们往往需要copy数据库，或借用第三方工具查阅或编辑数据库内容。此时我们可以通过重写Context的getDatabasePath(String name)、openOrCreateDatabase(String name, int mode, CursorFactory factory)、openOrCreateDatabase(String name, int mode, CursorFactory factory, DatabaseErrorHandler errorHandler)等三个方法来修改SQLite文件的存储路径。<br>

## 获取加密的数据库
   通过调用DaoMaster.OpenHelper类的getEncryptedWritableDb(password)或者getEncryptedReadableDb(password)方法，即可获取加密的数据库。<br>

## 数据库升级又不删除数据
   在实际开发的过程中，数据库的结构可能会有所改变。而使用DevOpenHelper每次升级数据库时，表都会删除重建。因此，实际使用中需要建立类继承 DaoMaster.OpenHelper，实现 onUpgrade()方法。<br>
 通过查询资料，对未加密的数据库，推荐使用升级辅助库[GreenDaoUpgradeHelper](https://github.com/yuweiguocn/GreenDaoUpgradeHelper/blob/master/README_CH.md)。<br>该库通过 MigrationHelper在删表重建的过程中，使用临时表保存数据并还原。<br>
 MigrationHelper.migrate()，暂时只接收 SQLiteDatabase ，不接收 Database，且对加密的数据库是无效的。而实际应用中，由于数据的重要性，数据库往往是必须要加密的。
<br>解决方案如下：添加一个新类继承DaoMaster.OpenHelper，添加构造函数并重写onUpgrade和getEncryptedWritableDb方法。同时修改MigrationHelper为EncryptedMigrationHelper。MyEncryptedSQLiteOpenHelper代码如下：<br>
 ```Java
private static class MyEncryptedSQLiteOpenHelper extends DaoMaster.OpenHelper {

        private final Context context;
        private final String name;
        private final int version = DaoMaster.SCHEMA_VERSION;

        private boolean loadSQLCipherNativeLibs = true;

        public MyEncryptedSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
            this.context=context;
            this.name=name;
        }

        private static final String UPGRADE="upgrade";

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {

            EncryptedMigrationHelper.migrate((EncryptedDatabase) db,AreaDao.class, PeopleDao.class, ProductDao.class);
            Log.e(UPGRADE,"upgrade run success");
        }

        @Override
        public Database getEncryptedWritableDb(String password) {
            MyEncryptedHelper encryptedHelper = new MyEncryptedHelper(context,name,version,loadSQLCipherNativeLibs);
            return encryptedHelper.wrap(encryptedHelper.getReadableDatabase(password));
        }

        private class MyEncryptedHelper extends net.sqlcipher.database.SQLiteOpenHelper {
            public MyEncryptedHelper(Context context, String name, int version, boolean loadLibs) {
                super(context, name, null, version);
                if (loadLibs) {
                    net.sqlcipher.database.SQLiteDatabase.loadLibs(context);
                }
            }

            @Override
            public void onCreate(net.sqlcipher.database.SQLiteDatabase db) {
                MyEncryptedSQLiteOpenHelper.this.onCreate(wrap(db));
            }

            @Override
            public void onUpgrade(net.sqlcipher.database.SQLiteDatabase db, int oldVersion, int newVersion) {
                MyEncryptedSQLiteOpenHelper.this.onUpgrade(wrap(db), oldVersion, newVersion);
            }

            @Override
            public void onOpen(net.sqlcipher.database.SQLiteDatabase db) {
                MyEncryptedSQLiteOpenHelper.this.onOpen(wrap(db));
            }

            protected Database wrap(net.sqlcipher.database.SQLiteDatabase sqLiteDatabase) {
                return new EncryptedDatabase(sqLiteDatabase);
            }
        }
    }
```
 初始化代码如下：
```Java
MyEncryptedSQLiteOpenHelper helper = new MyEncryptedSQLiteOpenHelper(context,"test.db",null);
daoMaster = new DaoMaster(helper.getEncryptedWritableDb("1234"));//获取可读写的加密数据库
```
