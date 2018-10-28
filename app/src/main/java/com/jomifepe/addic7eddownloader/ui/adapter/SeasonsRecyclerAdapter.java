package com.jomifepe.addic7eddownloader.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.model.Season;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SeasonsRecyclerAdapter extends BaseRecyclerAdapter<Season, SeasonsRecyclerAdapter.SeasonsRecyclerViewHolder> {

    public SeasonsRecyclerAdapter(RecyclerViewItemClick itemClickListener) {
        super(itemClickListener);
    }

    @NonNull
    @Override
    public SeasonsRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_seasons, parent, false);
        SeasonsRecyclerViewHolder viewHolder = new SeasonsRecyclerViewHolder(view);
        view.setOnClickListener(v -> itemClickListener.onItemClick(v, viewHolder.getLayoutPosition()));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SeasonsRecyclerViewHolder holder, int position) {
        Season season = listData.get(position);
        holder.txtDescription.setText(String.format(Locale.getDefault(), "Season %d", season.getNumber()));
        holder.txtNumOfEpisodes.setText(String.format(Locale.getDefault(), "%d episodes", season.getNumberOfEpisodes()));
    }

    static class SeasonsRecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_list_seasons_txtDescription) TextView txtDescription;
        @BindView(R.id.item_list_season_txtNumOfEpisodes) TextView txtNumOfEpisodes;

        public SeasonsRecyclerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
