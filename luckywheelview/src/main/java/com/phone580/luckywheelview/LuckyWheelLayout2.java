package com.phone580.luckywheelview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.List;

public class LuckyWheelLayout2 extends RelativeLayout {

    private Context mContext;
    private Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint wPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint yPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int screenWidth, screeHeight;
    /**
     * 跑马灯间隔时间
     */
    private int delayTime = 500;
    /**
     * 是否亮灯
     */
    private boolean isOnLight = false;
    private int radius;
    private int circleX, circleY;
    private RotateView2 rotateView2;
    private ImageView startBtn;
    /**
     * 中间对应的Button必须设置tag为 startbtn.
     */
    private static final String START_BTN_TAG = "startbtn";
    public static final int DEFAULT_TIME_PERIOD = 500;

    /**
     * 1. 初始化数据
     * 2. 动态添加转盘、按钮
     * @param priceList
     */
    public void initDataAndView(List<Price> priceList) {
        RotateView2 rotateView2 = new RotateView2(mContext);
        rotateView2.initDataAndView(priceList);
        addView(rotateView2);

        ImageView imageView = new ImageView(mContext);
        imageView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setClickable(true);
        imageView.setTag("startbtn");
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.drawable.ic_launcher_round);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                rotate(-1, 100);
            }
        });
        addView(imageView);

        postInvalidate();
    }

    public LuckyWheelLayout2(Context context) {
        this(context, null);
    }

    public LuckyWheelLayout2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LuckyWheelLayout2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        backgroundPaint.setColor(getResources().getColor(R.color.firstRoundColor));

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.luckywheelview);

        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screeHeight = getResources().getDisplayMetrics().heightPixels;

        screenWidth = (int) array.getDimension(R.styleable.luckywheelview_l_width, screenWidth);
        screeHeight = (int) array.getDimension(R.styleable.luckywheelview_l_height, screeHeight);

        wPaint.setColor(Color.WHITE);
        yPaint.setColor(Color.YELLOW);

        startLights();
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreeHeight() {
        return screeHeight;
    }

    /**
     * 循环跑马灯
     */
    private void startLights() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                isOnLight = !isOnLight;
                invalidate();
                postDelayed(this, delayTime);
            }
        }, delayTime);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int MinValue = Math.min(screenWidth, screeHeight);
//        MinValue -= Util.dp2px(mContext,10)*2;
        setMeasuredDimension(MinValue, MinValue);
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

        canvas.drawCircle(circleX, circleY, radius, backgroundPaint);

        drawLights(canvas, isOnLight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int centerX = (right - left) / 2;
        int centerY = (bottom - top) / 2;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            if (child instanceof RotateView2) {
                //修正奖品区域的位置
                rotateView2 = (RotateView2) child;
                int panWidth = child.getWidth();
                int panHeight = child.getHeight();
                child.layout(centerX - panWidth / 2, centerY - panHeight / 2, centerX + panWidth / 2, centerY + panHeight / 2);
            } else if (child instanceof ImageView) {
                if (TextUtils.equals((String) child.getTag(), START_BTN_TAG)) {
                    //设置中间按钮的位置
                    startBtn = (ImageView) child;
                    int btnWidth = child.getWidth();
                    int btnHeight = child.getHeight();
                    child.layout(centerX - btnWidth / 2, centerY - btnHeight / 2, centerX + btnWidth / 2, centerY + btnHeight / 2);
                }
            }
        }
    }

    /**
     * 绘制跑马灯
     *
     * @param canvas
     * @param isOnLight
     */
    private void drawLights(Canvas canvas, boolean isOnLight) {
        int pointDistance = radius;
        for (int i = 0; i <= 360; i += 20) {
            int x = (int) (pointDistance * Math.sin(Util.change(i))) + circleX;
            int y = (int) (pointDistance * Math.cos(Util.change(i))) + circleY;

            if (isOnLight)
                canvas.drawCircle(x, y, radius / 20, yPaint);
            else
                canvas.drawCircle(x, y, radius / 20, wPaint);
            isOnLight = !isOnLight;
        }
    }

    /**
     * 开始旋转
     *
     * @param pos       转到指定的转盘，-1 则随机
     * @param delayTime 外围灯光闪烁的间隔时间
     */
    public void rotate(int pos, int delayTime) {
        rotateView2.startRotate(pos);
        setDelayTime(delayTime);
        setStartBtnEnable(false);
    }

    protected void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    protected void setStartBtnEnable(boolean enable) {
        if (startBtn != null)
            startBtn.setEnabled(enable);
    }

    public interface AnimationEndListener {
        void endAnimation(int position);
    }

    private AnimationEndListener l;

    public void setAnimationEndListener(AnimationEndListener l) {
        this.l = l;
    }

    public AnimationEndListener getAnimationEndListener() {
        return l;
    }

}
