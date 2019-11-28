package com.zhouzhou.locationgaode;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.zhouzhou.locationgaode.bean.Constant;
import com.zhouzhou.locationgaode.bean.SignTableInfo;

/**
 * author : ZhouZhou
 * e-mail : zhou.zhou@sim.com
 * date   : 19-11-25下午4:03
 * desc   :
 * version: 1.0
 */
public class DBHelper extends SQLiteOpenHelper {

    private Context mContext;
    private static final int VERSION = 1;
    private static final String CREATE_SIGN = "create table Sign(" + " id Integer primary key autoincrement," + " UserName text," + " UserPassword integer)";
    private static final String CREATE_STATUS = "create table Status(" + " id Integer primary key autoincrement," + " Time text," + " Isin text," + " Isout text," + "Lat real," + " Lon real," + " TimeStart text," + "TimeStop)";

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory, VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SIGN);
        db.execSQL(CREATE_STATUS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Sign");
        db.execSQL("drop table if exists Status");
    }

    //查询登录名是否存在
    public String queryName(SQLiteDatabase db, String name) {
        db = this.getReadableDatabase();
        String result = "";
        Cursor cursor = null;
        try {
            if (db != null) {
                cursor = db.query("Sign", new String[]{"UserName"}, "UserName = ?", new String[]{name}, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        result = "true";
                    } else {
                        result = "查无此人";
                    }
                }
            }
        } catch (Exception e) {
            result = e.getMessage();
        } finally {
                db.close();
        }
        return result;
    }

    //查询密码是否正确
    public String queryPassword(SQLiteDatabase db, String password, String name) {
        db = this.getReadableDatabase();
        String result = "";
        Cursor cursor_password = null;
        try {
            cursor_password = db.query("Sign", new String[]{"UserPassword"}, "UserName = ?", new String[]{name}, null, null, null);
            if (cursor_password != null) {
                if (cursor_password.moveToNext()) {
                    String userPassword = cursor_password.getString(cursor_password.getColumnIndex("UserPassword"));
                    if (userPassword.equals(password)) {
                        result = "true";
                    } else {
                        result = "密码错误";
                    }
                }
            }
        } catch (Exception e) {
            result = e.getMessage();
        } finally {
                db.close();
        }
        return result;
    }

    /*
     *@Author: zhouzhou
     *@Date: 19-11-26
     *@Deecribe：添加用户
     *@Params:
     *@Return:
     *@Email：zhou.zhou@sim.com
     */
    public Boolean addUser(SQLiteDatabase db, ContentValues values) {
        db = this.getReadableDatabase();
        Boolean isSuccess = false;
        try{
            db.insert("Sign", null, values);
            if (queryName(db, values.get("UserName").toString()).equals("true")) {
                isSuccess = true;
            }
        }catch (Exception e){

        }finally {
            db.close();
        }
        return isSuccess;
    }
/*
*@Author: zhouzhou
*@Date: 19-11-28
*@Deecribe：添加设置数据
*@Params:
*@Return:
*@Email：zhou.zhou@sim.com
*/
    public void addLatlng(SQLiteDatabase db, ContentValues values){
        db = this.getReadableDatabase();
        String result = "";
        Cursor cursor = null;
        try {
            cursor = db.query("Status",null,"UserName = ?",new String[]{Constant.name},null,null,null);
            if(!cursor.moveToNext()){
                db.insert("Status",null,values);
            }
        }catch (Exception e){
        }finally {
            db.close();
        }

    }

    /*
    *@Author: zhouzhou
    *@Date: 19-11-28
    *@Deecribe：更新定位设置数据
    *@Params:
    *@Return:
    *@Email：zhou.zhou@sim.com
    */
    public void updateSettings(SQLiteDatabase db,SignTableInfo info){
        db = this.getReadableDatabase();
        Cursor cursor = null;
        ContentValues values = new ContentValues();
        values.put("UserName",Constant.name);
        values.put("Time",info.getTimeQuantum());
        values.put("TimeStart",info.getTimeStart());
        values.put("TimeStop",info.getTimeStop());
        values.put("Lat",info.getLatitude());
        values.put("Lng",info.getLongitude());
        try{
            cursor = db.query("Status",null,"UserName = ?",new String[]{Constant.name},null,null,null);
            if(cursor.moveToNext()){
                db.update("Status",values,"UserName = ?",new String[]{Constant.name});
            }else{
                addLatlng(db,values);
            }
        }catch(Exception e){

        }finally {
            db.close();
        }

    }


}
