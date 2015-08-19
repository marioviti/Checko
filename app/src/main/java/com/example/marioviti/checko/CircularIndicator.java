package com.example.marioviti.checko;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Custom View per PageFragment
 */

public class CircularIndicator extends View {

    private static final int START_ANGLE_POINT = 90;
    private final Paint paint;
    private RectF rect;
    private float angle;
    private final int strokeWidth = 40;
    private int dimensionsW, dimensionsH;

    public CircularIndicator( Context context, AttributeSet attrs ) {
        super( context, attrs );

        paint = new Paint();
        paint.setAntiAlias( true );
        paint.setStyle( Paint.Style.STROKE );
        paint.setStrokeWidth( strokeWidth );
        paint.setColor( Color.RED );
        angle = 356;
    }

    @Override
    protected void onDraw( Canvas canvas ) {
        super.onDraw(canvas);

        Log.d("CALL","-------------------onDraw");
        rect = new RectF( strokeWidth, strokeWidth, dimensionsW - strokeWidth , dimensionsH - strokeWidth);
        canvas.drawArc( rect, START_ANGLE_POINT, angle, false, paint );
    }

    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ) {
        Log.d("CALL","-------------------onMeasure");
        dimensionsW = getDefaultSize( getSuggestedMinimumWidth(), widthMeasureSpec );
        dimensionsH = getDefaultSize( getSuggestedMinimumHeight(), heightMeasureSpec );
        setMeasuredDimension( dimensionsW, dimensionsH );
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle( float angle ) {
        this.angle = angle;
    }
}
