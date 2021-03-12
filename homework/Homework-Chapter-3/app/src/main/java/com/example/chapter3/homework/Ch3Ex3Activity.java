package com.example.chapter3.homework;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用 ViewPager 和 Fragment 做一个简单版的好友列表界面
 * 1. 使用 ViewPager 和 Fragment 做个可滑动界面
 * 2. 使用 TabLayout 添加 Tab 支持
 * 3. 对于好友列表 Fragment，使用 Lottie 实现 Loading 效果，在 5s 后展示实际的列表，要求这里的动效是淡入淡出
 */
public class Ch3Ex3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ch3ex3);/*
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
            e.printStackTrace();*/


        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager pager = findViewById(R.id.view_pager);
        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            private List<Friend> initList()
            {
                List<Friend> friendList=new ArrayList<>();
                Friend friend = null;
                try {
                    InputStream txt=getResources().openRawResource(R.raw.data);

                    InputStreamReader inputStreamReader = new InputStreamReader(txt);
                    BufferedReader reader = new BufferedReader(inputStreamReader);

                    int i = 0;
                    String line = null;
                    while ((line=reader.readLine())!=null) {
                        //Toast.makeText(MainActivity.this,line,Toast.LENGTH_SHORT).show();
                        i++;
                        String[] info = line.split(" ");
                        friend = new Friend(info[0],info[1],info[2],info[3]);
                        friendList.add(friend);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return friendList;
            }

            @Override
            public Fragment getItem(int position) {
                List<Friend> friendList= initList();
                return new PlaceholderFragment(friendList);
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return "HELLO "+position;
            }
        });
        tabLayout.setupWithViewPager(pager);


        }



        // TODO: ex3-1. 添加 ViewPager 和 Fragment 做可滑动界面


        // TODO: ex3-2, 添加 TabLayout 支持 Tab

}
