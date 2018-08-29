package com.phone580.luckywheelview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 转盘背景
 */
public class LuckyWheelLayout extends RelativeLayout {
    private RotateView mRotateView;
    private Context mContext;
    /**
     * 开始按钮
     */
    private ImageView mStart;
    /**
     * 记录当前是否是第一次回调onMeasure
     */
    private boolean isFirst = true;
    private int radius;
    private int circleX, circleY;
    private Paint wPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint yPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * 跑马灯间隔时间
     */
    private int delayTime = 500;
    /**
     * 是否亮灯
     */
    private boolean isOnLight = false;

    //动画回调监听
    private RotateListener rotateListener;

    public void setRotateListener(RotateListener rotateListener) {
        mRotateView.setRotateListener(rotateListener);
        this.rotateListener = rotateListener;
    }

    public void setPriceList(List<Price> priceList) {
        mRotateView.setPriceList(priceList);
    }

    public LuckyWheelLayout(Context context) {
        super(context);
        init(context, null);
    }

    public LuckyWheelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LuckyWheelLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
//        if (attrs != null) {
//            //获得这个控件对应的属性。
//            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.wheelSurfView);
//
//            typedArray.recycle();
//        }

        wPaint.setColor(Color.WHITE);
        yPaint.setColor(Color.YELLOW);
        backgroundPaint.setColor(0xFFFF4500);

        //添加圆盘视图
        mRotateView = new RotateView(mContext, attrs);
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mRotateView.setLayoutParams(layoutParams);
        addView(mRotateView);

        //添加开始按钮
        mStart = new ImageView(mContext);
        mStart.setImageResource(R.drawable.btn_rotate);

        //给图片设置LayoutParams
        RelativeLayout.LayoutParams llStart =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        llStart.addRule(RelativeLayout.CENTER_IN_PARENT);
        mStart.setLayoutParams(llStart);
        addView(mStart);

        mStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用此方法是将主动权交个调用者 由调用者调用开始旋转的方法
                if (rotateListener != null)
                    rotateListener.beforeDrawALottery((ImageView) v);
            }
        });

        startLights();
    }

    /**
     * 开始转动
     */
    public void startRotate() {
        if (mRotateView != null) {
            mRotateView.startRotate();
        }
    }

    /**
     * 开始抽奖
     *
     * @param pisition 旋转最终的位置 注意 从1 开始 而且是逆时针递增
     */
    public void startDrawALottery(int pisition) {
        if (mRotateView != null) {
            mRotateView.cancelRotate();
            mRotateView.drawALottery(pisition);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //视图是个正方形的 所以有宽就足够了 默认值是500 也就是WRAP_CONTENT的时候
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        // Children are just made to fill our space.
        final int childWidthSize = getMeasuredWidth();
        //高度和宽度一样
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);

        //onMeasure调用获取到当前视图大小之后，
        // 手动按照一定的比例计算出中间开始按钮的大小，
        // 再设置给那个按钮，免得造成用户传的图片不合适之后显示贼难看
        // 只设置一次
        if (isFirst) {
            isFirst = !isFirst;
            //获取中间按钮的大小
            ViewTreeObserver vto = mStart.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void onGlobalLayout() {
                    mStart.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    float w = mStart.getMeasuredWidth();
                    float h = mStart.getMeasuredHeight();
                    //计算新的大小 默认为整个大小最大值的0.17
                    int newW = (int) (((float) childWidthSize) * 0.17);
                    int newH = (int) (((float) childWidthSize) * 0.17 * h / w);
                    ViewGroup.LayoutParams layoutParams = mStart.getLayoutParams();
                    layoutParams.width = newW;
                    layoutParams.height = newH;
                    mStart.setLayoutParams(layoutParams);
                }
            });
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;

        int MinValue = Math.min(width, height);

        radius = MinValue / 2;
        circleX = getWidth() / 2;
        circleY = getHeight() / 2;

        //绘制背景色
        canvas.drawCircle(circleX, circleY, radius - Util.dp2px(mContext, 8), backgroundPaint);
        drawLights(canvas,isOnLight);
    }

    /**
     * 绘制跑马灯
     *
     * @param canvas
     */
    private void drawLights(Canvas canvas, boolean isOnLight) {
        //偏移18
        int pointDistance = radius - Util.dp2px(mContext, 10);
        for (int i = 0; i <= 360; i += 20) {
            int x = (int) (pointDistance * Math.sin(Util.change(i))) + circleX;
            int y = (int) (pointDistance * Math.cos(Util.change(i))) + circleY;

            if (isOnLight)
                canvas.drawCircle(x, y, radius / Util.dp2px(mContext, 10), yPaint);
            else
                canvas.drawCircle(x, y, radius / Util.dp2px(mContext, 10), wPaint);
            isOnLight = !isOnLight;
        }
    }

    /**
     * 循环跑马灯
     */
    private void startLights() {
//        postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                isOnLight = !isOnLight;
//                invalidate();
//                postDelayed(this, delayTime);
//            }
//        }, delayTime);
        Runnable r=new Runnable() {
            @Override
            public void run() {
                isOnLight = !isOnLight;
                invalidate();
            }
        };
        Executors.newScheduledThreadPool (2).scheduleAtFixedRate(r, 0, delayTime, TimeUnit.MILLISECONDS);
    }

}
