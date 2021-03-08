package com.example.trendingtopic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class TopicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        String discription = getIntent().getStringExtra("discription");

        TextView textView = findViewById(R.id.topicItem);
        textView.setText(discription);
    }
}