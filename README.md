# DuanXin
告警短信语音提示APP

### 一、安卓App实现的功能
针对特定的提醒短信，夜间不容易轻易看到，因此需要实现的功能是夜间收到某条固定号码来的短信时，需要进行语音提醒，例如，我这里进行紧急告警提醒。

软件运行流程：
       软件运行流程，在本机上安装好这软件，项目启动后，可以在首页输入框配置需要监测的电话号码，例如这里配置155的号码后，通过155的手机发送短信给我本机，手机在收到短信的时候进行语音提示，用户看到短信后，可手动关闭语音提示。

软件运行效果：
![S81219-090552.jpg](https://upload-images.jianshu.io/upload_images/2227968-ad87bbe45b36c834.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![S81219-090600.jpg](https://upload-images.jianshu.io/upload_images/2227968-372cfe4821222fa8.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


### 二、关键代码
##### 2.1 监听手机短信
      通过监听短信的广播去分析短信的内容。
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
##### 2.2 出发播放音乐事件
      写一个播放音乐的services，播放本地音乐
      public class PlayingMusicServices extends Service {
     private MediaPlayer mp;
    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        // 开始播放音乐
        mp.start();
        // 音乐播放完毕的事件处理
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                // 循环播放
                try {
                    mp.start();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        // 播放音乐时发生错误的事件处理
        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mp, int what, int extra) {
                // TODO Auto-generated method stub
                // 释放资源
                try {
                    mp.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }
        });
        super.onStart(intent, startId);
    }
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        // 初始化音乐资源
        try {
            // 创建MediaPlayer对象
            mp = new MediaPlayer();
            // 将音乐保存在res/raw/xingshu.mp3,R.java中自动生成{public static final int xingshu=0x7f040000;}
            mp = MediaPlayer.create(PlayingMusicServices.this, R.raw.music1);
            // 在MediaPlayer取得播放资源与stop()之后要准备PlayBack的状态前一定要使用MediaPlayer.prepeare()
            mp.prepare();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        super.onCreate();
    }
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        // 服务停止时停止播放音乐并释放资源
        mp.stop();
        mp.release();
        super.onDestroy();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    }



##### 2.3 后台运行程序，监听短信的程序是在后台运行的，所以使用receiver进行后台配置，AndroidManifest.xml的配置如下
       <application
        android:allowBackup="true"
        android:icon="@mipmap/icon"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <!-- action:name = 的名称是固定的 -->
        <receiver android:name=".NoteReceiver">
            <intent-filter android:priority="1000">
                <action android:name="android.provider.Telephony.SMS_RECEIVED"/>
            </intent-filter>
        </receiver>
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <service
            android:name=".PlayingMusicServices"
            android:exported="true"
            android:process=":remote">
        </service>
    </application>

### 三、项目源代码
https://github.com/dpc761218914/DuanXin








