App Event: start
download: test (4.22km)
Routeselected: test (4.22km)
X: 4215 Y: 2701 zoom: 13
TileNumber: com.myroutes.helper.LatLongZoom@977415b
X: 8430 Y: 5403 zoom: 14
TileNumber: com.myroutes.helper.LatLongZoom@f1b5410
X: 16860 Y: 10807 zoom: 15
TileNumber: com.myroutes.helper.LatLongZoom@8c3310e
X: 4214 Y: 2701 zoom: 13
TileNumber: com.myroutes.helper.LatLongZoom@b05501a
X: 8429 Y: 5403 zoom: 14
TileNumber: com.myroutes.helper.LatLongZoom@54ae241
X: 16859 Y: 10806 zoom: 15
TileNumber: com.myroutes.helper.LatLongZoom@11853e6
X: 4214 Y: 2701 zoom: 13
TileNumber: com.myroutes.helper.LatLongZoom@acffd27
com.myroutes.mbtiles4j.MBTilesWriteException: Add Tile to MBTiles file failed
    at com.myroutes.mbtiles4j.MBTilesWriter.addTile(MBTilesWriter.java:113)
    at com.myroutes.adapter.RouteItemAdapter$FetchData.doInBackground(RouteItemAdapter.java:207)
    at com.myroutes.adapter.RouteItemAdapter$FetchData.doInBackground(RouteItemAdapter.java:137)
    at android.os.AsyncTask$2.call(AsyncTask.java:295)
    at java.util.concurrent.FutureTask.run(FutureTask.java:237)
    at android.os.AsyncTask$SerialExecutor$1.run(AsyncTask.java:234)
    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1113)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:588)
    at java.lang.Thread.run(Thread.java:818)
Caused by: com.myroutes.mbtiles4j.MBTilesException: Add Tile failed.
    at com.myroutes.mbtiles4j.SQLHelper.addTile(SQLHelper.java:81)
    at com.myroutes.mbtiles4j.MBTilesWriter.addTile(MBTilesWriter.java:111)
	... 8 more
Caused by: java.sql.SQLException: android.database.sqlite.SQLiteConstraintException: UNIQUE constraint failed: tiles.zoom_level, tiles.tile_column, tiles.tile_row (code 2067)
01-18 18:12:10.298 1535-1777/com.myroutes W/System.err:     at java.lang.reflect.Constructor.newInstance(Native Method)
01-18 18:12:10.298 1535-1777/com.myroutes W/System.err:     at org.sqldroid.SQLDroidConnection.chainException(SQLDroidConnection.java:158)
01-18 18:12:10.298 1535-1777/com.myroutes W/System.err:     at org.sqldroid.SQLiteDatabase.execSQL(SQLiteDatabase.java:147)
01-18 18:12:10.298 1535-1777/com.myroutes W/System.err:     at org.sqldroid.SQLDroidPreparedStatement.execute(SQLDroidPreparedStatement.java:190)
01-18 18:12:10.300 1535-1777/com.myroutes W/System.err:     at com.myroutes.mbtiles4j.SQLHelper.addTile(SQLHelper.java:78)
01-18 18:12:10.300 1535-1777/com.myroutes W/System.err: 	... 9 more
01-18 18:12:10.300 1535-1777/com.myroutes W/System.err: Caused by: android.database.sqlite.SQLiteConstraintException: UNIQUE constraint failed: tiles.zoom_level, tiles.tile_column, tiles.tile_row (code 2067)
01-18 18:12:10.304 1535-1777/com.myroutes W/System.err:     at android.database.sqlite.SQLiteConnection.nativeExecuteForChangedRowCount(Native Method)
01-18 18:12:10.304 1535-1777/com.myroutes W/System.err:     at android.database.sqlite.SQLiteConnection.executeForChangedRowCount(SQLiteConnection.java:734)
     at android.database.sqlite.SQLiteSession.executeForChangedRowCount(SQLiteSession.java:754)
     at android.database.sqlite.SQLiteStatement.executeUpdateDelete(SQLiteStatement.java:64)
     at android.database.sqlite.SQLiteDatabase.executeSql(SQLiteDatabase.java:1688)
     at android.database.sqlite.SQLiteDatabase.execSQL(SQLiteDatabase.java:1667)
     at org.sqldroid.SQLiteDatabase.execSQL(SQLiteDatabase.java:140)
 	... 11 more
 X: 8429 Y: 5403 zoom: 14
 TileNumber: com.myroutes.helper.LatLongZoom@cdadbd4
 com.myroutes.mbtiles4j.MBTilesWriteException: Add Tile to MBTiles file failed
     at com.myroutes.mbtiles4j.MBTilesWriter.addTile(MBTilesWriter.java:113)
     at com.myroutes.adapter.RouteItemAdapter$FetchData.doInBackground(RouteItemAdapter.java:207)
     at com.myroutes.adapter.RouteItemAdapter$FetchData.doInBackground(RouteItemAdapter.java:137)
     at android.os.AsyncTask$2.call(AsyncTask.java:295)
     at java.util.concurrent.FutureTask.run(FutureTask.java:237)
     at android.os.AsyncTask$SerialExecutor$1.run(AsyncTask.java:234)
     at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1113)
     at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:588)
     at java.lang.Thread.run(Thread.java:818)
 Caused by: com.myroutes.mbtiles4j.MBTilesException: Add Tile failed.
     at com.myroutes.mbtiles4j.SQLHelper.addTile(SQLHelper.java:81)
     at com.myroutes.mbtiles4j.MBTilesWriter.addTile(MBTilesWriter.java:111)
 	... 8 more
 Caused by: java.sql.SQLException: android.database.sqlite.SQLiteConstraintException: UNIQUE constraint failed: tiles.zoom_level, tiles.tile_column, tiles.tile_row (code 2067)
    at java.lang.reflect.Constructor.newInstance(Native Method)
    at org.sqldroid.SQLDroidConnection.chainException(SQLDroidConnection.java:158)
    at org.sqldroid.SQLiteDatabase.execSQL(SQLiteDatabase.java:147)
    at org.sqldroid.SQLDroidPreparedStatement.execute(SQLDroidPreparedStatement.java:190)
    at com.myroutes.mbtiles4j.SQLHelper.addTile(SQLHelper.java:78)
	... 9 more
Caused by: android.database.sqlite.SQLiteConstraintException: UNIQUE constraint failed: tiles.zoom_level, tiles.tile_column, tiles.tile_row (code 2067)
01-18 18:12:14.765 1535-1777/com.myroutes W/System.err:     at android.database.sqlite.SQLiteConnection.nativeExecuteForChangedRowCount(Native Method)
01-18 18:12:14.766 1535-1777/com.myroutes W/System.err:     at android.database.sqlite.SQLiteConnection.executeForChangedRowCount(SQLiteConnection.java:734)
    at android.database.sqlite.SQLiteSession.executeForChangedRowCount(SQLiteSession.java:754)
    at android.database.sqlite.SQLiteStatement.executeUpdateDelete(SQLiteStatement.java:64)
    at android.database.sqlite.SQLiteDatabase.executeSql(SQLiteDatabase.java:1688)
    at android.database.sqlite.SQLiteDatabase.execSQL(SQLiteDatabase.java:1667)
    at org.sqldroid.SQLiteDatabase.execSQL(SQLiteDatabase.java:140)
	... 11 more
X: 16858 Y: 10807 zoom: 15