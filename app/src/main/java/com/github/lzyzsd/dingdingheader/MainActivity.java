package com.github.lzyzsd.dingdingheader;

import android.graphics.Rect;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Rect titleBarAvatarRect = new Rect();
    private Rect headerAvatarRect = new Rect();
    private Rect titleBarTitleRect = new Rect();
    private Rect headerTitleRect = new Rect();
    float titleBarTitleContainerX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView titleBarAvatar = (ImageView) findViewById(R.id.iv_title_bar_avatar);
        final ImageView headerAvatar = (ImageView) findViewById(R.id.iv_header_avatar);
        final TextView titleBarTitle = (TextView) findViewById(R.id.tv_title_bar_title);
        final TextView headerTitle = (TextView) findViewById(R.id.tv_header_title);
        final View titleBarTitleContainer = findViewById(R.id.title_bar_title_container);
        final View headerLayout = findViewById(R.id.l_header);

        titleBarTitleContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                titleBarTitleContainerX = titleBarTitleContainer.getX();
            }
        });

        final AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();

        MyScrollView mainScrollView = (MyScrollView) findViewById(R.id.sv_main_scroll_view);
        mainScrollView.setOnScrollChangedListener(new MyScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(ScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                int headerHeight = headerLayout.getMeasuredHeight();
                int titleBarHeight = getResources().getDimensionPixelSize(R.dimen.titlebar_height);
                float headerTransitionY = Math.max(-scrollY, titleBarHeight-headerHeight);
                headerLayout.setTranslationY(headerTransitionY);

                float ratio = -headerTransitionY / (headerHeight - titleBarHeight);
                float interpolation = interpolator.getInterpolation(ratio);
                translateAvatar(headerTransitionY, interpolation, titleBarTitleContainerX, headerAvatar, titleBarAvatar, headerAvatarRect, titleBarAvatarRect);
                translateTitle(headerTransitionY, interpolation, titleBarTitleContainerX, headerTitle, titleBarTitle, headerAvatarRect, titleBarAvatarRect);
            }
        });
    }

    private void getRect(Rect rect, View view) {
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
    }

    private void translateAvatar(float headerTransitionY, float interpolation, float baseX,
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

    private void translateTitle(float headerTransitionY, float interpolation, float baseX,
                                View sourceView, View targetView, Rect sourceRect, Rect targetRect) {
        getRect(targetRect, targetView);
        getRect(sourceRect, sourceView);

        float translationY = computeTranslationY(interpolation, sourceRect, targetRect);
        float translationX = computeTranslationX(interpolation, baseX, sourceRect, targetRect);
        sourceView.setTranslationY(translationY - headerTransitionY);
        sourceView.setTranslationX(translationX);
    }

    private float computeTranslationY(float interpolation, Rect source, Rect target) {
        float distance = 0.5f * (source.top + source.bottom - target.top - target.bottom);
        return -interpolation * distance;
    }

    private float computeTranslationX(float interpolation, float baseX, Rect source, Rect target) {
        float distance = 0.5f * (source.left + source.right - baseX - baseX - target.left - target.right);
        return -interpolation * distance;
    }

    private float computeScaleX(float interpolation, Rect source, Rect target) {
        return 1.0f - (1.0f - target.width() / source.width()) * interpolation;
    }

    private float computeScaleY(float interpolation, Rect source, Rect target) {
        return 1.0f - (1.0f - target.height() / source.height()) * interpolation;
    }
}
