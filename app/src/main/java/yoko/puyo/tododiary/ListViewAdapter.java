//　項目管理画面で項目一覧を表示するListViewのAdapter
package yoko.puyo.tododiary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;
import java.util.TimeZone;

public class ListViewAdapter extends BaseAdapter{
    private static final String TAG = "abc";
    private ArrayList<Map<String, Object>> list;
    private Context context;
    private LayoutInflater inflater;    //リソースファイルからViewを作成するためのオブジェクト
    private final static String[] kataStr= {"やった/やらない","数字","テキスト"};

    public ListViewAdapter(Context context, ArrayList<Map<String, Object>> list) {
        super();
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        Log.d(TAG,"ListViewAdapter");
    }
    public void setList(ArrayList<Map<String, Object>> list) {
        this.list = list;
    }

    //----------------------------------------------------------------listの数を返す
    @Override
    public int getCount() {
        return list.size();
    }

    //----------------------------------------------------------------positionの位置にあるデータを返す
    @Override
    public Object getItem(int position) {
        return list.get(position).get("item");
    }

    //----------------------------------------------------------------positionの位置にあるデータのID（ユニークな値でなければならないのでここではpositionを返す）
    @Override
    public long getItemId(int position) {
        return position;
    }

    //----------------------------------------------------------------positionで指定した位置にある行として表示するビューを返す。これが必要な時に自動で呼ばれる
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {//contentView:xmlから作成する1行分のView
        final ViewHolder viewHolder;
        final int listIndex = position;

        if (convertView == null) {      //Viewがまだ生成されていない
            //第2引数はフラグメントを使うときには考えなければいけないがListViewの1行をinflateする場合nullでよいのではないか？
            convertView = inflater.inflate(R.layout.item_row,null);  //row.xmlからViewを作成
            viewHolder = new ViewHolder();  //ここでviewHolderを作るのでposition0で作成し、次に1で呼ばれた時はconvertViewがまだ、nullなのだろう
            viewHolder.item = (TextView)convertView.findViewById(R.id.rowItem);
            viewHolder.kata = (TextView)convertView.findViewById(R.id.rowKata);
            viewHolder.display = (Spinner)convertView.findViewById(R.id.rowDisplay);

            //--- displayのSpinnerにリスナ登録
            viewHolder.display.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                //parent:変更されたSpinner　view選択された項目　　position選択された項目の位置（0オリジン）　　id選択された項目のリソースID
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG,"spinner変更");
                    Spinner spinner=(Spinner)parent;
//                    spinner.setSelection(position);		//選択されたインデックス
                    MyDAO dao = new MyDAO(context);
                    Map<String,Object> map = list.get(listIndex);
                    dao.updateItemDisplay((String)map.get("item"),position);    //positionは　0:表示　1:非表示
                }
                public void onNothingSelected(AdapterView<?> parent) {}
            });
            //--- 項目名に長押しすると削除メニュー表示するリスナ登録
            viewHolder.item.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v) {
                    Log.d(TAG,"item LongTouch");
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    String[] items = {"削除"};
                    builder.setItems(items, new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which) {    //第2引数はダイアログのクリックリスナ
                            switch (which) {
                                case 0: //削除
                                    String item = viewHolder.item.getText().toString();
                                    MyDAO dao = new MyDAO(context);
                                    dao.deleteItem(item);
                                    ArrayList<Map<String, Object>> list = dao.selectAllItems();
                                    setList(list);
                                    notifyDataSetChanged();// update ListView
                                    break;
                            }
                        }
                    });
                    builder.setCancelable(true);    //cancelできるダイアログ
                    builder.show();     // show dialog

                    return false;        //Touchイベントは発生する
                }
            });
            //--- 項目名にタッチすると背景が変化
            viewHolder.item.setOnTouchListener(new View.OnTouchListener(){
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        viewHolder.item.setBackgroundColor(Color.LTGRAY);
                        return false;   //LongClickイベント発生させる
                    } else if(event.getAction() == MotionEvent.ACTION_UP) {
                        viewHolder.item.setBackgroundColor(Color.WHITE);
                        return true;    //これ以上イベント発生しない
                    }
                    return true;// True if the listener has consumed the event, false otherwise.
                }
            });
            convertView.setTag(viewHolder);                 //View自体がviewHolderの情報を持つ
        } else {                        //Viewが既に生成されている場合
            viewHolder = (ViewHolder)convertView.getTag();  //Viewが持っている情報を取り出した
        }
        Map<String, Object> map = list.get(position);
        viewHolder.item.setText((String)map.get("item"));       //

        String kata = kataStr[(Integer)map.get("kata")];
        viewHolder.kata.setText(kata);                          //

        int display = (Integer)map.get("display");
        viewHolder.display.setSelection(display);
        return convertView;
    }
    class ViewHolder {
        TextView item;
        TextView kata;
        Spinner  display;
    }
}
