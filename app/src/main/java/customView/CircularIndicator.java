package customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Custom View per PageFragment
 */

public class CircularIndicator extends View {

    private static final int START_ANGLE_POINT = 90;
    private final Paint fore_paint;
    private final Paint back_paint;
    private RectF rect;
    private float fore_angle;
    private float back_angle;
    private final int strokeWidth = 40;
    private int dimensionsW, dimensionsH;

    public CircularIndicator( Context context, AttributeSet attrs ) {
        super( context, attrs );

        fore_paint = new Paint();
        fore_paint.setAntiAlias(true);
        fore_paint.setStyle(Paint.Style.STROKE);
        fore_paint.setStrokeWidth(strokeWidth);
        fore_paint.setColor(Color.WHITE);

        back_paint = new Paint();
        back_paint.setAntiAlias(true);
        back_paint.setStyle(Paint.Style.STROKE);
        back_paint.setStrokeWidth(strokeWidth);
        back_paint.setColor(Color.BLACK);
        back_paint.setAlpha(50);

        fore_angle = 356;
        back_angle = 360;
    }

    @Override
    protected void onDraw( Canvas canvas ) {

        super.onDraw(canvas);
        rect = new RectF( strokeWidth, strokeWidth, dimensionsW - strokeWidth , dimensionsH - strokeWidth);
        canvas.drawArc( rect, 0, back_angle, false, back_paint);
        canvas.drawArc( rect, START_ANGLE_POINT, fore_angle, false, fore_paint);
    }

    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ) {

        dimensionsW = getDefaultSize( getSuggestedMinimumWidth(), widthMeasureSpec );
        dimensionsH = getDefaultSize( getSuggestedMinimumHeight(), heightMeasureSpec );
        setMeasuredDimension( dimensionsW, dimensionsH );
    }

    public float getAngle() {
        return fore_angle;
    }

    public void setAngle( float angle ) {
        this.fore_angle = angle;
    }
}
