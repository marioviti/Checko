package customView;

import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by marioviti on 19/08/15.
 */
public class CircularIndicatorAnimation extends Animation {

    private CircularIndicator  circle;

    private float oldAngle;
    private float newAngle;

    public CircularIndicatorAnimation(CircularIndicator circle, float newAngle) {
        this.oldAngle = circle.getAngle();
        this.newAngle = newAngle;
        this.circle = circle;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        float angle = oldAngle + ((newAngle - oldAngle) * interpolatedTime);

        circle.setAngle(angle);
        circle.requestLayout();
    }
}
