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
                /* 이미지, 비디오, 오디오 권한 모두 삭제 */
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
     * 사진 선택 도구 열기(단일 모드)
     */
    private void openImagesSelector(int pickImagesMax) {
        if (isSdkTiramisu()) {
            Toast.makeText(this, "현재 플랫폼에서 최대 " + Integer.toString(MediaStore.getPickImagesMaxLimit()) + "개까지 선택 할 수 있습니다.", Toast.LENGTH_SHORT).show();
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
     * 뒤로가기
     * 안드로이드13 플랫폼 API 사용하면 {@link #onBackPressed()}를 정의하더라도 콜백되지 않음.
     * AndroidX로 콜백을 등록하면 {@link #onBackPressed()}와 {@link OnBackPressedCallback}이 같이 콜백된다.
     * @param usePlatformApi
     */
    private void addPredictiveBackPressCallback(boolean usePlatformApi) {

        if (usePlatformApi) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                /*
                안드로이드 13 플랫폼 API로 뒤로가기 콜백 등록
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
            AndroidX로 뒤로가기 콜백 등록
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
     * 퀵 설정 메뉴 추가 및 서비스 등록하는 메소드.
     */
    private void addQuickTile() {
        if (isSdkTiramisu()) {

            startService(new Intent(MainActivity.this, MyQSTileService.class));

            StatusBarManager statusBarManager = getSystemService(StatusBarManager.class);
            Log.d(">>>", "addQuickTile() :: statusBarManager 생성");

            if (statusBarManager == null) return;
            statusBarManager.requestAddTileService(
                    new ComponentName(MainActivity.this, MyQSTileService.class),
                    getString(R.string.app_name),
                    Icon.createWithResource(MainActivity.this, R.drawable.my_default_icon_label),
                    new Executor() {
                        @Override
                        public void execute(Runnable runnable) {
                            Log.d(">>>", "addQuickTile() :: 퀵 설정 추가 성공");
                        }
                    },
                    (resultCodeFailure) -> {
                        Log.d(">>>", "addQuickTile() :: 퀵 설정 추가 실패 " + resultCodeFailure);
                    }
            );
        } else {
            showToast();
        }
    }

    // <editor-fold desc="공통">

    /**
     * 안드로이드 13 버전인지 확인하는 메소드.
     * @return 안드로이드 13 버전이면 {@code true}를 반환한다.
     */
    private boolean isSdkTiramisu() {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU;
    }

    private void showToast() {
        Toast.makeText(this, "현재 버전은 해당 기능을 제공하지 않습니다. SDK Verions: " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG);
    }

    /**
     * 런타임 권한을 요청한다.
     * @param permissions {@link com.example.bucketapptest.Manifest.permission} String 배열.
     */
    private void requestPermission(String[] permissions) {
        requestPermissions(permissions, REQUEST_CODE_PERMISSION);
    }

    /**
     * 앱의 언어 설정 변경하는 메소드. 런타임에 언어 설정을 변경하면 앱이 재시작된다.
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

    // <editor-fold desc="브로드캐스트 리시버">
    private BroadcastReceiver sharedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(">>>", "sharedBroadcastReceiver() :: 브로드캐스트 수신 완료 - 1");
        }
    };

    private BroadcastReceiver privateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(">>>", "privateBroadcastReceiver() :: 브로드캐스트 수신 완료 - 2");
        }
    };
    // </editor-fold>
}