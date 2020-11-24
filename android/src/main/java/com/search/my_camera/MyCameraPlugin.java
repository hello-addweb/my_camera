package com.search.my_camera;

import android.Manifest;
import com.google.android.gms.common.api.CommonStatusCodes;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import android.content.Intent;
import java.util.List;
import android.net.Uri;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import io.flutter.plugin.common.BinaryMessenger;
import androidx.annotation.NonNull;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.io.ByteArrayOutputStream;
import com.search.my_camera.OcrCaptureActivity;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import static com.uuzuche.lib_zxing.activity.CodeUtils.RESULT_SUCCESS;
import static com.uuzuche.lib_zxing.activity.CodeUtils.RESULT_TYPE;


public class MyCameraPlugin implements MethodCallHandler,PluginRegistry.ActivityResultListener{
    private Activity activity;
    private static final String CHANNEL = "my_camera";
    private static final int RC_OCR_READ = 8020;
    private MethodCall methodCall;
    private int callerId;

    private int REQUEST_CODE = 100;
    public Result result = null;
    public Result result1 = null;
    private MethodChannel channel;
    public  MycameraDelegate delegate;
    private MethodChannel.Result pendingResult;
    private ActivityPluginBinding activityBinding;
    private int REQUEST_IMAGE = 101;
   /* private MyCameraPlugin(
            Registrar registrar) {
        this.activity = registrar.activity();
    }*/

    public static void registerWith(Registrar registrar) {
      ZXingLibrary.initDisplayOpinion(registrar.activity());
       // MyCameraPlugin plugin = new MyCameraPlugin(registrar.activity());
//registrar.addActivityResultListener();
        MyCameraPlugin plugin = new MyCameraPlugin(registrar.activity());
        //methodChannel.setMethodCallHandler(plugin);
        registrar.addActivityResultListener(plugin);
      //  activityBinding.addRequestPermissionsResultListener(plugin);
        //registrar.addRequestPermissionsResultListener(delegate);
        registrar
                .platformViewRegistry()
                .registerViewFactory(
                        "plugins.flutter.io/my_camera", new MyCameraFactory(registrar));

       // final MethodChannel channel = new MethodChannel(registrar.messenger(), "my_camera");
        MethodChannel channel = new MethodChannel(registrar.messenger(), "my_camera");
       // channel.setMethodCallHandler(new MyCameraPlugin(plugin));
        channel.setMethodCallHandler(plugin);
        //channel.setMethodCallHandler(plugin);




    }
    private void setup(
            final BinaryMessenger messenger,

//            final Activity activity,
            final PluginRegistry.Registrar registrar,
            final ActivityPluginBinding activityBinding) {
      //  this.activity = activity;
      //  this.delegate = new MycameraDelegate(activity);
        channel = new MethodChannel(messenger, CHANNEL);
        channel.setMethodCallHandler(this);
        if (registrar != null) {
            // V1 embedding setup for activity listeners.
            // registrar.addActivityResultListener(delegate);
            //registrar.addRequestPermissionsResultListener(delegate);
        } else {
            // V2 embedding setup for activity listeners.
            activityBinding.addActivityResultListener(delegate);
            activityBinding.addRequestPermissionsResultListener(delegate);
        }
    }


    public MyCameraPlugin(Activity activity) {
    //   final BinaryMessenger messenger;
        this.activity = activity;

     this.delegate = new MycameraDelegate(activity);
        CheckPermissionUtils.initPermission(this.activity);
    }

    private static class MethodResultWrapper implements MethodChannel.Result {
        private MethodChannel.Result methodResult;
        private Handler handler;

        MethodResultWrapper(MethodChannel.Result result) {
            methodResult = result;
            handler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void success(final Object result) {
            handler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            methodResult.success(result);
                        }
                    });
        }

        @Override
        public void error(
                final String errorCode, final String errorMessage, final Object errorDetails) {
            handler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            methodResult.error(errorCode, errorMessage, errorDetails);
                        }
                    });
        }

        @Override
        public void notImplemented() {
            handler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            methodResult.notImplemented();
                        }
                    });
        }
    }
    @Override
    public void onMethodCall(MethodCall call, final Result result) {
        if (call.method.equals("checkForPermission")) {
            checkForPermission(result);
        } else {
            result.notImplemented();
        }
        //this.result=result;

       // MycameraDelegate.result=result;
       // MethodChannel.Result resultnew = new MethodResultWrapper(result);
       MethodChannel.Result result1 = new MethodResultWrapper(result);


// MethodResultWrapper.Result result1 =result;

       switch (call.method) {
            case "scan":
                delegate.scan(call, result);
                break;

            case "read":
                delegate.read(call, result);
                break;

            case "face":
                delegate.face(call, result);
                break;
        }

    }
    private void finishWithError(String errorCode, String errorMessage) {
        if (pendingResult == null) {
            // TODO - Return an error.
            return;
        }

        pendingResult.error(errorCode, errorMessage, null);
        clearMethodCallAndResult();
    }

    public boolean onActivityResult(int code, int resultCode, Intent intent) {

        //System.out.println("hello : "+intent.getExtras());
      //  System.out.println("hello : "+resul);
      //  System.out.println("hello : "+barcode);
        if (code == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && intent != null) {
                Bundle secondBundle = intent.getBundleExtra("secondBundle");
                if (secondBundle != null) {
                    try {
                        CodeUtils.AnalyzeCallback analyzeCallback = new CustomAnalyzeCallback(this.result, intent);
                        CodeUtils.analyzeBitmap(secondBundle.getString("path"), analyzeCallback);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        if (bundle.getInt(RESULT_TYPE) == RESULT_SUCCESS) {
                            String barcode = bundle.getString(CodeUtils.RESULT_STRING);
                            System.out.println("barcode: "+barcode);

                            MyCamera.result.success(barcode);
                        }else{
                            this.result.success(null);
                        }
                    }
                }
            } else {
                String errorCode = intent != null ? intent.getStringExtra("ERROR_CODE") : null;
                if (errorCode != null) {
                    this.result.error(errorCode, null, null);
                }
            }
            return true;
        } else if (code == REQUEST_IMAGE) {
            if (intent != null) {
                Uri uri = intent.getData();
                try {
                    CodeUtils.AnalyzeCallback analyzeCallback = new CustomAnalyzeCallback(this.result, intent);
                    CodeUtils.analyzeBitmap(ImageUtil.getImageAbsolutePath(activity, uri), analyzeCallback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }else if (code == RC_OCR_READ) {
            if (code == CommonStatusCodes.SUCCESS) {
                if (intent != null) {
                    ArrayList<MyTextBlock> blocks = intent
                            .getParcelableArrayListExtra(OcrCaptureActivity.OBJECT);
                    if (blocks != null && !blocks.isEmpty()) {
                        List<Map<String, Object>> list = new ArrayList<>();
                        for (MyTextBlock block : blocks) {
                            list.add(block.getMap());
                        }
                        System.out.println("hello : "+list);

                        finishWithSuccess(list);
                        return true;
                    }
                }
                finishWithError("No text recognized, intent data is null", null);
            } else if (resultCode == CommonStatusCodes.ERROR) {
                if (intent != null) {
                    Exception e = intent.getParcelableExtra(OcrCaptureActivity.ERROR);
                    finishWithError(e.getMessage(), null);
                } else {
                    finishWithError("Intent is null (the camera permission may not be granted)", null);
                }
            }
        }
        return false;

    }



    private void checkForPermission(final MethodChannel.Result result) {
        Dexter.withActivity(activity)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        result.success(report.areAllPermissionsGranted());
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }
    private void finishWithSuccess(Object object) {
        if (pendingResult == null) {
            // TODO - Return an error.
            return;
        }

        pendingResult.success(object);
        clearMethodCallAndResult();
    }
    private void clearMethodCallAndResult() {
        callerId = 0;
        methodCall = null;
        pendingResult = null;
    }

}
