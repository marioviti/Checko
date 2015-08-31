package customView;

import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by marioviti on 31/08/15.
 */
public class HistogramAnimation extends Animation {

    float[] newValues, oldValues, tempValues;
    HistogramView histogramView;

    public HistogramAnimation(HistogramView histogramView, float[] newValues) {
        this.oldValues = histogramView.getValues();
        this.newValues = newValues;
        this.histogramView = histogramView;
        tempValues = new float[newValues.length];
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        for(int i = 0; i<tempValues.length;i++)
            tempValues[i] = oldValues[i] + ((newValues[i] - oldValues[i]) * interpolatedTime);
        histogramView.setValues(tempValues);
        histogramView.requestLayout();

    }
}
