package com.psy.my;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.psy.util.Common;

import java.util.HashMap;
import java.util.List;

import cn.xdu.poscam.R;


public class RankAdapter extends BaseAdapter {
    private int resource;
    private List<HashMap<String, Object>> data;
    private Context context;
    private ViewHolder holder = null;
    private LayoutInflater inflater;
    private boolean first,second,third;

    public RankAdapter(Context context, List<HashMap<String, Object>> data,
                       int resource, String[] from, int[] to) {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.data = data;
        this.resource = resource;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount(){
        return 3;
    }

    @Override
    public int getItemViewType(int position){
        if (position==0){
            return 0;
        }
        if (position==1){
            return 1;
        }
        if (position==2){
            return 2;
        }
        return -1;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final int type = getItemViewType(position);
        // TODO Auto-generated method stub

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(resource, null);
            holder.rankIcon = (ImageView) convertView.findViewById(R.id.rankIcon);
            holder.rankNum = (TextView) convertView.findViewById(R.id.rankNum);
            holder.userName = (TextView) convertView.findViewById(R.id.userName);
            holder.userPb = (TextView) convertView.findViewById(R.id.userPb);
            holder.item = (LinearLayout) convertView.findViewById(R.id.item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (type==0){
            holder.item.setBackground(
                    context.getResources().getDrawable(R.drawable.two_side_bar));
            holder.rankIcon.setVisibility(View.VISIBLE);
            holder.rankNum.setVisibility(View.GONE);
            holder.rankIcon.setImageResource(R.drawable.first);
        }
        if (type==1){
            holder.rankIcon.setVisibility(View.VISIBLE);
            holder.rankNum.setVisibility(View.GONE);
            holder.rankIcon.setImageResource(R.drawable.second);
        }
        if (type==2){
            holder.rankIcon.setVisibility(View.VISIBLE);
            holder.rankNum.setVisibility(View.GONE);
            holder.rankIcon.setImageResource(R.drawable.third);
        }

        holder.rankNum.setText(data.get(position).get("rank_pose").toString());
        holder.userPb.setText(data.get(position).get("userPb").toString()+" P币");
        holder.userName.setText(data.get(position).get("userName").toString());
        return convertView;

    }


    @Override
    public boolean isEnabled(int position) {
        // TODO Auto-generated method stub
        return super.isEnabled(position);
    }

    private final class ViewHolder {
        public TextView rankNum, userName, userPb;
        public LinearLayout item;
        public ImageView rankIcon;
    }

}
