// 週ごとの履歴表示画面
package yoko.puyo.tododiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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

public class WeekHistoryActivity extends AppCompatActivity{
    private static final String TAG = "abc";

    private Button toDay;
    private Button toInput;
    LinearLayout data;
    ArrayList<Map<String, Object>> items;
    ArrayList<Map<String, Object>> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_history);

        Log.d(TAG,"DayHistory onCreate");
        MyDAO dao = new MyDAO(this);
        //------------------------------------------------------headWに項目を追加
        items = dao.selectAllItemsOfDisplayWithoutTextOrderByKata(); //TEXT型は除く
        addHead();
        //------------------------------------------------------dataAddLayoutWに項目追加
        data = findViewById(R.id.dataAddLayoutW);
        dataList = dao.selectAllDataWeek();
        dao.printListOfMap(dataList);
        addData();
        //------------------------------------------------------日別履歴へボタン
        toDay = (Button)findViewById(R.id.toDayW);
        toDay.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(WeekHistoryActivity.this,DayHistoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);          //#################################
                startActivity(intent);
            }
        });

        //------------------------------------------------------入力画面へボタン
        toInput = (Button)findViewById(R.id.toInputW);
        toInput.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(WeekHistoryActivity.this,RecordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);          //#################################
                startActivity(intent);
            }
        });
        //------------------------------------------------------ 広告
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        AdView mAdView = findViewById(R.id.adViewW);
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
        LinearLayout head = findViewById(R.id.headW);
        //-----日付
        TextView txtDate = new TextView(this);
        txtDate.setText("日曜～月曜\n合計or回数");
        txtDate.setGravity(Gravity.END);
        head.addView(txtDate, (int)Util.convertDp2Px(110, this), ViewGroup.LayoutParams.WRAP_CONTENT); //90dpにしたい第2引数width、第3引数height

        for(Map<String, Object> map:items) {
            TextView txtItem = new TextView(this);
            Log.d(TAG,"textSize=" + txtItem.getTextSize());
            txtItem.setTextSize(15);    //単位はｓｐ
            Log.d(TAG,"textSize2=" + txtItem.getTextSize());

            txtItem.setText((String)map.get("item"));
            int kata = (Integer)map.get("kata");
            if(kata == MyDAO.B_TYPE) {
                txtItem.setGravity(Gravity.END);
                head.addView(txtItem,(int)Util.convertDp2Px(28, this),LinearLayout.LayoutParams.WRAP_CONTENT);   //28dpにしたい
            } else if(kata == MyDAO.I_TYPE) {
                txtItem.setGravity(Gravity.END);
                head.addView(txtItem, (int)Util.convertDp2Px(50, this), LinearLayout.LayoutParams.WRAP_CONTENT);  //50dpにしたい
            }
        }
    }
    private void addData() {
        for(Map<String, Object> one : dataList) {               //one.get("豚さん")：6　one.get("歩数"):8000 など週合計がはいっている
            LinearLayout gyo = new LinearLayout(this);  //1行用のLinearLayout defaultがHORIZONTAL
            //-----日付データ
            TextView txtDate = new TextView(this);
            String day = (String) one.get("DAY1") + "\n" + (String) one.get("DAY2");
            txtDate.setText(day);
            txtDate.setGravity(Gravity.END);
            gyo.addView(txtDate, (int)Util.convertDp2Px(110, this), ViewGroup.LayoutParams.WRAP_CONTENT); //90dpにしたい

            //-----日付以外のデータ
            for (Map<String, Object> itemMap : items) {          //itemMap.get("item"):豚さん　　itemMap.get("kata"):BTYPE
                String item = (String) itemMap.get("item");      //項目名が順番に来るのでその項目名をキーにして値を取得しTextViewに表示
                Double val = (Double)one.get(item);
                String valStr;
                if(val == null) {
                    valStr = "";
                } else {
                    valStr = val.toString();
                    if(valStr.endsWith(".0")) { //小数点以下０なら小数点以下取り除く
                        valStr = valStr.substring(0,valStr.length()-2);
                    }
                }

                TextView txtItem = new TextView(this);
                txtItem.setText(valStr);
                txtItem.setGravity(Gravity.END|Gravity.CENTER_VERTICAL);

                int kata = (Integer) itemMap.get("kata");         //型によりサイズを変更
                if (kata == MyDAO.B_TYPE) {
                    gyo.addView(txtItem, (int)Util.convertDp2Px(28, this), ViewGroup.LayoutParams.MATCH_PARENT); //28dpにしたい
                } else if (kata == MyDAO.I_TYPE) {
                    gyo.addView(txtItem, (int)Util.convertDp2Px(50, this), ViewGroup.LayoutParams.MATCH_PARENT); //50dpにしたい
                }
            }
            data.addView(gyo, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);  //1日分のデータを表示
        }
    }
}
