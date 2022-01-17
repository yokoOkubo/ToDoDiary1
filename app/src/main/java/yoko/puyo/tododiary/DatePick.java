/**
 * 日付選択ダイアログ
 * DatePickerDialogを直接使うと、機種により、画面全体がカレンダーになったりするため、
 * DialogFragmentの上に乗せるらしい
 * このダイアログは、日付が変更された時のリスナを持っているので、そこで日付を画面に描く
 * 画面に描くためにはTextViewが必要であるが、このクラスはFragmentであるので、
 * getActivity().findViewByIdで、TextViewを知ることができる
 */
package yoko.puyo.tododiary;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;


public class DatePick extends DialogFragment{
    Calendar cal = Calendar.getInstance();
    TextView txtY;
    TextView txtM;
    TextView txtD;
    int nowY;
    int nowM;
    int nowD;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //dialogを表示しているRecordActivityの年月日を取得できる
        txtY = (TextView)getActivity().findViewById(R.id.recordYear);
        txtM = (TextView)getActivity().findViewById(R.id.recordMonth);
        txtD = (TextView)getActivity().findViewById(R.id.recordDate);

        nowY = Integer.parseInt(txtY.getText().toString());
        nowM = Integer.parseInt(txtM.getText().toString());
        nowD = Integer.parseInt(txtD.getText().toString());

        DatePickerDialog dialog = new DatePickerDialog(
                getActivity(),
                new DatePickerDialog.OnDateSetListener(){
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        txtY.setText(String.valueOf(year));
                        txtM.setText(String.valueOf(month+1));
                        txtD.setText(String.valueOf(dayOfMonth));
                    }
                },
                nowY,
                nowM-1,
                nowD
        );
        return dialog;
    }
}
