package com.example.bucketapptest;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import android.Manifest;
import android.app.LocaleManager;
import android.app.StatusBarManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import com.example.bucketapptest.Service.MyQSTileService;

import java.security.KeyStore;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    IntentFilter intentFilter = new IntentFilter("broadcast receiver test");

    // Request code
    private final static int REQUEST_CODE_PERMISSION                        = 0;
    private final static int REQUEST_CODE_IMAGES_SELECTOR_SINGLE_MODE       = 1;
    private final static int REQUEST_CODE_IMAGES_SELECTOR_MULTIPLE_MODE     = 2;

    private final static int PICK_IMAGES_MAX = 3;


    private Button btnNotificationPermission, btnSpeechService, btnImagePermission, btnRecordPermission, btnReadStoragePermission, btnRemovePermission,
            btnOpenImageSelectorSingleMode, btnOpenImageSelectorMultipleMode, btnSetLocalesEn, btnSetLocalesKo, btnAddQuickTile, btnSendBroadcast;

    private int backPressCallbackCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(">>>", "onCreate()");

        addBroadcastReceiver();
        addPredictiveBackPressCallback(false);

        btnNotificationPermission           = (Button) findViewById(R.id.btnNotificationPermission);
        btnSpeechService                    = (Button) findViewById(R.id.btnSpeechService);
        btnImagePermission                  = (Button) findViewById(R.id.btnImagePermission);
        btnRecordPermission                 = (Button) findViewById(R.id.btnRecordPermission);
        btnReadStoragePermission            = (Button) findViewById(R.id.btnReadStoragePermission);
        btnRemovePermission                 = (Button) findViewById(R.id.btnRemovePermission);
        btnOpenImageSelectorSingleMode      = (Button) findViewById(R.id.btnOpenImageSelectorSingleMode);
        btnOpenImageSelectorMultipleMode    = (Button) findViewById(R.id.btnOpenImageSelectorMultipleMode);
        btnSetLocalesEn                     = (Button) findViewById(R.id.btnSetLocalesEn);
        btnSetLocalesKo                     = (Button) findViewById(R.id.btnSetLocalesKo);
        btnAddQuickTile                     = (Button) findViewById(R.id.btnAddQuickTile);
        btnSendBroadcast                     = (Button) findViewById(R.id.btnSendBroadcast);

        btnNotificationPermission.setOnClickListener(this);
        btnSpeechService.setOnClickListener(this);
        btnImagePermission.setOnClickListener(this);
        btnRecordPermission.setOnClickListener(this);
        btnReadStoragePermission.setOnClickListener(this);
        btnRemovePermission.setOnClickListener(this);
        btnOpenImageSelectorSingleMode.setOnClickListener(this);
        btnOpenImageSelectorMultipleMode.setOnClickListener(this);
        btnSetLocalesEn.setOnClickListener(this);
        btnSetLocalesKo.setOnClickListener(this);
        btnAddQuickTile.setOnClickListener(this);
        btnSendBroadcast.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Log.d(">>>", "onBackPressed()");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnNotificationPermission:
                requestPermissionPostNotification();
                break;
            case R.id.btnSpeechService:
                startSpeechService();
                break;
            case R.id.btnImagePermission:
                requestPermission(new String[]{Manifest.permission.READ_MEDIA_IMAGES});
                break;
            case R.id.btnReadStoragePermission:
                requestPermission(new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_VIDEO});
                break;
            case R.id.btnRemovePermission:
                /* ?????????, ?????????, ????????? ?????? ?????? ?????? */
                revokePermission(new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_VIDEO});
                break;
            case R.id.btnOpenImageSelectorSingleMode:
                openImagesSelector(1);
                break;
            case R.id.btnOpenImageSelectorMultipleMode:
                openImagesSelector(PICK_IMAGES_MAX);
                break;
            case R.id.btnSetLocalesEn:
                setLocales("en-US");
                break;
            case R.id.btnSetLocalesKo:
                setLocales("ko-KR");
                break;
            case R.id.btnAddQuickTile:
                addQuickTile();
                break;
            case R.id.btnSendBroadcast:
                sendBroadcast(new Intent().setAction("broadcast receiver test"));
                break;
        }
    }


    private void requestPermissionPostNotification() {
        if (isSdkTiramisu()) {

        } else {
            requestPermission(new String[]{Manifest.permission.POST_NOTIFICATIONS});
        }
    }

    private void revokePermission(String[] permissions) {
        if (isSdkTiramisu()) {
            if (permissions.length == 1) {
                revokeSelfPermissionOnKill(permissions[0]);
            } else {
                revokeSelfPermissionsOnKill(Arrays.asList(permissions));
            }
        } else {
            showToast();
        }
    }

    private void startSpeechService() {
        Intent intent = new Intent(this, SpeechServiceActivity.class);
        startActivity(intent);
    }

    /**
     * ?????? ?????? ?????? ??????(?????? ??????)
     */
    private void openImagesSelector(int pickImagesMax) {
        if (isSdkTiramisu()) {
            Toast.makeText(this, "?????? ??????????????? ?????? " + Integer.toString(MediaStore.getPickImagesMaxLimit()) + "????????? ?????? ??? ??? ????????????.", Toast.LENGTH_SHORT).show();
        }

        if (Build.VERSION_CODES.S >= Build.VERSION.SDK_INT) {
            if (pickImagesMax == 1) {
                Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
                startActivityForResult(intent, REQUEST_CODE_IMAGES_SELECTOR_SINGLE_MODE);
            } else {
                Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
                intent.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, pickImagesMax);
//            intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_IMAGES_SELECTOR_MULTIPLE_MODE);
            }
        }
    }

    private void addBroadcastReceiver() {
        if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
            getApplicationContext().registerReceiver(sharedBroadcastReceiver, intentFilter, RECEIVER_EXPORTED);
            getApplicationContext().registerReceiver(privateBroadcastReceiver, intentFilter, RECEIVER_NOT_EXPORTED);
        }
    }


    /**
     * ????????????
     * ???????????????13 ????????? API ???????????? {@link #onBackPressed()}??? ?????????????????? ???????????? ??????.
     * AndroidX??? ????????? ???????????? {@link #onBackPressed()}??? {@link OnBackPressedCallback}??? ?????? ????????????.
     * @param usePlatformApi
     */
    private void addPredictiveBackPressCallback(boolean usePlatformApi) {

        if (usePlatformApi) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                /*
                ??????????????? 13 ????????? API??? ???????????? ?????? ??????
                 */
                getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                        OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                        () -> {
                            Log.d(">>>", "OnBackInvokedDispatcher()");
                        }
                );
            }
        } else {
            /*
            AndroidX??? ???????????? ?????? ??????
             */
            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    Log.d(">>>", "handleOnBackPressed()");
                }
            };

            this.getOnBackPressedDispatcher().addCallback(
                    this,
                    callback
            );
        }
    }

    /**
     * ??? ?????? ?????? ?????? ??? ????????? ???????????? ?????????.
     */
    private void addQuickTile() {
        if (isSdkTiramisu()) {

            startService(new Intent(MainActivity.this, MyQSTileService.class));

            StatusBarManager statusBarManager = getSystemService(StatusBarManager.class);
            Log.d(">>>", "addQuickTile() :: statusBarManager ??????");

            if (statusBarManager == null) return;
            statusBarManager.requestAddTileService(
                    new ComponentName(MainActivity.this, MyQSTileService.class),
                    getString(R.string.app_name),
                    Icon.createWithResource(MainActivity.this, R.drawable.my_default_icon_label),
                    new Executor() {
                        @Override
                        public void execute(Runnable runnable) {
                            Log.d(">>>", "addQuickTile() :: ??? ?????? ?????? ??????");
                        }
                    },
                    (resultCodeFailure) -> {
                        Log.d(">>>", "addQuickTile() :: ??? ?????? ?????? ?????? " + resultCodeFailure);
                    }
            );
        } else {
            showToast();
        }
    }

    // <editor-fold desc="??????">

    /**
     * ??????????????? 13 ???????????? ???????????? ?????????.
     * @return ??????????????? 13 ???????????? {@code true}??? ????????????.
     */
    private boolean isSdkTiramisu() {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU;
    }

    private void showToast() {
        Toast.makeText(this, "?????? ????????? ?????? ????????? ???????????? ????????????. SDK Verions: " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG);
    }

    /**
     * ????????? ????????? ????????????.
     * @param permissions {@link com.example.bucketapptest.Manifest.permission} String ??????.
     */
    private void requestPermission(String[] permissions) {
        requestPermissions(permissions, REQUEST_CODE_PERMISSION);
    }

    /**
     * ?????? ?????? ?????? ???????????? ?????????. ???????????? ?????? ????????? ???????????? ?????? ???????????????.
     * @param locales
     */
    private void setLocales(String locales) {

        if (isSdkTiramisu()) {
            this.getSystemService(LocaleManager.class
            ).setApplicationLocales(new LocaleList(Locale.forLanguageTag(locales)));
        } else {
            LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(locales);
            // Call this on the main thread as it may require Activity.restart()
            AppCompatDelegate.setApplicationLocales(appLocale);
        }
    }

    // </editor-fold>

    // <editor-fold desc="?????????????????? ?????????">
    private BroadcastReceiver sharedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(">>>", "sharedBroadcastReceiver() :: ?????????????????? ?????? ?????? - 1");
        }
    };

    private BroadcastReceiver privateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(">>>", "privateBroadcastReceiver() :: ?????????????????? ?????? ?????? - 2");
        }
    };
    // </editor-fold>
}