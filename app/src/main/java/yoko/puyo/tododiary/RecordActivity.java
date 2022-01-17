//今日の結果を入力する画面
package yoko.puyo.tododiary;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class RecordActivity extends AppCompatActivity{
    private static final String TAG = "abc";
    private TextView txtYear;
    private TextView txtMonth;
    private TextView txtDate;

    private Button btnDayHistory;
    private Button btnWeekHistory;
    private Button btnDelete;
    private Button btnToItem;

    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Log.d("abc","onCreate1");
        //----------------------------------------- 履歴ボタン
        btnDayHistory = (Button)findViewById(R.id.btnRecordDayHistory);
        btnWeekHistory = (Button)findViewById(R.id.btnRecordWeekHistory);
        btnDayHistory.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(RecordActivity.this,DayHistoryActivity.class);
                startActivity(intent);
            }
        });
        btnWeekHistory.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(RecordActivity.this,WeekHistoryActivity.class);
                startActivity(intent);
            }
        });

        //----------------------------------------- この日のデータ削除ボタン
        btnDelete = (Button)findViewById(R.id.btnRecordDelete);
        btnDelete.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                int y = Integer.parseInt(txtYear.getText().toString());
                int m = Integer.parseInt(txtMonth.getText().toString());
                int d = Integer.parseInt(txtDate.getText().toString());

                MyDAO dao = new MyDAO(RecordActivity.this);
                dao.deleteData(y,m,d);
            }
        });

        //----------------------------------------- 日付をセット
        txtYear = (TextView)findViewById(R.id.recordYear);
        txtMonth = (TextView)findViewById(R.id.recordMonth);
        txtDate = (TextView)findViewById(R.id.recordDate);
        setHiduke();    //今日の日付をセット
        //----------------------------------------- 日付変更ボタン
        Button btnChangeDate = (Button)findViewById(R.id.btnRecordChangeDate);
        btnChangeDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DatePick datePick = new DatePick();
                datePick.show(getFragmentManager(), "datePicker");
            }
        });
        //----------------------------------------- 入力View作成
        layout = findViewById(R.id.recordLinearLayout);
        MyDAO dao = new MyDAO(RecordActivity.this);
        ArrayList<Map<String, Object>> list = dao.selectAllItemsOfDisplayOrderByKata();    //Mapのkeyは　item　と　kata
        for(Map map: list) {
            int kata = (Integer)map.get("kata");
            if(kata == MyDAO.B_TYPE) {
                makeB((String)map.get("item"));
            } else if(kata == MyDAO.I_TYPE) {
                makeI((String)map.get("item"));
            } else {
                makeT((String)map.get("item"));
            }
        }

        //------------------------------------------------------ 広告
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        AdView mAdView = findViewById(R.id.adViewR);
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
    //============================================== 今日の日付をTextViewに表示
    public void setHiduke() {
        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        int d = cal.get(Calendar.DAY_OF_MONTH);
        Log.d("abc","setHiduke" + y + "/" + m + "/" + d);
        txtYear.setText(String.valueOf(y));
        txtMonth.setText(String.valueOf(m));
        txtDate.setText(String.valueOf(d));
    }
    //============================================== INTEGER型の値をDBへ
    private void updateDB(String item, int val) {
        Log.d(TAG,"int型updateDB");
        int y = Integer.parseInt(txtYear.getText().toString());
        int m = Integer.parseInt(txtMonth.getText().toString());
        int d = Integer.parseInt(txtDate.getText().toString());

        Log.d(TAG,"updateDB y="+y+"m="+m+"d="+d+"item="+item+"val="+val);
        MyDAO dao = new MyDAO(this);
        dao.updateData(y,m,d,item,val);
    }
    //============================================== DOUBLE型の値をDBへ
    private void updateDB(String item, double val) {
        Log.d(TAG,"int型updateDB");
        int y = Integer.parseInt(txtYear.getText().toString());
        int m = Integer.parseInt(txtMonth.getText().toString());
        int d = Integer.parseInt(txtDate.getText().toString());

        Log.d(TAG,"updateDB y="+y+"m="+m+"d="+d+"item="+item+"val="+val);
        MyDAO dao = new MyDAO(this);
        dao.updateData(y,m,d,item,val);
    }
    //============================================== テキスト型の値をDBへ
    private void updateDB(String item, String val) {
        int y = Integer.parseInt(txtYear.getText().toString());
        int m = Integer.parseInt(txtMonth.getText().toString());
        int d = Integer.parseInt(txtDate.getText().toString());

        Log.d(TAG,"updateDB y="+y+"m="+m+"d="+d+"item="+item+"val="+val);
        MyDAO dao = new MyDAO(this);
        dao.updateData(y,m,d,item,val);
    }
    //============================================== やった、やらない型の入力行作成
    private void makeB(final String item) {
        Log.d(TAG,"BTYPE");

        //----- 横に並べるLinearLayout
        LinearLayout l = new LinearLayout(this);

        //----- アイテムを表示するTextView
        final TextView txtItem = new TextView(this);
        txtItem.setText(item);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;

        //----- やりましたボタン
        Button btnOk = new Button(this);
        btnOk.setText("やった");
        btnOk.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                updateDB(item,1);
            }
        });
        btnOk.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {   //オレンジ
                    v.setBackgroundColor(0xffffa500);   //オレンジ
                }
                //ボタンを同じ位置で離したときはACTION_UP　ボタンを押しながら移動しようとしたときはACTION_CANCELが起きるので
                if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setBackgroundResource(android.R.drawable.btn_default);
                }
                return false;// True if the listener has consumed the event, false otherwise.
            }
        });

        //----- やらなかったボタン
        Button btnNo = new Button(this);
        btnNo.setText("やらなかった");
        btnNo.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                updateDB(item,0);
            }
        });
        btnNo.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(0xffffa500);   //オレンジ
                }
                if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setBackgroundResource(android.R.drawable.btn_default);
                }
                return false;// True if the listener has consumed the event, false otherwise.
            }
        });

        //----- 画面に表示
        layout.addView(l,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        l.addView(txtItem,params);
        l.addView(btnOk,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        l.addView(btnNo,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    //============================================== 数値型の入力行作成
    private void makeI(final String item) {
        Log.d(TAG,"ITYPE");

        //----- 横に並べるLinearLayout
        LinearLayout l = new LinearLayout(this);

        //----- アイテムを表示するTextView
        TextView txtItem = new TextView(this);
        txtItem.setText(item);

        //----- データ入力用EditText
        final EditText data = new EditText(this);
        data.setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;

        //----- 登録ボタン
        Button btn = new Button(this);
        btn.setText("登録");
        btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Log.d(TAG,"登録ボタン");
                try {
                    Log.d(TAG,"登録ボタン　データ=" + Double.parseDouble(data.getText().toString()));
                    updateDB(item, Double.parseDouble(data.getText().toString()));
                } catch(NumberFormatException e) {
                    Log.d(TAG,"登録ボタン2");
                    Toast.makeText(RecordActivity.this,"ここには数字を入力してください",Toast.LENGTH_SHORT);
                }
            }
        });
        btn.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(0xffffa500);   //オレンジ
                }
                if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setBackgroundResource(android.R.drawable.btn_default);
                }
                return false;// True if the listener has consumed the event, false otherwise.
            }
        });


        //----- 画面に表示
        layout.addView(l,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        l.addView(txtItem,params);
        l.addView(data,params);
        l.addView(btn,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    //============================================== テキスト型の入力行作成
    private void makeT(final String item) {
        Log.d(TAG,"TTYPE");

        //----- 横に並べるLinearLayout
        LinearLayout l = new LinearLayout(this);

        //----- アイテムを表示するTextView
        TextView txtItem = new TextView(this);
        txtItem.setText(item);

        //----- データ入力用EditText
        final EditText data = new EditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;

        //----- 登録ボタン
        Button btn = new Button(this);
        btn.setText("登録");
        btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                updateDB(item, data.getText().toString());
            }
        });
        btn.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(0xffffa500);   //オレンジ
                }
                if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setBackgroundResource(android.R.drawable.btn_default);
                }
                return false;// True if the listener has consumed the event, false otherwise.
            }
        });

        //----- 画面に表示
        layout.addView(l,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        l.addView(txtItem,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        l.addView(data,params);
        l.addView(btn,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
