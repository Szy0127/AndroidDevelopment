package com.example.trendingtopic;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder>
{
    static public class TopicViewHolder extends RecyclerView.ViewHolder
    {
        TextView index;
        TextView content;
        TextView visit;
        View topicView;
        public TopicViewHolder(View view)
        {
            super(view);
            topicView = view;
            index = (TextView)view.findViewById(R.id.index);
            content = (TextView)view.findViewById(R.id.content);
            visit = (TextView)view.findViewById(R.id.visit);
        }
    }
    private List<Topic> itopicList = new ArrayList<>();
    /*public TopicAdapter(List<Topic> topicList)
    {
        itopicList = topicList;
    }*/
    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_item,parent,false);
        TopicViewHolder holder = new TopicViewHolder(view);

        holder.topicView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = holder.getAdapterPosition();
                Topic topic = itopicList.get(position);
                //Toast.makeText(v.getContext(),topic.content,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(), TopicActivity.class);
                intent.putExtra("discription", topic.discription);
                v.getContext().startActivity(intent);
            }
        });



        return holder;
    }
    @Override
    public void onBindViewHolder(TopicViewHolder holder,int position)
    //public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        Topic topic = itopicList.get(position);
        holder.index.setText(String.valueOf(topic.index));
        holder.content.setText(topic.content);
        holder.visit.setText(String.valueOf(topic.visit));
    }
    @Override
    public int getItemCount()
    {
        return itopicList.size();
    }

    public void changeData(List<Topic> topicList)
    {
        itopicList.clear();
        itopicList.addAll(topicList);
        notifyDataSetChanged();
    }
}
