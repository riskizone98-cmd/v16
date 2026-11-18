package id.kyyvjestt.matchcounter;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.webkit.JavascriptInterface;
import android.media.AudioAttributes;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private WebView webView;
    private MediaPlayer introPlayer;
    private SoundPool soundPool;
    private int clickSoundId;
    private boolean clickSoundLoaded = false;
    private MediaPlayer ambientPlayer;
    private final Handler soundHandler = new Handler(Looper.getMainLooper());
    private boolean ambientStarted = false;
    private final Runnable startAmbientSound = () -> {
        if (!isFinishing() && !isDestroyed() && !ambientStarted) {
            startAmbientLoop();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.setStatusBarColor(android.graphics.Color.rgb(11, 17, 32));
        window.setNavigationBarColor(android.graphics.Color.rgb(11, 17, 32));

        // Do not draw the WebView underneath the Android status/navigation bars.
        window.getDecorView().setSystemUiVisibility(0);

        webView = new WebView(this);
        webView.setBackgroundColor(android.graphics.Color.rgb(11, 17, 32));
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(false);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        soundPool = new SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build())
                .build();
        soundPool.setOnLoadCompleteListener((pool, sampleId, status) -> {
            if (sampleId == clickSoundId) {
                clickSoundLoaded = (status == 0);
            }
        });
        clickSoundId = soundPool.load(this, R.raw.action_click_sound, 1);
        webView.addJavascriptInterface(new ActionSoundBridge(), "ActionSound");
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        setContentView(webView);
        playSoundOnLaunch();
        // Start the second sound once, 5 seconds after the app's first launch.
        soundHandler.postDelayed(startAmbientSound, 5_000);
        webView.loadUrl("file:///android_asset/index.html");
    }

    private class ActionSoundBridge {
        @JavascriptInterface
        public void play() {
            runOnUiThread(() -> {
                if (soundPool != null && clickSoundId != 0 && clickSoundLoaded) {
                    soundPool.play(clickSoundId, 1.0f, 1.0f, 1, 0, 1.0f);
                }
            });
        }
    }

    // Play the intro sound on every fresh app launch.
    private void playSoundOnLaunch() {
        introPlayer = MediaPlayer.create(this, R.raw.first_launch_sound);
        if (introPlayer != null) {
            introPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());
            introPlayer.setOnCompletionListener(player -> {
                player.release();
                introPlayer = null;
            });
            introPlayer.setOnErrorListener((player, what, extra) -> {
                player.release();
                introPlayer = null;
                return true;
            });
            introPlayer.start();
        }
    }


    // Play the added sound once after the 5-second delay.
    // It does not loop and is not restarted when the app returns from minimize.
    private void startAmbientLoop() {
        if (ambientStarted) return;
        ambientStarted = true;
        ambientPlayer = MediaPlayer.create(this, R.raw.ambient_loop_sound);
        if (ambientPlayer != null) {
            ambientPlayer.setLooping(false);
            ambientPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());
            ambientPlayer.setOnErrorListener((player, what, extra) -> {
                releaseAmbientPlayer();
                return true;
            });
            ambientPlayer.start();
        }
    }

    private void releaseAmbientPlayer() {
        if (ambientPlayer != null) {
            try { ambientPlayer.stop(); } catch (IllegalStateException ignored) {}
            ambientPlayer.release();
            ambientPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        // Stop the looping sound when minimized/backgrounded. Do not schedule it again.
        soundHandler.removeCallbacks(startAmbientSound);
        releaseAmbientPlayer();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        soundHandler.removeCallbacks(startAmbientSound);
        releaseAmbientPlayer();
        if (introPlayer != null) {
            introPlayer.release();
            introPlayer = null;
        }
        if (soundPool != null) {
            soundPool.setOnLoadCompleteListener(null);
            soundPool.release();
            soundPool = null;
            clickSoundLoaded = false;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
}
