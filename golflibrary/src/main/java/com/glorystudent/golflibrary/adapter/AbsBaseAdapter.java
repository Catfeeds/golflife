package com.glorystudent.golflibrary.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hyj on 2016/9/19.16:14
 * 单布局万能适配器
 */
public abstract class AbsBaseAdapter<T> extends BaseAdapter {

    protected Context context;
    private List<T> datas;
    private int resid;
    private int currentPosition;
    public int sizeCount;
    public AbsBaseAdapter(Context context, int resid){
        this.context = context;
        this.resid = resid;
        this.datas = new ArrayList<>();
    }

    public void setDatas(List<T> datas){
        this.datas = datas;

        sizeCount = datas.size();

        this.notifyDataSetChanged();
    }

    public void addDatas(List<T> datas) {
        this.datas.addAll(datas);
        this.notifyDataSetChanged();
    }

    public int getCurrentPosition(){
     return currentPosition;
    }
    public T getDatas(int position){
        return datas.get(position);
    }
    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView != null){
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(resid, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        currentPosition = position;
        //给不同的控制设置值
        bindView(viewHolder, datas.get(position));

        return convertView;
    }

    public abstract void bindView(ViewHolder viewHolder, T data);

    /**
     * 作用：缓存item中的控件对象，避免多次findViewById
     */
    protected class ViewHolder{
        SparseArray<View> sparseArray = new SparseArray<>();

        View layoutView;
        public ViewHolder(View layoutView){
            this.layoutView = layoutView;
        }

        public View getView(int id){
            View view = sparseArray.get(id);
            if(view == null){
                view = layoutView.findViewById(id);
                sparseArray.put(id, view);
            }
            return view;
        }

        /**
         * 设置TextView
         * @param id
         * @param value
         * @return
         */
        public ViewHolder setTextView(int id, String value){
            TextView textView = (TextView) getView(id);
            textView.setText(value);
            return this;
        }

        /**
         * 设置图片
         * @param id
         * @param url
         * @return
         */
        public ViewHolder setImageView(int id, String url,int defaultimg){
            ImageView imageView = (ImageView) getView(id);
            Glide.with(context).load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(defaultimg)
                    .into(imageView);
            return this;
        }
    }
}
