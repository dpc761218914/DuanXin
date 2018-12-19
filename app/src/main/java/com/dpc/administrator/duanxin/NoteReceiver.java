package com.dpc.administrator.duanxin;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/7/9.
 */

public class NoteReceiver extends BroadcastReceiver {

    private SharedPreferencesHelper sharedPreferencesHelper;

    private static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    public void onReceive(Context context, Intent intent) {

        sharedPreferencesHelper = new SharedPreferencesHelper(
                context, "anhua");


        String action = intent.getAction();
        //判断广播消息
        if (action.equals(SMS_RECEIVED_ACTION)){
            Bundle bundle = intent.getExtras();
            //如果不为空
            if (bundle != null){
                //将pdus里面的内容转化成Object[]数组
                Object pdusData[] = (Object[]) bundle.get("pdus");
                //解析短信
                SmsMessage[] msg = new SmsMessage[pdusData.length];
                for (int i = 0;i < msg.length;i++){
                    byte pdus[] = (byte[]) pdusData[i];
                    msg[i] = SmsMessage.createFromPdu(pdus);
                }
                StringBuffer content = new StringBuffer();//获取短信内容
                StringBuffer phoneNumber = new StringBuffer();//获取地址
                StringBuffer receiveData = new StringBuffer();//获取时间
                //分析短信具体参数
                for (SmsMessage temp : msg){
                    content.append(temp.getMessageBody());
                    phoneNumber.append(temp.getOriginatingAddress());
                    receiveData.append(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS")
                            .format(new Date(temp.getTimestampMillis())));
                }

               String myNumber="+86"+sharedPreferencesHelper.getSharedPreference("number", "暂未设置告警号码").toString().trim();

                //判断是否是告警电话
                if(phoneNumber.toString().equals(myNumber)){
                    Toast.makeText(context,"这是告警"+phoneNumber.toString()+content+receiveData, Toast.LENGTH_LONG).show();//短信内容
                    //如果是告警电话，需要有语音提示
                    Intent intent1 = new Intent(context,PlayingMusicServices.class);
                    context.startService(intent1);


                }
                /**
                 * 这里还可以进行好多操作，比如我们根据手机号进行拦截（取消广播继续传播）等等
                 */
                else{
                    //Toast.makeText(context,"着不是告警"+phoneNumber.toString()+content+receiveData, Toast.LENGTH_LONG).show();//短信内容
                }

            }
        }
    }
}