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

public class MapAdapter extends BaseAdapter {
    private Context mContext;
    private Integer[] mThumbIds = new Integer[150];

    public MapAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // method to accept incoming array of image ids
    public void setImageArray(Integer[] val) {
        mThumbIds = val;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(108, 108));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(2,2,2,2);
            imageView.setBackgroundColor(Color.BLACK);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }




}
