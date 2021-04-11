package com.domker.study.androidstudy;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class MediaPlayerActivity extends AppCompatActivity {
    private SurfaceView surfaceView;
    private MediaPlayer player;
    private SurfaceHolder holder;
    private Button play_button;
    private Button reset;
    private SeekBar seekBar;
    private TextView time;
    int duration;
    private Handler mHandler;
    private Button change_orientation;
    private int state;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("MediaPlayer");

        setContentView(R.layout.layout_media_player);
        play_button = findViewById(R.id.btn_play);
        reset = findViewById(R.id.btn_reset);
        seekBar = findViewById(R.id.seekBar);
        time = findViewById(R.id.tv_time);
        change_orientation = findViewById(R.id.btn_setting);
        surfaceView = findViewById(R.id.surfaceView);

        if (Configuration.ORIENTATION_LANDSCAPE == getResources()
                .getConfiguration().orientation) {
            state = R.string.fullScreek;
            change_orientation.setText(R.string.smallScreen);
        } else {
            state = R.string.smallScreen;
            change_orientation.setText(R.string.fullScreek);
        }


        change_orientation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHandler.removeMessages(1);//切换横竖屏以后会重置activity
                if (state == R.string.fullScreek) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }
        });
        player = new MediaPlayer();
        try {
            player.setDataSource(getResources().openRawResourceFd(R.raw.big_buck_bunny));
            holder = surfaceView.getHolder();
            holder.setFormat(PixelFormat.TRANSPARENT);
            holder.addCallback(new PlayerCallBack());
            player.prepare();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // 自动播放
                    player.start();
                    player.setLooping(true);
                }
            });
            player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    System.out.println(percent);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(player.isPlaying())
                {
                    player.pause();
                    play_button.setText("播放");
                }
                else {
                    player.start();
                    play_button.setText("暂停");
                    mHandler.sendEmptyMessage(1);
                }
            }
        });

        duration = player.getDuration();
        int minutes = duration/60/1000;
        String string_minutes = String.valueOf(minutes);
        if(string_minutes.length()==1)
        {
            string_minutes = "0"+string_minutes;
        }
        String seconds = String.valueOf(duration/1000-duration/60/1000);
        if(seconds.length()==1)
        {
            seconds = "0"+seconds;
        }
        final String string_duration = string_minutes+":"+seconds;
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        int t = player.getCurrentPosition();
                        seekBar.setProgress(t);

                        int minutes = t/60/1000;
                        String seconds = String.valueOf(t/1000-t/60/1000);
                        if(seconds.length()==1)
                        {
                            seconds = "0"+seconds;
                        }
                        final String string_t = String.valueOf(minutes)+":"+seconds;

                        time.setText(string_t+"/"+string_duration);
                        if (player!=null &&player.isPlaying()) {
                            sendMessageDelayed(obtainMessage(1), 1000);
                        }
                        break;
                }
            }
        };
      mHandler.sendEmptyMessage(1);

        seekBar.setMax(duration);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seekTo(seekBar.getProgress());
                int t = player.getCurrentPosition();

                int minutes = t/60/1000;
                String seconds = String.valueOf(t/1000-t/60/1000);
                if(seconds.length()==1)
                {
                    seconds = "0"+seconds;
                }
                final String string_t = String.valueOf(minutes)+":"+seconds;

                time.setText(string_t+"/"+string_duration);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.seekTo(0);
                if(!player.isPlaying())
                {
                    player.start();
                    mHandler.sendEmptyMessage(1);
                    time.setText("00:00/"+string_duration);
                    play_button.setText("暂停");
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.stop();
            player.release();
        }
    }

    private class PlayerCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            player.setDisplay(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }

}
