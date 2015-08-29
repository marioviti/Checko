package customView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.marioviti.checko.R;

/**
 * Created by marioviti on 29/08/15.
 */
public class CustomImageButton extends View {

    private int dimensionsW, dimensionsH;
    private float ratioImage; // width/height
    private Paint paint;
    private Bitmap image;

    public CustomImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        int type = attrs.getAttributeIntValue("http://schemas.android.com/apk/res-auto", "type", 0);
        paint = new Paint();
        switch (type) {
            case 0: {
                paint.setColor(getResources().getColor(R.color.btn_color_type1));
                this.image = BitmapFactory.decodeResource(context.getResources(), R.drawable.type0);
                ratioImage = (float)1.23;
                break;
            }
            case 1: {
                paint.setColor(getResources().getColor(R.color.btn_color_type2));
                this.image = BitmapFactory.decodeResource(context.getResources(), R.drawable.type1);
                ratioImage=(float)1.08;
                break;
            }
            case 2: {
                paint.setColor(getResources().getColor(R.color.btn_color_type3));
                this.image = BitmapFactory.decodeResource(context.getResources(), R.drawable.type2);
                ratioImage=(float)0.96;
                break;
            }
            case 3: {
                paint.setColor(getResources().getColor(R.color.btn_color_type4));
                this.image = BitmapFactory.decodeResource(context.getResources(), R.drawable.type3);
                ratioImage=(float)1;
                break;
            }
            case 4: {
                paint.setColor(getResources().getColor(R.color.btn_color_type5));
                this.image = BitmapFactory.decodeResource(context.getResources(), R.drawable.type4);
                ratioImage=(float)1.85;

                break;
            }
        }
        setClickable(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        dimensionsW = getDefaultSize( getSuggestedMinimumWidth(), widthMeasureSpec );
        dimensionsH = getDefaultSize( getSuggestedMinimumHeight(), heightMeasureSpec );
        //setMeasuredDimension(dimensionsW, dimensionsH);
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }

    /**
     * Implement this to do your drawing.
     *
     * @param canvas the canvas on which the background will be drawn
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0,0,dimensionsW,dimensionsH,paint);
        canvas.drawBitmap(this.image,0+dimensionsW/(float)2,0+dimensionsH/(float)2,null);
    }
}
