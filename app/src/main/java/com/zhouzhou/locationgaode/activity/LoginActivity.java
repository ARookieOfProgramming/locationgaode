package com.zhouzhou.locationgaode.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.zhouzhou.locationgaode.DBHelper;
import com.zhouzhou.locationgaode.R;
import com.zhouzhou.locationgaode.bean.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et_login_name)
    EditText etLoginName;
    @BindView(R.id.et_login_password)
    EditText etLoginPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_login_forget_password)
    TextView tvLoginForgetPassword;
    @BindView(R.id.tv_login_register)
    TextView tvLoginRegister;
    @BindView(R.id.ck_login_agreement)
    CheckBox ckLoginAgreement;


    private DBHelper dbHelper = null;
    private SQLiteDatabase db = null;
    private SharedPreferences.Editor editor = null;
    private SharedPreferences spf = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        //初始化数据库
        dbHelper = new DBHelper(this);
        db = dbHelper.getReadableDatabase();

        rememberUser();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void rememberUser() {
        spf = getSharedPreferences("user_info", MODE_PRIVATE);
        String name = spf.getString("name", "");
        String password = spf.getString("password", "");
        if (name.length() > 0 && password.length() > 0) {
            etLoginName.setText(name);
            etLoginPassword.setText(password);
            //queryUser(db);
        }

    }

    private void queryUser(SQLiteDatabase db) {
        String name = etLoginName.getText().toString();
        String password = etLoginPassword.getText().toString();
        String resultName = dbHelper.queryName(db,name);
        if (resultName.equals("true")) {
            String resultPassword = dbHelper.queryPassword(db, password, name);
            if (resultPassword.equals("true")){
                //结果正确,记住登录名，密码
                editor = getSharedPreferences("user_info", MODE_PRIVATE).edit();
                editor.putString("name", etLoginName.getText().toString());
                editor.putString("password", etLoginPassword.getText().toString());
                editor.apply();
                Constant.name = etLoginName.getText().toString();
                //打开地图
                startActivity(new Intent(this, MapActivity.class));
            }else if (resultPassword.equals("密码错误")){
                Toast.makeText(this, resultPassword, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, resultPassword, Toast.LENGTH_SHORT).show();
            }
        }
        else if (resultName.equals("查无此人")){
            Toast.makeText(this, resultName, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, resultName, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(1, 2, data);
        Bundle bundle = data.getBundleExtra("user");
        String name = bundle.getString("name");
        String password = bundle.getString("password");
        if (name.length() > 0 && password.length() > 0){
            etLoginName.setText(name);
            etLoginPassword.setText(password);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @OnClick(R.id.btn_login)
    public void onBtnLoginClicked() {
        if(etLoginName.getText().length() > 0 && etLoginPassword.getText().length() > 0){
            queryUser(db);
        }else {
            Toast.makeText(this, "请输入登录名或密码", Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.tv_login_forget_password)
    public void onTvLoginForgetPasswordClicked() {
    }

    @OnClick(R.id.tv_login_register)
    public void onTvLoginRegisterClicked() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

}
