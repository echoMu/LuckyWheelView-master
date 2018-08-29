package com.phone580.luckywheelview.master;

import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.phone580.luckywheelview.LuckyWheelLayout;
import com.phone580.luckywheelview.LuckyWheelLayout2;
import com.phone580.luckywheelview.Price;
import com.phone580.luckywheelview.RotateListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements LuckyWheelLayout2.AnimationEndListener{
    private LuckyWheelLayout2 luckyWheelLayout;
    private static List<Price> priceList=new ArrayList<>();

    static {
        Price price1=new Price("100M流量","https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534335246456&di=81ad434f1a9a42b4c8e84572c3605784&imgtype=0&src=http%3A%2F%2Fimg67.nipic.com%2Ffile%2F20150829%2F9182165_111723425280_1.jpg");
        priceList.add(price1);
        Price price2=new Price("200M流量","https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534334957968&di=f835bb0208aae61eab2335e1af7dcb84&imgtype=0&src=http%3A%2F%2Fwww.esoogle.com%2Ffile%2Fupload%2F201711%2F14%2F1143224248153.png");
        priceList.add(price2);
        Price price3=new Price("300M流量","https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534334989867&di=8f032ae9db6731c20bf037f20c99a321&imgtype=0&src=http%3A%2F%2Fbpic.588ku.com%2Felement_origin_min_pic%2F16%2F11%2F13%2Ff5c92174c5412b551f579da74a831dc7.jpg");
        priceList.add(price3);
        Price price4=new Price("400M流量","https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534335006027&di=7dd540909c6977d1b89975ad5b25c46d&imgtype=0&src=http%3A%2F%2Fimage3.quanmama.com%2FAdminImageUpload%2F2911692555.jpg");
        priceList.add(price4);
        Price price5=new Price("500M流量","https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534335168676&di=81dedee1993f9a219623e7e7937eeda9&imgtype=0&src=http%3A%2F%2Ff.hiphotos.baidu.com%2Fexp%2Fw%3D200%2Fsign%3D2dadfdd7aa4bd11304cdb0326aaea488%2F86d6277f9e2f0708cd0033a4ee24b899a901f209.jpg");
        priceList.add(price5);
        Price price6=new Price("600M流量","https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534335184180&di=0979ad8fb7b6fa4d79611b725dce9b76&imgtype=0&src=http%3A%2F%2Fpic.uzzf.com%2Fup%2F2017-4%2F20174111043531378.png");
        priceList.add(price6);
//        Price price7=new Price("700M流量","https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534335197066&di=5311007897b8bf8f41493b65d8d6a466&imgtype=0&src=http%3A%2F%2Fwww.xiaohei.com%2Fd%2Ffile%2Fapp%2Fanzhuo%2F2017-11-23%2Fp3kijeup0m1_94945.png");
//        priceList.add(price7);
//        Price price8=new Price("800M流量","https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534929929&di=cb656f48d9801db7148674a6778cb893&imgtype=jpg&er=1&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F01c59356d11a2732f875520f0aca0f.jpg%40900w_1l_2o_100sh.jpg");
//        priceList.add(price8);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        luckyWheelLayout =findViewById(R.id.luckwheel_layout);
        luckyWheelLayout.setAnimationEndListener(this);
//        RotateView rotateView=findViewById(R.id.rotateView);

        luckyWheelLayout.initDataAndView(priceList);
//        rotateView.initDataAndView(8,priceList);

        final LuckyWheelLayout luckyWheelLayout = findViewById(R.id.LuckyWheelLayout);
        luckyWheelLayout.setPriceList(priceList);

        //添加滚动监听
        luckyWheelLayout.setRotateListener(new RotateListener() {
            @Override
            public void rotateEnd(int position, String des) {
                Toast.makeText(MainActivity.this, "结束了位置：" + position + "   奖品：" + des, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void rotating(ValueAnimator valueAnimator) {

            }

            @Override
            public void rotateTimeout() {
                Toast.makeText(MainActivity.this,"请重新再试!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void beforeDrawALottery(ImageView goImg) {
                //开始转动
                luckyWheelLayout.startRotate();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //模拟获取结果
                        try {
                            Thread.sleep(1000*5);

                            luckyWheelLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    luckyWheelLayout.startDrawALottery((new Random().nextInt(5) + 1));
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    public void rotate(View view) {
        luckyWheelLayout.rotate(-1,100);
    }

    @Override
    public void endAnimation(int position) {
        //获得了哪个奖品
        Toast.makeText(this,"Position = "+position+","+priceList.get(position),Toast.LENGTH_SHORT).show();
    }
}
