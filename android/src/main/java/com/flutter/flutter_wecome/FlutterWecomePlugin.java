package com.flutter.flutter_wecome;

import androidx.annotation.NonNull;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.tencent.wework.api.IWWAPI;
import com.tencent.wework.api.IWWAPIEventHandler;
import com.tencent.wework.api.WWAPIFactory;
import com.tencent.wework.api.model.BaseMessage;
import com.tencent.wework.api.model.WWAuthMessage;
import com.tencent.wework.api.model.WWMediaConversation;
import com.tencent.wework.api.model.WWMediaFile;
import com.tencent.wework.api.model.WWMediaImage;
import com.tencent.wework.api.model.WWMediaLink;
import com.tencent.wework.api.model.WWMediaMergedConvs;
import com.tencent.wework.api.model.WWMediaText;
import com.tencent.wework.api.model.WWMediaVideo;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.EventChannel.StreamHandler;

/** FlutterWecomePlugin */
public class FlutterWecomePlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {

    private static IWWAPI api;
    private String appid;
    private String agentid;
    private String schema;

    private Context context;
    private MethodChannel channel;

    //    private static int code;//返回错误吗
    //    private static String loginCode;//获取access_code
    //    private static Result result;
    //    private Bitmap bitmap;
    //    private WXMediaMessage message;
    //    private String kind = "session";
    //    private final PluginRegistry.Registrar registrar;
    //    private BroadcastReceiver sendRespReceiver;


    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "flutter_wecome");
        channel.setMethodCallHandler(this);
    }

    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_wecome");
//        channel.setMethodCallHandler(new FlutterWecomePlugin());
//        channel.setMethodCallHandler(new FlutterWecomePlugin(registrar.context(), registrar));
        channel.setMethodCallHandler(new FlutterWecomePlugin());

//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("sendResp");
//        registrar.context().registerReceiver(createReceiver(), intentFilter);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull final Result result) {
        if (call.method.equals("getPlatformVersion")) {
            Log.d("wecomeLog", "getPlatformVersion");
//            Toast.makeText(context, appid, Toast.LENGTH_SHORT).show();
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("register")) {
            appid = call.argument("appid");
            schema = call.argument("schema");
            agentid = call.argument("agentid");
//            Toast.makeText(context, "register"+schema, Toast.LENGTH_SHORT).show();
            api = WWAPIFactory.createWWAPI(context);
            result.success(api.registerApp(schema));
        } else if (call.method.equals("isWecomeInstalled")) {
//            Toast.makeText(context, "Check", Toast.LENGTH_SHORT).show();
            // Check if wecome app installed
            if (api == null) {
                result.success(false);
            } else {
                result.success(api.isWWAppInstalled());
            }
        } else if (call.method.equals("getApiVersion")) {
            result.success(api.isWWAppSupportAPI());
        } else if (call.method.equals("openWecome")) {
//            Toast.makeText(context, "openwecome", Toast.LENGTH_SHORT).show();
            result.success(api.openWWApp());
        } else if (call.method.equals("login")) {
            final WWAuthMessage.Req req = new WWAuthMessage.Req();
            final String state = call.argument("state").toString();
            req.sch = schema;
            req.appId = appid;
            req.agentId = agentid;
            req.state = state;
            api.sendMessage(req, new IWWAPIEventHandler() {
                @Override
                public void handleResp(BaseMessage resp) {
                    if (resp instanceof WWAuthMessage.Resp) {
                        WWAuthMessage.Resp rsp = (WWAuthMessage.Resp) resp;
                        if (rsp.errCode == WWAuthMessage.ERR_CANCEL) {
                            result.success("登录取消");
                            Toast.makeText(context, "登录取消", Toast.LENGTH_SHORT).show();
                        }else if (rsp.errCode == WWAuthMessage.ERR_FAIL) {
                            result.success("登录失败");
                            Toast.makeText(context, "登录失败", Toast.LENGTH_SHORT).show();
                        } else if (rsp.errCode == WWAuthMessage.ERR_OK) {
                            result.success(rsp.code);
                            Toast.makeText(context, "登录成功：", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

//    private static BroadcastReceiver createReceiver() {
//        return new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                System.out.println(intent.getStringExtra("type"));
//                if (intent.getStringExtra("type").equals("SendAuthResp")) {
//                    result.success(intent.getStringExtra("code"));
//                }
//                else if (intent.getStringExtra("type").equals("PayResp")) {
//                    result.success(intent.getStringExtra("code"));
//                }
//                else if (intent.getStringExtra("type").equals("ShareResp")) {
//                    System.out.println(intent.getStringExtra("code"));
//                    result.success(intent.getStringExtra("code"));
//                }
//            }
//        };
//    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        context = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

    }

    @Override
    public void onDetachedFromActivity() {

    }
}
