package com.wpwiii.movement2contact;

import android.support.annotation.IntegerRes;
import android.widget.BaseAdapter;
import android.content.Context;
import android.widget.ImageView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.graphics.Color;
import com.wpwiii.movement2contact.R;
import java.util.Random;
import android.widget.AdapterView.OnItemClickListener;
import android.util.Log;

public class MapAdapter extends BaseAdapter {

    private Context _mContext;
    private Integer[] _mThumbIds = new Integer[GameActivity.MAX_ARRAY];
    private static final String TAG = "MapAdapter";
    private ImageView[] _imageViews = new ImageView[GameActivity.MAX_ARRAY];

    public MapAdapter(Context c) {
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

    // method to accept incoming array of image ids
    public void setImageArray(Integer[] val) {
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
