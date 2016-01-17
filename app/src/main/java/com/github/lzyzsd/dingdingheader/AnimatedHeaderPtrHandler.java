package com.github.lzyzsd.dingdingheader;

import android.view.View;
import android.widget.FrameLayout;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by bruce on 1/12/16.
 *
 *  注意这个类是我的tab中，带滚动动画的头部专用的下拉刷新Handler
 *  布局中主Layout的第一个子节点必须是scrollview
 */
public abstract class AnimatedHeaderPtrHandler implements PtrHandler {

    public static boolean canChildScrollUp(View view) {
        view = ((FrameLayout) view).getChildAt(0);
        if (android.os.Build.VERSION.SDK_INT < 14) {
            return view.getScrollY() > 0;
        } else {
            return view.canScrollVertically(-1);
        }
    }

    public static boolean checkContentCanBePulledDown(PtrFrameLayout frame, View content, View header) {
        return !canChildScrollUp(content);
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return !canChildScrollUp(content);
    }
}
