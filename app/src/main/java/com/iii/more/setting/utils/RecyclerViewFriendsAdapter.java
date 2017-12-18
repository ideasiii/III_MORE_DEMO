package com.iii.more.setting.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iii.more.main.R;
import com.iii.more.setting.AlarmLv1Activity;

import java.util.Collections;
import java.util.List;

/**
 * TODO: 此頁說明
 *
 * @author ReadyChen
 */

public class RecyclerViewFriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AlarmLv1Activity.Alarm> alarms = Collections.emptyList();
    private LayoutInflater mInflater;

    // header listener
    private DelToggleListener mDelToggleListener;
    private AddToggleListener mAddToggleListener;
    // body listener
    private ItemClickListener mItemClickListener;
    private DelClickListener mDelClickListener;

    // data is passed into the constructor
    public RecyclerViewFriendsAdapter(Context context, List<AlarmLv1Activity.Alarm> alarms) {
        this.mInflater = LayoutInflater.from(context);
        this.alarms = alarms;
    }

    private final int ITEM_HEADER = 0;
    private final int ITEM_BODY = 1;

    @Override
    public int getItemViewType(int position) {
        return alarms.get(position).itemType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder returnView = null;
        switch (viewType) {
            case ITEM_HEADER: {
                View view = mInflater.inflate(R.layout.alarm_item_header, parent, false);
                ViewHolderHeader viewHolderHeader = new ViewHolderHeader(view);
                returnView = viewHolderHeader;
            }
            break;
            case ITEM_BODY: {
                View view = mInflater.inflate(R.layout.alarm_item_body, parent, false);
                ViewHolderBody viewHolderBody = new ViewHolderBody(view);
                returnView = viewHolderBody;
            }
            break;
        }
        return returnView;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderHeader) {
            AlarmLv1Activity.Alarm alarm = alarms.get(position);
            ((ViewHolderHeader) holder).title.setText(alarm.name);
        } else if (holder instanceof ViewHolderBody) {
            AlarmLv1Activity.Alarm alarm = alarms.get(position);
            ViewHolderBody bodyHolder = (ViewHolderBody) holder;
            bodyHolder.time.setText(alarm.time);
            bodyHolder.name.setText(alarm.name);
            bodyHolder.story.setText(alarm.story);

            if( alarm.bShowDel ) {
                bodyHolder.ivDel.setVisibility(View.VISIBLE);
            } else {
                bodyHolder.ivDel.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    public class ViewHolderHeader extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title;
        public ImageView ivDel;
        public ImageView ivAdd;

        public ViewHolderHeader(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            ivDel = (ImageView) itemView.findViewById(R.id.ivDel);
            ivAdd = (ImageView) itemView.findViewById(R.id.ivAdd);
            ivDel.setTag("delToggle");
            ivAdd.setTag("addToggle");
            ivDel.setOnClickListener(this);
            ivAdd.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String tag = (String) view.getTag();
            if (tag != null) {
                if (tag.equals("delToggle")) {
                    if (mDelToggleListener != null) {
                        mDelToggleListener.onDelToggle(view, getAdapterPosition());
                    }
                }
                if (tag.equals("addToggle")) {
                    if (mAddToggleListener != null) {
                        mAddToggleListener.onAddToggle(view, getAdapterPosition());
                    }
                }
            }
        }
    }

    public class ViewHolderBody extends RecyclerView.ViewHolder implements View.OnClickListener {
        public LinearLayout llItem;
        public TextView name;
        public TextView time;
        public TextView story;
        public ImageView ivDel;

        public ViewHolderBody(View itemView) {
            super(itemView);
            llItem = (LinearLayout) itemView.findViewById(R.id.llItem);
            time = (TextView) itemView.findViewById(R.id.time);
            name = (TextView) itemView.findViewById(R.id.name);
            story = (TextView) itemView.findViewById(R.id.story);
            ivDel = (ImageView) itemView.findViewById(R.id.ivDel);

            llItem.setTag("llItem");
            ivDel.setTag("ivDel");
            llItem.setOnClickListener(this);
            ivDel.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String tag = (String) view.getTag();
            if (tag != null) {
                if (tag.equals("llItem")) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(view, getAdapterPosition());
                    }
                }
                if (tag.equals("ivDel")) {
                    if (mDelClickListener != null) {
                        mDelClickListener.onDelClick(view, getAdapterPosition());
                    }
                }
            }
        }
    }

    public String getItem(int position) {
        return alarms.get(position).name;
    }

    public void setDelToggleListener(DelToggleListener delToggleListener) {
        this.mDelToggleListener = delToggleListener;
    }
    public void setAddToggleListener(AddToggleListener addToggleListener) {
        this.mAddToggleListener = addToggleListener;
    }
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }
    public void setDelClickListener(DelClickListener delClickListener) {
        this.mDelClickListener = delClickListener;
    }

    public interface DelToggleListener {
        void onDelToggle(View view, int position);
    }
    public interface AddToggleListener {
        void onAddToggle(View view, int position);
    }
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    public interface DelClickListener {
        void onDelClick(View view, int position);
    }
}