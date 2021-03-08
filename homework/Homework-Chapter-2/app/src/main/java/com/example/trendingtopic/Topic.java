package com.example.trendingtopic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Topic
{
    public int index;
    public int visit;
    public String content;
    public String discription;
    public Topic(int index,String content,int visit,String discription)
    {
        this.index = index;
        this.content = content;
        this.visit = visit;
        this.discription = discription;
    }
}

