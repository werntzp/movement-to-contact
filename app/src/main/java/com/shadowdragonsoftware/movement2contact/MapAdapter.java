package com.shadowdragonsoftware.movement2contact;

import android.widget.BaseAdapter;
import android.content.Context;
import android.widget.ImageView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.graphics.Color;

public class MapAdapter extends BaseAdapter {

    private Context _mContext;
    private Integer[] _mThumbIds = new Integer[GameActivity.MAX_ARRAY];
    private static final String TAG = "MapAdapter";
    private ImageView[] _imageViews = new ImageView[GameActivity.MAX_ARRAY];

    MapAdapter(Context c) {
        _mContext = c;
    }

    public int getCount() {
        return _mThumbIds.length;
    }

    public Object getItem(int position) {
        return _imageViews[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    // method to update internal array
    void setItem(Object v, int img, int pos) {
        _imageViews[pos] = (ImageView) v;
        _mThumbIds[pos] = img;
    }

    // method to accept incoming array of image ids
    void setImageArray(Integer[] val) {
        _mThumbIds = val;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(_mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(108, 108));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(2,2,2,2);
            imageView.setBackgroundColor(Color.BLACK);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setTag(_mThumbIds[position]);
        imageView.setImageResource(_mThumbIds[position]);
        // store imageview into internal array so we can get to each imageview later
        _imageViews[position] = imageView;
        return imageView;
    }



}
