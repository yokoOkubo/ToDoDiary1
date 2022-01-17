//　項目の管理をする画面
package yoko.puyo.tododiary;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.Map;

public class ItemManagementActivity extends AppCompatActivity{
    private static final String TAG = "abc";
    private static final int ADD_CODE = 1000;
    ListView listView;
    ListViewAdapter adapter;
    TextView listTitle;     //"現在登録中の項目一覧","まだ登録された項目はありません"
    MyDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_management);

        //------------------------------------------------------ 表示するために登録されている項目を取得
        listTitle = findViewById(R.id.txtListTitle);

        dao = new MyDAO(this);
        ArrayList<Map<String, Object>> list = dao.selectAllItems();
        dao.printListOfMap(list);
        if (list.size() == 0) {
            listTitle.setText("まだ登録された項目はありません");
        }
        //------------------------------------------------------ 登録されている項目をListViewに表示
        listView = findViewById(R.id.itemListView);
        adapter = new ListViewAdapter(ItemManagementActivity.this,list);
        listView.setAdapter(adapter);
        //----------------- liｓｔViewが長押しされたときの処理はここで作成しない
        //----------------- listViewの上にTextViewやSpinnerなどが乗っているためそちらにListenerをつける
        //----------------- ただし、TextViewなどにLongClickListenerをつけて、戻り値falseとすると、こちらにもイベントが伝わる

        //------------------------------------------------------ 新しい項目の追加ボタンの処理
        Button btnAddItem = findViewById(R.id.btnAddItem);
        btnAddItem.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(ItemManagementActivity.this,AddItemActivity.class);
                startActivityForResult(intent,ADD_CODE);
            }
        });
        //------------------------------------------------------ すべての項目の削除ボタンの処理
        Button btnDeletItem = findViewById(R.id.btnDeleteItem);
        btnDeletItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dao.deleteAllItem();
                ArrayList<Map<String, Object>> list = dao.selectAllItems();
                adapter.setList(list);
                adapter.notifyDataSetChanged();
                listTitle.setText("まだ登録された項目はありません");
            }
        });
        //------------------------------------------------------ 広告
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        AdView mAdView = findViewById(R.id.adView2);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ADD_CODE && resultCode == RESULT_OK) {    //追加できたのでListViewを書き直し
            Log.d(TAG,"onActivityResult");
            ArrayList<Map<String, Object>> list = dao.selectAllItems();
            adapter.setList(list);
            adapter.notifyDataSetChanged();
            if(list.size() != 0) {
                listTitle.setText("現在登録中の項目一覧");
            } else {
                listTitle.setText("まだ登録された項目はありません");
            }
        }
    }
}
