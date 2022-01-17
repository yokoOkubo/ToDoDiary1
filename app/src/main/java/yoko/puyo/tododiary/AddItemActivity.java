// 新しい項目の追加を押した時の画面
package yoko.puyo.tododiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class AddItemActivity extends AppCompatActivity{
    private static final String TAG = "abc";
    RadioButton radioB;
    RadioButton radioI;
    RadioButton radioT;
    EditText item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        radioB = findViewById(R.id.radioB);
        radioI = findViewById(R.id.radioI);
        radioT = findViewById(R.id.radioT);
        item   = findViewById(R.id.item);

        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //-------------------------DBに書き込み
                int kata = MyDAO.B_TYPE;
                if (radioI.isChecked()) {
                    kata = MyDAO.I_TYPE;
                } else if(radioT.isChecked()) {
                    kata = MyDAO.T_TYPE;
                }
                MyDAO dao = new MyDAO(AddItemActivity.this);
                dao.insertItem(item.getText().toString(),kata);

                //-------------------------親画面に戻るとき追加したことがわかるようにResultCode渡す
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);	//resultCode=RESULT_OKとなる
                finish();				        //戻る　これで、下に隠れていた元の画面が表示される
            }
        });
        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                finish();
            }
        });

        //EditTextのフォーカスが外れたらキーボードの予測変換表を削除
        item.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG,"setOnFocusChangeListener" );
                if (hasFocus == false) {    // EditTextのフォーカスが外れた場合
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    //HIDE_IMPLICIT_ONLY : 予測変換表示のみ非表示  HIDE_NOT_ALWAYS:キーボードすべてを非表示
                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            }
        });

        RadioGroup group = findViewById(R.id.typeGroup);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG,"setOnCheckedChangeListener" );
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                //HIDE_IMPLICIT_ONLY : 予測変換表示のみ非表示(うまくいかない)  HIDE_NOT_ALWAYS:キーボードすべてを非表示
                imm.hideSoftInputFromWindow(item.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

    }
}
