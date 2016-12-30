# GreenDaoExample
Android GreenDao使用总结（包括模型生成、增删改查、修改存储路径、数据库更新升级和加解密数据库）。
其中数据库更新升级参考了https://github.com/yuweiguocn/GreenDaoUpgradeHelper。
详细介绍可参见博客：http://blog.csdn.net/wjk343977868/article/details/53943135。

## 数据库模型生成及读取操作
采用注释方式生成数据库模型，并对数据库进行读写操作。

## 修改数据库文件路径
默认情况下，新创建的数据存储在data的包名目录下，设备如果不root的话，是无法查看SQLite数据库文件的。而实际应用中，我们往往需要copy数据库，或借用第三方工具查阅或编辑数据库内容。此时我们可以通过重写Context的getDatabasePath(String name)、openOrCreateDatabase(String name, int mode, CursorFactory factory)、openOrCreateDatabase(String name, int mode, CursorFactory factory, DatabaseErrorHandler errorHandler)等三个方法来修改SQLite文件的存储路径。

## 获取加密的数据库
通过调用DaoMaster.OpenHelper类的getEncryptedWritableDb(password)或者getEncryptedReadableDb(password)方法，即可获取加密的数据库。

## 数据库升级又不删除数据
 在实际开发的过程中，数据库的结构可能会有所改变。而使用DevOpenHelper每次升级数据库时，表都会删除重建。因此，实际使用中需要建立类继承 DaoMaster.OpenHelper，实现 onUpgrade()方法。<br>
 通过查询资料，对未加密的数据库，推荐使用升级辅助库[GreenDaoUpgradeHelper](https://github.com/yuweiguocn/GreenDaoUpgradeHelper/blob/master/README_CH.md)。
 <br>该库通过 MigrationHelper在删表重建的过程中，使用临时表保存数据并还原。<br>
 MigrationHelper.migrate()，暂时只接收 SQLiteDatabase ，不接收 Database，且对加密的数据库是无效的。而实际应用中，由于数据的重要性，数据库往往是必须要加密的。
