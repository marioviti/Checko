package customView;

import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by marioviti on 28/08/15.
 */
public class CalendarRowBarSelectAnimation  extends Animation {

    private CalendarRowBarView  rowBar;

    private float oldAlpha;
    private float newAlpha;

    public CalendarRowBarSelectAnimation(CalendarRowBarView rowBar, int newAlpha) {

        this.oldAlpha = rowBar.getAlphaVal();
        this.newAlpha = newAlpha;
        this.rowBar = rowBar;
    }
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        int alpha = (int) (oldAlpha + ((newAlpha - oldAlpha) * interpolatedTime));
        rowBar.setAlphaVal(alpha);
        rowBar.requestLayout();
    }
}
