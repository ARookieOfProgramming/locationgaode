package com.zhouzhou.locationgaode.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.zhouzhou.locationgaode.LocationWork;
import java.util.concurrent.TimeUnit;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

/**
 * author : ZhouZhou
 * e-mail : zhou.zhou@sim.com
 * date   : 19-12-4下午1:14
 * desc   :
 * version: 1.0
 */
public class MyService extends Service {
private PeriodicWorkRequest periodicWorkRequest = null;
    public MyService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }
private int i = 0;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "ff", Toast.LENGTH_SHORT).show();
        //1.约束条件
//        Constraints myconstraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        //2.传入参数
//        Data data = new Data.Builder().putString("demo","helloworld").build();
        //3.构造work
        periodicWorkRequest = new PeriodicWorkRequest
                .Builder(LocationWork.class, 15, TimeUnit.MINUTES).build();
//                .setConstraints(myconstraints).setInputData(data).build();
        //OneTimeWorkRequest httpwork = new OneTimeWorkRequest.Builder(LocationWork.class).setConstraints(myconstraints).setInputData(data).build();
        //4.放入执行队列
        WorkManager.getInstance().enqueue(periodicWorkRequest);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Timer timer = new Timer();
//                TimerTask timerTask = new TimerTask() {
//                    @Override
//                    public void run() {
//
//                        Intent intent1 = new Intent();
//                        intent1.setAction("MYBROADCAST_RECIVER");
//                        intent1.setPackage("com.zhouzhou.locationgaode");
//                        intent1.putExtra("test", "test" + i);
//                        sendBroadcast(intent1);
//                        i++;
//                    }
//                };
//                timer.schedule(timerTask,0,3 * 1000);
//            }
//        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
