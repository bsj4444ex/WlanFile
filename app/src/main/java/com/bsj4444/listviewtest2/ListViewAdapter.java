package com.bsj4444.listviewtest2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2016/9/23.
 */
public class ListViewAdapter extends BaseAdapter {

    private List<String> mData;
    private LayoutInflater mInflater;

    public ListViewAdapter(Context context,List<String> data){
        this.mData=data;
        mInflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            holder=new ViewHolder();
            //通过layoutInflater实例化布局
            convertView=mInflater.inflate(R.layout.item,null);
            holder.img=(ImageView)convertView.findViewById(R.id.imageView);
            holder.title=(TextView)convertView.findViewById(R.id.textView);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }
        holder.img.setBackgroundResource(R.drawable.in_icon);
        holder.title.setText(mData.get(position));
        return convertView;
    }
    public final class ViewHolder{
        public ImageView img;
        public TextView title;
    }
}
