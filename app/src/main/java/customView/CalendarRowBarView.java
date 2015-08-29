package customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.marioviti.checko.R;

import Support.SupporHolder;

/**
 * Created by marioviti on 25/08/15.
 */
public class CalendarRowBarView extends View {

    private int dimensionsW, dimensionsH;
    private Paint paintAlpha;
    private Paint[] paint;
    private int [] settedValues = null;
    private int [] rec;
    private float[] recPercent;
    private int N = 5;
    private int alphaVal = 100;
    private String date;
    private int CahceID;

    public CalendarRowBarView( Context context, AttributeSet attrs ) {

        super(context, attrs);
        rec = new int[N];
        recPercent =  new float[N];
        paint = new Paint[N];
        paintAlpha = new Paint();
        paintAlpha.setColor(Color.BLACK);
        int j;
        if(settedValues == null)
            for(int i = 0;i<N; i++) {
                j=i+1;
                rec[i] = (int)attrs.getAttributeFloatValue("http://schemas.android.com/apk/res-auto", "rec"+j, 20);
            }
        paint[0] = new Paint();
        paint[0].setColor(getResources().getColor(R.color.btn_color_type1));
        paint[1] = new Paint();
        paint[1].setColor(getResources().getColor(R.color.btn_color_type2));
        paint[2] = new Paint();
        paint[2].setColor(getResources().getColor(R.color.btn_color_type3));
        paint[3] = new Paint();
        paint[3].setColor(getResources().getColor(R.color.btn_color_type4));
        paint[4] = new Paint();
        paint[4].setColor(getResources().getColor(R.color.btn_color_type5));
    }

    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ) {

        dimensionsW = getDefaultSize( getSuggestedMinimumWidth(), widthMeasureSpec );
        dimensionsH = getDefaultSize( getSuggestedMinimumHeight(), heightMeasureSpec );
        setMeasuredDimension(dimensionsW, dimensionsH);
    }

    private void convertPercentage() {

        float sum = 0;
        for(int i = 0;i<N; i++)
            sum+=(float)rec[i];
        for(int i = 0;i<N; i++) {
            recPercent[i] = ((float) rec[i] / sum) * (float) dimensionsW;
        }
    }
    @Override
    protected void onDraw( Canvas canvas ) {

        if(settedValues!=null)
            rec = settedValues;
        convertPercentage();
        super.onDraw(canvas);
        canvas.drawRect(0, 0, (float) dimensionsW, (float) dimensionsH, paint[0]);
        canvas.drawRect(recPercent[0], 0, (float)dimensionsW, (float)dimensionsH, paint[1]);
        canvas.drawRect(recPercent[0]+recPercent[1], 0, (float)dimensionsW, (float)dimensionsH, paint[2]);
        canvas.drawRect(recPercent[0] + recPercent[1] + recPercent[2], 0, (float) dimensionsW, (float) dimensionsH, paint[3]);
        canvas.drawRect(recPercent[0] + recPercent[1] + recPercent[2] + recPercent[3], 0, (float) dimensionsW, (float) dimensionsH, paint[4]);
        paintAlpha.setAlpha(alphaVal);
        if ( this.getDateID() != SupporHolder.currentDayID)
            canvas.drawRect(0, 0, (float) dimensionsW, (float) dimensionsH, paintAlpha);
    }

    public void setPositionDATECacheID(int pos) { this.CahceID = pos; }

    public int getPositionDATECacheID() { return this.CahceID; }

    //Animation Handles
    public int getAlphaVal() {
        return this.alphaVal;
    }

    public void setAlphaVal( int alphaVal) {
        this.alphaVal = alphaVal;
    }

    //Utility
    public int getDateID() {
        return settedValues[5];
    }

    public String getDate() {
        return date;
    }

    public void setValues (int [] settedValues) {
        this.settedValues = settedValues;
    }

    public void setDate (String date) {
        this.date = date;
    }
}
