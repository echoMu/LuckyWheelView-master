package com.phone580.luckywheelview;

import android.animation.ValueAnimator;
import android.widget.ImageView;

/**
 * 用于回调动画过程和结果
 */

public interface RotateListener {

    /**
     * 动画结束 返回当前位置 注意 位置是最上面是1 然后依次逆时针递增
     *
     * @param position
     * @param des      所指分区文字描述
     */
    void rotateEnd(int position, String des);

    /**
     * 动画进行中 返回动画中间量
     *
     * @param valueAnimator
     */
    void rotating(ValueAnimator valueAnimator);

    /**
     * 在转盘转动的时间内，还未请求到抽奖结果
     */
    void rotateTimeout();

    /**
     * 点击了按钮 但是没有旋转 调用者可以在这里处理一些逻辑 比如弹出对话框确定用户是否要抽奖
     *
     * @param goImg
     */
    void beforeDrawALottery(ImageView goImg);
}
