package yoko.puyo.tododiary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyOpenHelper extends SQLiteOpenHelper{
    public static final int DB_VERSION = 1;     //これが変わるとonUpgradeが呼び出される
//    public static final String DB_NAME = Environment.getExternalStorageDirectory() + "/MY_DATA.db";     //SDカードに入れたかったができなかった
    public static final String DATA_TABLE = "DATA_TABLE";
    public static final String ITEM_TABLE = "ITEM_TABLE";
    private static final String TAG = "abc";

    public MyOpenHelper(Context context) {
        super(context, "MYDB", null, DB_VERSION);      //MYDBを使えばOKだが、DB_NAMEを使うと、SDカードに入るはずだがだめ
        Log.d(TAG,"MyOpenHelperConstructer");

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        
        Log.d(TAG,"MyOpenHelper onCreate1");
        createTableItem(db);
        Log.d(TAG,"MyOpenHelper onCreate2");
        createTableData(db);
        Log.d(TAG,"MyOpenHelper onCreate3");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG,"onUpgrade");
        db.execSQL(
                "DROP TABLE IF EXISTS " + ITEM_TABLE
        );
        db.execSQL(
                "DROP TABLE IF EXISTS " + DATA_TABLE
        );
        createTableItem(db);
        createTableData(db);
    }
    public void createTableItem(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + ITEM_TABLE +  " ( " +
                        " ID          INTEGER　 PRIMARY KEY, " +     //値を指定しないとauto increment
                        " ITEM        TEXT, " +                     //DATA_TABLEに追加する列名
                        " KATA        INTEGER, " +                  //型　0:integer 1:real 2:text
                        " DISPLAY     INTEGER) "                    //1:表示 0:非表示
        );
    }
    public void createTableData(SQLiteDatabase db) {
        //ID1,ID2の次にITEM_TABLEに登録されたITEM名の列ができる。データ型はKATAで指定されたものになる。
        db.execSQL(
                "CREATE TABLE " + DATA_TABLE +  " ( " +
                        " ID1         INTEGER," +           //最初はIDのみ　年*100+週番号
                        " ID2         INTEGER," +           //最初はIDのみ　月*100+日
                        " PRIMARY KEY(ID1,ID2))"
        );

    }
}
