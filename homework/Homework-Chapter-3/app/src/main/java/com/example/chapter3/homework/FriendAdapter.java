package com.example.chapter3.homework;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder>
{
    static public class FriendViewHolder extends RecyclerView.ViewHolder
    {
        ImageView image;
        TextView name;
        TextView content;
        TextView time;
        View friendView;
        public FriendViewHolder(View view)
        {
            super(view);
            friendView = view;
            image = (ImageView)view.findViewById(R.id.image);
            name = (TextView)view.findViewById(R.id.name);
            content = (TextView)view.findViewById(R.id.content);
            time = (TextView)view.findViewById(R.id.time);
        }
    }
    private List<Friend> ifriendList = new ArrayList<>();
    public FriendAdapter(List<Friend> friendList)
    {
        ifriendList = friendList;
    }
    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item,parent,false);
        FriendViewHolder holder = new FriendViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(FriendViewHolder holder,int position)
    {
        Friend friend = ifriendList.get(position);
        int imageid = 0;
        try {
            imageid = R.drawable.class.getField("p"+friend.imageid).getInt(new R.drawable());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        holder.image.setImageResource(imageid);
        holder.name.setText(String.valueOf(friend.name));
        holder.content.setText(friend.content);
        holder.time.setText(String.valueOf(friend.time));
    }
    @Override
    public int getItemCount()
    {
        return ifriendList.size();
    }

}