package customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by marioviti on 31/08/15.
 */
public class HistogramView extends View{

    int dimensionsW, dimensionsH;
    float spanDim;
    float [] values, normalizedValues;
    Paint paint;

    public HistogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(values!=null) {
            this.normalizedValues = new float[values.length];
        }else {
            this.values = new float[]{5, 5, 5, 5};
            this.normalizedValues = new float[values.length];
        }
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAlpha(50);
    }

    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ) {

        dimensionsW = getDefaultSize( getSuggestedMinimumWidth(), widthMeasureSpec );
        dimensionsH = getDefaultSize( getSuggestedMinimumHeight(), heightMeasureSpec );
        spanDim = dimensionsH/4;
        setMeasuredDimension(dimensionsW, dimensionsH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setNormalizedValues();
        for(int i = 0; i<normalizedValues.length; i++)
            canvas.drawRect((float)0, spanDim*i, (float)dimensionsW*normalizedValues[i], spanDim*(i+1), paint);
    }

    private void setNormalizedValues() {
        float max = 0;
        for(int i= 0; i<values.length; i++) {
            if (values[i]>max)
                max = values[i];
        }
        if(max!=0) {
            for (int i = 0; i < values.length; i++) {
                normalizedValues[i] = values[i] / max;
            }
        }
    }

    public void setValues(float[] values) {
        if(this.values==null) {
            this.normalizedValues = new float[values.length];
        }
        this.values = values;
    }

    public float[] getValues() {return this.values;}
}
