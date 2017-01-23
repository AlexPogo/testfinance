package mn.factory.testanimation.testanimation.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import mn.factory.testanimation.testanimation.R;
import mn.factory.testanimation.testanimation.utils.UiUtils;

/**
 * Created by pogodaev on 17.01.17.
 */

public class BottomSheetView extends LinearLayout{

    private float startY;

    public BottomSheetView(Context context) {
        super(context);
        init();
    }

    public BottomSheetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomSheetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.bottom_sheet_item_info, this);
        this.post(() -> {
            startY = getY();
            setY(UiUtils.getScreenHeight(getContext()));
        });
        addView(new GraphView(getContext()));
    }

    public void showBottomSheet(){
        ObjectAnimator show = ObjectAnimator
                .ofFloat(this, "y", UiUtils.getScreenHeight(getContext()), startY);
                show.setInterpolator(new FastOutSlowInInterpolator());
        show.start();
        /*ViewCompat.animate(this)
                .withLayer()
                .translationYBy(getY())
                .translationY()
                .setDuration(500)
                .setInterpolator(new FastOutSlowInInterpolator())
                .start();*/
    }

    public void hideBottomSheet(){
        ObjectAnimator show = ObjectAnimator
                .ofFloat(this, "y", startY, UiUtils.getScreenHeight(getContext()));
        show.setInterpolator(new FastOutSlowInInterpolator());
        show.start();
        /*ViewCompat.animate(this)
                .withLayer()
                .translationYBy(UiUtils.getScreenHeight(getContext()) / 2)
                .translationY(UiUtils.getScreenHeight(getContext()))
                .setDuration(500)
                .setInterpolator(new FastOutSlowInInterpolator())
                .start();*/
    }
}
