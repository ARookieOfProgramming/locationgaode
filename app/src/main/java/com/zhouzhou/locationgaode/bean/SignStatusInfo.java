package com.zhouzhou.locationgaode.bean;

import com.amap.api.maps2d.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * author : ZhouZhou
 * e-mail : zhou.zhou@sim.com
 * date   : 19-12-2下午4:45
 * desc   :签到状态实体
 * version: 1.0
 */
public class SignStatusInfo {

    private String userName;//用户名
    private String signInIdentity = "no";//签到标识
    private String sighOutIdentity = "no";//签退标识
    private String signInSend = "no";//签到通知标识
    private String getSignOutSend = "no";//离开通知标识
    private String nowadays = "2000-00-00";//当天日期
    private String signInDate = "2000-00-00";//签到时间
    private String signOutDate = "2000-00-00";//签退时间
    private List<LatLng> pointList = new ArrayList<>();//储存的点坐标

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getSignInIdentity() {
        return signInIdentity;
    }

    public void setSignInIdentity(String signInIdentity) {
        this.signInIdentity = signInIdentity;
    }

    public String getSighOutIdentity() {
        return sighOutIdentity;
    }

    public void setSighOutIdentity(String sighOutIdentity) {
        this.sighOutIdentity = sighOutIdentity;
    }

    public String getSignInSend() {
        return signInSend;
    }

    public void setSignInSend(String signInSend) {
        this.signInSend = signInSend;
    }

    public String getGetSignOutSend() {
        return getSignOutSend;
    }

    public void setGetSignOutSend(String getSignOutSend) {
        this.getSignOutSend = getSignOutSend;
    }

    public String getNowadays() {
        return nowadays;
    }

    public void setNowadays(String nowadays) {
        this.nowadays = nowadays;
    }

    public String getSignInDate() {
        return signInDate;
    }

    public void setSignInDate(String signInDate) {
        this.signInDate = signInDate;
    }

    public String getSignOutDate() {
        return signOutDate;
    }

    public void setSignOutDate(String signOutDate) {
        this.signOutDate = signOutDate;
    }

    public List<LatLng> getPointList() {
        return pointList;
    }

    public void setPointList(List<LatLng> pointList) {
        this.pointList = pointList;
    }



    public SignStatusInfo() {
    }


    @Override
    public String toString() {
        return "SignStatusInfo{" + "signInIdentity='" + signInIdentity + '\'' + ", sighOutIdentity='" + sighOutIdentity + '\'' + ", signInSend='" + signInSend + '\'' + ", getSignOutSend='" + getSignOutSend + '\'' + ", nowadays='" + nowadays + '\'' + ", signInDate='" + signInDate + '\'' + ", signOutDate='" + signOutDate + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SignStatusInfo that = (SignStatusInfo) o;
        return Objects.equals(signInIdentity, that.signInIdentity) && Objects.equals(sighOutIdentity, that.sighOutIdentity) && Objects.equals(signInSend, that.signInSend) && Objects.equals(getSignOutSend, that.getSignOutSend) && Objects.equals(nowadays, that.nowadays) && Objects.equals(signInDate, that.signInDate) && Objects.equals(signOutDate, that.signOutDate);
    }

    @Override
    public int hashCode() {

        return Objects.hash(signInIdentity, sighOutIdentity, signInSend, getSignOutSend, nowadays, signInDate, signOutDate);
    }
}
