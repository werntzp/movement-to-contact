package com.shadowdragonsoftware.movement2contact;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AchievementsAdapter extends ArrayAdapter<String> {

        private final Activity context;
        private final String[] title;
        private final String[] desc;
        private final Integer[] img;

        AchievementsAdapter(Activity context, String[] title, String[] desc, Integer[] img) {
            super(context, R.layout.achievements_list, title);
            this.context=context;
            this.title=title;
            this.desc=desc;
            this.img=img;
        }

        public View getView(int position,View view,ViewGroup parent) {
            LayoutInflater inflater=context.getLayoutInflater();
            View rowView=inflater.inflate(R.layout.achievements_list, null,true);

            TextView tvwTitle = rowView.findViewById(R.id.achievement_title);
            TextView tvwDesc = rowView.findViewById(R.id.achievement_desc);
            ImageView imgIcon =  rowView.findViewById(R.id.achievement_icon);

            if (img[position] == R.drawable.achievement_unlocked) {
                tvwTitle.setTextColor(Color.parseColor("#21FF06"));
                tvwDesc.setTextColor(Color.parseColor("#21FF06"));
            }
            else {
                tvwTitle.setTextColor(Color.GRAY);
                tvwDesc.setTextColor(Color.GRAY);
            }

            tvwTitle.setText(title[position]);
            tvwDesc.setText(desc[position]);
            imgIcon.setImageResource(img[position]);
            return rowView;

        }

}
