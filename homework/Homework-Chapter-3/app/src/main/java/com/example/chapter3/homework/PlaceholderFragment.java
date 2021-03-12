package com.example.chapter3.homework;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.List;




public class PlaceholderFragment extends Fragment {
    private List<Friend> friendList;

    PlaceholderFragment(List<Friend> friendlist)
    {
        friendList = friendlist;
    }
    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO ex3-3: 修改 fragment_placeholder，添加 loading 控件和列表视图控件
        View view = inflater.inflate(R.layout.fragment_placeholder, container, false);
        //LottieAnimationView animationView;
        //animationView = view.findViewById(R.id.animation_view);

        FriendAdapter adaper = new FriendAdapter(friendList);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adaper);
        recyclerView.setVisibility(8);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().postDelayed(new Runnable() {
            @SuppressLint("WrongConstant")
            @Override
            public void run() {
                // 这里会在 5s 后执行
                // TODO ex3-4：实现动画，将 lottie 控件淡出，列表数据淡入
                RecyclerView recyclerView = (RecyclerView)getView().findViewById(R.id.rv);
                LottieAnimationView animationView = getView().findViewById(R.id.animation_view);
                //animationView.pauseAnimation();
                recyclerView.setVisibility(0);
                int duration = 500;
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(recyclerView,"alpha",0, (float) 1);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(animationView,"alpha",1,0);
                animator1.setDuration(duration);
                animator2.setDuration(duration);
                // TODO ex2-3: 将上面创建的其他 ObjectAnimator 都添加到 AnimatorSet 中
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(animator1,animator2);
                animatorSet.start();

                animationView.setVisibility(8);


            }
        }, 5000);
    }

}
