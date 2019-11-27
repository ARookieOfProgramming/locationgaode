//package broadcast;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.Toast;
//
//import com.amap.api.fence.GeoFence;
//
//
//
//public class MyReceiver extends BroadcastReceiver {
//
//    public static final String GEOFENCE_BROADCAST_ACTION = "com.location.apis.geofencedemo.broadcast";
//    private int status ;
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        if (intent.getAction().equals(GEOFENCE_BROADCAST_ACTION)) {
//            //解析广播内容
//            //获取Bundle
//            Bundle bundle = intent.getExtras();
//            //获取围栏行为：
//            status = bundle.getInt(GeoFence.BUNDLE_KEY_FENCESTATUS);
//            Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
//            //获取自定义的围栏标识：
//            String customId = bundle.getString(GeoFence.BUNDLE_KEY_CUSTOMID);
//            //获取围栏ID:
//            String fenceId = bundle.getString(GeoFence.BUNDLE_KEY_FENCEID);
//            //获取当前有触发的围栏对象：
//            GeoFence fence = bundle.getParcelable(GeoFence.BUNDLE_KEY_FENCE);
//        }
//    }
//
//
//
//}
