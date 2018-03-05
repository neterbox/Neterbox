package com.neterbox.customadapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.neterbox.*;
import com.neterbox.R;
<<<<<<< HEAD
import com.neterbox.jsonpojo.circle.CircleListDatum;
import com.neterbox.jsonpojo.circlepostlist.CirclePostListPojo;
=======
import com.neterbox.jsonpojo.FriendRequestList.FriendRequestListDatum;
import com.neterbox.jsonpojo.circle.CircleListDatum;
>>>>>>> 7a229364e04a7f07edcebd5859c752759a0f714d

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sejal on 2/1/2018.
 */

public class Circle_post_Adapter extends BaseAdapter {

    Activity activity;
    private LayoutInflater inflater;
    public Resources res;
<<<<<<< HEAD
    List<CirclePostListPojo> circlePostListPojos=new ArrayList<>();

    public Circle_post_Adapter(Activity a,List<CirclePostListPojo> circlePostListPojos) {
        this.activity = a;
        this.circlePostListPojos=circlePostListPojos;
=======
    List<CircleListDatum> circleListData=new ArrayList<>();

    public Circle_post_Adapter(Activity a,List<CircleListDatum> circleListData) {
        this.activity = a;
        this.circleListData=circleListData;
>>>>>>> 7a229364e04a7f07edcebd5859c752759a0f714d
        inflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
<<<<<<< HEAD
        return circlePostListPojos.size();
=======
        return circleListData.size();
>>>>>>> 7a229364e04a7f07edcebd5859c752759a0f714d
    }

    @Override
    public Object getItem(int i) {
<<<<<<< HEAD
        return circlePostListPojos.get(i);
=======
        return circleListData.get(i);
>>>>>>> 7a229364e04a7f07edcebd5859c752759a0f714d
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
<<<<<<< HEAD
        final MyViewHolder viewHolder;

        if (view == null) {
            view = inflater.inflate(R.layout.circlepostitem, viewGroup, false);
            viewHolder = new MyViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (MyViewHolder) view.getTag();
        }
        return view;
=======


        View convertView = null;
        Circle_post_Adapter.MyViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.circlepostitem, viewGroup, false);
            viewHolder = new Circle_post_Adapter.MyViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (Circle_post_Adapter.MyViewHolder) convertView.getTag();
        }

        return convertView;
>>>>>>> 7a229364e04a7f07edcebd5859c752759a0f714d
    }

    private class MyViewHolder {

        LayoutInflater inflater;
        LinearLayout lcircle_post_comment, lcircle_post_likes, lfollowing, lcircle_post_channel;
        CircleImageView cvcircle_post_profile;
        TextView tcircle_post_name, tcirce_post_seen, tcircle_post_time, tcircle_post_cap,
                tcircle_post_commentno, tcircle_post_likes, tfollowing, tcircle_post_channel;
        ImageView icircle_post_pic, icircle_post_likes, icircle_post_share;

        public MyViewHolder(View item) {

            cvcircle_post_profile = (CircleImageView) item.findViewById(R.id.cvcircle_post_profile);
            tcircle_post_name = (TextView) item.findViewById(R.id.tcircle_post_name);
            tcirce_post_seen = (TextView) item.findViewById(R.id.tcirce_post_seen);
            tcircle_post_time = (TextView) item.findViewById(R.id.tcircle_post_time);
            tcircle_post_cap = (TextView) item.findViewById(R.id.tcircle_post_cap);
            tcircle_post_commentno = (TextView) item.findViewById(R.id.tcircle_post_commentno);
            tcircle_post_likes = (TextView) item.findViewById(R.id.tcircle_post_likes);
            tfollowing = (TextView) item.findViewById(R.id.tfollowing);
            tcircle_post_channel = (TextView) item.findViewById(R.id.tcircle_post_channel);

            icircle_post_pic = (ImageView) item.findViewById(R.id.icircle_post_pic);
            icircle_post_likes = (ImageView) item.findViewById(R.id.icircle_post_likes);
            icircle_post_share = (ImageView) item.findViewById(R.id.icircle_post_share);

            lcircle_post_comment = (LinearLayout) item.findViewById(R.id.lcircle_post_comment);
            lcircle_post_likes = (LinearLayout) item.findViewById(R.id.lcircle_post_likes);
            lfollowing = (LinearLayout) item.findViewById(R.id.lfollowing);
            lcircle_post_channel = (LinearLayout) item.findViewById(R.id.lcircle_post_channel);

        }
    }
}