package com.fey.weather;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DecimalFormat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    TextView textView;
    String mCity = "";
    String mCurrentLocation = "";

    String mTem = "";

    TextView mCitytv;
    TextView mWeathertv;
    TextView mTemtv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //textView = (TextView) findViewById(R.id.tv);
        //开启定位功能

        findview();

        getLocation();


    }

    private void findview() {
        mCitytv = (TextView) findViewById(R.id.x_CurrentCity);
        mWeathertv = (TextView) findViewById(R.id.x_CurrentWeather);
        mTemtv = (TextView) findViewById(R.id.x_CurrentTem);

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String location = bundle.getString("位置");
            Log.i("TAG", "传来的位置=" + location);
            new ASyncUploadImage().execute(location);
        }
    };

    private void getLocation() {
        //获取地理位置管理器
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        initLocation();
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        mLocationClient.start();
        mLocationClient.requestLocation();
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("gcj02");//可选，默认gcj02，设置返回的定位结果坐标系
        //int span = 1000;
        //option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            double lati = location.getLatitude();
            double longa = location.getLongitude();
            String a = location.getCity();
            Log.i("TAG", "location.getAddrStr()=" + location.getAddrStr());
            Log.i("TAG", "location.getCity()=" + location.getCity());
            DecimalFormat format = new DecimalFormat("#####.00");
            String str3 = format.format(lati);
            String str1 = format.format(longa);
            int i = location.getLocType();
            Log.i("TAG", "经度=" + str1 + "纬度=" + str3 + "  code=" + i);
           /* Message msg = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putString("位置", a);
            msg.setData(bundle);
            msg.what = 0;
            handler.sendMessage(msg);*/
            //  handler.sendEmptyMessage(0);
            mCitytv.setText(a);
            ASyncUploadImage as = new ASyncUploadImage();
            as.execute(a);
        }
    }

    private class ASyncUploadImage extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("TAG", "s=" + s);
            getJson(s);
        }

        @Override
        protected String doInBackground(String... params) {

            return getData(params[0]);
        }
    }

    private String getData(String string) {
        String strUTF8 = null;
        try {
            strUTF8 = URLDecoder.decode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = "http://op.juhe.cn/onebox/weather/query?cityname=" + strUTF8 + "&key=a7c1f7ebf5f9484e8e5722f54e66d227";
        Log.i("TAG", "url=" + url);
        InputStreamReader isr;
        String result = "";
        try {
            isr = new InputStreamReader(new URL(url).openStream(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String line = "";
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getJsonData(String temp) {
        String strUTF8 = null;
        OkHttpClient ok = new OkHttpClient();
        String str = temp;
        Log.i("TAG", "parms[0]=" + temp);
        try {
            strUTF8 = URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = "http://op.juhe.cn/onebox/weather/query?cityname=" + strUTF8 + "&key=a7c1f7ebf5f9484e8e5722f54e66d227";
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = ok.newCall(request);
//请求加入调度

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("TAG", "失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("TAG", response.body().string());
                getJson(response.body().string());
            }
        });
        return "";
    }

    private void getJson(String json) {
        Log.i("TAG", "json=" + json);
        try {
            JSONObject jsonObject = new JSONObject(json);
            String reason = jsonObject.getString("reason");
            Log.i("TAG", "reason=" + reason);
            String result = jsonObject.getString("result");
            Log.i("TAG", "result=" + result);
            JSONObject jsonResult = new JSONObject(result);
            String data = jsonResult.getString("data");
            Log.i("TAG", "data=" + data);
            JSONObject jsonData = new JSONObject(data);
            String realtime = jsonData.getString("realtime");
            Log.i("TAG", "realtime=" + realtime);
            JSONObject jsonRealtime = new JSONObject(realtime);
            String weather = jsonRealtime.getString("weather");
            JSONObject jsonWeather = new JSONObject(weather);
            String info = jsonWeather.getString("info");
            Log.i("TAG", "info=" + info);
            String temperature = jsonWeather.getString("temperature");
            Log.i("TAG", "temperature=" + temperature);
            mTemtv.setText(temperature);
            mWeathertv.setText(info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
