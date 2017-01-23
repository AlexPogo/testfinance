package mn.factory.testanimation.testanimation.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;

import mn.factory.testanimation.testanimation.R;
import mn.factory.testanimation.testanimation.utils.UiUtils;

import static android.R.attr.bitmap;

/**
 * Created by pogodaev on 19.01.17.
 */

public class GraphView extends View {

    private Paint mPaintPath;
    private Paint mPaintText;
    private Paint mPaintIcon;
    private Path mPath;

    private PointF labelCoord;
    private Canvas canvasLabel;
    private Rect bounds;

    private Bitmap labelImage;

    private ObjectAnimator animator;

    private float length;
    private float paddingLeft;
    private float paddingRight;
    private boolean lineAnimEnd = false;

    private String text = "91.03";

    public GraphView(Context context) {
        super(context);
        init();
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        setBackgroundResource(R.drawable.graph_bg);

        mPaintPath = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintPath.setStyle(Paint.Style.STROKE);
        mPaintPath.setStrokeWidth(2);

        paddingLeft = UiUtils.convertDpToPixel(30, getContext());
        paddingRight = UiUtils.convertDpToPixel(60, getContext());
        mPath = makeFollowPath();

        animator = ObjectAnimator.ofFloat(this, "phase", 1.0f, 0.0f);
        animator.setDuration(1500);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                lineAnimEnd = true;
            }
        });

        PathMeasure measure = new PathMeasure(mPath, false);
        length = measure.getLength();

        //set text middle label image
        Resources resources = getResources();
        float scale = resources.getDisplayMetrics().density;
        labelImage = BitmapFactory.decodeResource(resources,
                R.drawable.ic_label_icon);
        android.graphics.Bitmap.Config bitmapConfig =
                labelImage.getConfig();
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        labelImage = labelImage.copy(bitmapConfig, true);
        canvasLabel = new Canvas(labelImage);
        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setStyle(Paint.Style.STROKE);
        mPaintText.setColor(Color.BLACK);
        mPaintText.setTextSize((int) (8 * scale));
        bounds = new Rect();
        mPaintText.getTextBounds(text, 0, text.length(), bounds);
    }

    public void startDrawAnimation() {
        animator.start();
    }

    //call by animation, don't remove!
    public void setPhase(float phase) {
        mPaintPath.setPathEffect(createPathEffect(length, phase, 0.0f));
        invalidate();
    }

    private static PathEffect createPathEffect(float pathLength, float phase, float offset) {
        return new DashPathEffect(new float[]{pathLength, pathLength},
                Math.max(phase * pathLength, offset));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(paddingLeft, this.getHeight() / 2);
        invalidate();

        if(lineAnimEnd) {
            int x = (labelImage.getWidth() - bounds.width())/2;
            int y = (labelImage.getHeight() + bounds.height())/2;
            canvas.drawBitmap(labelImage, labelCoord.x - 15, labelCoord.y - labelImage.getHeight() / 2, mPaintText);
            canvasLabel.drawText(text, x, y, mPaintText);
        }
        mPaintPath.setColor(Color.WHITE);
        canvas.drawPath(mPath, mPaintPath);
    }

    public void stopDrawAnimation(){
        animator.end();
        lineAnimEnd = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                mPath = makeFollowPath();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private Path makeFollowPath() {
        //end graph bg
        final int maxWidth = (int) (UiUtils.getScreenWidth(getContext()) - paddingRight - paddingLeft);
        Path p = new Path();
        p.moveTo(0, 0);
        for (int i = 1; i <= 20; i++) {
            float currentX = i * UiUtils.convertDpToPixel(15, getContext());
            float currentY = (float) Math.random() * 85;
            if(currentX >= maxWidth) {
                p.lineTo(currentX, getHeight() / 2);
                labelCoord = new PointF(currentX, getHeight() / 2);
                return p;
            }
            p.lineTo(currentX, currentY);
        }
        return p;
    }
}
