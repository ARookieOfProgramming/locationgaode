package com.zhouzhou.locationgaode.activity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.zhouzhou.locationgaode.DBHelper;
import com.zhouzhou.locationgaode.R;
import com.zhouzhou.locationgaode.bean.Constant;
import com.zhouzhou.locationgaode.bean.SignTableInfo;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.tv_setting_center_latitude)
    TextView tvSettingCenterLatitude;
    @BindView(R.id.tv_setting_center_longitude)
    TextView tvSettingCenterLongitude;
    @BindView(R.id.ll_setting_center)
    LinearLayout llSettingCenter;
    @BindView(R.id.tv_setting_radius)
    TextView tvSettingRadius;
    @BindView(R.id.ll_setting_radius)
    LinearLayout llSettingRadius;
    @BindView(R.id.tv_setting_time_notification_long)
    TextView tvSettingTimeNotificationLong;
    @BindView(R.id.ll_setting_notification)
    LinearLayout llSettingNotification;
    @BindView(R.id.ll_setting_auto)
    LinearLayout llSettingAuto;
    @BindView(R.id.ll_setting_review)
    TextView llSettingReview;
    @BindView(R.id.btn_settings_save)
    Button btnSettingsSave;
    @BindView(R.id.tv_setting_time_auto_start)
    TextView tvSettingTimeAutoStart;
    @BindView(R.id.tv_setting_time_auto_stop)
    TextView tvSettingTimeAutoStop;

    private DBHelper dbHelper = null;
    private SQLiteDatabase db = null;

    private SignTableInfo info = new SignTableInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        dbHelper = new DBHelper(this, "SignIn", null);
        init();
    }

    /*
     *@Author: zhouzhou
     *@Date: 19-11-28
     *@Deecribe：恢复数据
     *@Params:
     *@Return:
     *@Email：zhou.zhou@sim.com
     */
    private void init() {
        Cursor query = db.query("Status", new String[]{"Lat"}, "UserName = ?", new String[]{Constant.name}, null, null, null);
        tvSettingCenterLatitude.setText(query.getString(query.getColumnIndex("Lat")));
    }
    @OnClick(R.id.ll_setting_center)
    public void onLlSettingCenterClicked() {
        String[] choices = {"手动输入", "地图标点"};
        new AlertDialog.Builder(this).setTitle("请选择设置方式").setSingleChoiceItems(choices, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    dialog.dismiss();
                    dialoglatlng();
                }
                if (which == 1) {
                    //跳转到地图去标点，标完点回来
                    dialog.dismiss();
                }
            }
        }).create().show();
    }

    @OnClick(R.id.ll_setting_radius)
    public void onLlSettingRadiusClicked() {
        dialogRadius();
    }

    @OnClick(R.id.ll_setting_notification)
    public void onLlSettingNotificationClicked() {
        setTimeQuantum();
    }

    @OnClick(R.id.ll_setting_review)
    public void onLlSettingReviewClicked() {
        Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_settings_save)
    public void onViewClicked() {
        writeToDatabase();
    }
    @OnClick(R.id.tv_setting_time_auto_start)
    public void onTvSettingTimeAutoStartClicked() {
        myTimePicker(Constant.TYPE2);

    }

    @OnClick(R.id.tv_setting_time_auto_stop)
    public void onTvSettingTimeAutoStopClicked() {
        myTimePicker(Constant.TYPE3);
    }
    /*
     *@Author: zhouzhou
     *@Date: 19-11-28
     *@Deecribe：输入经纬度
     *@Params:
     *@Return:
     *@Email：zhou.zhou@sim.com
     */
    private void dialoglatlng() {
        View view = getLayoutInflater().inflate(R.layout.dialog_input_latlng, null);
        final EditText et_lat = (EditText) view.findViewById(R.id.et_dialog_first);
        final EditText et_lng = (EditText) view.findViewById(R.id.et_dialog_second);
        new AlertDialog.Builder(SettingsActivity.this).setTitle("请输入经纬度").setView(view).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (et_lat.getText().length() > 0 && et_lng.getText().length() > 0) {
                    double lat = Double.parseDouble(et_lat.getText().toString());
                    double lng = Double.parseDouble(et_lng.getText().toString());
                    //判断输入是否合法
                    String result = inspectLatlng(lat, lng);
                    if (result.equals("true")) {
                        //经纬度添加进info，等待保存
                        info.setLatitude(lat);
                        info.setLongitude(lng);
                        //展示输入的数据
                        tvSettingCenterLatitude.setText(tvSettingCenterLatitude.getText().toString() + et_lat.getText());
                        tvSettingCenterLongitude.setText(tvSettingCenterLongitude.getText().toString() + et_lng.getText());
                    } else {
                        Toast.makeText(SettingsActivity.this, result, Toast.LENGTH_SHORT).show();
                    }

                    dialog.dismiss();
                } else {
                    Toast.makeText(SettingsActivity.this, "设置失败,未填写完整", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }
    /*
     *@Author: zhouzhou
     *@Date: 19-11-28
     *@Deecribe：检查输入的经纬度是否合法
     *@Params:
     *@Return:
     *@Email：zhou.zhou@sim.com
     */
    private String inspectLatlng(double lat, double lng) {
        int count = 0;
        String result = "true";
        if (lat > 90.0 || lat < 0.0) {
            result = "纬度应小于90大于0";
            count++;
        }
        if (lng > 180.0 || lng < 0.0) {
            result = "经度应小于180且大于0";
            count++;
        }
        if (count == 2) {
            result = "纬度应小于90大于0;经度应小于180且大于0";
        }
        return result;
    }

    /*
     *@Author: zhouzhou
     *@Date: 19-11-28
     *@Deecribe：设置打卡半径
     *@Params:
     *@Return:
     *@Email：zhou.zhou@sim.com
     */
    private void dialogRadius() {
        View view = getLayoutInflater().inflate(R.layout.dialog_input_radius, null);
        final EditText viewRadius = view.findViewById(R.id.et_dialog_radius);
        viewRadius.setHint("打卡半径：");
        new AlertDialog.Builder(this).setTitle("请输入打卡半径").setView(view).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                double radius = Double.parseDouble(viewRadius.getText().toString());
                tvSettingRadius.setText(tvSettingRadius.getText().toString() + viewRadius.getText() + " 米");
                info.setRadius(radius);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).create().show();
    }

    /*
     *@Author: zhouzhou
     *@Date: 19-11-28
     *@Deecribe：设置离开提醒时间
     *@Params:
     *@Return:
     *@Email：zhou.zhou@sim.com
     */
    private void setTimeQuantum() {
        myTimePicker(Constant.TYPE1);
    }

    /*
     *@Author: zhouzhou
     *@Date: 19-11-28
     *@Deecribe：时间选择器,
     *@Params:
     *@Return:
     *@Email：zhou.zhou@sim.com
     */
    private void myTimePicker(final int type) {
        LocalTime now = LocalTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Date date = new Date();
                String time = new SimpleDateFormat("HH:mm").format(date);
                if (type == Constant.TYPE1) {
                    //set入info
                    info.setTimeQuantum(time);
                    tvSettingTimeNotificationLong.setText(tvSettingTimeNotificationLong.getText().toString() + hourOfDay + "时" + minute + "分");
                }
                if (type == Constant.TYPE2) {
                    info.setTimeStart(time);
                    tvSettingTimeAutoStart.setText(tvSettingTimeAutoStart.getText().toString() + time);
                }
                if (type == 3){
                    info.setTimeStop(time);
                    tvSettingTimeAutoStop.setText(tvSettingTimeAutoStop.getText().toString() + time);
                }
            }
        }, hour, minute, true).show();
    }

    /*
     *@Author: zhouzhou
     *@Date: 19-11-28
     *@Deecribe：点击保存后写入数据库
     *@Params:
     *@Return:
     *@Email：zhou.zhou@sim.com
     */
    private void writeToDatabase() {
        dbHelper.updateSettings(db, info);
    }
}
