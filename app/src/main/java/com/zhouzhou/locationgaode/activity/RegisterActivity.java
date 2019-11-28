package com.zhouzhou.locationgaode.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zhouzhou.locationgaode.DBHelper;
import com.zhouzhou.locationgaode.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.et_register_name)
    EditText etRegisterName;
    @BindView(R.id.et_register_password)
    EditText etRegisterPassword;
    @BindView(R.id.et_register_confirm)
    EditText etRegisterConfirm;
    @BindView(R.id.btn_register)
    Button btnRegister;
    private DBHelper dbHelper = null;
    private SQLiteDatabase db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        dbHelper = new DBHelper(this, "SignIn", null);
        db = dbHelper.getReadableDatabase();
    }

    /*
    *@Author: zhouzhou
    *@Date: 19-11-26
    *@Deecribe：注册名是否已存在
    *@Params:
    *@Return:
    *@Email：zhou.zhou@sim.com
    */
    private String confirmName() {
        String result = "";
        String name = etRegisterName.getText().toString();
        String password = etRegisterPassword.getText().toString();
        String passwordConfirm = etRegisterConfirm.getText().toString();
        if (name.length() == 0 || password.length() == 0 || passwordConfirm.length() == 0) {
            result = "请输入用户名或密码";
            return result;
        }
        if (dbHelper.queryName(db, name).equals("true")) {
            result = "用户名已存在";
        } else {
            if (password.equals(passwordConfirm)) {
                result = "true";
            } else {
                result = "两次密码不一致";
            }
        }
        return result;
    }

    public void registerUser() {
        ContentValues values = new ContentValues();
        values.put("UserName", etRegisterName.getText().toString());
        values.put("UserPassword", etRegisterPassword.getText().toString());
        //数据写入数据库
        Boolean isSuccess = dbHelper.addUser(db, values);
        //成功则返回登录界面
        if (isSuccess) {
            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            Bundle bundle = new Bundle();
            bundle.putCharSequence("name", etRegisterName.getText());
            bundle.putCharSequence("password", etRegisterPassword.getText());
            intent.putExtra("user", bundle);
            //setResult(2,intent);
            startActivity(intent);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        String result = dbHelper.queryOriginName(db);
        if (result.equals("true")){
            ContentValues values = new ContentValues();
            values.put("Time","00:10");
            values.put("Radius","100");
            values.put("Issign","false");
            values.put("Lat","31.00");
            values.put("Lon","137.00");
            values.put("TimeStart","08:00");
            values.put("TimeStop","19:00");
            dbHelper.addOriginData(db,values);
        }else{
            Toast.makeText(this, "初始化数据失败", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_register)
    public void onViewClicked() {
        String result = confirmName();
        if (result.equals("true")) {
            registerUser();
        } else {
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        }
    }


}
