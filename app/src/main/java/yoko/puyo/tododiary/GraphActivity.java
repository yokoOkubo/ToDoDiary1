// グラフ表示画面
package yoko.puyo.tododiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class GraphActivity extends AppCompatActivity{
    private static final String TAG="abc";
    Button btnPrev;
    Button btnNext;
    GraphView graphView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Intent intent = getIntent();
        String item = intent.getStringExtra("item");  //どの項目についてのグラフか
        graphView = findViewById(R.id.graph);
        graphView.setItem(item);

        btnPrev = findViewById(R.id.btnPrev);
        btnPrev.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                graphView.prevMonth();
            }
        });

        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                graphView.nextMonth();
            }
        });

        //------------------------------------------------------ 広告
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        AdView mAdView = findViewById(R.id.adViewGraph);
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
}
