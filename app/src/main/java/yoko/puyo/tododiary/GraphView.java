// グラフ表示View
package yoko.puyo.tododiary;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import androidx.annotation.Nullable;

public class GraphView extends View{
    private String TAG = "abc";
    private int   rectW = 10;
    private int   rectH = 10;
    private Rect rect;
    private Paint paint;
    private ArrayList<Data> list = new ArrayList<Data>();
    private String item;
    private int year;
    private int month;
    private double dataMax = 0;

    public GraphView(Context context) {
        super(context);
        init();
    }

    public GraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG,"GraphView");
        init();
    }

    public GraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, Paint paint) {
        super(context, attrs, defStyleAttr);
        this.paint = paint;
        init();
    }
    public void setItem(String item) {
        Log.d(TAG,"setItem item="+item);

        this.item = item;
        MyDAO dao = new MyDAO(getContext());
        dataMax = dao.getMax(item);
        setData();
    }
    public void init() {
        paint = new Paint();
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG,"onDraw"+"x="+canvas.getWidth()+" y="+canvas.getHeight());

        canvas.drawColor(Color.WHITE);          //背景

        float  density = getResources().getDisplayMetrics().density;
        Log.d(TAG,"density="+density);
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);    //枠線＋塗りつぶし
        int h = (int)(500 * density / 31);
        double w = (dataMax==0)? 0 : 200.0 * density / dataMax;
        int left = (int)(50*density);
        int right;
        int top;
        int bottom;
        paint.setTextSize(25*density);
        canvas.drawText(year+"年"+month+"月  " + item,left,h+h,paint);                 //タイトル

        paint.setTextSize(15*density);
        for(int i=0; i<list.size(); i++) {
            right = (int)(left + list.get(i).value*w);
            top = h*i+h+h+h;
            bottom = (int)(top + h * 0.8);
            canvas.drawRect(left,top,right,bottom, paint);                              //棒グラフ
            canvas.drawText(list.get(i).date,10,bottom,paint);                      //日付
            if (list.get(i).value != 0) {
                String val = String.valueOf(list.get(i).value);
                if(val.endsWith(".0")) {
                    val = val.substring(0,val.length()-2);      //小数点以下削除
                }
                canvas.drawText(" "+val, right, bottom, paint);  //値
            }
            Log.d(TAG,"rect left,top,right,bottom="+left+","+top+","+right+","+bottom);
        }
        //-------- 文字
//        paint.setColor(Color.BLACK);
//        paint.setStrokeWidth(2);
//        paint.setStyle(Paint.Style.FILL);

//        canvas.drawText("x="+canvas.getWidth()+" y="+canvas.getHeight(),0,300,paint);
    }
    @Override
    protected void onSizeChanged(int w, int h, int old_w, int old_h) {

    }
    @Override
    /**
     * 描画するためにこのビューのサイズを測定する必要があり、その時呼ばれる。
     * 引数には、ビューグループは自分の下のビューのサイズなどのMeasure仕様を引数に入れる　match_parentなど指定されることがあるため。
     * 引数の上位2ビットにモードが入っている　モードとしては次のものがある。
     * 　　EXACTLY:親が決めた。sizeで指定した値に幅（高さ）を設定すること。
     * 　　UNSPECIFIED : 親は子に制約を課していない。それが望むどんなサイズでもかまわない
     * 　　AT_MOST : sizeで指定した値以下にする。子Viewもそれ前提でサイズを決める
     * モードによってサイズを決め、最後に、　setMeasuredDimension(width,height)　で、サイズを申告する
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float  density = getResources().getDisplayMetrics().density;

        int  hmode = MeasureSpec.getMode(heightMeasureSpec);    //指定されたMeasure仕様からモードを抽出。heightMeasureSpecの上位2ビットに格納されている
        int  wmode = MeasureSpec.getMode(widthMeasureSpec);

        int  hsize = MeasureSpec.getSize(heightMeasureSpec);    //下位30bit取得
        int  wsize = MeasureSpec.getSize(widthMeasureSpec);

        // 高さを求める
        int  height = 0;
        if(hmode == MeasureSpec.EXACTLY) {          //親が決めたのでそれを使う
            height = hsize;
        } else if(hmode ==MeasureSpec.UNSPECIFIED){ //自由に決めてよい
            height = (int)( 700f * density );       //ピクセルに変換している
        } else {                                    //hsizeの範囲なら自由に決めてよい
            height = (int)( 700f * density );
            if(height > hsize) {
                height = hsize;
            }
        }

        // 幅を求める
        int  width = 0;
        if( wmode==MeasureSpec.EXACTLY ) {
            width = wsize;
        } else if(wmode ==MeasureSpec.UNSPECIFIED){
            width = (int)( 400f * density );
        } else {
            width = (int)( 400f * density );
            if(width > wsize) {
                width = wsize;
            }
        }

        setMeasuredDimension( width , height );     //自分のサイズを申告
    }
    public void nextMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(year,month,1);  //次月
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        setData();
    }
    public void prevMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(year,month-2,1);  //次月
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        setData();
    }
    public void setData() {

        list = new ArrayList<Data>();
        if(item == null) return;

        MyDAO dao = new MyDAO(getContext());
        ArrayList<Map<String, Object>> dbList = dao.selectDataByItem(item, year, month);
        dao.printListOfMap(dbList);
        Log.d(TAG,"setData item="+item+" dataSize="+dbList.size());
        int dbIndex = 0;
        int last = getLastDay();
//        String ym = year+"年"+month+"月";

        int d = 0;
        int day = 0;
        for(dbIndex=0; dbIndex<dbList.size(); dbIndex++) {
            Map<String, Object> map = dbList.get(dbIndex);
            day = (Integer)map.get("day");
            double val = (Double)map.get("val");

            for(d=d+1; d<day; d++) {    //DBに入っていない日付は０のデータ作成
                list.add(new Data(d + "日", 0));
            }
            list.add(new Data(day+"日", val));    //DBのデータ作成
        }
        for(d=d+1; d<=last; d++) {
            list.add(new Data( d + "日", 0));
        }
        invalidate();
    }
    private int getLastDay() {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);    //次月の1日
        cal.set(Calendar.DATE,cal.get(Calendar.DATE)-1);
        return cal.get(Calendar.DATE);
    }
    class Data{
        String date;
        double    value;

        Data(String date, double value) {
            this.date = date;
            this.value = value;
        }
    }
}
