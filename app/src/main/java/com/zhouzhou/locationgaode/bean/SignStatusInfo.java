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

    private String signInIdentity = "no";//签到标识
    private String signOutIdentity = "no";//签退标识
    private int signInSend = 0;//签到通知标识
    private int signOutSend = 0;//离开通知标识
    private String signInDate = "2000:00:00 00:00";//签到时间
    private String signOutDate = "2000:00:00 00:00";//签退时间
    private String isUpdate = "no";//是否已经更新
    private List<LatLng> pointList = new ArrayList<>();//点的坐标集合
    public List<LatLng> getPointList() {
        return pointList;
    }
    public void setPointList(List<LatLng> pointList) {
        this.pointList = pointList;
    }
    public int getSignOutSend() {
        return signOutSend;
    }

    public void setSignOutSend(int signOutSend) {
        this.signOutSend = signOutSend;
    }



    public String getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(String isUpdate) {
        this.isUpdate = isUpdate;
    }
    public int getSignInSend() {
        return signInSend;
    }

    public void setSignInSend(int signInSend) {
        this.signInSend = signInSend;
    }

    public int signOutSend() {
        return signOutSend;
    }

    public void setsignOutSend(int signOutSend) {
        this.signOutSend = signOutSend;
    }

    public String getSignInIdentity() {
        return signInIdentity;
    }

    public void setSignInIdentity(String signInIdentity) {
        this.signInIdentity = signInIdentity;
    }

    public String getsignOutIdentity() {
        return signOutIdentity;
    }

    public void setsignOutIdentity(String signOutIdentity) {
        this.signOutIdentity = signOutIdentity;
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

    public SignStatusInfo() {
    }


    @Override
    public String toString() {
        return "SignStatusInfo{" + "signInIdentity='" + signInIdentity + '\'' + ", signOutIdentity='" + signOutIdentity + '\'' + ", signInSend='" + signInSend + '\'' + ", signOutSend='" + signOutSend + '\'' + ", nowadays='" + '\'' + ", signInDate='" + signInDate + '\'' + ", signOutDate='" + signOutDate + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SignStatusInfo that = (SignStatusInfo) o;
        return Objects.equals(signInIdentity, that.signInIdentity) && Objects.equals(signOutIdentity, that.signOutIdentity) && Objects.equals(signInSend, that.signInSend) && Objects.equals(signOutSend, that.signOutSend) && Objects.equals(signInDate, that.signInDate) && Objects.equals(signOutDate, that.signOutDate);
    }

    @Override
    public int hashCode() {

        return Objects.hash(signInIdentity, signOutIdentity, signInSend, signOutSend, signInDate, signOutDate);
    }
}
