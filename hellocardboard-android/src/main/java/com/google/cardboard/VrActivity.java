/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cardboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.spherical.SphericalGLSurfaceView;
import com.google.cardboard.video.spherical.OurSphericalGLSurfaceView;

/**
 * A Google Cardboard VR NDK sample application.
 *
 * <p>This is the main Activity for the sample application. It initializes a GLSurfaceView to allow
 * rendering.
 */
// TODO(b/184737638): Remove decorator once the AndroidX migration is completed.
@SuppressWarnings("deprecation")
public class VrActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
  static {
    System.loadLibrary("cardboard_jni");
  }

  public static final String SPHERICAL_STEREO_MODE_EXTRA = "spherical_stereo_mode";
  public static final String SPHERICAL_STEREO_MODE_MONO = "mono";
  public static final String SPHERICAL_STEREO_MODE_TOP_BOTTOM = "top_bottom";
  public static final String SPHERICAL_STEREO_MODE_LEFT_RIGHT = "left_right";

  private static final String TAG = VrActivity.class.getSimpleName();
  private static final String url = "https://storage.googleapis.com/exoplayer-test-media-1/360/sphericalv2.mp4";


  //Uri uri = Uri.parse("https://storage.googleapis.com/exoplayer-test-media-1/360/sphericalv2.mp4");
  //https://storage.googleapis.com/exoplayer-test-media-1/360/iceland0.ts
  //https://storage.googleapis.com/exoplayer-test-media-1/360/congo.mp4
  //https://storage.googleapis.com/exoplayer-test-media-1/360/sphericalv2.mp4
  // Permission request codes
  private static final int PERMISSIONS_REQUEST_CODE = 2;

  // Opaque native pointer to the native CardboardApp instance.
  // This object is owned by the VrActivity instance and passed to the native methods.
  private long nativeApp;

  private GLSurfaceView glView;
  //private SphericalGLSurfaceView sphericalGLSurfaceView;
  private OurSphericalGLSurfaceView sphericalGLSurfaceView;


  private SimpleExoPlayer player;

  private OurPlayerView playerView;

  protected String userAgent;

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public void onCreate(Bundle savedInstance) {
    Log.e("TYPE_ON_CREATE", "Tipo TYPE_ON_CREATE!!");
    super.onCreate(savedInstance);
    userAgent  = Util.getUserAgent(this, "PES360");
    setContentView(R.layout.activity_vr);
    nativeApp = nativeOnCreate(getAssets());


    playerView = findViewById(R.id.player_view);

    sphericalGLSurfaceView = (OurSphericalGLSurfaceView) playerView.getVideoSurfaceView();

    //glView = findViewById(R.id.surface_view);



    //String sphericalStereoMode = getIntent().getStringExtra(SPHERICAL_STEREO_MODE_EXTRA);
    String sphericalStereoMode = SPHERICAL_STEREO_MODE_TOP_BOTTOM;
    if (sphericalStereoMode != null) {
      int stereoMode;
      switch (sphericalStereoMode) {
        case SPHERICAL_STEREO_MODE_MONO:
          Log.e("TYPE_ON_CREATE", "SPHERICAL_STEREO_MODE_MONO");
          stereoMode = C.STEREO_MODE_MONO;
          break;
        case SPHERICAL_STEREO_MODE_TOP_BOTTOM:
          Log.e("TYPE_ON_CREATE", "SPHERICAL_STEREO_MODE_TOP_BOTTOM");
          stereoMode = C.STEREO_MODE_TOP_BOTTOM;
          break;
        case SPHERICAL_STEREO_MODE_LEFT_RIGHT:
          Log.e("TYPE_ON_CREATE", "STEREO_MODE_LEFT_RIGHT");
          stereoMode = C.STEREO_MODE_LEFT_RIGHT;
          break;
        default:
          Log.e("TYPE_ON_CREATE", "error_unrecognized_stereo_mode");
          showToast(R.string.error_unrecognized_stereo_mode);
          finish();
          return;
      }
      sphericalGLSurfaceView.setDefaultStereoMode(stereoMode);
    }
//    Renderer renderer = new Renderer();
//    sphericalGLSurfaceView.setRenderer(renderer);
    sphericalGLSurfaceView.setRenderMode(OurSphericalGLSurfaceView.RENDERMODE_CONTINUOUSLY);
//    sphericalGLSurfaceView.setOnTouchListener(
//        (v, event) -> {
//          if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            // Signal a trigger event.
//            sphericalGLSurfaceView.queueEvent(
//                () -> {
//                  nativeOnTriggerEvent(nativeApp);
//                });
//            return true;
//          }
//          return false;
//        });




    playerView.requestFocus();

//    glView.setEGLContextClientVersion(2);
//    Renderer renderer = new Renderer();
//    glView.setRenderer(renderer);
//    glView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
//    glView.setOnTouchListener(
//        (v, event) -> {
//          if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            // Signal a trigger event.
//            glView.queueEvent(
//                () -> {
//                  nativeOnTriggerEvent(nativeApp);
//                });
//            return true;
//          }
//          return false;
//        });

    // TODO(b/139010241): Avoid that action and status bar are displayed when pressing settings
    // button.
    setImmersiveSticky();
    View decorView = getWindow().getDecorView();
    decorView.setOnSystemUiVisibilityChangeListener(
        (visibility) -> {
          if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
            setImmersiveSticky();
          }
        });

    // Forces screen to max brightness.
    WindowManager.LayoutParams layout = getWindow().getAttributes();
    layout.screenBrightness = 1.f;
    getWindow().setAttributes(layout);

    // Prevents screen from dimming/locking.
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  }

  private void showToast(int messageId) {
    showToast(getString(messageId));
  }

  private void showToast(String message) {
    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
  }

  @Override
  protected void onPause() {
    super.onPause();

    if (Build.VERSION.SDK_INT <= 23) {
      releasePlayer();
    }
    nativeOnPause(nativeApp);
    sphericalGLSurfaceView.onPause();
  }

  @Override
  protected void onResume() {
    super.onResume();

    // On Android P and below, checks for activity to READ_EXTERNAL_STORAGE. When it is not granted,
    // the application will request them. For Android Q and above, READ_EXTERNAL_STORAGE is optional
    // and scoped storage will be used instead. If it is provided (but not checked) and there are
    // device parameters saved in external storage those will be migrated to scoped storage.
    if (VERSION.SDK_INT < VERSION_CODES.Q && !isReadExternalStorageEnabled()) {
      requestPermissions();
      return;
    }

    if ((Build.VERSION.SDK_INT <= 23 || player == null)) {
      initializePlayer();
    }

    sphericalGLSurfaceView.onResume();
    nativeOnResume(nativeApp);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    if (Build.VERSION.SDK_INT <= 23) {
      releasePlayer();
    }
    nativeOnDestroy(nativeApp);
    nativeApp = 0;
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    if (hasFocus) {
      setImmersiveSticky();
    }
  }

  private class Renderer implements GLSurfaceView.Renderer {
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
      nativeOnSurfaceCreated(nativeApp);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
      nativeSetScreenParams(nativeApp, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
      nativeOnDrawFrame(nativeApp);
    }
  }

  /** Callback for when close button is pressed. */
  public void closeSample(View view) {
    Log.d(TAG, "Leaving VR sample");
    finish();
  }

  /** Callback for when settings_menu button is pressed. */
  public void showSettings(View view) {
    PopupMenu popup = new PopupMenu(this, view);
    MenuInflater inflater = popup.getMenuInflater();
    inflater.inflate(R.menu.settings_menu, popup.getMenu());
    popup.setOnMenuItemClickListener(this);
    popup.show();
  }

  @Override
  public boolean onMenuItemClick(MenuItem item) {
    if (item.getItemId() == R.id.switch_viewer) {
      nativeSwitchViewer(nativeApp);
      return true;
    }
    return false;
  }

  /**
   * Checks for READ_EXTERNAL_STORAGE permission.
   *
   * @return whether the READ_EXTERNAL_STORAGE is already granted.
   */
  private boolean isReadExternalStorageEnabled() {
    return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_GRANTED;
  }

  /** Handles the requests for activity permission to READ_EXTERNAL_STORAGE. */
  private void requestPermissions() {
    final String[] permissions = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE};
    ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_CODE);
  }

  /**
   * Callback for the result from requesting permissions.
   *
   * <p>When READ_EXTERNAL_STORAGE permission is not granted, the settings view will be launched
   * with a toast explaining why it is required.
   */
  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (!isReadExternalStorageEnabled()) {
      Toast.makeText(this, R.string.read_storage_permission, Toast.LENGTH_LONG).show();
      if (!ActivityCompat.shouldShowRequestPermissionRationale(
          this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
        // Permission denied with checking "Do not ask again". Note that in Android R "Do not ask
        // again" is not available anymore.
        launchPermissionsSettings();
      }
      finish();
    }
  }

  private void launchPermissionsSettings() {
    Intent intent = new Intent();
    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    intent.setData(Uri.fromParts("package", getPackageName(), null));
    startActivity(intent);
  }

  private void setImmersiveSticky() {
    getWindow()
        .getDecorView()
        .setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
  }

  private void initializePlayer() {
    if (player == null) {

      player = new SimpleExoPlayer.Builder(this).build();


      MediaItem mediaItem = new MediaItem.Builder()
              .setUri(url)
              //.setUri("http://gpds.ene.unb.br/videos/normal/clans_4320x2160_30_3100000.mp4")
              .build();

      player.setMediaItem(mediaItem);
      //Uri uri = Uri.parse("https://storage.googleapis.com/exoplayer-test-media-1/360/sphericalv2.mp4");
      //https://storage.googleapis.com/exoplayer-test-media-1/360/iceland0.ts
      //https://storage.googleapis.com/exoplayer-test-media-1/360/congo.mp4
      //https://storage.googleapis.com/exoplayer-test-media-1/360/sphericalv2.mp4
      //MediaSource mediaSource = buildMediaSource(uri);
      player.setVideoSurfaceView(sphericalGLSurfaceView);

      //player.setPlayWhenReady(playWhenReady);
      player.prepare();

    }


    playerView.setPlayer(player);
  }

  private void releasePlayer() {
    if (player != null) {
      player.release();
      player = null;
    }
  }

  private native long nativeOnCreate(AssetManager assetManager);

  private native void nativeOnDestroy(long nativeApp);

  private native void nativeOnSurfaceCreated(long nativeApp);

  private native void nativeOnDrawFrame(long nativeApp);

  private native void nativeOnTriggerEvent(long nativeApp);

  private native void nativeOnPause(long nativeApp);

  private native void nativeOnResume(long nativeApp);

  private native void nativeSetScreenParams(long nativeApp, int width, int height);

  private native void nativeSwitchViewer(long nativeApp);
}
