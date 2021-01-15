package com.pengjie0668.ffmpeg;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pengjie0668.ffmpeg.demo.listener.FFOnErrorListener;
import com.pengjie0668.ffmpeg.demo.listener.FFOnLoadListener;
import com.pengjie0668.ffmpeg.demo.listener.FFOnPauseResumeListener;
import com.pengjie0668.ffmpeg.demo.listener.FFOnPreparedListener;
import com.pengjie0668.ffmpeg.demo.opengl.MGLSurfaceView;
import com.pengjie0668.ffmpeg.demo.player.FFPlayer;
import com.pengjie0668.ffmpeg.demo.util.LogUtil;
import com.pengjie0668.ffmpeg.demo.util.VideoSupportUtil;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private String url = "http://vjs.zencdn.net/v/oceans.mp4";
    private FFPlayer ffPlayer;

    private Button btStartPlay, btPause, btResume, btStop, btNext, btSelectFile;
    private TextView urlTv;


    private MGLSurfaceView mglSurfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initCallBack();

        PermissionUtils.isGrantExternalRW(this, 999);

        VideoSupportUtil.init();
    }


    private void initView() {

        btSelectFile = findViewById(R.id.select_file_bt);
        btStartPlay = findViewById(R.id.bt_start);
        btPause = findViewById(R.id.bt_pause);
        btResume = findViewById(R.id.bt_resume);
        btStop = findViewById(R.id.bt_stop);
        btNext = findViewById(R.id.bt_next);
        urlTv = findViewById(R.id.url_tv);
        urlTv.setText(url);
        mglSurfaceView = findViewById(R.id.surface_yuv);


        //开始播放
        btStartPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ffPlayer.setSource("http://mpge.5nd.com/2015/2015-11-26/69708/1.mp3");
                ffPlayer.setSource(url);
                urlTv.setText(url);
//                ffPlayer.setSource("/storage/emulated/0/hp8.mp4");
                ffPlayer.prepared();
            }
        });

        btPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ffPlayer.pause();
            }
        });

        btResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ffPlayer.resume();
            }
        });

        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ffPlayer.stop();
            }
        });

        File file = new File(Environment.getExternalStorageDirectory(), "/ffmusic");
        file.mkdir();


        btSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new  Intent(Intent.ACTION_GET_CONTENT);
                intent.setType( "video/*" );
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try  {
                    startActivityForResult( Intent.createChooser(intent, "选择播放文件" ), 19);
                } catch  (android.content.ActivityNotFoundException ex) {
                    LogUtil.e(TAG, "Please install a File Manager.");
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        switch  (requestCode) {
            case  19:{
                if  (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    try {
                        String path = FileUtils.getImageAbsolutePath(this, uri);
                        url = path;
                        urlTv.setText(url);
                        LogUtil.d(TAG, uri + " path:" +  path);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            break ;
            default:
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void initCallBack(){

        ffPlayer = new FFPlayer();
        ffPlayer.setMGLSurfaceView(mglSurfaceView);
        ffPlayer.setPreparedListener(new FFOnPreparedListener() {
            @Override
            public void onPrepared() {
                LogUtil.d(TAG, "FFMpeg 准备好 开始播放音频！call jniStart");
                ffPlayer.start();
            }
        });

        ffPlayer.setFfOnLoadListener(new FFOnLoadListener() {
            @Override
            public void onLoad(boolean load) {
                if(load) {
                    //LogUtil.d(TAG, "LoadListener 加载中...");
                } else {
                    //LogUtil.d(TAG, "LoadListener 播放中...");
                }
            }
        });

        ffPlayer.setFfOnPauseResumeListener(new FFOnPauseResumeListener() {
            @Override
            public void onPause(boolean pause) {
                if(pause) {
                    LogUtil.d(TAG, "暂停中...");
                } else {
                    LogUtil.d(TAG, "播放中...");
                }
            }
        });

        ffPlayer.setFfOnErrorListener(new FFOnErrorListener() {
            @Override
            public void onError(int code, final String msg) {
                LogUtil.e(TAG, "code:" + code + " msg:" +msg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


    }

}
