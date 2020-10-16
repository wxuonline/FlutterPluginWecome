package com.flutter.flutter_wecome_example;

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

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.EventChannel.StreamHandler;



public class MainActivity extends FlutterActivity implements MethodCallHandler {
//    /** Plugin registration. */
//    public static void registerWith(Registrar registrar) {
//        final MethodChannel channel = new MethodChannel(registrar.messenger(), "wecome");
//        channel.setMethodCallHandler(new MainActivity());
//    }
//
//    @Override
//    public void onMethodCall(MethodCall call, Result result) {
//        if (call.method.equals("getPlatformVersion")) {
//            result.success("Android " + android.os.Build.VERSION.RELEASE);
//        } else if (call.method.equals("calculate")) {
//            int a = call.argument("a");
//            int b = call.argument("b");
//            int r = a + b;
//            result.success("" + r);
//        } else if (call.method.equals("register")) {
//            APPID = call.argument("appid");
//            Toast.makeText(MainActivity.this, APPID, Toast.LENGTH_SHORT).show();
//            iwwapi = WWAPIFactory.createWWAPI(this);
//            result.success(iwwapi.registerApp(SCHEMA));
//        } else {
//            result.notImplemented();
//        }
//    }
//}

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
    private final PluginRegistry.Registrar registrar;
    private BroadcastReceiver sendRespReceiver;

    private MainActivity(Context ctx, Registrar registrar) {
        this.registrar = registrar;
        context = ctx;
    }

    public static int getCode() {
        return code;
    }

    public static void setCode(int value) {
        code = value;
    }

    public static String getLoginCode() {
        return loginCode;
    }

    public static void setLoginCode(String value) {
        loginCode = value;
    }

//    private Handler handler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message osMessage) {
//            WWAuthMessage.Req request = new WWAuthMessage.Req();
//            request.scene = kind.equals("timeline")
//                    ? WWAuthMessage.Req.WXSceneTimeline
//                    : kind.equals("favorite")
//                    ? WWAuthMessage.Req.WXSceneFavorite
//                    : WWAuthMessage.Req.WXSceneSession;
//
//            if (bitmap != null) {
//                Bitmap thumbBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
//                message.thumbData = convertBitmapToByteArray(thumbBitmap, true);
//            }
//            switch (osMessage.what) {
//                case 0:
//                    request.transaction = String.valueOf(System.currentTimeMillis());
//                    request.message = message;
//                    api.sendReq(request);
//                    break;
//                case 1:
//                    if (bitmap == null) {
//                        Toast.makeText(context, "图片路径错误", Toast.LENGTH_SHORT).show();
//                        break;
//                    }
//                    WXImageObject imageObject = new WXImageObject(bitmap);
//                    message.mediaObject = imageObject;
//                    request.transaction = String.valueOf(System.currentTimeMillis());
//                    request.message = message;
//                    api.sendReq(request);
//                    break;
//                case 2:
//                    request.transaction = String.valueOf(System.currentTimeMillis());
//                    request.message = message;
//                    api.sendReq(request);
//                    break;
//                case 3:
//                    request.transaction = String.valueOf(System.currentTimeMillis());
//                    request.message = message;
//                    api.sendReq(request);
//                    break;
//                default:
//                    break;
//            }
//            return false;
//        }
//    });

    /** Plugin registration. */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "wechat");
        channel.setMethodCallHandler(new MainActivity(registrar.context(), registrar));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("sendResp");
        registrar.context().registerReceiver(createReceiver(), intentFilter);
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        }
        else if (call.method.equals("register")) {
//            appid = call.argument("appid");
//            Toast.makeText(MainActivity.this, appid, Toast.LENGTH_SHORT).show();

            api = WWAPIFactory.createWWAPI(this);
            result.success(api.registerApp(APPID));
        }
        // Check if wechat app installed
        else if (call.method.equals("isWecomeInstalled")) {
            if (api == null) {
                result.success(false);
            } else {
                result.success(api.isWWAppInstalled());
            }
        }
        else if (call.method.equals("getApiVersion")) {
            result.success(api.isWWAppSupportAPI());
        }
        else if (call.method.equals("openWechat")) {
            result.success(api.openWWApp());
        }
//        else if (call.method.equals("share")) {
//            final String kind = call.argument("kind");
//            final String to = call.argument("to");
//            final String coverUrl = call.argument("coverUrl");
//            WWAuthMessage.Req request = new WWAuthMessage.Req();
//            message = new WXMediaMessage();
//            request.scene = to.equals("timeline")
//                    ? WWAuthMessage.Req.WXSceneTimeline
//                    : to.equals("favorite")
//                    ? WWAuthMessage.Req.WXSceneFavorite
//                    : WWAuthMessage.Req.WXSceneSession;
//            switch (kind) {
//                case "text":
//                    WXTextObject textObject = new WXTextObject();
//                    final String text = call.argument("text");
//                    textObject.text = text;
//                    message.mediaObject = textObject;
//                    message.description = text;
//                    request.transaction = String.valueOf(System.currentTimeMillis());
//                    request.message = message;
//                    api.sendReq(request);
//                    break;
//                case "music":
//                    WXMusicObject musicObject = new WXMusicObject();
//                    musicObject.musicUrl = call.argument("resourceUrl").toString();
//                    musicObject.musicDataUrl = call.argument("resourceUrl").toString();
//                    musicObject.musicLowBandDataUrl = call.argument("resourceUrl").toString();
//                    musicObject.musicLowBandUrl = call.argument("resourceUrl").toString();
//                    message = new WXMediaMessage();
//                    message.mediaObject = musicObject;
//                    message.title = call.argument("title").toString();
//                    message.description = call.argument("description").toString();
//                    //网络图片或者本地图片
//                    new Thread() {
//                        public void run() {
//                            Message osMessage = new Message();
//                            bitmap = GetBitmap(coverUrl);
//                            osMessage.what = 2;
//                            handler.sendMessage(osMessage);
//                        }
//                    }.start();
//                    break;
//                case "video":
//                    WXVideoObject videoObject = new WXVideoObject();
//                    videoObject.videoUrl = call.argument("resourceUrl").toString();
//                    videoObject.videoLowBandUrl = call.argument("resourceUrl").toString();
//                    message = new WXMediaMessage();
//                    message.mediaObject = videoObject;
//                    message.title = call.argument("title").toString();
//                    message.description = call.argument("description").toString();
//                    //网络图片或者本地图片
//                    new Thread() {
//                        public void run() {
//                            Message osMessage = new Message();
//                            bitmap = GetBitmap(coverUrl);
//                            osMessage.what = 3;
//                            handler.sendMessage(osMessage);
//                        }
//                    }.start();
//                    break;
//                case "image":
//                    message = new WXMediaMessage();
//                    message.title = call.argument("title").toString();
//                    message.description = call.argument("description").toString();
//                    final String imageResourceUrl = call.argument("resourceUrl");
//                    //网络图片或者本地图片
//                    new Thread() {
//                        public void run() {
//                            Message osMessage = new Message();
//                            bitmap = GetBitmap(imageResourceUrl);
//                            osMessage.what = 1;
//                            handler.sendMessage(osMessage);
//                        }
//                    }.start();
//                    break;
//                case "webpage":
//                    WXWebpageObject webpageObject = new WXWebpageObject();
//                    webpageObject.webpageUrl = call.argument("url").toString();
//                    message = new WXMediaMessage();
//                    message.mediaObject = webpageObject;
//                    message.title = call.argument("title").toString();
//                    message.description = call.argument("description").toString();
//                    //网络图片或者本地图片
//                    new Thread() {
//                        public void run() {
//                            Message osMessage = new Message();
//                            bitmap = GetBitmap(coverUrl);
//                            osMessage.what = 0;
//                            handler.sendMessage(osMessage);
//                        }
//                    }.start();
//                    break;
//            }
//        }
        else if (call.method.equals("login")) {
            final WWAuthMessage.Req req = new WWAuthMessage.Req();
            req.sch = SCHEMA;
            req.appId = APPID;
            req.agentId = AGENTID;
            req.state = STATE;
            api.sendMessage(req, new IWWAPIEventHandler() {
                @Override
                public void handleResp(BaseMessage resp) {
                    if (resp instanceof WWAuthMessage.Resp) {
                        WWAuthMessage.Resp rsp = (WWAuthMessage.Resp) resp;
                        if (rsp.errCode == WWAuthMessage.ERR_CANCEL) {
                            Toast.makeText(MainActivity.this, "登录取消", Toast.LENGTH_SHORT).show();
                        }else if (rsp.errCode == WWAuthMessage.ERR_FAIL) {
                            Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        } else if (rsp.errCode == WWAuthMessage.ERR_OK) {
                            Toast.makeText(MainActivity.this, "登录成功：" + rsp.code,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
        else {
            result.notImplemented();
        }
    }

//    public Bitmap GetBitmap(String url) {
//        Bitmap bitmap = null;
//        InputStream in = null;
//        BufferedOutputStream out = null;
//        try {
//            in = new BufferedInputStream(new URL(url).openStream(), 1024);
//            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
//            out = new BufferedOutputStream(dataStream, 1024);
//            copy(in, out);
//            out.flush();
//            byte[] data = dataStream.toByteArray();
//            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//            return bitmap;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

//    public byte[] convertBitmapToByteArray(final Bitmap bitmap, final boolean needRecycle) {
//        ByteArrayOutputStream output = new ByteArrayOutputStream();
//        bitmap.compress(CompressFormat.PNG, 100, output);
//        if (needRecycle) {
//            bitmap.recycle();
//        }
//
//        byte[] result = output.toByteArray();
//        try {
//            output.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return result;
//    }
//
//    private static void copy(InputStream in, OutputStream out) throws IOException {
//        byte[] b = new byte[1024];
//        int read;
//        while ((read = in.read(b)) != -1) {
//            out.write(b, 0, read);
//        }
//    }

    private static BroadcastReceiver createReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println(intent.getStringExtra("type"));
                if (intent.getStringExtra("type").equals("SendAuthResp")) {
                    result.success(intent.getStringExtra("code"));
                }
                else if (intent.getStringExtra("type").equals("PayResp")) {
                    result.success(intent.getStringExtra("code"));
                }
                else if (intent.getStringExtra("type").equals("ShareResp")) {
                    System.out.println(intent.getStringExtra("code"));
                    result.success(intent.getStringExtra("code"));
                }
            }
        };
    }
}