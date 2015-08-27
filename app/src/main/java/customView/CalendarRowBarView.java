package customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by marioviti on 25/08/15.
 */
public class CalendarRowBarView extends View {

    private int dimensionsW, dimensionsH;
    private Paint[] paint;
    private float[] rec;
    private float[] recPercent;
    private int N = 5;

    public CalendarRowBarView( Context context, AttributeSet attrs ) {

        super(context, attrs);
        SupportContentValues.incEntry();
        rec = SupportContentValues.giveValues();
        recPercent =  new float[N];
        paint = new Paint[N];
        int j;
        /*for(int i = 0;i<N; i++) {
            j=i+1;
            rec[i] = attrs.getAttributeFloatValue("http://schemas.android.com/apk/res-auto", "rec"+j, 30);
        }*/

        paint[0] = new Paint();
        paint[0].setColor(Color.RED);
        paint[1] = new Paint();
        paint[1].setColor(Color.BLUE);
        paint[2] = new Paint();
        paint[2].setColor(Color.YELLOW);
        paint[3] = new Paint();
        paint[3].setColor(Color.CYAN);
        paint[4] = new Paint();
        paint[4].setColor(Color.GREEN);

    }

    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ) {

        dimensionsW = getDefaultSize( getSuggestedMinimumWidth(), widthMeasureSpec );
        dimensionsH = getDefaultSize( getSuggestedMinimumHeight(), heightMeasureSpec );
        setMeasuredDimension(dimensionsW, dimensionsH);
    }

    private void convertPercentage(){
        for(int i = 0;i<N; i++)
        recPercent[i] = (rec[i]/(float)100)*(float)dimensionsW;
    }
    @Override
    protected void onDraw( Canvas canvas ) {

        convertPercentage();
        super.onDraw(canvas);
        Log.d("dim",""+rec[0]+" "+rec[1]+" "+rec[2]+" "+rec[3]+" "+rec[4]+" ");
        canvas.drawRect(0, 0, (float)dimensionsW, (float) dimensionsH, paint[0]);
        canvas.drawRect(recPercent[0], 0, (float)dimensionsW, (float)dimensionsH, paint[1]);
        canvas.drawRect(recPercent[0]+recPercent[1], 0, (float)dimensionsW, (float)dimensionsH, paint[2]);
        canvas.drawRect(recPercent[0]+recPercent[1]+recPercent[2], 0, (float)dimensionsW,(float)dimensionsH, paint[3]);
        canvas.drawRect(recPercent[0]+recPercent[1]+recPercent[2]+recPercent[3], 0, (float)dimensionsW, (float)dimensionsH, paint[4]);
    }
}
