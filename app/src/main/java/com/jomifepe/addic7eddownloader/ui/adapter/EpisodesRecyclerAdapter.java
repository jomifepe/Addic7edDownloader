package com.jomifepe.addic7eddownloader.ui.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.model.Episode;
import com.jomifepe.addic7eddownloader.ui.adapter.listener.RecyclerViewItemShortClick;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EpisodesRecyclerAdapter extends BaseRecyclerAdapter<Episode, EpisodesRecyclerAdapter.ViewHolder> {

    public EpisodesRecyclerAdapter(RecyclerViewItemShortClick itemClickListener) {
        super(itemClickListener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_episodes, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(v -> itemClickListener.onItemShortClick(v, viewHolder.getLayoutPosition()));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Episode episode = listData.get(position);
        holder.txtNumber.setText(String.format(Locale.getDefault(), "Episode %d", episode.getNumber()));
        holder.txtTitle.setText(episode.getTitle());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_list_episodes_txtNumber) TextView txtNumber;
        @BindView(R.id.item_list_episodes_txtTitle) TextView txtTitle;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
