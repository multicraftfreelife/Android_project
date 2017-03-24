package mobi.MultiCraft;


import android.app.NativeActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.appodeal.ads.Appodeal;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class GameActivity extends NativeActivity {
    static {
        System.loadLibrary("gnustl_shared");
        System.loadLibrary("multicraft");
    }

    private int messageReturnCode;
    private String messageReturnValue;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        String appKey = "babb31173891382d382bb4bf2bfdc5b6ea736a80f42e5c6b";
        Appodeal.disableNetwork(this, "cheetah");
        Appodeal.initialize(this, appKey, Appodeal.INTERSTITIAL| Appodeal.NON_SKIPPABLE_VIDEO);
        Appodeal.cache(this, Appodeal.INTERSTITIAL | Appodeal.NON_SKIPPABLE_VIDEO);
        startAd(true);
        messageReturnCode = -1;
        messageReturnValue = "";
        makeFullScreen();
    }


    public void makeFullScreen() {
        if (Build.VERSION.SDK_INT >= 19) {
            this.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            makeFullScreen();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startAd(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        makeFullScreen();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        stopAd();
    }

    @Override
    public void onBackPressed() {
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                String text = data.getStringExtra("text");
                messageReturnCode = 0;
                messageReturnValue = text;
            } else {
                messageReturnCode = 1;
            }
        }
    }

    private void startAd(boolean isFirstTime) {
        int initialDelay = isFirstTime ? 1 : 300;
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                showAppodeal();
            }
        }, initialDelay, 300, TimeUnit.SECONDS);
    }

    public void showAppodeal() {
        Appodeal.show(this, Appodeal.INTERSTITIAL | Appodeal.NON_SKIPPABLE_VIDEO);
    }

    public void copyAssets() {
    }

    public void showDialog(String acceptButton, String hint, String current, int editType) {
        Intent intent = new Intent(this, DialogHalper.class);
        Bundle params = new Bundle();
        params.putString("acceptButton", acceptButton);
        params.putString("hint", hint);
        params.putString("current", current);
        params.putInt("editType", editType);
        intent.putExtras(params);
        startActivityForResult(intent, 101);
        messageReturnValue = "";
        messageReturnCode = -1;
    }

    public int getDialogState() {
        return messageReturnCode;
    }

    public String getDialogValue() {
        messageReturnCode = -1;
        return messageReturnValue;
    }

    public float getDensity() {
        return getResources().getDisplayMetrics().density;
    }

    public int getDisplayHeight() {
        return getResources().getDisplayMetrics().heightPixels;
    }

    public int getDisplayWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }

}