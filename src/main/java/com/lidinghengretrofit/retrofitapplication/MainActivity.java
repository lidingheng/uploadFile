package com.lidinghengretrofit.retrofitapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import http.AppConfig;
import http.BaseSubscriber;
import http.ExceptionHandle;
import http.RetrofitClient;
import okhttp3.ResponseBody;
import util.MyApplication;

public class MainActivity extends AppCompatActivity {

    public EditText login_name;
    public EditText login_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login_name = (EditText)findViewById(R.id.login_name);
        login_password = (EditText)findViewById(R.id.login_password);
    }


    public void login(View view){
        String strName = login_name.getText().toString();
        String strPassword = login_password.getText().toString();

        Map<String, Object> map=new HashMap<>();
        map.put("loginName",strName);
        map.put("password",strPassword);
        map.put("phoneCode","86");
        map.put("pushRegisterId","190e35f7e07d3e773fa");
        RetrofitClient.getIntance(MainActivity.this).createBaseApi().login(AppConfig.LOGIN, map, MyApplication.getIntance().getLanguage(), new BaseSubscriber<ResponseBody>(MainActivity.this) {
            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {
                Toast.makeText(MainActivity.this, "Error: "+e.message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(ResponseBody responseBody) {

                //Toast.makeText(MainActivity.this, "success: "+responseBody.toString() , Toast.LENGTH_SHORT).show();

                String result = null;
                try {
                    result = responseBody.string();
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = (String) jsonObject.get("msg");
                    Toast.makeText(MainActivity.this, "  "+msg, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
