package com.phone580.luckywheelview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

public class RotateView extends View {

    private Context mContext;
    /**
     * 记录视图的大小
     */
    private int mWidth;
    private Paint mPaint;
    /**
     * 文字画笔
     */
    private Paint mTextPaint;
    /**
     * 中心点横坐标
     */
    private int mCenter;
    /**
     * 绘制扇形的半径
     */
    private int mRadius;
    /**
     * 每一个扇形的角度
     */
    private float mAngle;
    /**
     * 是否处于旋转、但未抽奖状态
     */
    private boolean isRatate = false;
    private List<Bitmap> bitmaps = new ArrayList<>();
    /**
     * 奖品数据列表
     */
    private List<Price> priceList = new ArrayList<>();
    /**
     * 最低圈数 默认值3 也就是说每次旋转都会最少转3圈
     */
    private int mMinTimes = 3;
    /**
     * 最高圈数 默认值10 也就是说每次旋转都会最高转10圈
     */
    private int mMaxTimes = 10;
    /**
     * 分类数量
     */
    private int mTypeNum;
    /**
     * 动画时间时间
     */
    private int mTime = 3000;
    private Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private ObjectAnimator linearAnimator;
    private float startAngle = 0;
    private AnimatorSet animatorSet;

    /**
     * 动画回调监听
     */
    private RotateListener rotateListener;

    public RotateListener getRotateListener() {
        return rotateListener;
    }

    public void setRotateListener(RotateListener rotateListener) {
        this.rotateListener = rotateListener;
    }

    public RotateView(Context context) {
        super(context);
        init(context, null);
    }

    public RotateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RotateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setPriceList(final List<Price> priceList) {
        this.priceList = priceList;
        mTypeNum = priceList.size();
        //每一个扇形的角度
        mAngle = (float) (360.0 / mTypeNum);

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < mTypeNum; i++) {
                            bitmaps.add(Util.getBitmapFromUrl(priceList.get(i).getPriceIcon()));
                        }

                        Log.d("echoMu", "mTypeNum:" + mTypeNum);
                        Log.d("echoMu", "bitmaps:" + bitmaps.size());

                        //所有图标的bitmap都已经拿到了
                        if (bitmaps.size() == mTypeNum) {
                            bitmaps = rotateBitmaps(bitmaps);

                            if (isAttachedToWindow())
                                postInvalidate();
                        }
                    }
                }
        ).start();
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        setBackgroundColor(Color.TRANSPARENT);

        backgroundPaint.setColor(0xFFFF4500);

        if (attrs != null) {
            //获得这个控件对应的属性。
//            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.wheelSurfView);
        }

        //文字画笔
        mTextPaint = new Paint();
        //设置填充样式
        mTextPaint.setStyle(Paint.Style.STROKE);
        //设置抗锯齿
        mTextPaint.setAntiAlias(true);
        //设置边界模糊
        mTextPaint.setDither(true);
        //设置画笔颜色
        mTextPaint.setColor(getResources().getColor(R.color.firstRoundColor));
        //设置字体大小
        mTextPaint.setTextSize(32);

        //其他画笔
        mPaint = new Paint();
        //设置填充样式
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        //设置抗锯齿
        mPaint.setAntiAlias(true);
        //设置边界模糊
        mPaint.setDither(true);
    }

    /**
     * 开始转动
     */
    public void startRotate() {
        if (mTypeNum != bitmaps.size())
            return;

        isRatate = true;

        animatorSet = new AnimatorSet();

        //1.先匀加速转3圈
        ObjectAnimator  accelerateAnimator= ObjectAnimator.ofFloat(RotateView.this, "rotation", startAngle==0?startAngle:startAngle-mAngle / 2 , 360 * mMinTimes);
        accelerateAnimator.setDuration(mTime);
        accelerateAnimator.setInterpolator(new AccelerateInterpolator());

        //2.匀速转动
        final int newAngle = 360 * mMaxTimes;
        int num = (int) ((newAngle - 0) / mAngle);
        linearAnimator = ObjectAnimator.ofFloat(RotateView.this, "rotation", startAngle==0?startAngle:startAngle-mAngle / 2, 360 * mMinTimes);
        Log.d("echoMu", "开始转动 newAngle:" + newAngle);
        linearAnimator.setDuration(mTime / 2);
        linearAnimator.setRepeatCount(10000);
        linearAnimator.setInterpolator(new LinearInterpolator());
        linearAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Log.d("echoMu", "onAnimationUpdate rotateAngel:" + (float) valueAnimator.getAnimatedValue("rotation"));
            }
        });
        linearAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                Log.d("echoMu", "onAnimationCancel");

                isRatate = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                Log.d("echoMu", "onAnimationEnd");
                if (isRatate) {
                    Log.d("echoMu", "还没有请求到抽奖结果");
                    rotateListener.rotateTimeout();
                } else {
                    Log.d("echoMu", "请求到了");
//                    drawALottery();
                }
            }
        });

        animatorSet.play(accelerateAnimator).before(linearAnimator);
        animatorSet.start();
    }

    public void cancelRotate() {
        if (animatorSet != null && linearAnimator != null) {
            animatorSet.cancel();
            linearAnimator.cancel();
        }
    }

    /**
     * 开始转动
     * pos 位置 1 开始 这里的位置上是按照逆时针递增的 比如当前指的那个选项是第一个  那么他左边的那个是第二个 以此类推
     */
    public void drawALottery(final int pos) {
        if (mTypeNum != bitmaps.size())
            return;

        startAngle = 360 - (pos - 1) * mAngle ;
        Log.d("echoMu", "pos:" + pos + " startAngle:" + startAngle);
        //中奖区域要位于最中间 -mAngle / 2
        ObjectAnimator anim = ObjectAnimator.ofFloat(RotateView.this, "rotation", startAngle, 360 * mMinTimes + startAngle-mAngle / 2 );
        anim.setDuration(mTime);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //将动画的过程态回调给调用者
                if (rotateListener != null)
                    rotateListener.rotating(animation);
            }
        });
        final float[] f = {0};
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                //当旋转结束的时候回调给调用者当前所选择的内容
                if (rotateListener != null) {
                    //去空格和前后空格后输出
                    String des = priceList.get(pos - 1).getPriceName();
                    rotateListener.rotateEnd(pos, des);
                } else {
                    throw new RuntimeException("need to setRotateListener!");
                }
            }
        });
        // 正式开始启动执行动画
        anim.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //视图是个正方形的 所以有宽就足够了 默认值是500 也就是WRAP_CONTENT的时候
        int desiredWidth = 400;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int width;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //将测得的宽度保存起来
        mWidth = width;

        mCenter = mWidth / 2;
        //绘制扇形的半径 减掉50是为了防止边界溢出  具体效果你自己注释掉-50自己测试
        mRadius = mWidth / 2 - 60;

        //MUST CALL THIS
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d("echoMu", "执行onDraw");

        // 计算初始角度
        // 从最上面开始绘制扇形会好看一点
        float startAngle = - 90;

        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;

        if (mTypeNum != bitmaps.size())
            return;

        for (int i = 0; i < mTypeNum; i++) {
            Price price = priceList.get(i);

            //设置绘制时画笔的颜色
            if (i % 2 == 0) {
                mPaint.setColor(getResources().getColor(R.color.lightpink));
            } else {
                mPaint.setColor(getResources().getColor(R.color.floralwhite));
            }

            //画一个扇形
            RectF rect = new RectF(mCenter - mRadius, mCenter - mRadius, mCenter
                    + mRadius, mCenter + mRadius);
            canvas.drawArc(rect, startAngle, mAngle, true, mPaint);

            //绘制文字
            drawText(startAngle, price.getPriceName(), mRadius, mTextPaint, canvas);

            int imgWidth = mRadius / 4;

            int w = (int) (Math.abs(Math.cos(Math.toRadians(Math.abs(180 - mAngle * i)))) *
                    imgWidth + imgWidth * Math.abs(Math.sin(Math.toRadians(Math.abs(180 - mAngle * i)))));
            int h = (int) (Math.abs(Math.sin(Math.toRadians(Math.abs(180 - mAngle * i)))) *
                    imgWidth + imgWidth * Math.abs(Math.cos(Math.toRadians(Math.abs(180 - mAngle * i)))));

            float angle = (float) Math.toRadians(startAngle + mAngle / 2);

            //确定图片在圆弧中 中心点的位置
            float x = (float) (width / 2 + (mRadius / 2 + mRadius / 12) * Math.cos(angle));
            float y = (float) (height / 2 + (mRadius / 2 + mRadius / 12) * Math.sin(angle));
            // 确定绘制图片的位置
            RectF rect1 = new RectF(x - w / 2, y - h / 2, x + w / 2, y + h / 2);
            canvas.drawBitmap(bitmaps.get(i), null, rect1, null);

            //重置开始角度
            startAngle = startAngle + mAngle;
        }
    }

    /**
     * 绘制文字
     * @param startAngle
     * @param string
     * @param radius
     * @param textPaint
     * @param canvas
     */
    private void drawText(float startAngle, String string, int radius, Paint textPaint, Canvas canvas) {
        //创建绘制路径
        Path circlePath = new Path();
        //范围也是整个圆盘
        RectF rect = new RectF(mCenter - radius, mCenter - radius, mCenter
                + radius, mCenter + radius);
        //给定扇形的范围
        circlePath.addArc(rect, startAngle, mAngle);

        //圆弧的水平偏移
        float textWidth = textPaint.measureText(string);
        //圆弧的垂直偏移
        float hOffset = (float) (Math.sin(mAngle / 2 / 180 * Math.PI) * radius) - textWidth / 2;

        //绘制文字
        canvas.drawTextOnPath(string, circlePath, hOffset, radius / 4, textPaint);
    }

    /**
     * 旋转图片
     * @param source
     * @return
     */
    public static List<Bitmap> rotateBitmaps(List<Bitmap> source) {
        float mAngle = (float) (360 / source.size());
        List<Bitmap> result = new ArrayList<>();
        for (int i = 0; i < source.size(); i++) {
            Bitmap bitmap = source.get(i);
            int ww = bitmap.getWidth();
            int hh = bitmap.getHeight();
            // 定义矩阵对象
            Matrix matrix = new Matrix();
            // 缩放原图
            matrix.postScale(1f, 1f);
            // 向左旋转，参数为正则向右旋转
            matrix.postRotate(mAngle * i);
            //bmp.getWidth(), 分别表示重绘后的位图宽高
            Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, ww, hh,
                    matrix, true);
            dstbmp.setHasAlpha(true);
            result.add(dstbmp);
        }
        return result;
    }

}
