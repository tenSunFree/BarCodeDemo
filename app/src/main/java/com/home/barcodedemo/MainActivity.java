package com.home.barcodedemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vondear.rxfeature.tool.RxBarCode;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_WRITE_SETTINGS = 1;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private ImageView barCodeImageView, barCodeImageView2, barCodeImageView3;
    private TextView barCodeTextView, barCodeTextView2, barCodeTextView3;
    private IndicatorSeekBar adjustBrightnessIndicatorSeekBar;
    private CheckBox automaticBrightnessCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        judgeTheRequestForWriteSettingsPermissions();
        replaceActionBar();
        initializationBarCodeView();
        initializationAutomaticBrightnessCheckBox();
        initializationScheduleIndicatorSeekBar();
    }

    /** 判斷是否取得WRITE_SETTINGS的權限, 如果沒有就執行請求 */
    private void judgeTheRequestForWriteSettingsPermissions() {
        if (!Settings.System.canWrite(this)) {
            requestWriteSettings();
        }
    }

    /** 請求取得WRITE_SETTINGS的權限 */
    private void requestWriteSettings() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
    }

    private void replaceActionBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);                                      // 打開 up button
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,             // 實作 drawer toggle 並放入 toolbar
                R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void initializationBarCodeView() {
        barCodeImageView = findViewById(R.id.barCodeImageView);
        barCodeImageView2 = findViewById(R.id.barCodeImageView2);
        barCodeImageView3 = findViewById(R.id.barCodeImageView3);
        barCodeTextView = findViewById(R.id.barCodeTextView);
        barCodeTextView2 = findViewById(R.id.barCodeTextView2);
        barCodeTextView3 = findViewById(R.id.barCodeTextView3);
    }

    private void initializationAutomaticBrightnessCheckBox() {
        automaticBrightnessCheckBox = findViewById(R.id.automaticBrightnessCheckBox);
        automaticBrightnessCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (automaticBrightnessCheckBox.isChecked()) {
                    BCDemoApplication.autoBrightness(MainActivity.this, true);
                } else {
                    BCDemoApplication.autoBrightness(MainActivity.this, false);
                }
            }
        });
    }

    private void initializationScheduleIndicatorSeekBar() {
        adjustBrightnessIndicatorSeekBar = findViewById(R.id.adjustBrightnessIndicatorSeekBar);
        adjustBrightnessIndicatorSeekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                // currentSeekBarProgress = seekParams.progress;
                Log.d("more", "progress: " + seekParams.progress);
                BCDemoApplication.setBrightness(MainActivity.this, seekParams.progress);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                Log.d("more", "onStopTrackingTouch");
                Log.d("more", "onStopTrackingTouch, getProgress: " + seekBar.getProgress());
                BCDemoApplication.saveBrightness(MainActivity.this, seekBar.getProgress());

            }
        });
    }

    /** 初始化條形碼以及讓亮度部分的呈現與本機同步 */
    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                generateBarCode();
            }
        }, 100);
        automaticBrightnessCheckBox.setChecked(BCDemoApplication.isAutoBrightness(MainActivity.this));
        adjustBrightnessIndicatorSeekBar.setProgress(BCDemoApplication.getScreenBrightness(MainActivity.this));
    }

    private void generateBarCode() {
        RxBarCode.builder(barCodeTextView.getText().toString()).
                backColor(getResources().getColor(R.color.transparent)).
                codeColor(getResources().getColor(R.color.black)).
                codeWidth(barCodeImageView.getWidth()).
                codeHeight(barCodeImageView.getHeight()).
                into(barCodeImageView);
        RxBarCode.builder(barCodeTextView2.getText().toString()).
                backColor(getResources().getColor(R.color.transparent)).
                codeColor(getResources().getColor(R.color.black)).
                codeWidth(barCodeImageView2.getWidth()).
                codeHeight(barCodeImageView2.getHeight()).
                into(barCodeImageView2);
        RxBarCode.builder(barCodeTextView3.getText().toString()).
                backColor(getResources().getColor(R.color.transparent)).
                codeColor(getResources().getColor(R.color.black)).
                codeWidth(barCodeImageView3.getWidth()).
                codeHeight(barCodeImageView3.getHeight()).
                into(barCodeImageView3);
    }

    /** 判斷使用者如果沒有允許WRITE_SETTINGS的權限, 會再次進入請求權限的畫面 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            if (!Settings.System.canWrite(this)) {
                requestWriteSettings();
                Toast.makeText(MainActivity.this, "請先允許修改系統設定", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
