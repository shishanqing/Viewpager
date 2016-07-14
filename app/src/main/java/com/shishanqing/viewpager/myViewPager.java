package com.shishanqing.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by shishanqing on 16-7-14.
 *分析：
 * ViewPager在添加一个View或者销毁一个View时，是我们自己的PageAdapter中控制的，
 * 于是我们可以在ViewPager里面维系一个HashMap<Position，View>，
 * 然后滑动的时候，通过get（position）取出，比如上述效果，始终是右边的View变化，要么从小到大，要么从大到小
 * 那么滑倒下一页：左边的View:map.get(position) ，右边的View : map.get(position+1) .
 * 那么滑倒上一页：左边的View : map.get(position) ， 右边的View : map.get(position+1) ，
 * 一样的，因为滑到上一页，position为当前页-1
 */
public class myViewPager extends ViewPager {

    private float mTrans;
    private float mScale;

    //最大的缩小比例
    private static final float SCALE_MAX = 0.5f;

    private static final String TAG = "myViewPager";

    //滑动时左边的元素
    private View leftView;

    //滑动时右边的元素
    private View rightView;

    //保存position和对应的View
    private HashMap<Integer, View> mChildrenView = new LinkedHashMap<Integer, View>();

    public myViewPager(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    protected void onPageScrolled(int position, float offset, int offsetPixels) {

        //滑动特别小的距离时，我们认为没有动，可有可无的判断
        float effectOffset = isSmall(offset) ? 0 : offset;

        //获取左边的view
        leftView = findViewFromObject(position);

        //获取右边的View
        rightView = findViewFromObject(position + 1);

        //添加切换动画效果
        animateStack(leftView, rightView, effectOffset, offsetPixels);


        super.onPageScrolled(position, offset, offsetPixels);
    }

    public void setObjectForPosition(View view, int position)
    {
        mChildrenView.put(position, view);
    }

    private boolean isSmall(float positionOffset){
        return Math.abs(positionOffset) < 0.0001;
    }

    //获取当前位置的View
    private View findViewFromObject(int position){
        return mChildrenView.get(position);
    }

    //动画效果
    private void animateStack(View leftView, View rightView, float effectOffset, int offsetPixels){
        if(rightView != null){
            /**
             * 缩小比例 如果手指从右到左的滑动（切换到后一个）：0.0~1.0，即从一半到最大
             * 如果手指从左到右的滑动（切换到前一个）：1.0~0，即从最大到一半
             */
            mScale = (1 - SCALE_MAX) * effectOffset + SCALE_MAX;

            /**
             * x偏移量： 如果手指从右到左的滑动（切换到后一个）：0-720
             * 如果手指从左到右的滑动（切换到前一个）：720-0
             */
            mTrans = -getWidth() - getPageMargin() + offsetPixels;
            ViewHelper.setScaleX(rightView, mScale);
            ViewHelper.setScaleY(rightView, mScale);
            ViewHelper.setTranslationX(rightView, mTrans);
        }

        if(leftView != null){
            leftView.bringToFront();
        }
    }


}
