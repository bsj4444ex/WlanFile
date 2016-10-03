package com.bsj4444.listviewtest2;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ListView;

/**
 * Created by Administrator on 2016/9/23.
 * 滑到尽头有弹性效果的ListView
 */
public class ListView2 extends ListView {

    private static int mMaxOverDistance = 50;//该值决定留白处大小

    public ListView2(Context context) {
        super(context);
        initView(context);
    }

    public ListView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ListView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY,
                                   int scrollX, int scrollY,
                                   int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY,
                                   boolean isTouchEvent) {
        return super.overScrollBy(deltaX, deltaY,
                scrollX, scrollY,
                scrollRangeX, scrollRangeY,
                maxOverScrollX, mMaxOverDistance,
                isTouchEvent);
    }
    private void initView(Context mContext) {
        //Displaymetrics 是取得手机屏幕大小的关键类
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        float density = metrics.density;
        mMaxOverDistance = (int) (density * mMaxOverDistance);
    }
}
