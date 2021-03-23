package com.bytedance.practice5;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.bytedance.practice5.model.Message;
import com.bytedance.practice5.model.MessageListResponse;
import com.bytedance.practice5.socket.SocketActivity;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "chapter5";
    private FeedAdapter adapter = new FeedAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        findViewById(R.id.btn_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,UploadActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_mine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(Constants.STUDENT_ID);
            }
        });

        findViewById(R.id.btn_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(null);
            }
        });
        findViewById(R.id.btn_socket).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SocketActivity.class);
                startActivity(intent);
            }
        });



    }

    //TODO 2
    // 用HttpUrlConnection实现获取留言列表数据，用Gson解析数据，更新UI（调用adapter.setData()方法）
    // 注意网络请求和UI更新分别应该放在哪个线程中
    private void getData(String studentId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Message> messages = getMessagesFromRemote(studentId);
                if (messages != null && !messages.isEmpty())
                {
                    new Handler(getMainLooper()).post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            adapter.setData(messages);
                        }
                    });
                }
            }
        }).start();
    }

    private List<Message> getMessagesFromRemote(String studentId){
        List<Message> messages= null;
        String urlPath= Constants.BASE_URL + "messages";//有斜杠了
        if (studentId != null)
        {
            urlPath += ("?student_id=" + studentId);
        }
        try
        {
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(6000);
            connection.setRequestMethod("GET");

            if(connection.getResponseCode() == 200)
            {
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader =new BufferedReader(new InputStreamReader(inputStream));

                MessageListResponse response = new Gson().fromJson(reader, MessageListResponse.class);
                if(response.success)
                {
                    messages = response.feeds;
                }
                else {
                        throw new Exception();
                    }

                reader.close();
                inputStream.close();
            }
            else {
                throw new Exception();
            }
            connection.disconnect();

        } catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "错误" , Toast.LENGTH_SHORT).show();
        }

        return messages;
    }


}