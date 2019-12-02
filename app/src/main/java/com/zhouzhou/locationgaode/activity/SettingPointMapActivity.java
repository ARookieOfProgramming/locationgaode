package com.zhouzhou.locationgaode.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.zhouzhou.locationgaode.DBHelper;
import com.zhouzhou.locationgaode.R;
import com.zhouzhou.locationgaode.bean.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingPointMapActivity extends AppCompatActivity {

    @BindView(R.id.settings_map)
    MapView settingsMap;
    @BindView(R.id.btn_point_sure)
    Button btnPointSure;

    private AMap aMap = null;
    private Circle circle = null;
    private float radius = 0f;
    private DBHelper dbHelper = null;
    private SQLiteDatabase db = null;
    private LatLng point = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_point_map);
        ButterKnife.bind(this);

        dbHelper = new DBHelper(this);
        db = dbHelper.getReadableDatabase();
        Intent intent = getIntent();
        String ra = intent.getStringExtra("radius");
        radius = Float.parseFloat(ra);
        init();
        settingsMap.onCreate(savedInstanceState);
    }

    private void init() {

        if (aMap == null) {
            aMap = settingsMap.getMap();
        }
        setMystyle();
        aMap.moveCamera(CameraUpdateFactory.zoomTo(10.0f));
        aMap.setOnMapClickListener(mapClickListener);
    }

    private void setMystyle() {
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//定位一次，且将视角移动到地图中心点。
        //myLocationSty|le.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        //myLocationStyle.showMyLocation(true);
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0))//精度圈填充颜色,argb(透明度,red,green,blue)(透明度ff完全不透明，0完全透明)
                .strokeColor(Color.argb(150, 12, 32, 56));//精度圈边框颜色
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false
    }

    private AMap.OnMapClickListener mapClickListener = new AMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            if (circle != null) {
                circle.remove();
            }
            drawCircle(latLng);
        }
    };

    private void drawCircle(LatLng latLng) {
        circle = aMap.addCircle(new CircleOptions().center(latLng)//中心点
                .radius(radius)//半径
                .strokeColor(Color.argb(100, 0, 221, 255))//边框颜色
                .fillColor(Color.argb(40, 0, 221, 255)).strokeWidth(5f));//填充颜色
        point = latLng;
    }

    @OnClick(R.id.btn_point_sure)
    public void onViewClicked() {
        if (point == null) {
            Toast.makeText(this, "您还未标点", Toast.LENGTH_SHORT).show();
        } else {
            setResult(Constant.resultCode, new Intent().putExtra("latlng", new double[]{point.latitude, point.longitude}));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行settingsMap.onDestroy()，销毁地图
        settingsMap.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行settingsMap.onResume ()，重新绘制加载地图
        settingsMap.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行settingsMap.onPause ()，暂停地图的绘制
        settingsMap.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行settingsMap.onSaveInstanceState (outState)，保存地图当前的状态
        settingsMap.onSaveInstanceState(outState);
    }
}
