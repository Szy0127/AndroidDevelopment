package com.SJTU7.Tiktok;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.iceteck.silicompressorr.SiliCompressor;

public class UploadActivity extends AppCompatActivity {
    private final static int PERMISSION_REQUEST_CODE = 1001;
    private static final String TAG = "Upload";
    private static final long MAX_FILE_SIZE = 30 * 1024 * 1024;
    private static final int REQUEST_CODE_COVER_IMAGE = 101;
    private static final int REQUEST_CODE_VIDEO = 202;
    private static final String COVER_IMAGE_TYPE = "image/*";
    private static final String VIDEO_TYPE = "video/*";

    private static final int MSG_START_COMPRESS = 0;
    private static final int MSG_END_COMPRESS = 1;

    private Retrofit retrofit;
    //    private IApi api;
    private IApi service;
    private Uri coverImageUri;
    private Uri videoUri;
    public String compressPath;
    public Boolean flag = false;

    private SimpleDraweeView coverSD;
    private SimpleDraweeView videoSD;
    private EditText extraContentEditText;
    private LottieAnimationView animationView;

    private TextView btn_home;
    private TextView btn_record;
    private TextView btn_upload;
    private TextView btn_mine;

    private Button btn_submit;
    private Button btn_compress;
    private LottieAnimationView lottieAnimationView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initNetwork();
        setContentView(R.layout.activity_upload);
        setMenu();
        coverSD = findViewById(R.id.sd_cover);
        videoSD = findViewById(R.id.sd_video);
        animationView = findViewById(R.id.animation_view);
        btn_submit = findViewById(R.id.btn_submit);
        btn_compress = findViewById(R.id.btn_compress);
        lottieAnimationView = findViewById(R.id.lottie_view);

        extraContentEditText = findViewById(R.id.et_extra_content);

        findViewById(R.id.btn_cover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile(REQUEST_CODE_COVER_IMAGE, COVER_IMAGE_TYPE, "????????????");
            }
        });

        findViewById(R.id.btn_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile(REQUEST_CODE_VIDEO, VIDEO_TYPE, "????????????");
            }
        });

        findViewById(R.id.btn_compress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_compress.setEnabled(false);
                compress();
            }
        });

        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        requestPermission();
        solveInfoFromCamera();
    }

    private void requestPermission() {
        boolean hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean hasAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (hasCameraPermission && hasAudioPermission) {

        } else {
            List<String> permission = new ArrayList<String>();
            if (!hasCameraPermission) {
                permission.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (!hasAudioPermission) {
                permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            ActivityCompat.requestPermissions(this, permission.toArray(new String[permission.size()]), PERMISSION_REQUEST_CODE);
        }

    }

    private void solveInfoFromCamera()
    {
        if (Constants.upload) {
            String videoPath = Constants.mp4Path;
            Log.d(TAG, "received video path:  " + videoPath);
            videoUri = Uri.parse("file://" + videoPath);
            Log.d(TAG, "received video uri:  " + videoUri);
            coverImageUri = getVideoThumb(this,videoUri);
            coverSD.setImageURI(coverImageUri);
            videoSD.setImageURI(coverImageUri);
            Constants.upload = false;
            Constants.mp4Path = "";
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        solveInfoFromCamera();
    }

    private Uri getVideoThumb(Context context, Uri uri) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(context,uri);
        Bitmap cover = media.getFrameAtTime();
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }
        String coverName = "COVER_"+System.currentTimeMillis() + ".jpg";
        File coverFile = new File(mediaStorageDir, coverName);
        try {
            FileOutputStream fos = new FileOutputStream(coverFile);
            cover.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ????????????????????????????????????
        String path = coverFile.getAbsolutePath();
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), path, coverName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // ????????????????????????
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri coverUri = Uri.fromFile(coverFile);
        Log.d(TAG, "cover uri:  " + coverUri);
        intent.setData(coverUri);
        context.sendBroadcast(intent);
        return coverUri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_COVER_IMAGE == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                coverImageUri = data.getData();
                coverSD.setImageURI(coverImageUri);

                if (coverImageUri != null) {
                    Log.d(TAG, "pick cover image " + coverImageUri.toString());
                } else {
                    Log.d(TAG, "uri2File fail " + data.getData());
                }

            } else {
                Log.d(TAG, "file pick fail");
            }
        }

        if (REQUEST_CODE_VIDEO == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                videoUri = data.getData();
                videoSD.setImageURI(getVideoThumb(this,videoUri));

                if (videoUri != null) {
                    Log.d(TAG, "pick video " + videoUri.toString());
                } else {
                    Log.d(TAG, "uri2File fail " + data.getData());
                }
            } else {
                Log.d(TAG, "file pick fail");
            }
        }
    }

    //  ??????????????????????????????
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Toast.makeText(UploadActivity.this, "????????????????????????", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.btn_submit).setEnabled(false);
                    animationView.setVisibility(View.VISIBLE);
                    animationView.getBackground().setAlpha(180);
                    animationView.bringToFront();
                    animationView.playAnimation();
                    break;
                case 1:
                    Toast.makeText(UploadActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.btn_submit).setEnabled(true);
                    animationView.setVisibility(View.INVISIBLE);
                    animationView.pauseAnimation();
                    break;
            }
            return false;
        }
    });

    private void compress()
    {
        /**
         * ????????????
         * ??????????????????????????????????????? Uri
         * ????????????????????????????????????????????????
         * Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) ??????????????????????????????
         */
        new Thread(){
            @Override
            public void run(){
                super.run();
                try {
                    //String systemPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
                    String systemPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
                    Log.d(TAG, "systemPath: "+ systemPath);
                    mHandler.sendMessage(Message.obtain(mHandler, MSG_START_COMPRESS));

                    compressPath = SiliCompressor.with(UploadActivity.this).compressVideo(videoUri,systemPath);
                    Log.d(TAG, "compressPath: "+ compressPath);
                    mHandler.sendMessage(Message.obtain(mHandler, MSG_END_COMPRESS));

                    Looper.prepare();
                    Toast.makeText(UploadActivity.this,"????????????",Toast.LENGTH_SHORT).show();


                    flag=true;
                    //  ??? path ????????? Uri
                    videoUri = Uri.parse("file://"+compressPath);
                    Log.d(TAG, "submit - videoUri: " + videoUri);
                    btn_compress.setText("????????????");
                    Looper.loop();

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(UploadActivity.this,"????????????",Toast.LENGTH_SHORT).show();
                    Looper.loop();

                }
            }
        }.start();
    }
    private void initNetwork() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api-sjtu-camp-2021.bytedance.com/homework/invoke/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // ??????api??????
        service = retrofit.create(IApi.class);
    }

    private void getFile(int requestCode, String type, String title) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, requestCode);
    }

    private void submit_recover(){
        btn_submit.setText("??????");
        findViewById(R.id.btn_submit).setEnabled(true);
    }



    private void submit() {


        //  ??????????????????

        byte[] coverImageData = readDataFromUri(coverImageUri);


        //  ??????????????????
        byte[] videoData = readDataFromUri(videoUri);

        btn_submit.setText("????????????");
        findViewById(R.id.btn_submit).setEnabled(false);
        lottieAnimationView.setVisibility(View.VISIBLE);
        lottieAnimationView.playAnimation();

        Runnable button_recover = new Runnable() {
            @Override
            public void run() {
                btn_submit.setText("????????????");
                findViewById(R.id.btn_submit).setEnabled(true);
                lottieAnimationView.setVisibility(View.INVISIBLE);
                lottieAnimationView.pauseAnimation();
            }
        };


        //  ????????????????????????????????????
        if (coverImageData == null || coverImageData.length == 0) {
            Toast.makeText(this, "???????????????", Toast.LENGTH_SHORT).show();
            submit_recover();
            lottieAnimationView.setVisibility(View.INVISIBLE);
            lottieAnimationView.pauseAnimation();
            return;
        }

        if (videoData == null || videoData.length == 0) {
            Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
            submit_recover();
            lottieAnimationView.setVisibility(View.INVISIBLE);
            lottieAnimationView.pauseAnimation();
            return;
        }

        String content = extraContentEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "?????????????????????", Toast.LENGTH_SHORT).show();
            submit_recover();
            lottieAnimationView.setVisibility(View.INVISIBLE);
            lottieAnimationView.pauseAnimation();
            return;
        }

        if ( coverImageData.length + videoData.length >= MAX_FILE_SIZE) {
            Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
            submit_recover();
            lottieAnimationView.setVisibility(View.INVISIBLE);
            lottieAnimationView.pauseAnimation();
            return;
        }

        // ??????api.submitMessage()??????????????????
        // ???????????????????????????activity???????????????toast


        //  ?????? Body ?????????????????????????????????
        MultipartBody.Part image_part = MultipartBody.Part.createFormData(
                "cover_image",
                "cover.png",
                RequestBody.create(MediaType.parse("multipart/form_data"), coverImageData)
        );
        MultipartBody.Part video_part = MultipartBody.Part.createFormData(
                "video",
                "video_file.mp4",
                RequestBody.create(MediaType.parse("multipart/form_data"), videoData)
        );

        //  ?????? Call ????????????????????? submit ??????????????????????????????






        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<UploadResponse> call = service.submitMessage(
                        Constants.USER_ID,
                        Constants.USER_NAME,
                        content,
                        image_part,
                        video_part);
                try {
                    Response<UploadResponse> response = call.execute();
                    if (response.isSuccessful() && response.body().success){
                        Log.d("upload", "run: back");
                        Intent intent = new Intent(UploadActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Log.d("upload", "");
                        mHandler.post(button_recover);

                    }
                } catch(IOException e){
                    e.printStackTrace();
                    mHandler.post(button_recover);
                }
            }
        }).start();
    }

    private byte[] readDataFromUri(Uri uri) {
        byte[] data = null;
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            data = Util.inputStream2bytes(is);
            Log.d(TAG, "readDataFromUri: here");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public void setMenu()
    {
        btn_home = findViewById(R.id.btn_home);
        btn_record = findViewById(R.id.btn_record);
        btn_upload = findViewById(R.id.btn_upload);
        btn_mine = findViewById(R.id.btn_mine);
        btn_upload.setTextColor(Color.WHITE);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadActivity.this,MainActivity.class));
            }
        });
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadActivity.this,CustomCameraActivity.class));
            }
        });
        btn_mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadActivity.this,MineActivity.class));
            }
        });
    }
}

