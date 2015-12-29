package com.hansheng.jgpushexample.activity;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hansheng.jgpushexample.R;
import com.hansheng.jgpushexample.utils.ExampleUtil;

import java.util.LinkedHashSet;
import java.util.Set;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 设置 推送相关参数
 */
public class PushSetActivity extends InstrumentedActivity implements OnClickListener {
    private static final String TAG = "JPush";
    
	Button mSetTag;//标签  可为每个用户打多个标签。为安装了应用程序的用户，打上标签。其目的主要是方便开发者根据标签，来批量下发 Push 消息。
	Button mSetAlias;//别名 每个用户只能指定一个别名。为安装了应用程序的用户，取个别名来标识。以后给该用户 Push 消息时，就可以用此别名来指定。
	Button mStyleBasic;//设置通知栏中消息显示的样式 ：基础样式
	Button mStyleCustom;//设置通知栏中消息显示的样式 ：自定义样式
	Button mSetPushTime;//设置通知接收的时间

	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.push_set_dialog);
		init();
		initListener();
	}
	
	private void init(){
	    mSetTag = (Button)findViewById(R.id.bt_tag);
	    mSetAlias = (Button)findViewById(R.id.bt_alias);
	    mStyleBasic = (Button)findViewById(R.id.setStyle1);
	    mStyleCustom = (Button)findViewById(R.id.setStyle2);
	    mSetPushTime = (Button)findViewById(R.id.bu_setTime);
	}
	
	private void initListener(){
		mSetTag.setOnClickListener(this);
		mSetAlias.setOnClickListener(this);
		mStyleBasic.setOnClickListener(this);
		mStyleCustom.setOnClickListener(this);
		mSetPushTime.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_tag:
			setTag();
			break;
		case R.id.bt_alias:
			setAlias();
			break;
		case R.id.setStyle1:
			setStyleBasic();
			break;
		case R.id.setStyle2:
			setStyleCustom();
			break;
		case R.id.bu_setTime:
			Intent intent = new Intent(PushSetActivity.this, SettingActivity.class);
			startActivity(intent);
			break;
		}
	}

    /**
     * 设置标签，可以设置多个，有“,”分割
     */
	private void setTag(){
		EditText tagEdit = (EditText) findViewById(R.id.et_tag);
		String tag = tagEdit.getText().toString().trim();
        // 检查 tag 的有效性
		if (TextUtils.isEmpty(tag)) {
			Toast.makeText(PushSetActivity.this,R.string.error_tag_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		// ","隔开的多个 转换成 Set
		String[] sArray = tag.split(",");
		Set<String> tagSet = new LinkedHashSet<String>();
		for (String sTagItme : sArray) {
			if (!ExampleUtil.isValidTagAndAlias(sTagItme)) {// 校验Tag Alias 只能是数字,英文字母和中文
				Toast.makeText(PushSetActivity.this,R.string.error_tag_gs_empty, Toast.LENGTH_SHORT).show();
				return;
			}
			tagSet.add(sTagItme);
		}
		//调用JPush API设置Tag
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TAGS, tagSet));

	}

    /**
     * 设置别名，只能有一个
     */
	private void setAlias(){
		EditText aliasEdit = (EditText) findViewById(R.id.et_alias);
		String alias = aliasEdit.getText().toString().trim();
		if (TextUtils.isEmpty(alias)) {
			Toast.makeText(PushSetActivity.this,R.string.error_alias_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		if (!ExampleUtil.isValidTagAndAlias(alias)) {
			Toast.makeText(PushSetActivity.this,R.string.error_tag_gs_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		//调用JPush API设置Alias
		mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
	}
	
	
	/**
	 *设置通知提示方式 - 基础属性
	 */
	private void setStyleBasic(){
		BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(PushSetActivity.this);
		builder.statusBarDrawable = R.mipmap.ic_launcher;
		builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为点击后自动消失
		builder.notificationDefaults = Notification.DEFAULT_SOUND;  //设置为铃声（ Notification.DEFAULT_SOUND）或者震动（ Notification.DEFAULT_VIBRATE）  
		JPushInterface.setPushNotificationBuilder(1, builder);
		Toast.makeText(PushSetActivity.this, "Basic Builder - 1", Toast.LENGTH_SHORT).show();
	}
	
	
	/**
	 * 设置通知栏样式 - 定义通知栏Layout
     * 通过customer_notitfication_layout布局文件设置通知栏中的样式，注意这里"2"是指样式的编号，可以在服务端通过设置这个编号去设置客户端接收消息的样式
	 */
	private void setStyleCustom(){
		CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(PushSetActivity.this,R.layout.customer_notitfication_layout,R.id.icon, R.id.title, R.id.text);
		builder.layoutIconDrawable = R.mipmap.ic_launcher;
		builder.developerArg0 = "developerArg2";
		JPushInterface.setPushNotificationBuilder(2, builder);
		Toast.makeText(PushSetActivity.this,"Custom Builder - 2", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}


    
	private static final int MSG_SET_ALIAS = 1001;
	private static final int MSG_SET_TAGS = 1002;

    /**
     * handler 消息接收，用于发起修改别名/标签的请求
     * 也有单独设置的方法  setAlias setTags ，实际运用过程中建议单独设置
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case MSG_SET_ALIAS:
                Log.d(TAG, "Set alias in handler.");
                //注意：null 此次调用不设置此值。（注：不是指的字符串"null"）
                //      "" （空字符串）表示取消之前的设置。
                //每次调用设置有效的别名，覆盖之前的设置。
                JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, mAliasCallback);
                break;
            case MSG_SET_TAGS:
                Log.d(TAG, "Set tags in handler.");
                //每次调用至少设置一个 tag，覆盖之前的设置，不是新增。
                JPushInterface.setAliasAndTags(getApplicationContext(), null, (Set<String>) msg.obj, mTagsCallback);
                break;
            default:
                Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };


//    方法  ：filterValidTag
//    设置 tags 时，如果其中一个 tag 无效，则整个设置过程失败。
//    如果 App 的 tags 会在运行过程中动态设置，并且存在对 JPush SDK tag 规定的无效字符，
//    则有可能一个 tag 无效导致这次调用里所有的 tags 更新失败。
//    这时你可以调用本方法 filterValidTags 来过滤掉无效的 tags，得到有效的 tags，
//    再调用 JPush SDK 的 set tags / alias 方法。
//    public static Set<String> filterValidTags(Set<String> tags)
//    有效的 tag 集合。


    /**
     * 设置标签的回调方法 ，判断设置成功或失败 --标签
     * 0 表示调用成功。
     * 6001	无效的设置，tag/alias 不应参数都为 null
     * 6002	设置超时	建议重试
     * 6003	alias 字符串不合法	有效的别名、标签组成：字母（区分大小写）、数字、下划线、汉字。
     * 6004	alias超长。最多 40个字节	中文 UTF-8 是 3 个字节
     * 6005	某一个 tag 字符串不合法	有效的别名、标签组成：字母（区分大小写）、数字、下划线、汉字。
     * 6006	某一个 tag 超长。一个 tag 最多 40个字节	中文 UTF-8 是 3 个字节
     * 6007	tags 数量超出限制。最多 100个	这是一台设备的限制。一个应用全局的标签数量无限制。
     * 6008	tag 超出总长度限制	总长度最多 1K 字节
     * 6011	10s内设置tag或alias大于10次	短时间内操作过于频繁

     */
    private final TagAliasCallback mTagsCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs ;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    if (ExampleUtil.isConnected(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS, tags), 1000 * 60);
                    } else {
                        Log.i(TAG, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }

            ExampleUtil.showToast(logs, getApplicationContext());
        }

    };


    /**
     * 设置别名的回调方法 ，判断设置成功或失败 ---别名
     */
    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs ;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    if (ExampleUtil.isConnected(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    } else {
                        Log.i(TAG, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }

            ExampleUtil.showToast(logs, getApplicationContext());
        }

    };
}