package com.zhouzhou.locationgaode;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import androidx.work.Worker;

/**
 * author : ZhouZhou
 * e-mail : zhou.zhou@sim.com
 * date   : 19-12-4下午3:13
 * desc   :后台定位
 * version: 1.0
 */
public class LocationWork extends Worker {

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new MyAMapLocationListener();
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    int i = 0;
    @NonNull
    @Override
    public WorkerResult doWork() {
//        //初始化定位
//        mLocationClient = new AMapLocationClient(getApplicationContext());
//        //设置定位回调监听
//        mLocationClient.setLocationListener(mLocationListener);
//        //给定位客户端对象设置定位参数
//        setOption();
//        //启动定位
//        mLocationClient.startLocation();
        //接收传进来的参数
                                Intent intent1 = new Intent();
                                intent1.setAction("MYBROADCAST_RECIVER");
                                intent1.setPackage("com.zhouzhou.locationgaode");
                                intent1.putExtra("test", "test" + i);
                                getApplicationContext().sendBroadcast(intent1);
//        String str = this.getInputData().getString("demo","");
        Log.i("HttpWork","执行了哦"+i);
//
//        i++;
//        mLocationClient.stopLocation();
        return WorkerResult.SUCCESS;
    }



    private class MyAMapLocationListener implements AMapLocationListener{
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
        }
    }

    private void setOption(){
        if (null != mLocationOption){
            //初始化AMapLocationClientOption对象
            mLocationOption = new AMapLocationClientOption();

            //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
            mLocationOption.setInterval(1000);
            //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置是否允许模拟位置,默认为true，允许模拟位置
            mLocationOption.setMockEnable(true);
            //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
            mLocationOption.setHttpTimeOut(20000);

            //关闭缓存机制
            mLocationOption.setLocationCacheEnable(false);
        }

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }
}
