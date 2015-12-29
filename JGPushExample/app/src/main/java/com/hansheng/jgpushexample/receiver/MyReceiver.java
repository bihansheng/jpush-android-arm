package com.hansheng.jgpushexample.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.hansheng.jgpushexample.activity.MainActivity;
import com.hansheng.jgpushexample.activity.TestActivity;
import com.hansheng.jgpushexample.utils.ExampleUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 收到推送，通过广播的方式，转发给开发者App，这样开发者就可以灵活地进行处理
 *
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 *
 * 可以在androidManif。xml文件中配接受的广播类型
 */
public class MyReceiver extends BroadcastReceiver {
    //相关字段是说明
//    public static final String ACTION_CONNECTION_CHANGE;      // 动作：链接状态发生该表
//    public static final String ACTION_REGISTRATION_ID;        // 动作：通过注册id来发布消息
//    public static final String ACTION_STOPPUSH;               // 动作：停止接收推送
//    public static final String ACTION_RESTOREPUSH;            // 动作：恢复接收推送
//    public static final String ACTION_MESSAGE_RECEIVED;       // 动作：接收到自定义消息，自定义消息不会展示在通知栏，完全要开发者写代码去处理
//    public static final String ACTION_NOTIFICATION_RECEIVED;  // 动作：接收到通知
//    public static final String ACTION_NOTIFICATION_OPENED;    // 动作： 通知被打开,一般情况下，用户不需要配置此 receiver action。
//    public static final String ACTION_NOTIFICATION_RECEIVED_PROXY;
//    public static final String ACTION_STATUS;
//    public static final String ACTION_ACTIVITY_OPENDED;        // 动作 ：消息被点击
//    public static final String EXTRA_CONNECTION_CHANGE;        // 参数： 链接状态
//    public static final String EXTRA_REGISTRATION_ID;          //  参数：SDK 向 JPush Server 注册所得到的注册 全局唯一的 ID ，可以通过此 ID向对应的客户端发送消息和通知。
//    public static final String EXTRA_APP_KEY;
//    public static final String EXTRA_NOTIFICATION_DEVELOPER_ARG0;
//    public static final String EXTRA_NOTIFICATION_TITLE;       //通知界面上的“通知标题”字段。对应 API 通知内容的 title 字段。
//    public static final String EXTRA_PUSH_ID;                  //推送ID
//    public static final String EXTRA_MSG_ID;                   //唯一标识消息的 ID, 可用于上报统计等。
//    public static final String EXTRA_NOTI_TYPE;                //消息类型
//    public static final String EXTRA_ALERT;                    //通知内容。alert 字段
//    public static final String EXTRA_MESSAGE;                  //自定义消息内容，消息内容的 message 字段
//    public static final String EXTRA_CONTENT_TYPE;             //内容类型，消息内容的 content_type 字段。保存服务器推送下来的内容类型。
//    public static final String EXTRA_TITLE;                    //参数:  消息内容的 title 字段。
//    public static final String EXTRA_EXTRA;                    //附加字段。这是个 JSON 字符串， extras 字段,对应消息设置中的“可选设置”中的内容
//    public static final String EXTRA_NOTIFICATION_ID;          //参数：通知栏的Notification ID，可以用于清除Notification
//
//    public static final String EXTRA_ACTIVITY_PARAM;
//    public static final String EXTRA_RICHPUSH_FILE_PATH;        //富媒体通消息推送下载后的文件路径和文件名。
//    public static final String EXTRA_RICHPUSH_FILE_TYPE;
//    public static final String EXTRA_RICHPUSH_HTML_PATH;        //富媒体通知推送下载的HTML的文件路径,用于展现WebView。
//    public static final String EXTRA_RICHPUSH_HTML_RES;
//    public static final String EXTRA_STATUS;
//    private static final Integer a;
//    public static final String ACTION_RICHPUSH_CALLBACK;        //动作 ：用户收到到RICH PUSH CALLBACK ，可以根据
//    JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

	private static final String TAG = "JPush>>>>>>";

	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {

            //唯一的该设备的标识
            //也可以能够通过 getRegistrationID 方法获取    只有当应用程序成功注册到 JPush 的服务器时才返回对应的值，否则返回空字符串。
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            //可以通过 RegistrationID 进行点对点推送
            //可以通过 RegistrationID 来推送消息和通知， 参考文档 Push API v2， 当 receiver_type = 5 并且设置 receiver_value 为 RegistrationID 时候即可根据 RegistrationID 推送。
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {

            // SDK 对自定义消息，只是传递，不会有任何界面上的展示。
            //如果开发者想推送自定义消息 Push，则需要在 AndroidManifest.xml 里配置此 Receiver action，并且在自己写的 BroadcastReceiver 里接收处理。
            Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        	processCustomMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {

            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
        	
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {

            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
            
        	//打开自定义的Activity
        	Intent i = new Intent(context, TestActivity.class);
        	i.putExtras(bundle);
        	//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
        	context.startActivity(i);
        	
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {

            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
        	
        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {

        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
        	Log.w(TAG, "[MyReceiver]" + intent.getAction() +"连接状态发生改变 "+connected);

        } else {
        	Log.d(TAG, "[MyReceiver] 未知类型 " + intent.getAction());
        }
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {//通知 ID
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){//连接状态
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {//回调动作参数
					Log.i(TAG, "This message has no Extra data");
					continue;
				}
				try {
					JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it =  json.keys();
					while (it.hasNext()) {
						String myKey = it.next().toString();
						sb.append("\nkey:" + key + ", value: [" +
								myKey + " - " +json.optString(myKey) + "]");
					}
				} catch (JSONException e) {
					Log.e(TAG, "Get message extra JSON error!");
				}
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
	
	//将得到自定义消息的数据通过广播形式传递给具体的Activity处理
	private void processCustomMessage(Context context, Bundle bundle) {
		if (MainActivity.isForeground) {
			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
            //判断参数是否可用，如果可以就将extras设置到msgIntent的MainActivity.KEY_EXTRAS中
			if (!ExampleUtil.isEmpty(extras)) {
				try {
					JSONObject extraJson = new JSONObject(extras);
					if (null != extraJson && extraJson.length() > 0) {
						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
					}
				} catch (JSONException e) {
				}
			}
			context.sendBroadcast(msgIntent);
		}
	}
}
