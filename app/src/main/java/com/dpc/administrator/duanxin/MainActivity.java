package com.dpc.administrator.duanxin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;

public class MainActivity extends AppCompatActivity {

    private SharedPreferencesHelper sharedPreferencesHelper;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private EditText et_number;
    private TextView tv_number;
    private ButtonFlat btn_test_music;
    private ButtonRectangle btn_stop_music;
    private ButtonRectangle btn_set_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //实例化SP
        sharedPreferencesHelper = new SharedPreferencesHelper(MainActivity.this, "anhua");

        tv_number = (TextView) findViewById(R.id.tv_number);
        btn_test_music = (ButtonFlat) findViewById(R.id.btn_test_music);
        btn_stop_music = (ButtonRectangle) findViewById(R.id.btn_stop_music);
        et_number = (EditText) findViewById(R.id.et_number);
        btn_set_number = (ButtonRectangle) findViewById(R.id.btn_set_number);


        tv_number.setText("告警号码为："+sharedPreferencesHelper.getSharedPreference("number", "暂未设置告警号码").toString().trim());
        btn_test_music.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //显示启动服务
                Intent intent = new Intent(MainActivity.this, PlayingMusicServices.class);
                startService(intent);
            }
        });

        btn_stop_music.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //关闭服务
                Intent intent = new Intent(MainActivity.this, PlayingMusicServices.class);
                stopService(intent);
                Toast.makeText(MainActivity.this,"语音提示已关闭！", Toast.LENGTH_LONG).show();//短信内容
            }
        });

        btn_set_number.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String number=et_number.getText().toString();
                sharedPreferencesHelper.put("number", number);
                tv_number.setText("告警号码为："+sharedPreferencesHelper.getSharedPreference("number", "暂未设置告警号码").toString().trim());
                Toast.makeText(MainActivity.this,"更新告警号码成功！", Toast.LENGTH_LONG).show();//短信内容
            }
        });

    }
}
