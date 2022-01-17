package yoko.puyo.tododiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MyDAO{
    public static final String DATA_TABLE = MyOpenHelper.DATA_TABLE;
    public static final String ITEM_TABLE = MyOpenHelper.ITEM_TABLE;
    public static final int B_TYPE = 0;
    public static final int I_TYPE = 1;
    public static final int T_TYPE = 2;
    private static final String TAG = "abc";

    MyOpenHelper helper;
    SQLiteDatabase db;

    //------------------------------------------------コンストラクタ
    public MyDAO(Context context) {
        helper = new MyOpenHelper(context);
        db = helper.getWritableDatabase();
    }
    //------------------------------------------------この日付のデータがなければINSERT後　item列をval(int)の値でupdate
    public void updateData(int year, int month, int date, String item, int val){    //#################################
        //ID作成
        int id1 = makeId1(year,month,date);     //年*100＋週番号
        int id2 = makeId2(month,date);          //月*100+日

        //まだこのIDのレコードがなければINSERTしてから
        if (!isExist(id1,id2)) {
            insertData(id1,id2);
        }
        Log.d(TAG,"update3 "+ item + "=" + val);
        ContentValues values = new ContentValues();     //SQLiteにデータを追加するためのクラス
        values.put(item, val);

        db.update(DATA_TABLE, values, " ID1="+id1+" AND ID2="+id2, null);
    }
    //------------------------------------------------この日付のデータがなければINSERT後　item列をval(double)の値でupdate
    public void updateData(int year, int month, int date, String item, double val){    //#################################
        //ID作成
        int id1 = makeId1(year,month,date);     //年*100＋週番号
        int id2 = makeId2(month,date);          //月*100+日

        //まだこのIDのレコードがなければINSERTしてから
        if (!isExist(id1,id2)) {
            insertData(id1,id2);
        }
        Log.d(TAG,"update3 "+ item + "=" + val);
        ContentValues values = new ContentValues();
        values.put(item, val);

        db.update(DATA_TABLE, values, " ID1="+id1+" AND ID2="+id2, null);
    }
    //------------------------------------------------この日付のデータがなければINSERT後　item列をval(String)の値でupdate
    public void updateData(int year, int month, int date, String item, String val){
        //ID作成
        int id1 = makeId1(year,month,date);     //年*100＋週番号
        int id2 = makeId2(month,date);          //月*100+日

        //まだこのIDのレコードがなければINSERTしてから
        if (!isExist(id1,id2)) {
            insertData(id1,id2);
        }
        Log.d(TAG,"update13 "+ item + "=" + val);
        ContentValues values = new ContentValues();
        values.put(item, val);

        db.update(DATA_TABLE, values, " ID1="+id1+" AND ID2="+id2, null);
    }
    //------------------------------------------------ 日付を受け取りIDを作成しインサート(ID以外はすべてnull)
    public void insertData(int year, int month, int date) {
        int id1 = makeId1(year,month,date);
        int id2 = makeId2(month,date);

        insertData(id1,id2);
    }
    //------------------------------------------------　IDを受け取りインサート(ID以外はすべてnull)
    public void insertData(int id1,int id2) {

        //"INSERT INTO DATA_TABLE(ID1,ID2) VALUES(id1,id2)"というSQL作成
        String sql = "INSERT INTO " + DATA_TABLE + "(ID1,ID2) VALUES(" + id1+","+id2 + ")";
        Log.d(TAG,sql);
        db.execSQL(sql);
    }
    //------------------------------------------------DATA_TABLEの全レコード削除(test用)
    public void deleteAllData() {
        db.execSQL( "DELETE FROM " + DATA_TABLE );
    }
    //------------------------------------------------ITEM_TABLEの全レコード削除(test用)
    public void deleteAllItem() {
        db.execSQL( "DELETE FROM " + ITEM_TABLE );
        db.execSQL(
                "DROP TABLE IF EXISTS " + DATA_TABLE
        );
        helper.createTableData(db);
    }
    //------------------------------------------------年月日指定しDATA_TABLE削除
    public void deleteData(int year, int month, int date) {
        int id1 = makeId1(year,month,date);
        int id2 = makeId2(month,date);

        //自分でSQLを作成するなら
        db.execSQL( "DELETE FROM " + DATA_TABLE + " WHERE ID1=" + id1 +" AND ID2=" + id2 );

        //deleteメソッドを使うなら
//        String[] vals = {String.valueOf(id1),String.valueOf(id2)};
//        db.delete(TABLE_NAME,"ID1=? AND ID2=?",vals);
    }
    //------------------------------------------------isExist
    public boolean isExist(int id1, int id2){
        Log.d("abc","isExist");
        String[] str = {String.valueOf(id1), String.valueOf(id2)};
        Cursor cursor = db.rawQuery("SELECT ID1,ID2 FROM " + DATA_TABLE + " WHERE ID1=? AND ID2=?",str);
        if(cursor.getCount() == 0) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            return true;
        }
    }
    //------------------------------------------------makeId1 年週
    private int makeId1(int year, int month, int date) {
        Calendar cal = Calendar.getInstance();
        cal.set(year,month-1,date);
        int w = cal.get(Calendar.WEEK_OF_YEAR);
        return  year*100 + w;
    }
    //------------------------------------------------makeId2 月日
    private int makeId2(int month, int date) {
        return  month*100 + date;
    }

    //------------------------------------------------printMap
    public void printMap(Map<String,Object> map) {
        Set<String> keys = map.keySet();
        for(String key : keys) {
            Log.d(TAG, key + "=" + map.get(key));
        }
    }
    //------------------------------------------------printMap
    public void printMap1(Map<String,Object> map) {
        StringBuilder str = new StringBuilder("");
        Set<String> keys = map.keySet();
        for(String key : keys) {
            str.append(key);
            str.append("=");
            str.append(map.get(key));
            str.append("   ");
        }
        Log.d(TAG, str.toString());
    }
    //------------------------------------------------printMap
    public void printListOfMap(ArrayList<Map<String,Object>> list) {

        for(Map<String,Object>map : list) {
            StringBuilder str = new StringBuilder("");
            Set<String> keys = map.keySet();
            for (String key : keys) {
                str.append(key);
                str.append("=");
                str.append(map.get(key));
                str.append("   ");
            }
            Log.d(TAG, str.toString());
        }
    }
    //------------------------------------------------ DATA_TABLEに列を追加
    public void alterTableAdd(String item, int kata) {
        String type = "INTEGER";    //B_TYPE
        if(kata == I_TYPE)  type = "REAL";
        if(kata == T_TYPE)  type = "TEXT";

        String sql = "ALTER TABLE " + DATA_TABLE + " ADD COLUMN " + item + " " + type;
        Log.d(TAG,sql);
        db.execSQL(sql);
    }
    //------------------------------------------------ITEM_TABLEに新しい列データを
    public void insertItem(String item, int kata) {
        String sql = "INSERT INTO " + ITEM_TABLE + "(ITEM,KATA,DISPLAY) VALUES('" + item + "'," + kata + ",1)";
        Log.d(TAG,sql);
        db.execSQL(sql);
        alterTableAdd(item,kata);
    }
    //------------------------------------------------ ITEM_TABLEの内容で新しいDATA_TABLE作成
    // 表から列を削除したいがSQLiteにはその命令がないためITEM_TABLEからは、すでに列データが削除されているものとし次の手順で列を削除
    // (1)DATA_TABLEを別名でコピー　(2)DATA_TABLEをITEM_TABLEにしたがって新しく作成　(3)コピーしたファイルから新しいテーブルにデータを挿入(4)別名で保存したテーブル削除
    public void alterTableByItemTable() {
        //-----------------DATA_TABLEを別の名前にしておく
        db.execSQL("DROP TABLE IF EXISTS T");
        db.execSQL("ALTER TABLE " + DATA_TABLE + " RENAME TO T");

        //-----------------DATA_TABLEをITEM_TABLEにしたがって新しく作成
        String[] kataStr = {"INTEGER","REAL","TEXT"};
        ArrayList<Map<String,Object>> items = selectAllItemsOrderByID(); //列名と型取得
        String sql = "CREATE TABLE " + DATA_TABLE +  " (ID1 INTEGER,ID2 INTEGER,";
        String cols = "ID1,ID2,";   //TテーブルからDATA_TABLEにデータを戻す時の列名
        for(Map<String,Object> map : items) {
            String item = (String)map.get("item");
            int kata = (Integer)map.get("kata");
            sql += item + " " + kataStr[kata] + ",";    //CREATE TABLE文作成
            cols += item + ",";                         //TテーブルからDATA_TABLEにデータを戻す時の列名
        }
        sql += " PRIMARY KEY(ID1,ID2))";
        Log.d(TAG,sql);
        db.execSQL(sql);

        //-----------------TテーブルからDATA_TABLEにデータを戻す
        cols = cols.substring(0,cols.length()-1);              //最後のカンマ取り除く
        sql = "INSERT INTO " + DATA_TABLE + "(" + cols +") SELECT "+ cols + " FROM T";
        Log.d(TAG,sql);
        db.execSQL(sql);

        //-----------------別名にしておいた古いテーブルを削除
        db.execSQL("DROP TABLE T");
    }
    //------------------------------------------------ITEM_TABLEからレコードを削除
    public void deleteItem(String item) {
        //最初にITEM_TABLEからITEM=itemのレコードを削除
        String sql = "DELETE FROM " + ITEM_TABLE + " WHERE ITEM='" + item +"'";
        Log.d(TAG,sql);
        db.execSQL(sql);
        alterTableByItemTable();
    }
//    //------------------------------------------------このIDのレコードのdisplayを変更
//    public void updateItemDisplay(int id, int display){
//        ContentValues values = new ContentValues();
//        Log.d(TAG,"updateItemDisplay display="+ display);
//        values.put("display", display);
//
//        db.update(ITEM_TABLE, values, " ID="+id, null);
//    }
    //------------------------------------------------このitem名のレコードのdisplayを変更
    public void updateItemDisplay(String item, int display){
        ContentValues values = new ContentValues();
        Log.d(TAG,"updateItemDisplay display="+ display);
        values.put("display", display);

        db.update(ITEM_TABLE, values, " ITEM='"+item+"'", null);
    }
    //------------------------------------------------　指定項目の最大値データ取得
    public double getMax(String item){
        Log.d(TAG,"getMax "+item);
        String[] columns = {"max("+item+")"};
        Cursor cursor = db.query(
                DATA_TABLE,
                columns,                // The array of columns to return (pass null to get all)
                null,         // The columns for the WHERE clause
                null,      // The values for the WHERE ?clause
                null,          // don't group the rows
                null,           // don't filter by row groups
                null           // The sort order
        );

        cursor.moveToFirst();
        int count = cursor.getCount();
        double max = 0;
        if(count>0) {
            max = cursor.getDouble(0);
        }
        cursor.close(); // 忘れずに！
        return max;
    }
    //------------------------------------------------　指定項目の指定月データ取得
    public ArrayList<Map<String, Object>> selectDataByItem(String item,int year,int month){
        Log.d(TAG,"selectDataByItem "+item+" "+year+" "+ month);
        String[] columns = {"ID2",item};
        String where = " ID1>="+(year*100)+" AND ID1<"+((year+1)*100) + " AND ID2>=" + (month*100) + " AND ID2<" + ((month+1)*100);
        Cursor cursor = db.query(
                DATA_TABLE,
                columns,                // The array of columns to return (pass null to get all)
                where,                  // The columns for the WHERE clause
                null,      // The values for the WHERE ?clause
                null,          // don't group the rows
                null,           // don't filter by row groups
                "ID2 ASC"      // The sort order
        );

        cursor.moveToFirst();
        int count = cursor.getCount();
        ArrayList<Map<String,Object>> list = new ArrayList<Map<String,Object>>();       //"PROGRAMMING"=1などのMap
        for (int i = 0; i < count; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            //日付
            int id2 = cursor.getInt(0);
            int day = id2 % 100;
            map.put("day", day);
            double val = cursor.getDouble(1);       //このメソッドを使うのはREAL型データ
            map.put("val", val);
             list.add(map);
            cursor.moveToNext();
        }
        cursor.close(); // 忘れずに！
        return list;
    }
    //------------------------------------------------　すべての項目データ取得
    public ArrayList<Map<String, Object>> selectAllItems(){
        Log.d(TAG,"selectItems");
        Cursor cursor = db.query(
                ITEM_TABLE,
                null,                // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,           // The values for the WHERE ?clause
                null,               // don't group the rows
                null,                // don't filter by row groups
                "KATA ASC,ID ASC"   // The sort order
        );
        return cursorToList(cursor);
    }
    //------------------------------------------------　すべての項目データ取得
    private ArrayList<Map<String, Object>> cursorToList(Cursor cursor){
        cursor.moveToFirst();

        int count = cursor.getCount();
        Log.d(TAG,"cursorToList　count=" + count);

        ArrayList<Map<String,Object>> list = new ArrayList<Map<String,Object>>();       //"PROGRAMMING"=1などのMap
        for (int i = 0; i < count; i++) {
            Map<String,Object> map = new HashMap<String,Object>();
            //日付
            int id = cursor.getInt(0);
            String item = cursor.getString(1);
            int kata = cursor.getInt(2);
            int display = cursor.getInt(3);
            map.put("id", id);
            map.put("item", item);
            map.put("kata", kata);
            map.put("display", display);
            list.add(map);
            cursor.moveToNext();
        }
        cursor.close(); // 忘れずに！
        return list;
    }
    //------------------------------------------------　すべての項目データ取得
    public ArrayList<Map<String, Object>> selectAllItemsOrderByID(){
        Log.d(TAG,"selectAllItemsOrderByID");
        Cursor cursor = db.query(
                ITEM_TABLE,
                null,                // The array of columns to return (pass null to get all)
                null,               // The columns for the WHERE clause
                null,           // The values for the WHERE ?clause
                null,               // don't group the rows
                null,                // don't filter by row groups
                "ID ASC"            // The sort order
        );
        return cursorToList(cursor);
    }
    //------------------------------------------------　すべての項目データ取得
    public ArrayList<Map<String, Object>> selectAllItemsOfDisplayOrderByKata(){
        Log.d(TAG,"selectItems");
        Cursor cursor = db.query(
                ITEM_TABLE,
                null,                // The array of columns to return (pass null to get all)
                "DISPLAY=1",        // The columns for the WHERE clause
                null,           // The values for the WHERE ?clause
                null,               // don't group the rows
                null,                // don't filter by row groups
                "kata ASC,ID ASC"          // The sort order
        );
        return cursorToList(cursor);
    }
    //------------------------------------------------　すべての項目データ取得
    public ArrayList<Map<String, Object>> selectAllItemsOfDisplayWithoutTextOrderByKata(){
        Log.d(TAG,"selectItems");
        Cursor cursor = db.query(
                ITEM_TABLE,
                null,                // The array of columns to return (pass null to get all)
                "DISPLAY=1 AND KATA<>" + T_TYPE,        // The columns for the WHERE clause
                null,           // The values for the WHERE ?clause
                null,               // don't group the rows
                null,                // don't filter by row groups
                "kata ASC, ID ASC"          // The sort order
        );
        return cursorToList(cursor);
    }
    //------------------------------------------------
    public ArrayList<Map<String, Object>> selectAllDataDay(){
        Log.d(TAG,"selectAllDataDay1");
        //どんな列があるかすべて取得
        ArrayList<Map<String, Object>> cols = selectAllItemsOrderByID();
        printListOfMap(cols);
        Log.d(TAG,"selectAllDataDay2");

        //
        Cursor cursor = db.query(
                DATA_TABLE,
                null,               // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,           // The values for the WHERE clause
                null,               // don't group the rows
                null,                // don't filter by row groups
                "ID1 DESC, ID2 DESC"  // The sort order
        );
        cursor.moveToFirst();

        int count = cursor.getCount();
        Log.d(TAG,"selectAllDataDay　count=" + count);

        ArrayList<Map<String,Object>> list = new ArrayList<Map<String,Object>>();       //"PROGRAMMING"=1などのMap
        for (int i = 0; i < count; i++) {
            Map<String,Object> map = new HashMap<String,Object>();
            //日付
            int id1 = cursor.getInt(0);
            int id2 = cursor.getInt(1);
            int y = id1 / 100;
            int w = id1 % 100;   //週番号
            int m = id2 / 100;
            int d = id2 % 100;
            String date = y + "/" + m + "/" + d;
            map.put("ID", date);
            Log.d("abc",date+"("+w+")");

            //ID以外の列について 列名＝値　をmapに保存
            for(int j=0; j<cols.size(); j++) {
                Map<String,Object> col = cols.get(j);
                String retu = (String)col.get("item");


                if((int)col.get("display") == 1) {
                    if((int)col.get("kata") == I_TYPE) {            //値が数字
                        Log.d(TAG,"select(I) item=["+retu+"]"+cursor.getDouble(j+2));
                        map.put(retu,cursor.getDouble(j+2));        //###############################
                    } else if((int)col.get("kata") == T_TYPE) {      //値がテキスト
                        Log.d(TAG,"select(T) item=["+retu+"]"+cursor.getString(j+2));
                        map.put(retu,cursor.getString(j+2));
                    } else {                                    //値が〇or×
                        if(cursor.getInt(j+2)==1) {
                            Log.d(TAG,"select(B) item=["+retu+"]"+cursor.getInt(j+2));
                            map.put(retu, "〇");
                        } else {
                            Log.d(TAG,"select(B) item=["+retu+"]"+cursor.getInt(j+2));
                            map.put(retu, "×");
                        }
                    }
                }
            }
//            printMap(map);
            list.add(map);
            cursor.moveToNext();
        }
        cursor.close(); // 忘れずに！
        return list;
    }
    //------------------------------------------------
    public ArrayList<Map<String, Object>> selectAllDataWeek(){
        ArrayList<Map<String, Object>> items = selectAllItemsOfDisplayWithoutTextOrderByKata();
        String[] selectItems = getSelectItems(items);   //ID1,MIN(ID2),SUM(豚さん),SUM(英語),SUM(歩数)   のようにSELECT用
        Cursor cursor = db.query(
                DATA_TABLE,
                selectItems,                 // The array of columns to return (pass null to get all)
                null,               // The columns for the WHERE clause
                null,           // The values for the WHERE clause
                "ID1",               // don't group the rows
                null,                // don't filter by row groups
                "ID1 DESC"  // The sort order
        );

        cursor.moveToFirst();

        int count = cursor.getCount();
        Log.d("abc","selectAllWeek1　count=" + count);

        ArrayList<Map<String,Object>> list = new ArrayList<Map<String,Object>>();//"PROGRAMMING"=1などのMap
        Calendar cal = Calendar.getInstance();

        for (int i = 0; i < count; i++) {
            Map<String,Object> map = new HashMap<String,Object>();
            //日付
            int id1 = cursor.getInt(0);
            int y = id1 / 100;                  //年取得
            int w = id1 % 100;                  //週番号取得

            //当該週の最初の日をDAY1に格納
            cal.set(Calendar.YEAR,y);           //その年の
            cal.set(Calendar.WEEK_OF_YEAR,w);   //その週番号の
            cal.set(Calendar.DAY_OF_WEEK,1);    //週の最初の日をセット
            y = cal.get(Calendar.YEAR);         //calの日にちの年と
            int m = cal.get(Calendar.MONTH)+1;  //月と
            int d = cal.get(Calendar.DATE);     //日を取得しDAY1とする
            map.put("DAY1",y + "/" + m + "/" + d);

            //当該週の最後の日をDAY2に格納
            cal.set(Calendar.DAY_OF_WEEK,7);    //週の最後の日をセット
            y = cal.get(Calendar.YEAR);         //calの日にちの年と
            m = cal.get(Calendar.MONTH)+1;      //月と
            d = cal.get(Calendar.DATE);         //日を取得しDAY2とする
            map.put("DAY2",y + "/" + m + "/" + d);

            //日付以外の項目(INTEGER項目)
            int j=2;
            for(Map<String,Object> mm : items) {
                map.put((String)mm.get("item"), cursor.getDouble(j++));    //豚さん＝sum(豚さん)　等
            }
            printMap(map);
            list.add(map);
            cursor.moveToNext();
        }
        cursor.close(); // 忘れずに！
        return list;
    }
    //------------------------------------------------ 週用SELECT 項目を作成
    // {"ID1","MIN(ID"),"SUM(英語)","SUM(歩数)・・・} などの配列を作成する
    // ここに入る項目は非表示を除くまた、テキスト型も除く

    public String[] getSelectItems(ArrayList<Map<String, Object>> items) {
        Log.d(TAG,"getSelectItems1");
        int count = items.size();

        String[] list = new String[count + 2];
        list[0] = "ID1";
        list[1] = "MIN(ID2)";
        for (int i = 0; i < count; i++) {
            String item = (String)items.get(i).get("item");
            list[i+2] = "SUM(" + item + ")";
        }
        return list;
    }
}
