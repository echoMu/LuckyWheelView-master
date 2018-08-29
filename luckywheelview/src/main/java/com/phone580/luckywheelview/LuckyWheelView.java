package com.phone580.luckywheelview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class LuckyWheelView extends View {

    private Paint paint = new Paint();

    private float fristRoundWidth, secondRoundWidth;
    private int firstRoundColor, secondRoundColor;
    private static final int N = 8;

    public LuckyWheelView(Context context) {
        this(context, null);
    }

    public LuckyWheelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LuckyWheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.LuckyWheelView2);
        //获取自定义属性和默认值，第一个参数是从用户属性中得到的设置，如果用户没有设置，那么就用默认的属性，即：第二个参数
        //最外层圆环的宽度
        fristRoundWidth = mTypedArray.getDimension(R.styleable.LuckyWheelView2_firstRoundWidth, Util.dp2px(context, 12));
        //最外层圆环的颜色
        firstRoundColor = mTypedArray.getColor(R.styleable.LuckyWheelView2_firstRoundColor, getResources().getColor(R.color.firstRoundColor));
        //最里层圆环的宽度
        secondRoundWidth = mTypedArray.getDimension(R.styleable.LuckyWheelView2_secondRoundWidth, Util.dp2px(context, 8));
        //最里层圆环的颜色
        secondRoundColor = mTypedArray.getColor(R.styleable.LuckyWheelView2_secondRoundColor, getResources().getColor(R.color.secondRoundColor));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //获取圆心的x坐标
        Log.d("echoMu", "1:getWidth() " + getWidth());
        int centre = getWidth() / 2;
        Log.d("echoMu", "1:centre " + centre);

        //设置空心 也就是画圆环
        paint.setStyle(Paint.Style.STROKE);
        //消除锯齿
        paint.setAntiAlias(true);

        //画最外层的圆环
        //圆的半径
        int radius = (int) (centre - fristRoundWidth);
        Log.d("echoMu", "fristRoundWidth " + fristRoundWidth);
        Log.d("echoMu", "2:radius " + radius);
        //设置最外层圆环的颜色
        paint.setColor(firstRoundColor);
        //设置最外层圆环的宽度
        paint.setStrokeWidth(fristRoundWidth);
        //画出最外层圆环
        canvas.drawCircle(centre, centre, radius, paint);

        //画最里层的圆环
        //圆的半径 //再去除内层环形宽度的一般
        int radius2 = (int) (radius - secondRoundWidth);
        Log.d("echoMu", "secondRoundWidth " + secondRoundWidth);
        Log.d("echoMu", "3:radius2 " + radius2);
        //设置最里层圆环的颜色
        paint.setColor(secondRoundColor);
        //设置最里层圆环的宽度
        paint.setStrokeWidth(secondRoundWidth);
        //画出最外层圆环
        canvas.drawCircle(centre, centre, radius2, paint);

        //画奖品分区 等分的扇形
        paint.setStyle(Paint.Style.FILL);
        RectF rectF=new RectF(centre - radius2 + secondRoundWidth / 2, centre - radius2 + secondRoundWidth / 2, centre + radius2 - secondRoundWidth / 2, centre + radius2 - secondRoundWidth / 2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float divideAngle = 360 / N;
            for (int i = 0; i < N; i++) {
                if (i % 2 == 0) {
                    //设置最里层圆环的颜色
                    paint.setColor(0xffffffff);
                } else {
                    //设置最里层圆环的颜色
                    paint.setColor(0xffFFC1C1);
                }
                Log.d("echoMu", "4:centre - radius2 " + (centre - radius2));
                //再去除内层环形宽度的一般
                canvas.drawArc(rectF, -(divideAngle / 2 + 90) + divideAngle * i, divideAngle, true, paint);

                paint.setColor(getResources().getColor(R.color.firstRoundColor));
                paint.setTextSize(Util.dp2px(getContext(), 16));
                Path path=new Path();
                path.addArc(rectF,-(divideAngle / 2 + 90) + divideAngle * i, divideAngle);
                String text="330M流量";
                float textWidth=paint.measureText(text);
                canvas.drawTextOnPath(text,path, (float) (centre*2*Math.PI/N/2-textWidth/2),centre/5,paint);
            }
        }


    }

}
