package com.github.lzyzsd.dingdingheader;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.lang.reflect.Field;

import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

public class MainActivity extends AppCompatActivity {

    private Rect titleBarAvatarRect = new Rect();
    private Rect headerAvatarRect = new Rect();
    private int[] avatarLocation = new int[2];
    private int[] titleLocation = new int[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initPullToRefreshLayout();

        final ImageView titleBarAvatar = (ImageView) findViewById(R.id.iv_title_bar_avatar);
        final ImageView headerAvatar = (ImageView) findViewById(R.id.iv_header_avatar);
        final TextView titleBarTitle = (TextView) findViewById(R.id.tv_title_bar_title);
        final TextView headerTitle = (TextView) findViewById(R.id.tv_header_title);
        final View headerLayout = findViewById(R.id.l_header);
        titleBarAvatar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                titleBarAvatar.getLocationInWindow(avatarLocation);
                removeOnGlobalLayoutListener(titleBarAvatar, this);
            }
        });

        titleBarTitle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                titleBarTitle.getLocationInWindow(titleLocation);
                removeOnGlobalLayoutListener(titleBarTitle, this);
            }
        });

        final AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();

        MyScrollView mainScrollView = (MyScrollView) findViewById(R.id.sv_main_scroll_view);
        mainScrollView.setOnScrollChangedListener(new MyScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(ScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                int headerHeight = headerLayout.getMeasuredHeight();
                int titleBarHeight = getResources().getDimensionPixelSize(R.dimen.titlebar_height);
                float headerTransitionY = Math.max(-scrollY, titleBarHeight - headerHeight);
                headerLayout.setTranslationY(headerTransitionY);

                float ratio = -headerTransitionY / (headerHeight - titleBarHeight);
                float interpolation = interpolator.getInterpolation(ratio);
                doTranslation(headerTransitionY, interpolation, avatarLocation[0], headerAvatar, titleBarAvatar, headerAvatarRect, titleBarAvatarRect);
                doTranslation(headerTransitionY, interpolation, titleLocation[0], headerTitle, titleBarTitle, headerAvatarRect, titleBarAvatarRect);
            }
        });
    }

    private void getRect(Rect rect, View view) {
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
    }

    private void doTranslation(float headerTransitionY, float interpolation, int baseX,
                               View sourceView, View targetView, Rect sourceRect, Rect targetRect) {
        getRect(targetRect, targetView);
        getRect(sourceRect, sourceView);

        float translationY = computeTranslationY(interpolation, sourceRect, targetRect);
        float translationX = computeTranslationX(interpolation, baseX, sourceRect, targetRect);
        float scaleX = computeScaleX(interpolation, sourceRect, targetRect);
        float scaleY = computeScaleY(interpolation, sourceRect, targetRect);

        sourceView.setTranslationY(translationY - headerTransitionY);
        sourceView.setTranslationX(translationX);
        sourceView.setScaleX(scaleX);
        sourceView.setScaleY(scaleY);
    }

    private float computeTranslationY(float interpolation, Rect source, Rect target) {
        float distance = 0.5f * (source.top + source.bottom - target.top - target.bottom);
        return -interpolation * distance;
    }

    private float computeTranslationX(float interpolation, int baseX, Rect source, Rect target) {
        float distance = 0.5f * (source.left + source.right - (baseX + baseX + target.width()));
        return -interpolation * distance;
    }

    private float computeScaleX(float interpolation, Rect source, Rect target) {
        return 1.0f - (1.0f - (float)target.width() / source.width()) * interpolation;
    }

    private float computeScaleY(float interpolation, Rect source, Rect target) {
        return 1.0f - (1.0f - (float)target.height() / source.height()) * interpolation;
    }

    private void initPullToRefreshLayout() {
        PtrLocalDisplay.init(this);
        final PtrFrameLayout ptrFrameLayout = (PtrFrameLayout) findViewById(R.id.store_house_ptr_frame);
        PtrClassicDefaultHeader header = new PtrClassicDefaultHeader(this);
        header.setBackgroundColor(getResources().getColor(R.color.header_bg));
        int ptrHeaderOffset = getResources().getDimensionPixelSize(R.dimen.titlebar_height)
                + PtrLocalDisplay.dp2px(60) + (int) getStatusBarHeight();
        ptrFrameLayout.setOffsetToKeepHeaderWhileLoading(ptrHeaderOffset);
        final int closeHeaderTime = 1500;
        ptrFrameLayout.setDurationToCloseHeader(closeHeaderTime);
        ptrFrameLayout.setHeaderView(header);
        ptrFrameLayout.addPtrUIHandler(header);
        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return AnimatedHeaderPtrHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                final int loadingTime = 3000;
                ptrFrameLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ptrFrameLayout.refreshComplete();
                    }
                }, loadingTime);
            }
        });
    }

    private void removeOnGlobalLayoutListener(View view, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        } else {
            view.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        }
    }

    static float sSystemStatusBarHeight;
    public float getStatusBarHeight() {
        sSystemStatusBarHeight = (float) PtrLocalDisplay.dp2px(25);

        try {
            Class c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            sSystemStatusBarHeight = (float) getResources().getDimensionPixelSize(x);
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return sSystemStatusBarHeight;
    }
}
