package com.zhouzhou.locationgaode.bean;

/**
 * author : ZhouZhou
 * e-mail : zhou.zhou@sim.com
 * date   : 19-11-27下午4:39
 * desc   :
 * version: 1.0
 */
public class Constant {

    //当前位置相对于围栏的位置关系广播标识
    public static final int IN = 1;
    public static final int  OUT = 2;
    public static final int STAYED = 3;

    //发送通知的区分标识
    public static final int TYPE1 = 4;
    public static final int TYPE2 = 5;
    public static final int TYPE3 = 6;

    //通知的channel
    public static final String ID = "T";

    //当前的登录名
    public static  String name = "";

    //message的标识
    public static final int WHAT = 1;

    //activity的回调结果码
    public static final int resultCode = 9;

    //时间格式的区分码
    public static final int timeSimple = 10;
    public static final int timeFull = 11;
    public static final int timeHour = 12;
}
