// 日別履歴
package yoko.puyo.tododiary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.Map;

public class DayHistoryActivity extends AppCompatActivity{
    private static final String TAG = "abc";

    private Button toWeek;
    private Button toInput;
//    LinearLayout head;
    LinearLayout data;
    ArrayList<Map<String, Object>> items;
    ArrayList<Map<String, Object>> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_history);

        Log.d(TAG,"DayHistory onCreate");
        MyDAO dao = new MyDAO(this);
        //------------------------------------------------------headDに項目を追加
//        head = findViewById(R.id.headD);
//        Log.d(TAG,"onCreate head="+head);
        items = dao.selectAllItemsOfDisplayOrderByKata();
        addHead();
        //------------------------------------------------------dataAddLayoutDに項目追加
        data = findViewById(R.id.dataAddLayoutD);
        dataList = dao.selectAllDataDay();
        dao.printListOfMap(dataList);
        addData();
        //------------------------------------------------------週別履歴へボタン
        toWeek = (Button)findViewById(R.id.toWeekD);
        toWeek.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(DayHistoryActivity.this,WeekHistoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);         //バックスタックを消す
                startActivity(intent);
            }
        });

        //------------------------------------------------------入力画面へボタン
        toInput = (Button)findViewById(R.id.toInputD);
        toInput.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(DayHistoryActivity.this,RecordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);          //バックスタックを消す
                startActivity(intent);
            }
        });
        //------------------------------------------------------ 広告
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        AdView mAdView = findViewById(R.id.adViewD);
//        //test用広告の時
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .build();
        //本番広告の時
//        AdRequest adRequest = new AdRequest.Builder().build();
        Bundle extras = new Bundle();
        extras.putString("max_ad_content_rating", "G");
        AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras) .tagForChildDirectedTreatment(true) .build();
        mAdView.loadAd(adRequest);
    }
    private void addHead() {
         LinearLayout head = findViewById(R.id.headD);  //タイトル表示用LinearLayout
        //-----日付データ
        TextView txtDate = new TextView(this);
        txtDate.setText("年月日");
        txtDate.setGravity(Gravity.END);
        head.addView(txtDate, (int)Util.convertDp2Px(110, this), ViewGroup.LayoutParams.WRAP_CONTENT); //90dpにしたい第2引数width、第3引数height

        for(Map<String, Object> map:items) {
            TextView txtItem = new TextView(this);
            txtItem.setTextSize(15);    //単位はｓｐ(dpと似ているが、画面上のサイズはユーザのフォントサイズ設定に準ず)
            Log.d(TAG,"textSize2=" + txtItem.getTextSize());


            int kata = (Integer)map.get("kata");
            if(kata == MyDAO.B_TYPE) {
                txtItem.setText((String)map.get("item"));
                txtItem.setGravity(Gravity.END);
                head.addView(txtItem,(int)Util.convertDp2Px(28, this),LinearLayout.LayoutParams.WRAP_CONTENT);   //28dpにしたい
            } else if(kata == MyDAO.I_TYPE) {
                txtItem.setText(Html.fromHtml("<u>"+(String)map.get("item")+"</u>"));   //下線を引く＃＃＃＃＃＃＃＃＃＃＃＃＃＃＃＃＃＃
                txtItem.setGravity(Gravity.END);
                makeToGraph(txtItem);
                head.addView(txtItem, (int)Util.convertDp2Px(70, this), LinearLayout.LayoutParams.WRAP_CONTENT);  //70dpにしたい
            } else {
                txtItem.setText((String)map.get("item"));
                txtItem.setPadding(10,0,0,0);
                head.addView(txtItem, (int)Util.convertDp2Px(200, this), LinearLayout.LayoutParams.WRAP_CONTENT); //200dpにしたい
            }
        }
    }
    //------------------------------------------------------ タップした時グラフ表示するリスナをtxtItemにつける
    private void makeToGraph(TextView txtItem) {
        final String item = txtItem.getText().toString();
         txtItem.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Log.d(TAG,"onLongClick");
                Intent intent = new Intent(DayHistoryActivity.this,GraphActivity.class);
                intent.putExtra("item",item);
                startActivity(intent);

            }
       });
//        txtItem.setOnLongClickListener(new View.OnLongClickListener() {
//
//            @Override
//            public boolean onLongClick(View v) {    //長押しの時に何かしたい
//                return true;    // 戻り値をtrueにするとOnClickイベントは発生しない
//            }
//        });
//        txtItem.setOnTouchListener(new View.OnTouchListener(){//マウスダウンorマウスアップの時になにかしたい()
//            public boolean onTouch(View v, MotionEvent event) {
//                if(event.getAction() == MotionEvent.ACTION_DOWN) {
//
//                } else if(event.getAction() == MotionEvent.ACTION_UP) {
//                }
//                return true;
//            }
//        });

    }
    private void addData() {
        for(Map<String, Object> one : dataList) {               //one.get("豚さん")：〇　one.get("歩数"):8000 などがはいっているが 豚さん などは
            LinearLayout gyo = new LinearLayout(this);  //1行用のLinearLayout defaultがHORIZONTAL
            //-----日付データ
            TextView txtDate = new TextView(this);
            txtDate.setText((String) one.get("ID"));
            txtDate.setGravity(Gravity.END);
            gyo.addView(txtDate, (int)Util.convertDp2Px(110, this), ViewGroup.LayoutParams.WRAP_CONTENT); //90dpにしたい第2引数width、第3引数height

            //-----日付以外のデータ
            for (Map<String, Object> itemMap : items) {          //itemMap.get("item"):豚さん　　itemMap.get("kata"):BTYPE
                String item = (String) itemMap.get("item");      //項目名が順番に来るのでその項目名をキーにして値を取得しTextViewに表示
                TextView txtItem = new TextView(this);
                Object obj = one.get(item);

                int kata = (Integer) itemMap.get("kata");         //型によりサイズを変更して
                if (kata == MyDAO.B_TYPE) {
                    txtItem.setText((obj==null)? "×" : (String)obj);
                    txtItem.setGravity(Gravity.END);

                    gyo.addView(txtItem, (int)Util.convertDp2Px(28, this), ViewGroup.LayoutParams.WRAP_CONTENT); //28dpにしたい
                } else if (kata == MyDAO.I_TYPE) {
                    String val;
                    if(obj == null) {
                        val = "";
                    } else {
                        val = ((Double)obj).toString();
                        if(val.endsWith(".0")) {
                            val = val.substring(0,val.length()-2);  //.0なら取り除く
                        }
                    }
                    txtItem.setText(val);
                    txtItem.setGravity(Gravity.END);
                    gyo.addView(txtItem, (int)Util.convertDp2Px(70, this), ViewGroup.LayoutParams.WRAP_CONTENT); //50dpにしたい
                } else {
                    txtItem.setText((obj==null)? "" : (String)obj);
                    txtItem.setPadding(10,0,0,0);
                    gyo.addView(txtItem, (int)Util.convertDp2Px(200, this), ViewGroup.LayoutParams.WRAP_CONTENT); //200dpにしたい
                }
            }
            data.addView(gyo, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);  //1日分のデータを表示
        }
    }


}
