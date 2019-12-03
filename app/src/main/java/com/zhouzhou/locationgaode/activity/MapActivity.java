package com.zhouzhou.locationgaode.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
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
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.zhouzhou.locationgaode.DBHelper;
import com.zhouzhou.locationgaode.R;
import com.zhouzhou.locationgaode.bean.Constant;
import com.zhouzhou.locationgaode.bean.SignStatusInfo;
import com.zhouzhou.locationgaode.bean.SignStatusInfoFull;
import com.zhouzhou.locationgaode.bean.SignTableInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.amap.api.fence.GeoFenceClient.GEOFENCE_IN;
import static com.amap.api.fence.GeoFenceClient.GEOFENCE_OUT;
import static com.amap.api.fence.GeoFenceClient.GEOFENCE_STAYED;


public class MapActivity extends AppCompatActivity {


    @BindView(R.id.map)
    MapView map;

    //定义接收广播的action字符串
    public static final String GEOFENCE_BROADCAST_ACTION = "com.location.apis.geofencedemo.broadcast";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.design_navigation_view)
    NavigationView designNavigationView;
    @BindView(R.id.design_drawer_view)
    DrawerLayout designDrawerView;
    @BindView(R.id.btn_sign)
    Button btnSign;

    private AMapLocationClient mLocationClient = null;//声明AMapLocationClient类对象
    private AMapLocationListener mLocationListener = new myAMapLocationListener();//声明定位回调监听器
    private Boolean isPassed = false;//权限通过
    private AMapLocationClientOption mLocationOption = null;//定位参数设置
    private AMap aMap = null;
    private MyLocationStyle myLocationStyle = null;//蓝点style设置
    private Circle circle = null;

    private GeoFenceClient mGeoFenceClient = null;//地理围栏客户端
    private boolean btnChange = false;//按钮点击
    private int geoCount = 0;//地理围栏数

    private Vibrator vibrator = null;//震动相关
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private SignStatusInfoFull statusInfoFull = null;
    private SignStatusInfo statusInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        getPerssions();

        mGeoFenceClient = new GeoFenceClient(getApplicationContext());
        //创建并设置PendingIntent
        mGeoFenceClient.createPendingIntent(GEOFENCE_BROADCAST_ACTION);
        vibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);//震动
        map.onCreate(savedInstanceState);//在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图

        dbHelper = new DBHelper(this);
        db = dbHelper.getReadableDatabase();

        init();
        initToolBar();

    }

    private void init() {
        if (aMap == null) {
            aMap = map.getMap();
            aMap.moveCamera(CameraUpdateFactory.zoomTo(10));
        }
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false
        mLocationClient = new AMapLocationClient(getApplicationContext());//初始化定位
        mLocationClient.setLocationListener(mLocationListener);//设置定位回调监听
        mLocationClient.setLocationOption(setOption());//设置option
        aMap.setMyLocationStyle(setMyLocationType());//设置定位蓝点的Style
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(GEOFENCE_BROADCAST_ACTION);
        registerReceiver(mGeoFenceReceiver, filter);
    }

    private void initToolBar() {
        toolbar.setLogo(R.drawable.sign_in);
        toolbar.setNavigationIcon(R.drawable.menu);
        toolbar.setTitle("考勤打卡");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        designNavigationView.setNavigationItemSelectedListener(new mYOnNavigationItemSelectedListener());

        SignStatusInfo info = dbHelper.getStatusInfo(db,dbHelper.dateToString(new Date(),Constant.timeSimple)).getStatusInfo();
        Boolean b1 = "yes".equals(info.getSignInIdentity());
        Boolean b2 = "yes".equals(info.getsignOutIdentity());
        if (b1 && b2){
            btnSign.setText("打卡完成，回家休息喽");
        }else if (b1 && !b2){
       btnSign.setText("一键签退");
        }else if (!b1 && b2){
            btnSign.setText("一键签到");
        }else{
            btnSign.setText("一键签到");
        }
    }

    /*
     *@Author: zhouzhou
     *@Date: 19-11-27
     *@Deecribe:设置地理围栏
     *@Params:
     *@Return:
     *@Email：zhou.zhou@sim.com
     */
    private void setMyGeoFence(final LatLng latLng) {
        //实例化地理围栏客户端
        //设置希望侦测的围栏触发行为，默认只侦测用户进入围栏的行为
        //public static final int GEOFENCE_IN 进入地理围栏
        //public static final int GEOFENCE_OUT 退出地理围栏
        //public static final int GEOFENCE_STAYED 停留在地理围栏内10分钟
        if (geoCount <= 1) {
            mGeoFenceClient.setActivateAction(GEOFENCE_IN | GEOFENCE_OUT | GEOFENCE_STAYED);
            DPoint dPoint = new DPoint(latLng.latitude, latLng.longitude);
            float radius = (float) dbHelper.queryInfo(db, Constant.name).getRadius();
            mGeoFenceClient.addGeoFence(dPoint, radius, "chuangjian");
            mGeoFenceClient.setGeoFenceListener(new GeoFenceListener() {
                @Override
                public void onGeoFenceCreateFinished(List<GeoFence> list, int i, String s) {
                    if (i == GeoFence.ADDGEOFENCE_SUCCESS) {//判断围栏是否创建成功
                        addCircle(latLng);//设置成功画圆
                        //geoFenceList是已经添加的围栏列表，可据此查看创建的围栏
                    } else {
                        Toast.makeText(MapActivity.this, "您已经添加过了", Toast.LENGTH_SHORT).show();
                        //tvReult.setText("添加范围失败!!");
                    }
                }
            });

        }

    }

    /*
     *@Author: zhouzhou
     *@Date: 19-11-27
     *@Deecribe：在地图上添加一个圆
     *@Params:
     *@Return:
     *@Email：zhou.zhou@sim.com
     */
    private void addCircle(LatLng latLng) {
        circle = aMap.addCircle(new CircleOptions().center(latLng)//中心点
                .radius((float) dbHelper.queryInfo(db, Constant.name).getRadius())//半径
                .strokeColor(Color.argb(100, 0, 221, 255))//边框颜色
                .fillColor(Color.argb(40, 0, 221, 255)).strokeWidth(5f));//填充颜色
    }

    /*
     *@Author: zhouzhou
     *@Date: 19-11-27
     *@Deecribe：定位蓝点样式设置
     *@Params:
     *@Return:
     *@Email：zhou.zhou@sim.com
     */
    private MyLocationStyle setMyLocationType() {
        if (myLocationStyle == null) {
            myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
            //myLocationStyle
            myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0))//精度圈填充颜色,argb(透明度,red,green,blue)(透明度ff完全不透明，0完全透明)
                    .strokeColor(Color.argb(150, 12, 32, 56))//精度圈边框颜色
                    .showMyLocation(true)//设置是否显示定位小蓝点，用于满足只想使用定位，不想使用定位小蓝点的场景，设置false以后图面上不再有定位蓝点的概念，但是会持续回调位置信息。
                    .interval(1000)//设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
                    .myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);//蓝点定位模式
            //.myLocationIcon();//蓝点图标
            if (null != mLocationClient) {
                mLocationClient.setLocationOption(setOption());
                //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
                mLocationClient.stopLocation();
                mLocationClient.startLocation();
            }
        }
        return myLocationStyle;
    }

    @OnClick(R.id.btn_sign)
    public void onViewClicked() {
        statusInfoFull = dbHelper.getStatusInfo(db, dbHelper.dateToString(new Date(), Constant.timeSimple));
        statusInfo = statusInfoFull.getStatusInfo();
        mLocationClient.startLocation();

        //        isClicked = true;
    }

    /*
     *@Author: zhouzhou
     *@Date: 19-11-27
     *@Deecribe：startLocation()成功定位之后的回调
     *@Params:
     *@Return:
     *@Email：zhou.zhou@sim.com
     */
    private class myAMapLocationListener implements AMapLocationListener {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    //                    if (isClicked){
                    //                        isClicked = false;
                    mLocationClient.stopLocation();
                    Message message = new Message();
                    message.obj = aMapLocation;
                    handler.sendMessage(message);
                    //                    }
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Boolean btnClicked = true;
            AMapLocation aMapLocation = (AMapLocation) msg.obj;
            SignTableInfo info = dbHelper.queryInfo(db, Constant.name);
            //判断范围（签到有距离限制，签退无）
            float distance = AMapUtils.calculateLineDistance(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()), new LatLng(info.getLatitude(), info.getLongitude()));
            //在范围内，签到，toast成功
            if("一键签到".equals(btnSign.getText().toString())){
                if (statusInfoFull.getStatusInfo().getSignInIdentity().equals("no")) {
                    if (distance <= info.getRadius()) {
                        updateInfo("setSignInIdentity");
                        if (statusInfoFull.getStatusInfo().getSignInIdentity().equals("yes")) {
                            Toast.makeText(MapActivity.this, "签到成功", Toast.LENGTH_SHORT).show();
                            btnSign.setText("一键签退");
                            btnClicked = false;
                        }
                    } else {
                        Toast.makeText(MapActivity.this, "未到打卡范围", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MapActivity.this, "已签到", Toast.LENGTH_SHORT).show();
                }
            }else if ("一键签退".equals(btnSign.getText().toString())){
                //时间大于下班点自动签退
                if (statusInfoFull.getStatusInfo().getsignOutIdentity().equals("no") && btnClicked) {
                        updateInfo("setsignOutIdentity");
                        if (statusInfoFull.getStatusInfo().getsignOutIdentity().equals("yes")) {
                            Toast.makeText(MapActivity.this, "签退成功", Toast.LENGTH_SHORT).show();
                            btnSign.setText("打卡完成，回家休息喽");
                        }
                } else {
                    Toast.makeText(MapActivity.this, "已签退", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(MapActivity.this, "快回家吃饭吧", Toast.LENGTH_SHORT).show();
            }
        }

    };

    /*
     *@Author: zhouzhou
     *@Date: 19-11-27
     *@Deecribe：地图初始化相关属性
     *@Params:
     *@Return:
     *@Email：zhou.zhou@sim.com
     */
    private AMapLocationClientOption setOption() {
        // if (mLocationOption == null) {
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Transport);
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        //        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //        mLocationOption.setSensorEnable(true);
        //        mLocationOption.setInterval(1000);
        //}
        return mLocationOption;
    }

    private void updateInfo(String type) {
        ContentValues values = new ContentValues();
        switch (type) {
            case "setSignInIdentity":
                statusInfo.setSignInIdentity("yes");
                statusInfo.setSignInDate(dbHelper.dateToString(new Date(),Constant.timeFull));
                break;
            case "setsignOutIdentity":
                statusInfo.setsignOutIdentity("yes");
                statusInfo.setSignOutDate(dbHelper.dateToString(new Date(),Constant.timeFull));
                break;
            case "signInSend":
                statusInfo.setSignInSend(1);
                break;
            case "signOutSend":
                statusInfo.setsignOutSend(1);
                break;
            case "total":
                statusInfo.setSignInIdentity("no");
                statusInfo.setsignOutIdentity("no");
                statusInfo.setSignInSend(0);
                statusInfo.setsignOutSend(0);
                break;
        }
        String json = dbHelper.toJson(statusInfo);
        values.put("info", json);
        dbHelper.upSignStatus(db, values, dbHelper.dateToString(new Date(), Constant.timeSimple));
    }

    /*
     *@Author: zhouzhou
     *@Date: 19-11-27
     *@Deecribe：动态获取权限
     *@Params:
     *@Return:
     *@Email：zhou.zhou@sim.com
     */
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

    /*
     *@Author: zhouzhou
     *@Date: 19-11-27
     *@Deecribe：权限获取回调
     *@Params:
     *@Return:
     *@Email：zhou.zhou@sim.com
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //权限通过
                } else {
                    finish();
                }
        }
    }

    private BroadcastReceiver mGeoFenceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            statusInfoFull = dbHelper.getStatusInfo(db, dbHelper.dateToString(new Date(), Constant.timeSimple));
            statusInfo = statusInfoFull.getStatusInfo();
            if (intent.getAction().equals(GEOFENCE_BROADCAST_ACTION)) {
                //解析广播内容
                //获取Bundle
                Bundle bundle = intent.getExtras();
                //获取围栏行为：
                int status = bundle.getInt(GeoFence.BUNDLE_KEY_FENCESTATUS);
                boolean signIn = statusInfo.getSignInIdentity().equals("no");
                switch (status) {
                    case GEOFENCE_IN:
                        //到达签到距离，提示签到
                        if (signIn) {
                            if (determineTime()) {
                                if (statusInfo.getSignInSend() == 0) {
                                    showNotification(Constant.IN);
                                }

                            }
                        }
                        break;
                    case GEOFENCE_OUT:
                        if (statusInfo.getsignOutIdentity().equals("no") && !signIn) {
                            if (determineTime()) {
                                if (statusInfo.getSignOutSend() == 0) {
                                    //到达签退距离，提示签到
                                    showNotification(Constant.OUT);
                                    //或一定时间签退
                                    timeCountNotification();
                                }
                            }
                        }
                        break;
                    case GEOFENCE_STAYED:
                        //十分钟收集一次坐标
                        break;
                }
            }
        }
    };

    //通知栏相关
    private NotificationManager manager = null;
    private NotificationCompat.Builder builder = null;
    private Notification notification = null;

    private void showNotification(int type) {
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(Constant.ID, "b", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(notificationChannel);
        }
        Intent intent = new Intent(this, MapActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder = new NotificationCompat.Builder(this).setChannelId(Constant.ID).setWhen(System.currentTimeMillis())//通知栏显示时间
                .setSmallIcon(R.mipmap.ic_launcher)//通知栏小图标
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))//通知栏下拉是图标
                .setContentIntent(pendingIntent)//关联点击通知栏跳转页面
                .setPriority(NotificationCompat.PRIORITY_MAX)//设置通知消息优先级
                .setAutoCancel(true)//设置点击通知栏消息后，通知消息自动消失
                //                .setSound(Uri.fromFile(new File("/system/MP3/music.mp3"))) //通知栏消息提示音
                .setVibrate(new long[]{0, 1000, 1000, 1000}) //通知栏消息震动
                .setLights(Color.GREEN, 1000, 2000); //通知栏消息闪灯(亮一秒间隔两秒再亮)
        //.setDefaults(NotificationCompat.DEFAULT_ALL); //通知栏提示音、震动、闪灯等都设置为默认
        vibrator.vibrate(1000);//震动1秒
        if (type == Constant.IN) {
            builder.setContentTitle("打卡提醒").setContentText("已进入签到范围");
            notification = builder.build();
            updateInfo("signInSend");
            manager.notify(Constant.IN, notification);
        }
        if (type == Constant.OUT) {
            builder.setContentTitle("打卡提醒").setContentText("已离开签退范围");
            notification = builder.build();
            updateInfo("signOutSend");
            manager.notify(Constant.IN, notification);
        }
        if (type == Constant.STAYED) {
            builder.setContentTitle("打卡提醒").setContentText("签到范围待机");
            notification = builder.build();
            manager.notify(Constant.IN, notification);
        }
    }

    /*
     *@Author: zhouzhou
     *@Date: 19-11-28
     *@Deecribe：判断当前时间是否在时间段内
     *@Params:
     *@Return:
     *@Email：zhou.zhou@sim.com
     */
    private Boolean determineTime() {
        //LocalDateTime now = LocalDateTime.now();
        GregorianCalendar gre = new GregorianCalendar();
        //Date date = new Date(now.getYear() - 1900, now.getMonthValue() - 1, now.getDayOfMonth()); //年要减去1900，月份是0-11
        Date date = new Date();
        Date date1 = new Date();
        gre.setTime(date);
        int weekday = gre.get(Calendar.DAY_OF_WEEK) - 1; //0是星期天
        if (weekday > 5) {
            return false;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        SignTableInfo info = dbHelper.queryInfo(db, Constant.name);
        //开始时间
        try {
            date1 = simpleDateFormat.parse(info.getTimeStart());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date.getHours() < date1.getHours()) {
            return false;
        }
        //结束时间
        try {
            date1 = simpleDateFormat.parse(info.getTimeStop());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date.getHours() > date1.getHours()) {
            return false;
        }
        return true;
    }

    private Timer timer = null;

    /*
     *@Author: zhouzhou
     *@Date: 19-12-2
     *@Deecribe：计时器
     *@Params:
     *@Return:
     *@Email：zhou.zhou@sim.com
     */
    private void timeCountNotification() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if ("yes".equals(statusInfo.getsignOutIdentity())){
                    timer.cancel();
                }else{
                    showNotification(Constant.OUT);
                    timer.cancel();
                }
            }
        };
        if ("no".equals(statusInfo.getsignOutIdentity())){
            String timeQuantum = dbHelper.queryInfo(db, Constant.name).getTimeQuantum();
            Date date = dbHelper.stringToDate(timeQuantum, Constant.timeHour);
            long longtime = 1;
            if (date.getHours() > 0){
                longtime = date.getHours() * 60;
            }
            if (date.getMinutes() > 0){
                longtime = longtime * date.getMinutes() * 60 * 1000;
            }
            timer.schedule(task, longtime);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tool_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_location:
                updateInfo("total");
                break;
            case android.R.id.home:
                designDrawerView.openDrawer(GravityCompat.START);
                break;
            case R.id.action_time_auto:
                startActivity(new Intent(this,SettingsActivity.class));
                break;
        }

        return true;
    }

    private class mYOnNavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.nav_view_settings:
                    startActivity(new Intent(MapActivity.this, SettingsActivity.class));
                    break;
                case R.id.nav_view_about:
                    Toast.makeText(MapActivity.this, "关于", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.nav_view_login_out:
                    startActivity(new Intent(MapActivity.this, LoginActivity.class));
                    break;
            }
            return true;
        }
    }

    @Override
    protected void onDestroy() {

        if (mGeoFenceClient != null) {
            mGeoFenceClient.removeGeoFence();
        }
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        if (null != map) {
            map.onDestroy();
        }
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
        try {
            unregisterReceiver(mGeoFenceReceiver);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        SignTableInfo info = dbHelper.queryInfo(db, Constant.name);
        double v1 = info.getLatitude();
        double v2 = info.getLongitude();
        mGeoFenceClient.removeGeoFence();
        if (circle != null) {
            circle.remove();
        }
        setMyGeoFence(new LatLng(v1, v2));
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
