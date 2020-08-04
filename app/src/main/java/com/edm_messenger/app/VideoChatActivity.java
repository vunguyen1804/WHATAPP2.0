//package com.edm_messenger.app;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.view.SurfaceView;
//import android.widget.FrameLayout;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.edm_messenger.R;
//
//import java.util.Random;
//
//import io.agora.rtc.Constants;
//import io.agora.rtc.IRtcEngineEventHandler;
//import io.agora.rtc.RtcEngine;
//import io.agora.rtc.video.VideoCanvas;
//
///**
// * Class: VideoChatActivity.java
// * Description: Handle the video chat
// *
// */
//
//public class VideoChatActivity extends AppCompatActivity {
//
//    private static final int PERMISSION_REQ_ID = 22;
//    private static final String[] REQUESTED_PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};
//
//    private RtcEngine mRtcEngine;
//    private IRtcEngineEventHandler mRtcEventHandler;
//
//    public boolean checkSelfPermission(String permission, int requestCode) {
//        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this,
//                    REQUESTED_PERMISSIONS,
//                    requestCode);
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String permissions[], @NonNull int[] grantResults) {
//
//        switch (requestCode) {
//            case PERMISSION_REQ_ID: {
//                if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
//                    break;
//                }
//                // if permission granted, initialize the engine
//                initializeAgoraEngine();
//                break;
//            }
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.video_chat_activity);
//        mRtcEventHandler = new IRtcEngineEventHandler() {
//
//
//            @Override
//            public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        setupRemoteVideo(uid);
//                    }
//                });
//            }
//
//
//        };
//        initializeAgoraEngine();
//
//    }
//
//    private void initializeAgoraEngine() {
//        try {
//            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
//            joinChannel();
//            setupLocalVideo();
//            setupVideoProfile();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void setupVideoProfile() {
//        mRtcEngine.enableVideo();
//        mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_360P, false);
//    }
//
//    private void setupLocalVideo() {
//        FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
//        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
//        surfaceView.setZOrderMediaOverlay(true);
//        container.addView(surfaceView);
//        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, 0));
//    }
//
//    private void joinChannel() {
//        mRtcEngine.joinChannel(null, "aye", "Extra Optional Data", new Random().nextInt(10000000)+1); // if you do not specify the uid, Agora will assign one.
//    }
//
//    private void setupRemoteVideo(int uid) {
//        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);
//
//        if (container.getChildCount() >= 1) {
//            return;
//        }
//
//        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
//        container.addView(surfaceView);
//        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, uid));
//        surfaceView.setTag(uid);
//
//    }
//
//    private void leaveChannel() {
//        mRtcEngine.leaveChannel();
//    }
//}