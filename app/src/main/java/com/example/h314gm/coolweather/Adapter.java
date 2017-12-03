package com.example.h314gm.coolweather;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 314GM on 2017/11/30.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> implements View.OnClickListener{

    private Context context;

    List<String> StringList;

    public interface OnRecyclerViewItemClickListener{  //在Adapter中创建一个实现点击接口，其中view是点击的Item，data是我们的数据
        void onItemClick(View view , int position);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;//定义完接口，添加接口和设置Adapter接口的方法：

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView contentTextView;
        public ViewHolder(View view)
        {
            super(view);
            contentTextView=view.findViewById(R.id.content);
        }
    }
    public Adapter(List<String> List) {
        this.StringList = List;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context==null)
        {
            context=parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);
        /**
         * 在这里写的点击事件无法获区||不好获取逻辑除非是跳转什么的
         * 所以重新定义一个接口OnRecyclerViewItemClickListener，
         * 使得RecycleView可以像ListView一样直接在Activity中添加点击事件
         * 具体思路是：
         * 在Adapter中创建一个点击接口OnRecyclerViewItemClickListener，其中view是点击的Item，data是我们的数据
         * 定义完接口，添加接口和设置Adapter接口的方法：
         * onCreateViewHolder这里是创建ViewHolder的地方，也就是每一个item
         * 在创建的时候就设定好点击事件view.setOnClickListener(this);(Adapter实现View.OnClickListener接口)
         * onBindViewHolder这里是设置每一个item内容的地方，在这个地方把数据存入Tag，
         * 再在onClick(View v)中用onItemClick(v,(String)v.getTag())把数据传回给Activity中写的匿名内部类
         */
        /**
         * 面向接口OnRecyclerViewItemClickListener编程
         * 传递了参数
         * */
       return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String str=StringList.get(position);
        holder.contentTextView.setText(str);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return StringList.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }

}
