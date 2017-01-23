package mn.factory.testanimation.testanimation.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mn.factory.testanimation.testanimation.R;
import mn.factory.testanimation.testanimation.adapters.AdapterMainList;
import mn.factory.testanimation.testanimation.models.FinanceModel;
import mn.factory.testanimation.testanimation.utils.UiUtils;
import mn.factory.testanimation.testanimation.views.GraphView;
import mn.factory.testanimation.testanimation.views.LockableBottomSheetBehavior;

public class MainActivity extends AppCompatActivity {

    private boolean openItem = false;
    private boolean animEnd = false;

    //toolbar
    private ImageView abMenuBtn;
    private TextView abTitleView;
    private Spinner abSpinner;

    private int fillColor;

    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.bottom_sheet)
    View bottomSheet;
    @BindView(R.id.appBar)
    AppBarLayout appBarLayout;
    @BindView(R.id.main_finance_recycle_view)
    RecyclerView recyclerView;
    @BindView(R.id.root_view)
    CoordinatorLayout rootView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_change_container)
    ViewGroup mainChangeContainer;
    @BindView(R.id.app_bar_data)
    ViewGroup appBarData;
    @BindView(R.id.graphView)
    GraphView graphView;
    @BindView(R.id.app_data_price)
    TextView appDataPrice;

    private AnimatedVectorDrawable mMenuDrawable;
    private AnimatedVectorDrawable mBackDrawable;
    private BottomSheetBehavior mBottomSheetBehavior;

    private FinanceModel current;

    private AdapterMainList adapterMainList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initToolbar();
        initMainTabs();
        initRecycleView();
        initBottomSheet();

        changeMainChangeContainerHeight((int) UiUtils.convertDpToPixel(110, this));
    }

    private void initBottomSheet() {
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setPeekHeight((int) (UiUtils.getScreenHeight(this) / 2 - toolbar.getHeight()));
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_SETTLING:
                        if (!animEnd) {
                            ((LockableBottomSheetBehavior) mBottomSheetBehavior).setLocked(true);
                            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        } else {
                            changeItemView();
                        }
                        break;
                    default:
                        ((LockableBottomSheetBehavior) mBottomSheetBehavior).setLocked(false);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }

    private void initRecycleView() {
        adapterMainList = new AdapterMainList(this, createTestData(), getItemClickListener());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterMainList);
    }

    private void hideFAB() {
        animateFab(fab.getHeight() + 100, 0, 0);
    }

    private void showFab() {
        animateFab(0, 200, 500);
    }

    private void animateFab(int y, int delay, int duration) {
        ViewCompat.animate(fab)
                .withLayer()
                .translationY(y)
                .setDuration(duration)
                .setStartDelay(delay)
                .setInterpolator(new BounceInterpolator())
                .start();
    }

    private void initMainTabs() {
        tabLayout.removeAllTabs();
        for (String title : getResources().getStringArray(R.array.main_tabs)) {
            tabLayout.addTab(createTab(title));
        }
        changeFontInViewGroup(tabLayout);
    }

    private void changeItemTabs() {
        createFadeOutAnimation(tabLayout);
        tabLayout.removeAllTabs();
        for (String title : getResources().getStringArray(R.array.item_tabs)) {
            tabLayout.addTab(createTab(title));
        }
        changeFontInViewGroup(tabLayout);
        createFadeInAnimation(tabLayout);
    }

    private void changeMainTabs() {
        createFadeOutAnimation(tabLayout);
        initMainTabs();
        changeFontInViewGroup(tabLayout);
        createFadeInAnimation(tabLayout);
    }

    private TabLayout.Tab createTab(String title) {
        return tabLayout.newTab().setText(title);
    }

    void changeFontInViewGroup(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (TextView.class.isAssignableFrom(child.getClass())) {
                ((TextView) child).setTypeface(Typeface.create(getResources().getString(R.string.roboto_medium), Typeface.NORMAL));
                ((TextView) child).setTextSize(UiUtils.convertDpToPixel(16, this));
            } else if (ViewGroup.class.isAssignableFrom(child.getClass())) {
                changeFontInViewGroup((ViewGroup) viewGroup.getChildAt(i));
            }
        }
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.ic_menu_vector);

        mMenuDrawable = (AnimatedVectorDrawable) getDrawable(R.drawable.ic_menu_animatable);
        mBackDrawable = (AnimatedVectorDrawable) getDrawable(R.drawable.ic_back_animatable);

        try {
            Field fNavBtn = toolbar.getClass().getDeclaredField("mNavButtonView");
            fNavBtn.setAccessible(true);
            abMenuBtn = (ImageButton) fNavBtn.get(toolbar);
            abMenuBtn.setOnClickListener(view -> {
                if(openItem) changeItemView();
            });
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        try {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            abTitleView = (TextView) f.get(toolbar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeTitle(String title) {
        createFadeOutAnimation(abTitleView);
        abTitleView.setText(title);
        createFadeInAnimation(abTitleView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem itemSpinner = menu.findItem(R.id.spinner);
        abSpinner = (Spinner) MenuItemCompat.getActionView(itemSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.bar_filter_names, R.layout.custom_spinner_view);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        abSpinner.setAdapter(adapter);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        /*menu.findItem(R.id.spinner).setVisible(!openItem);
        menu.findItem(R.id.add).setVisible(!openItem);
        menu.findItem(R.id.settings).setVisible(!openItem);*/

        AnimatorSet set = new AnimatorSet();
        ObjectAnimator fadeSpinner, fadeAddBtn, fadeSettingsBtn;
        if(!openItem){
            fadeSpinner = ObjectAnimator.ofFloat(menu.findItem(R.id.spinner).getActionView(), "alpha", 1.0f, 0.0f);
            fadeAddBtn = ObjectAnimator.ofFloat(menu.findItem(R.id.add).getActionView(), "alpha", 1.0f, 0.0f);
            fadeSettingsBtn = ObjectAnimator.ofFloat(menu.findItem(R.id.settings).getActionView(), "alpha", 1.0f, 0.0f);
        } else {
            set.setStartDelay(100);
            fadeSpinner = ObjectAnimator.ofFloat(menu.findItem(R.id.spinner).getActionView(), "alpha", 0.0f, 1.0f);
            fadeAddBtn = ObjectAnimator.ofFloat(menu.findItem(R.id.add).getActionView(), "alpha", 0.0f, 1.0f);
            fadeSettingsBtn = ObjectAnimator.ofFloat(menu.findItem(R.id.settings).getActionView(), "alpha", 0.0f, 1.0f);
        }
        set.setDuration(100);
        set.play(fadeSpinner).with(fadeAddBtn).with(fadeSettingsBtn);
        set.start();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (openItem) {
            changeItemView();
        } else {
            finish();
        }
    }

    private List<FinanceModel> createTestData() {
        List<FinanceModel> tempData = new ArrayList<>();
        tempData.add(new FinanceModel("AAPL", 3661.31f, 0.25f, false, true));
        tempData.add(new FinanceModel("ADBE", 3621.01f, 0.83f, true, false));
        tempData.add(new FinanceModel("AKAM", 661.23f, 1.23f, true, true));
        tempData.add(new FinanceModel("CSCO", 2341.76f, 11.4f, false, false));
        tempData.add(new FinanceModel("GOOG", 2346.56f, 0.34f, false, true));
        tempData.add(new FinanceModel("TSLA", 6544.05f, 12.3f, true, true));
        tempData.add(new FinanceModel("AKAM", 5476.26f, 0.4f, true, false));
        tempData.add(new FinanceModel("CSCO", 551.85f, 1.22f, false, true));
        tempData.add(new FinanceModel("GOPR", 8746.34f, 0.22f, true, false));
        tempData.add(new FinanceModel("ARVG", 4564.64f, 1.02f, false, true));
        tempData.add(new FinanceModel("PLTO", 4562.45f, 0.86f, false, false));
        return tempData;
    }

    private void changeItemView() {
        animEnd = false;
        if (!openItem) {
            hideFAB();
            changeItemTabs();
            slideContainerDown();
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            abMenuBtn.setImageDrawable(mBackDrawable);
            mBackDrawable.start();
            changeTitle(current.getName());
            showAppBarData();
            graphView.startDrawAnimation();
        } else {
            showFab();
            changeMainTabs();
            slideContainerUp();
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            abMenuBtn.setImageDrawable(mMenuDrawable);
            mMenuDrawable.start();
            changeTitle("Finance");
            hideAppBarData();
            graphView.stopDrawAnimation();
        }
        openItem = !openItem;
    }

    private void slideContainerUp() {
        slideContainer(UiUtils.getScreenHeight(this) / 2, toolbar.getHeight() * 2);
    }

    private void slideContainerDown() {
        slideContainer(toolbar.getHeight() * 2, UiUtils.getScreenHeight(this) / 2);
    }

    private void slideContainer(float heightFrom, float heightTo) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt((int) heightFrom, (int) heightTo);
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(animation ->
                changeMainChangeContainerHeight((int) animation.getAnimatedValue())
        );
        valueAnimator.start();
        revealColor();
        invalidateOptionsMenu();
    }

    private void showAppBarData() {
        createFadeInAnimation(appBarData);
    }

    private void hideAppBarData() {
        createFadeOutAnimation(appBarData);
    }

    private void changeMainChangeContainerHeight(int value) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mainChangeContainer.getLayoutParams();
        params.height = value;
        mainChangeContainer.setLayoutParams(params);
    }

    private void revealColor() {
        if (!openItem) {
            animateRevealColorFill(mainChangeContainer, fillColor);
        } else {
            animateRevealColorFillReverse(mainChangeContainer, fillColor);
        }
    }

    private void animateRevealColorFill(ViewGroup viewRoot, @ColorRes int color) {
        animateRevealColor(viewRoot, 0, (float) Math.hypot(viewRoot.getWidth(), viewRoot.getHeight()), 100)
                .addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        viewRoot.setBackgroundColor(ContextCompat.getColor(MainActivity.this, color));
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        animEnd = true;
                    }
                });
    }

    private void animateRevealColorFillReverse(ViewGroup viewRoot, @ColorRes int color) {
        animateRevealColor(viewRoot, (float) Math.hypot(viewRoot.getWidth(), viewRoot.getHeight()), 0, 0)
                .addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        super.onAnimationStart(animator);
                        viewRoot.setBackgroundColor(ContextCompat.getColor(MainActivity.this, color));
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        super.onAnimationEnd(animator);
                        viewRoot.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.main_app_bar_color));
                    }
                });
    }

    private Animator animateRevealColor(ViewGroup viewRoot, float startRadius, float finalRadius, int delay) {
        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, 0, 0, startRadius, finalRadius);
        anim.setDuration(300);
        anim.setStartDelay(delay);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.start();
        return anim;
    }

    private void createFadeInAnimation(View target) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(target, "alpha", 0, 1);
        fadeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                target.setVisibility(View.VISIBLE);
            }
        });
        fadeIn.start();
    }

    private void createFadeOutAnimation(View target) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(target, "alpha", 1, 0);
        fadeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                target.setVisibility(View.INVISIBLE);
            }
        });
        fadeIn.start();
    }

    private AdapterMainList.OnItemClickListener getItemClickListener() {
        return view -> {
            current = adapterMainList.getItem(recyclerView.getChildLayoutPosition(view));
            fillColor = current.isGrow() ? R.color.grow_green_color : R.color.grow_red_color;
            appDataPrice.setText(current.getPrice());
            changeItemView();
        };
    }

}
