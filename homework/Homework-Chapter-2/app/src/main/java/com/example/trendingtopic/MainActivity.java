package com.example.trendingtopic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Topic> topicList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTopics();
        TopicAdapter adaper = new TopicAdapter();
        adaper.changeData(topicList);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaper);

        EditText search = (EditText)findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                //Toast.makeText(MainActivity.this,topicList.get(0).content,Toast.LENGTH_SHORT).show();
                List<Topic> filters = new ArrayList<>();
                String searchContent = String.valueOf(s);
                for (Topic item : topicList) {
                    if (item.content.contains(searchContent)) {
                        filters.add(item);
                    }
                }
                adaper.changeData(filters);
            }

        });}

    private void initTopics()
    {
        Topic topic = null;
        try {
            InputStream txt=getResources().openRawResource(R.raw.topicdata);

            InputStreamReader inputStreamReader = new InputStreamReader(txt);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            int i = 0;
            String line = null;
            while ((line=reader.readLine())!=null) {
                //Toast.makeText(MainActivity.this,line,Toast.LENGTH_SHORT).show();
                i++;
                String[] info = line.split(" ");
                topic = new Topic(i,info[0],Integer.valueOf(info[1]),info[2]);
                topicList.add(topic);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
