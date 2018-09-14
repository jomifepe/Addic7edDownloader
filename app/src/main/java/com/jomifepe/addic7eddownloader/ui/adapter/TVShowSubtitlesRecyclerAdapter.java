package com.jomifepe.addic7eddownloader.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.model.Subtitle;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TVShowSubtitlesRecyclerAdapter extends BaseRecyclerAdapter<Subtitle, TVShowSubtitlesRecyclerAdapter.SubtitlesRecyclerViewHolder> {

    public TVShowSubtitlesRecyclerAdapter(RecyclerViewItemClickListener itemClickListener) {
        super(itemClickListener);
    }

    @NonNull
    @Override
    public SubtitlesRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_subtitles, parent, false);
        SubtitlesRecyclerViewHolder viewHolder = new SubtitlesRecyclerViewHolder(view);
        view.setOnClickListener(v -> itemClickListener.onItemClick(v, viewHolder.getLayoutPosition()));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SubtitlesRecyclerViewHolder holder, int position) {
        Subtitle subtitle = listData.get(position);
        holder.txtName.setText(subtitle.toString());
    }

    static class SubtitlesRecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_list_subtitles_txtName) TextView txtName;

        public SubtitlesRecyclerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
