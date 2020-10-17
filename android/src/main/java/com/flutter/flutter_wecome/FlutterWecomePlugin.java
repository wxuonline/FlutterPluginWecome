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
//    private static Registrar _registrar;

    private static IWWAPI api;
    private static String APPID = "wxbaaa22057237762a";
    private static String AGENTID = "1000091";
    private static String SCHEMA = "wwauthbaaa22057237762a000091";
    private static String STATE = "hengan";

    private static int code;//返回错误吗
    private static String loginCode;//获取access_code
    private static Result result;
    private Context context;
    //    private String appid;
    private Bitmap bitmap;
    //    private WXMediaMessage message;
    private String kind = "session";
//    private final PluginRegistry.Registrar registrar;
    private BroadcastReceiver sendRespReceiver;



    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;

//    private FlutterWecomePlugin(Context ctx, Registrar registrar) {
////        this.registrar = registrar;
////        context = ctx;
//    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "wecome");
        channel.setMethodCallHandler(this);
    }

    // This static function is optional and equivalent to onAttachedToEngine. It supports the old
    // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
    // plugin registration via this function while apps migrate to use the new Android APIs
    // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
    //
    // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
    // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
    // depending on the user's project. onAttachedToEngine or registerWith must both be defined
    // in the same class.
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "wecome");
//        channel.setMethodCallHandler(new FlutterWecomePlugin());
//        channel.setMethodCallHandler(new FlutterWecomePlugin(registrar.context(), registrar));
        channel.setMethodCallHandler(new FlutterWecomePlugin());

//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("sendResp");
//        registrar.context().registerReceiver(createReceiver(), intentFilter);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals("getPlatformVersion")) {
            Log.d("TAG", "getPlatformVersion");
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("register")) {
            // appid = call.argument("appid");
             Toast.makeText(context, APPID, Toast.LENGTH_SHORT).show();
            api = WWAPIFactory.createWWAPI(context);
            result.success(api.registerApp(APPID));

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
