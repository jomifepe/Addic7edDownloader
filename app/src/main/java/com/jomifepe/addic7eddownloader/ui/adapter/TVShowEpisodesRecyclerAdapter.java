package com.jomifepe.addic7eddownloader.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.model.Episode;
import com.jomifepe.addic7eddownloader.model.Season;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TVShowEpisodesRecyclerAdapter extends BaseRecyclerAdapter<Episode, TVShowEpisodesRecyclerAdapter.EpisodesRecyclerViewHolder> {

    public TVShowEpisodesRecyclerAdapter(RecyclerViewItemClickListener itemClickListener) {
        super(itemClickListener);
    }

    @NonNull
    @Override
    public EpisodesRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_episodes, parent, false);
        EpisodesRecyclerViewHolder viewHolder = new EpisodesRecyclerViewHolder(view);
        view.setOnClickListener(v -> itemClickListener.onItemClick(v, viewHolder.getLayoutPosition()));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodesRecyclerViewHolder holder, int position) {
        Episode episode = listData.get(position);
        holder.txtNumber.setText(String.format(Locale.getDefault(), "Episode %d", episode.getNumber()));
        holder.txtTitle.setText(episode.getTitle());
    }

    static class EpisodesRecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_list_episodes_txtNumber) TextView txtNumber;
        @BindView(R.id.item_list_episodes_txtTitle) TextView txtTitle;

        public EpisodesRecyclerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
