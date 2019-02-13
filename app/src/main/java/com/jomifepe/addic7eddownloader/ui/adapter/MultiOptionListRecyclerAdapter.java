package com.jomifepe.addic7eddownloader.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.model.Season;
import com.jomifepe.addic7eddownloader.ui.adapter.listener.RecyclerViewItemShortClick;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MultiOptionListRecyclerAdapter extends BaseRecyclerAdapter<Pair<Integer, String>,
        MultiOptionListRecyclerAdapter.MultiOptionListRecyclerViewHolder> {

    public MultiOptionListRecyclerAdapter(RecyclerViewItemShortClick itemClickListener) {
        super(itemClickListener);
    }

    @NonNull
    @Override
    public MultiOptionListRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_simple_option, parent, false);
        MultiOptionListRecyclerViewHolder viewHolder = new MultiOptionListRecyclerViewHolder(view);
        view.setOnClickListener(v -> itemClickListener
                .onItemShortClick(v, viewHolder.getLayoutPosition()));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MultiOptionListRecyclerViewHolder holder, int position) {
        Pair<Integer, String> option = listData.get(position);
        Integer iconRes = option.first;
        if (iconRes != null) {
            holder.imgOptionIcon.setVisibility(View.VISIBLE);
            holder.imgOptionIcon.setImageResource(iconRes);
        } else {
            holder.imgOptionIcon.setVisibility(View.GONE);
        }
        holder.tvOptionText.setText(option.second);
    }

    static class MultiOptionListRecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_item_multi_option_text) TextView tvOptionText;
        @BindView(R.id.img_item_multi_option_icon) ImageView imgOptionIcon;

        MultiOptionListRecyclerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
