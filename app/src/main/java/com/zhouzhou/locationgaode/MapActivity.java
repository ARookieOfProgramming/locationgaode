package com.zhouzhou.locationgaode;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.fence.GeoFence;
import com.amap.api.fence.GeoFenceClient;
import com.amap.api.fence.GeoFenceListener;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.DPoint;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.MyLocationStyle;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.amap.api.fence.GeoFenceClient.GEOFENCE_IN;
import static com.amap.api.fence.GeoFenceClient.GEOFENCE_OUT;
import static com.amap.api.fence.GeoFenceClient.GEOFENCE_STAYED;

public class MapActivity extends AppCompatActivity {


    @BindView(R.id.map)
    MapView map;
    private AMapLocationClient mLocationClient = null;//声明AMapLocationClient类对象
    private AMapLocationListener mLocationListener = new myAMapLocationListener();//声明定位回调监听器
    private Boolean isPassed = false;//权限通过
    private AMapLocationClientOption mLocationOption = null;//定位参数设置
    private AMap aMap = null;
    private MyLocationStyle myLocationStyle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        getPerssions();
        if (aMap == null){
            aMap = map.getMap();
        }
        aMap.setMyLocationStyle(setMyLocationType());//设置蓝点样式
        mLocationClient = new AMapLocationClient(getApplicationContext());//初始化定位
        mLocationClient.setLocationListener(mLocationListener);//设置定位回调监听
        mLocationClient.setLocationOption(setOption());//设置option
        mLocationClient.startLocation();
        setGeoFence();//设置地理围栏
        map.onCreate(savedInstanceState);//在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图


    }

    private void setGeoFence() {
        //实例化地理围栏客户端
        GeoFenceClient mGeoFenceClient = new GeoFenceClient(getApplicationContext());

        //设置希望侦测的围栏触发行为，默认只侦测用户进入围栏的行为
        //public static final int GEOFENCE_IN 进入地理围栏
        //public static final int GEOFENCE_OUT 退出地理围栏
        //public static final int GEOFENCE_STAYED 停留在地理围栏内10分钟

        mGeoFenceClient.setActivateAction(GEOFENCE_IN|GEOFENCE_OUT|GEOFENCE_STAYED);
        DPoint dPoint = new DPoint(39.992702, 116.470470);
        mGeoFenceClient.addGeoFence(dPoint,10000,"chuangjian");
    }

private GeoFenceListener GFListener = new GeoFenceListener() {
    @Override
    public void onGeoFenceCreateFinished(List<GeoFence> list, int i, String s) {
        if(i == GeoFence.ADDGEOFENCE_SUCCESS){//判断围栏是否创建成功
           // tvReult.setText("添加围栏成功!!");
            Toast.makeText(MapActivity.this, "成功", Toast.LENGTH_SHORT).show();
            //geoFenceList是已经添加的围栏列表，可据此查看创建的围栏
        } else {
            Toast.makeText(MapActivity.this, "失败", Toast.LENGTH_SHORT).show();

            //tvReult.setText("添加围栏失败!!");
        }
    }
};
    private MyLocationStyle setMyLocationType() {
        if (myLocationStyle == null){
            myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
            myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
            aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
            aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
            aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
            myLocationStyle.showMyLocation(true);//设置是否显示定位小蓝点，用于满足只想使用定位，不想使用定位小蓝点的场景，设置false以后图面上不再有定位蓝点的概念，但是会持续回调位置信息。
            //myLocationStyle.myLocationIcon();//蓝点图标
            myLocationStyle.strokeColor(R.color.strokeColor);//精度圈边框颜色
            myLocationStyle.radiusFillColor(R.color.radiusFillColor);//精度圈填充颜色
        }
        return myLocationStyle;
    }

    private void getPerssions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> perssionList = new ArrayList<>();

            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                perssionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                perssionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (perssionList.size() > 0) {
                requestPermissions(perssionList.toArray(new String[perssionList.size()]), 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //权限通过
                } else {
                    //权限被拒绝
                }
        }
    }

    private class myAMapLocationListener implements AMapLocationListener {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    //Toast.makeText(MapActivity.this, aMapLocation.getLatitude() + "", Toast.LENGTH_SHORT).show();
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
                }
            }
        }
    }

    private AMap.OnMyLocationChangeListener listener = new AMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            Toast.makeText(MapActivity.this, location.getLatitude() + "", Toast.LENGTH_SHORT).show();
        }
    };
    private AMapLocationClientOption setOption() {
        if (mLocationOption == null) {
            mLocationOption = new AMapLocationClientOption();
            mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Sport);
            //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        }
        return mLocationOption;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        map.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        map.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        map.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        map.onSaveInstanceState(outState);
    }
}
