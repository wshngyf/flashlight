package com.fey.flashlight;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.qhad.ads.sdk.adcore.Qhad;
import com.qhad.ads.sdk.interfaces.IQhAdEventListener;
import com.qhad.ads.sdk.interfaces.IQhBannerAd;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Camera camera;
    Camera.Parameters mParameters;
    ImageButton mLight = null;
    ImageButton mSosLight = null;
    ImageButton mRandomLight = null;
    int mLightFlag = 0;
    Boolean mOneFlashSwitch = false;
    Boolean mFlashSwitch = false;
    Timer timer = null;//定时器
    Boolean mSosFlashSwitch = false;
    int mSosFlag = 0;
    private RelativeLayout adContainer = null;
    private RelativeLayout adContainer1 = null;
    /*    private RelativeLayout adContainer2 = null;
        private RelativeLayout adContainer3 = null;
        private RelativeLayout adContainer4 = null;
        private RelativeLayout adContainer5 = null;
        private RelativeLayout adContainer6 = null;*/
    private IQhBannerAd bannerad = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //0723更新
/*        RelativeLayout rl = (RelativeLayout) findViewById(R.id.myry);
        RelativeLayout rlad1 = new RelativeLayout(this);
        RelativeLayout.LayoutParams rlad1lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rlad1lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rlad1.setLayoutParams(rlad1lp);
        rl.addView(rlad1);*/


        //绑定广告父容器
        adContainer = (RelativeLayout) findViewById(R.id.banner_adcontainer);
        adContainer1 = (RelativeLayout) findViewById(R.id.banner_adcontainer1);
       /*  adContainer2 = (RelativeLayout) findViewById(R.id.banner_adcontainer2);
        adContainer3 = (RelativeLayout) findViewById(R.id.banner_adcontainer3);
        adContainer4 = (RelativeLayout) findViewById(R.id.banner_adcontainer4);
        adContainer5 = (RelativeLayout) findViewById(R.id.banner_adcontainer5);
        adContainer6 = (RelativeLayout) findViewById(R.id.banner_adcontainer6);
*/

       /* adContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        //绑定3个按钮
        findview();
        //初始化相机


        //按钮的点击事件
        initBtnOnclick();
    }

    String adSpaceid = "a5PQQSYGQm"; // 广告位ID adSpaceid

    private void addAds(ViewGroup adContainer) {
        if (adContainer.getId() == R.id.banner_adcontainer1) {
            adSpaceid = "FuaGGTgPaR";
        }


        bannerad = Qhad.showBanner(adContainer, MainActivity.this, adSpaceid, false); // 请求广告

        /**
         * 设置广告回调
         */
        bannerad.setAdEventListener(new IQhAdEventListener() {
            @Override
            public void onAdviewGotAdSucceed() {

            }

            @Override
            public void onAdviewGotAdFail() {

            }

            @Override
            public void onAdviewRendered() {

            }

            @Override
            public void onAdviewIntoLandpage() {

            }

            @Override
            public void onAdviewDismissedLandpage() {

            }

            @Override
            public void onAdviewClicked() {

            }

            @Override
            public void onAdviewClosed() {

            }

            @Override
            public void onAdviewDestroyed() {

            }
        });
    }

    private void initBtnOnclick() {
        mLight.setOnClickListener(new MyLightBtnOnclick());
        mSosLight.setOnClickListener(new MyLightBtnOnclick());
        mRandomLight.setOnClickListener(new MyLightBtnOnclick());
        mLight.setBackgroundResource(R.mipmap.light_off);
        mRandomLight.setBackgroundResource(R.mipmap.stat_off);
        mSosLight.setBackgroundResource(R.mipmap.sos_off);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (!mOneFlashSwitch) {
            mLight.setBackgroundResource(R.mipmap.light_off);
        }
        if (!mSosFlashSwitch) {
            mSosLight.setBackgroundResource(R.mipmap.sos_off);
        }
        if (!mFlashSwitch) {
            mRandomLight.setBackgroundResource(R.mipmap.stat_off);
        }
    }


    private class MyLightBtnOnclick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //普通手电筒
                case R.id.btn_sdt:
                    //如果闪光灯关闭 而且SOS 和 flash 打开 则关闭其它
                    if ((mSosFlashSwitch) || (mFlashSwitch)) {
                        //如果闪光灯开着 则关闭
                        if (mSosFlashSwitch) {
                            //如果SOS灯打开则关闭
                            closeSosFlashLight();
                            mSosFlashSwitch = false;
                        } else if (mFlashSwitch) {
                            closeFlashLight();
                            mFlashSwitch = false;
                        }
                        // openOneFlashLight();
                        Log.i("TAG", "闪光灯已开");
                    }
                    mRandomLight.setBackgroundResource(R.mipmap.stat_off);
                    mSosLight.setBackgroundResource(R.mipmap.sos_off);
                    if (mOneFlashSwitch) {
                        mLight.setBackgroundResource(R.mipmap.light_off);
                    } else {
                        mLight.setBackgroundResource(R.mipmap.light_on);
                    }
                    openOneFlashLight();
                    break;
                //SOS闪光灯
                case R.id.btn_sos:
                    mLight.setBackgroundResource(R.mipmap.light_off);
                    mRandomLight.setBackgroundResource(R.mipmap.stat_off);
                    if (mFlashSwitch) {
                        closeFlashLight();
                        mFlashSwitch = false;
                    }
                    if (!mSosFlashSwitch) {
                        mSosLight.setBackgroundResource(R.mipmap.sos_on);
                        //如果没有打开SOS闪光灯
                        openSosFlashLight();
                        //SOS当前标志打开
                        mSosFlashSwitch = true;
                    } else {
                        mSosLight.setBackgroundResource(R.mipmap.sos_off);
                        closeSosFlashLight();
                        //SOS当前标志关闭
                        mSosFlashSwitch = false;
                    }

                    break;
                //酒吧模式
                case R.id.btn_random:
                    mLight.setBackgroundResource(R.mipmap.light_off);
                    mSosLight.setBackgroundResource(R.mipmap.sos_off);
                    if (mSosFlashSwitch) {
                        closeSosFlashLight();
                    }
                    if (!mFlashSwitch) {
                        mRandomLight.setBackgroundResource(R.mipmap.stat_on);

                        //如果此时手电筒没有打开  点击闪光灯
                        openFlashLight();
                        mFlashSwitch = true;
                    } else if (mFlashSwitch) {
                        mRandomLight.setBackgroundResource(R.mipmap.stat_off);

                        closeFlashLight();
                        mFlashSwitch = false;
                    }
                    break;
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        addAds(adContainer);
        addAds(adContainer1);
      /*     addAds(adContainer2) ;
        addAds(adContainer3) ;
        addAds(adContainer4) ;
        addAds(adContainer5) ;
        addAds(adContainer6) ;*/

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mOneFlashSwitch) {
                openOneFlashLight();
                mOneFlashSwitch = false;
            } else if (mSosFlashSwitch) {
                closeSosFlashLight();
                //SOS当前标志关闭
                mSosFlashSwitch = false;
            } else if (mFlashSwitch) {
                closeFlashLight();
                mFlashSwitch = false;
            }
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void openSosFlashLight() {
        //SOS闪光灯打开
        mSosFlashSwitch = true;
        timer = new Timer();
        mySosTimerTask task = new mySosTimerTask();
        timer.schedule(task, 0, 1000);
    }

    private void closeSosFlashLight() {
        timer.cancel();
        timer = null;
        //如果这时候是打开的 则把关闭
        if (mOneFlashSwitch) {
            openOneFlashLight();
        }
        mSosFlashSwitch = false;
    }

    private void openFlashLight() {
        timer = new Timer();
        myTimerTask task = new myTimerTask();
        timer.schedule(task, 0, 500);
        mFlashSwitch = true;//把闪光灯设置为true
    }


    private void closeFlashLight() {
        timer.cancel();
        timer = null;
        //关闭以后判断一下闪光灯是否开着 如果开着就关闭
        if (mOneFlashSwitch) {
            openOneFlashLight();
        }
        mFlashSwitch = false;
    }

    private void findview() {
        mLight = (ImageButton) findViewById(R.id.btn_sdt);
        mSosLight = (ImageButton) findViewById(R.id.btn_sos);
        mRandomLight = (ImageButton) findViewById(R.id.btn_random);
    }

    //创建Camera对象 打开手电筒
    private void openLight() {
        camera = Camera.open();
        mParameters = camera.getParameters();
        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(mParameters);
        //camera.startPreview();
    }

    private void closeLight() {
        mParameters = camera.getParameters();
        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(mParameters);
        if (null != camera) {
            camera.stopPreview();  //关掉亮灯
            camera.release();   //关掉照相机
        }
        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(mParameters);
        camera.stopPreview();
        camera.release();
    }

    private void openOneFlashLight() {
        if (!mOneFlashSwitch) {
            openLight();
            mOneFlashSwitch = true;
        } else {
            closeLight();
            mOneFlashSwitch = false;
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                openOneFlashLight();
            }
        }
    };


    private class myTimerTask extends TimerTask {

        @Override
        public void run() {

            handler.sendEmptyMessage(1);
            /*Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);*/
        }
    }

    private class mySosTimerTask extends TimerTask {

        @Override
        public void run() {

            if (6 > mSosFlag) {
                Log.i("TAG", "发送1==" + mSosFlag);
                handler.sendEmptyMessage(1);
            } else if (14 > mSosFlag) {
                switch (mSosFlag) {
                    case 6:
                        handler.sendEmptyMessage(1);
                        Log.i("TAG", "发送亮==" + mSosFlag);
                        break;
                    case 7:
                        handler.sendEmptyMessage(2);
                        Log.i("TAG", "发送2==" + mSosFlag);
                        break;
                    case 8:
                        handler.sendEmptyMessage(1);
                        Log.i("TAG", "发送灭==" + mSosFlag);
                        break;
                    case 9:
                        handler.sendEmptyMessage(1);
                        Log.i("TAG", "发送亮==" + mSosFlag);
                        break;
                    case 10:
                        handler.sendEmptyMessage(2);
                        Log.i("TAG", "发送2==" + mSosFlag);
                        break;
                    case 11:
                        handler.sendEmptyMessage(1);
                        Log.i("TAG", "发送灭==" + mSosFlag);
                        break;
                    case 12:
                        handler.sendEmptyMessage(1);
                        Log.i("TAG", "发送亮==" + mSosFlag);
                        break;
                    case 13:
                        handler.sendEmptyMessage(2);
                        Log.i("TAG", "发送2==" + mSosFlag);
                        break;
                }
            }
            mSosFlag++;
            if (14 == mSosFlag) {
                mSosFlag = 0;
            }
        }
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            // 需要做的事:发送消息

        }
    };

}
