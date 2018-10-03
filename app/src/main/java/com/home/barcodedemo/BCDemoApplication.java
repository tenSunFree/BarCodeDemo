package com.home.barcodedemo;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.view.WindowManager;

import com.vondear.rxtool.RxTool;

public class BCDemoApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RxTool.init(this);
    }

    /**
     * 判斷是否開啟了自動亮度調節
     */
    public static boolean isAutoBrightness(Context context) {
        ContentResolver resolver = context.getContentResolver();
        boolean automicBrightness = false;
        try {
            automicBrightness = Settings.System.getInt(resolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return automicBrightness;
    }

    /**
     * 獲取螢幕的亮度
     */
    public static int getScreenBrightness(Context context) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = context.getContentResolver();
        try {
            nowBrightnessValue = android.provider.Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }

    /**
     * 設定當前Activity顯示時的亮度
     * 螢幕亮度最大數值一般為255，各款手機有所不同
     * screenBrightness 的取值範圍在[0,1]之間
     */
    public static void setBrightness(Activity activity, int brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 開啟關閉自動亮度調節
     */
    public static boolean autoBrightness(Context activity, boolean flag) {
        int value = 0;
        if (flag) {
            value = Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC; //開啟
        } else {
            value = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;//關閉
        }
        return Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                value);
    }

    /**
     * 儲存亮度設定狀態，退出app也能保持設定狀態
     */
    public static void saveBrightness(Context context, int brightness) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = android.provider.Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
        android.provider.Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
        resolver.notifyChange(uri, null);
    }
}
