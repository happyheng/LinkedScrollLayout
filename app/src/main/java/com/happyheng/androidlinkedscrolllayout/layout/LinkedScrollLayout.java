package com.happyheng.androidlinkedscrolllayout.layout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.happyheng.androidlinkedscrolllayout.layout.adapter.LinkedScrollAdapter;
import com.happyheng.androidlinkedscrolllayout.R;
import com.happyheng.androidlinkedscrolllayout.utils.DensityUtils;

/**
 * 此Layout具有顶部TitleView，并且下方含有ViewPager。能够解决它们之间的滑动冲突。
 * Created by liuheng on 16/5/9.
 */
public class LinkedScrollLayout extends LinearLayout {

    private View mTitleView;
    private ViewPager mViewPager;

    //此Layout所需的Adapter
    private LinkedScrollAdapter mAdapter;

    //分别为 判断是否为touch的最小距离，和是否为每一次touch的最小距离
    private int mFirstTouchSlop, mTouchSlop;

    //监听滑动速度的对象
    private VelocityTracker mVelocityTracker;
    //上下最好滑动的距离
    private int mMinVelocityScrollHeight;
    //滑动的ValueAnimator
    private ValueAnimator mValueAnimator;


    public LinkedScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(VERTICAL);

        LayoutInflater.from(context).inflate(R.layout.view_linked_scroll, this, true);
        mTitleView = findViewById(R.id.title_ll);
        mViewPager = (ViewPager) findViewById(R.id.down_vp);


        mFirstTouchSlop = DensityUtils.dp2px(context, 5.0f);
        mTouchSlop = DensityUtils.dp2px(context, 1.5f);

        //初始化
        mVelocityTracker = VelocityTracker.obtain();
        //最小为24dp，即差不多为屏幕的二十分之一
        mMinVelocityScrollHeight = DensityUtils.dp2px(context, 24.0f);

    }

    public void setAdapter(LinkedScrollAdapter adapter) {
        this.mAdapter = adapter;

        mViewPager.setAdapter(adapter);
    }


    private int mTitleViewHeight;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        mTitleViewHeight = mTitleView.getMeasuredHeight();
    }


    private float mMoveX, mMoveY;
    //是否已经判断的标志位
    private boolean mIsJudge = false;
    //是否拦截的标志位
    private boolean mIsIntercept = false;


    /**
     * 是否拦截的方法
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (mAdapter != null) {

            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    //如果此时正在执行动画，停止动画，防止冲突
                    if (mValueAnimator != null && mValueAnimator.isRunning()) {
                        mValueAnimator.cancel();
                    }


                    mMoveX = ev.getX();
                    mMoveY = ev.getY();
                    mIsIntercept = false;
                    mIsJudge = false;

                case MotionEvent.ACTION_MOVE:

                    float moveX = ev.getX();
                    float moveY = ev.getY();

                    float differY = Math.abs(moveY - mMoveY);
                    float differX = Math.abs(moveX - mMoveX);

                    //如果尚未做出判断且移动的距离已超过了判断距离，那么开始判断
                    if (!mIsJudge && differY > mFirstTouchSlop) {

                        mIsIntercept = false;

                        //只有竖直移动才可能拦截，横向不拦截
                        if (isVerticalScroll(differX, differY)) {

                            //1、如果是上下滑动且titleView没有滑动到最上面开始拦截
                            float titleTop = mTitleView.getTop();
                            if (titleTop != -mTitleViewHeight) {
                                mIsIntercept = true;
                            }
                            //2、如果滑动到了最上面且现在的Fragment已经滑动到了最上面，并且还是向下滑动，那么也拦截
                            else {
                                if ((moveY - mMoveY) > 0 && mAdapter.isFragmentTop(mViewPager.getCurrentItem())) {
                                    mIsIntercept = true;
                                }
                            }
                        }
                        mIsJudge = true;
                    }
            }
            return mIsIntercept;
        }
        return false;
    }


    /**
     * 根据两次 x,y的位置，判断是否是纵向滑动
     */
    private boolean isVerticalScroll(float differX, float differY) {

        //只要两次Y的绝对值差大于两次X的绝对值差即可
        if (differY > differX) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //添加至滑动监听
        mVelocityTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                //如果触摸的是TitleView的话，那么设置自己为targetView
                if (event.getY() < mTitleView.getBottom()) {
                    return true;
                }
                return false;
            case MotionEvent.ACTION_MOVE:

                float moveY = event.getY();
                float moveByY = moveY - mMoveY;
                mMoveY = moveY;

                float oldTitleTop = mTitleView.getTop();
                int titleTop = (int) (oldTitleTop + moveByY);

                setTitleViewTop(titleTop);
                break;
            case MotionEvent.ACTION_UP:

                //判断当ev事件是MotionEvent.ACTION_UP时：计算速率
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(300);
                float yVelocity = velocityTracker.getYVelocity();
                //如果小于最小的滑动距离
                if (Math.abs(yVelocity) < mMinVelocityScrollHeight) {
                    break;
                }
                beginAnimation(velocityTracker.getYVelocity());
                break;

        }
        return true;
    }

    /**
     * 开始动画的方法，使TitleView有滑动后缓慢停止的效果
     *
     * @param yVelocity
     */
    private void beginAnimation(float yVelocity) {

        initAnimator(yVelocity);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                Float value = (Float) animation.getAnimatedValue();
                setTitleViewTop(value.intValue());
            }
        });

        mValueAnimator.start();
    }

    /**
     * 初始化Animator的方法，注意，此方法会根据最后得到的值来判断，如果超出了临界值，使其最后停在临界值上
     *
     * @param yVelocity Y轴移动的向量
     */
    private void initAnimator(float yVelocity) {
        float oldTitleTop = mTitleView.getTop();

        //进行判断，如果滑动超过限定值，使其等于限定值
        float resultTitleTop = oldTitleTop + yVelocity;
        if (resultTitleTop > 0) {
            resultTitleTop = 0;
        } else if (resultTitleTop < -mTitleViewHeight) {
            resultTitleTop = -mTitleViewHeight;
        }

        mValueAnimator = ValueAnimator.ofFloat(oldTitleTop, resultTitleTop);
        mValueAnimator.setDuration(300);
        //设置缓慢减速的插值器
        mValueAnimator.setInterpolator(new DecelerateInterpolator());

    }


    /**
     * 设置TitileView顶部Top的方法,此方法会判断是否合法
     *
     * @param titleTop TitleView的topMargin
     */
    private void setTitleViewTop(int titleTop) {

        float oldTitleTop = mTitleView.getTop();

        //为了减小性能消耗，当移动的距离超过了最小移动距离后，才进行移动
        if (Math.abs(titleTop - oldTitleTop) < mTouchSlop) {
            return;
        }

        if (titleTop > 0) {
            if (oldTitleTop == 0) {
                return;
            }
            titleTop = 0;
        }

        if (titleTop < -mTitleViewHeight) {
            if (oldTitleTop == -mTitleViewHeight) {
                return;
            }
            titleTop = -mTitleViewHeight;
        }
        LinearLayout.LayoutParams params = (LayoutParams) mTitleView.getLayoutParams();
        params.setMargins(0, titleTop, 0, 0);
        mTitleView.requestLayout();

    }


}
