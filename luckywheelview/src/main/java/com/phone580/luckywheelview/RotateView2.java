package com.phone580.luckywheelview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

public class RotateView2 extends View {

    private Context mContext;
    private int screenWidth, screeHeight;

    /**
     * 奖品区域的数目
     */
    private int panNum ;
    /**
     * 第一个奖品的初始角度
     */
    private int initAngle = 0;
    /**
     * 单个奖品区域的角度
     */
    private int panAngle = 0;
    private int panAngleHalf = 0;
    /**
     * 转盘的半径
     */
    private int radius = 0;
    /**
     * 奖品数据列表
     */
    private List<Price> priceList = new ArrayList<>();
    /**
     * 奖品图标的bitmap列表
     */
    private List<Bitmap> bitmaps = new ArrayList<>();

    /**
     * 深色区域的画笔
     */
    private Paint pPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * 浅色区域的画笔
     */
    private Paint wPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * 文字画笔
     */
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * 旋转一圈所需要的时间
     */
    private static final long ONE_WHEEL_TIME = 500;

    /**
     * 是否处于转动状态
     *
     */
    private boolean isOnRotating = false;

    /**
     * 1. 初始化数据
     * 2. 获取奖品图标bitmap
     *
     * @param priceList
     */
    public void initDataAndView(final List<Price> priceList) {
        if(priceList==null||priceList.size()==0)
            throw new IllegalArgumentException("请传入数据！");

        final int panNum=priceList.size();
        Log.d("echoMu", "initDataAndView panNum:" + panNum);

        if (360 % panNum != 0 || panNum == 0)
            throw new IllegalArgumentException("盘数不合法！");

        this.panNum = panNum;
        this.priceList = priceList;

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < panNum; i++) {
                            bitmaps.add(Util.getBitmapFromUrl(priceList.get(i).getPriceIcon()));
                        }

                        Log.d("echoMu", "panNum:" + panNum);
                        Log.d("echoMu", "bitmaps:" + bitmaps.size());

                        //所有图标的bitmap都已经拿到了
                        if (bitmaps.size() == panNum) {
                            if (isAttachedToWindow())
                                postInvalidate();
                        }
                    }
                }
        ).start();

    }


    public RotateView2(Context context) {
        this(context, null);
    }

    public RotateView2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotateView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d("echoMu", "RotateView");

        this.mContext = context;

        //获取当前屏幕宽高参数
//        screenWidth = getResources().getDisplayMetrics().widthPixels;
//        screeHeight = getResources().getDisplayMetrics().heightPixels;

//        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LuckyWheelView);

        pPaint.setColor(getResources().getColor(R.color.lightpink));
        wPaint.setColor(getResources().getColor(R.color.floralwhite));
        textPaint.setColor(getResources().getColor(R.color.firstRoundColor));
        textPaint.setTextSize(Util.dp2px(context, 16));
        setClickable(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        screenWidth = ((LuckyWheelLayout2) getParent()).getScreenWidth();
        screeHeight = ((LuckyWheelLayout2) getParent()).getScreeHeight();

        int minValue = Math.min(screenWidth, screeHeight);
        minValue -= Util.dp2px(mContext, 56);
        setMeasuredDimension(minValue, minValue);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isOnRotating) {
            initAngle = 360 / panNum;
            panAngle = 360 / panNum;
            panAngleHalf = panAngle / 2;
        }

        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();

        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;
        int minValue = Math.min(width, height);
        radius = minValue / 2;

        //构建转盘对应的矩形
        RectF rectF = new RectF(getPaddingLeft(), getPaddingTop(), width, height);
        // 绘制扇形的奖品区域
        // 因为drawArc函数是以x正方向顺时针为方向的，所以起始角度要看区域数是不是4的倍数，如果不是，需要跳过位于x正方向的部分区域所占的角度
        int angle = (panNum % 4 == 0) ? initAngle : initAngle - panAngleHalf;
        for (int i = 0; i < panNum; i++) {
            Log.d("echoMu", "angle:" + angle);
            if (i % 2 == 0) {
                canvas.drawArc(rectF, angle, panAngle, true, pPaint);
            } else {
                canvas.drawArc(rectF, angle, panAngle, true, wPaint);
            }
            angle += panAngle;
        }

        //绘制奖品文字
        Price price = new Price();
        for (int i = 0; i < panNum; i++) {
            price = priceList.get(i);
            drawText(price.getPriceName(), (panNum % 4 == 0) ? initAngle + panAngleHalf + (panAngleHalf * 3 / 4) : initAngle + panAngleHalf, radius, canvas, rectF);
            initAngle += panAngle;
        }

        //绘制奖品图标
        for (int i = 0; i < panNum; i++) {
            drawIcon(width / 2, height / 2, radius, (panNum % 4 == 0) ? initAngle + panAngleHalf : initAngle, i, canvas);
            initAngle += panAngle;
        }
    }

    /**
     * 绘制文字，用drawTextOnPath，需要偏移圆弧路径成为斜线路径
     *
     * @param str
     * @param startAngle
     * @param radius
     * @param mCanvas
     * @param rectF
     */
    private void drawText(String str, float startAngle, int radius, Canvas mCanvas, RectF rectF) {
        Path path = new Path();

        path.addArc(rectF, startAngle, panAngle);
        float textWidth = textPaint.measureText(str);
        float textFontSpacing = textPaint.getFontSpacing();

        //圆弧的垂直偏移
        float vOffset = radius / 6;
        //文字所在圆的半径
        float textRadius = radius - vOffset-textFontSpacing;
        //圆弧的水平偏移
        float hOffset = (panNum % 4 == 0) ? ((float) (2 * textRadius * Math.PI / panNum / 2))
                : ((float) (2 * textRadius * Math.PI / panNum / 2 - textWidth / 2));

        mCanvas.drawTextOnPath(str, path, hOffset, vOffset, textPaint);
    }

    /**
     * 绘制奖品图标
     *
     * @param xx
     * @param yy
     * @param mRadius
     * @param startAngle
     * @param i
     * @param mCanvas
     */
    private void drawIcon(int xx, int yy, int mRadius, float startAngle, int i, Canvas mCanvas) {
        if (bitmaps.size() != panNum)
            return;

        int imgWidth = mRadius / 4;

        float angle = (float) Math.toRadians(panAngle + startAngle);

        //确定图片在圆弧中 中心点的位置
        float x = (float) (xx + (mRadius / 2 + mRadius / 12) * Math.cos(angle));
        float y = (float) (yy + (mRadius / 2 + mRadius / 12) * Math.sin(angle));

        // 确定绘制图片的位置
        RectF rect = new RectF(x - imgWidth * 2 / 3, y - imgWidth * 2 / 3, x + imgWidth
                * 2 / 3, y + imgWidth * 2 / 3);

        Bitmap bitmap = bitmaps.get(i);

//        int ww = bitmap.getWidth();
//        int hh = bitmap.getHeight();
//        // 定义矩阵对象
//        Matrix matrix = new Matrix();
//        // 缩放原图
//        matrix.postScale(1f, 1f);
//        // 向左旋转，参数为正则向右旋转
//        matrix.postRotate(180+i*panAngle+30);
//        //bmp.getWidth(), 分别表示重绘后的位图宽高
//        Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, ww, hh,
//                matrix, true);
//        dstbmp.setHasAlpha(true);

        mCanvas.drawBitmap(bitmap, null, rect, null);
    }

    /**
     * 开始转动
     *
     * @param pos 如果 pos = -1 则随机，如果指定某个值，则转到某个指定区域
     */
    public void startRotate(int pos) {
        isOnRotating = true;
        Log.d("echoMu", "startRotate...");

        int lap = (int) (Math.random() * 12) + 4;

        int angle = 0;
        if (pos < 0) {
            angle = (int) (Math.random() * 360);
        } else {
            int initPos = queryPosition();
            if (pos > initPos) {
                angle = (pos - initPos) * panAngle;
                lap -= 1;
                angle = 360 - angle;
            } else if (pos < initPos) {
                angle = (initPos - pos) * panAngle;
            }
        }

        int increaseDegree = lap * 360 + angle;
        long time = (lap + angle / 360) * ONE_WHEEL_TIME;
        int DesRotate = increaseDegree + initAngle;

        //为了每次都能旋转到转盘的中间位置
        int offRotate = DesRotate % 360 % panAngle;
        DesRotate -= offRotate;
        DesRotate += panAngleHalf;

        ValueAnimator animator = ValueAnimator.ofInt(initAngle, DesRotate);
        Log.d("echoMu", "initAngle:" + initAngle + "\nDesRotate:" + DesRotate);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(time);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int updateValue = (int) animation.getAnimatedValue();
                initAngle = (updateValue % 360 + 360) % 360;
                ViewCompat.postInvalidateOnAnimation(RotateView2.this);
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                if (((LuckyWheelLayout2) getParent()).getAnimationEndListener() != null) {
                    ((LuckyWheelLayout2) getParent()).setStartBtnEnable(true);
                    ((LuckyWheelLayout2) getParent()).setDelayTime(LuckyWheelLayout2.DEFAULT_TIME_PERIOD);
                    ((LuckyWheelLayout2) getParent()).getAnimationEndListener().endAnimation(queryPosition());
                }
            }
        });
        animator.start();
    }

    private int queryPosition() {
        initAngle = (initAngle % 360 + 360) % 360;
        int pos = initAngle / panAngle;
        if (panNum == 4) pos++;
        return calculateAngle(pos);
    }

    private int calculateAngle(int pos) {
        if (pos >= 0 && pos <= panNum / 2) {
            pos = panNum / 2 - pos;
        } else {
            pos = (panNum - pos) + panNum / 2;
        }
        return pos;
    }

    @Override
    protected void onDetachedFromWindow() {
        clearAnimation();
        if (getParent() instanceof LuckyWheelLayout2) {
            ((LuckyWheelLayout2) getParent()).getHandler().removeCallbacksAndMessages(null);
        }
        super.onDetachedFromWindow();
    }

}
