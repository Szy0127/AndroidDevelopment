package com.byted.camp.todolist;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class BackgroundActivity extends AppCompatActivity {

    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private ImageView image4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);
        setTitle("设置背景");

        image1 = (ImageView)findViewById(R.id.jienigui);
        image2 = (ImageView)findViewById(R.id.pikaqiu);
        image3 = (ImageView)findViewById(R.id.jiqimao);
        image4 = (ImageView)findViewById(R.id.shidiqi);
        final List<ImageView> imageViews=new ArrayList<>();
        imageViews.add(image1);
        imageViews.add(image2);
        imageViews.add(image3);
        imageViews.add(image4);
        for(int i = 0;i < imageViews.size();i++)
        {
            imageViews.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.background = getResources().getResourceEntryName(view.getId());
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            });
        }
    }
}
