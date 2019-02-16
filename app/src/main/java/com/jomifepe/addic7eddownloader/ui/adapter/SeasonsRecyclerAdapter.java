package com.jomifepe.addic7eddownloader.ui.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.model.Season;
import com.jomifepe.addic7eddownloader.ui.adapter.listener.RecyclerViewItemShortClick;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SeasonsRecyclerAdapter extends BaseRecyclerAdapter<Season, SeasonsRecyclerAdapter.ViewHolder> {

    public SeasonsRecyclerAdapter(RecyclerViewItemShortClick itemClickListener) {
        super(itemClickListener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_seasons, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(v -> itemClickListener.onItemShortClick(v, viewHolder.getLayoutPosition()));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Season season = listData.get(position);
        holder.txtDescription.setText(String.format(Locale.getDefault(), "Season %d", season.getNumber()));
        Integer numberOfEpisodes = season.getNumberOfEpisodes();
        if (numberOfEpisodes != null) {
            holder.txtNumOfEpisodes.setVisibility(View.VISIBLE);
            holder.txtNumOfEpisodes.setText(String.format(Locale.getDefault(), "%d episodes", numberOfEpisodes));
        } else {
            holder.txtNumOfEpisodes.setVisibility(View.GONE);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_item_list_seasons_description) TextView txtDescription;
        @BindView(R.id.tv_item_list_season_num_of_episodes) TextView txtNumOfEpisodes;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
