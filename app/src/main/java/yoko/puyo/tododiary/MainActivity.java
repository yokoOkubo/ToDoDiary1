// TOP画面
package yoko.puyo.tododiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnItem = findViewById(R.id.btnItemManagement);
        btnItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {       //項目の管理ボタン
                Intent intent = new Intent(MainActivity.this,ItemManagementActivity.class);
                startActivity(intent);
            }
        });
        Button btnRecord = findViewById(R.id.btnRecord);
        btnRecord.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {    //記録ボタン
                Intent intent = new Intent(MainActivity.this,RecordActivity.class);
                startActivity(intent);
            }
        });

        //------------------------------------------------------ 広告
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        AdView mAdView = findViewById(R.id.adView);
        //test用広告の時
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
