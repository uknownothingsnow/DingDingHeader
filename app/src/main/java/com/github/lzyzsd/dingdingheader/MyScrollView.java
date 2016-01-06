package com.github.lzyzsd.dingdingheader;

/**
 * Created by bruce on 1/5/16.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;


public class MyScrollView extends ScrollView {


    public interface OnScrollChangedListener {


        void onScrollChanged(ScrollView view, int l, int t, int oldl, int oldt);
    }


    private boolean mIsOverScrollEnabled = false;
    private OnScrollChangedListener mOnScrollChangedListener;


    public MyScrollView(Context context) {
        super(context);
    }


    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    public void setOverScrollEnabled(boolean enabled) {
        mIsOverScrollEnabled = enabled;
    }


    public boolean isOverScrollEnabled() {
        return mIsOverScrollEnabled;
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }


    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        mOnScrollChangedListener = listener;
    }


    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY,
                                   boolean isTouchEvent) {
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, mIsOverScrollEnabled ? maxOverScrollX : 0,
                mIsOverScrollEnabled ? maxOverScrollY : 0, isTouchEvent);
    }
}