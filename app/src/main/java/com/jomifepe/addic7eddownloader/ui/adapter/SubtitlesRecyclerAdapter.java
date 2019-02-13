package com.jomifepe.addic7eddownloader.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.model.Subtitle;
import com.jomifepe.addic7eddownloader.ui.adapter.listener.RecyclerViewItemShortClick;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubtitlesRecyclerAdapter extends BaseRecyclerAdapter<Subtitle, SubtitlesRecyclerAdapter.SubtitlesRecyclerViewHolder> {

    public SubtitlesRecyclerAdapter(RecyclerViewItemShortClick itemClickListener) {
        super(itemClickListener);
    }

    @NonNull
    @Override
    public SubtitlesRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_subtitles, parent, false);
        SubtitlesRecyclerViewHolder viewHolder = new SubtitlesRecyclerViewHolder(view);
        view.setOnClickListener(v -> itemClickListener.onItemShortClick(v, viewHolder.getLayoutPosition()));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SubtitlesRecyclerViewHolder holder, int position) {
        Subtitle subtitle = listData.get(position);
        holder.txtDescription.setText(String.format(Locale.getDefault(), "%s - %s", subtitle.getLanguage(), subtitle.getVersion()));
        holder.imgHighDefinition.setVisibility(subtitle.isHd() ? View.VISIBLE : View.GONE);
        holder.imgHearingImpaired.setVisibility(subtitle.isHearingImpaired() ? View.VISIBLE : View.GONE);
        holder.imgCorrected.setVisibility(subtitle.isCorrected() ? View.VISIBLE : View.GONE);
    }

    static class SubtitlesRecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_list_subtitles_txtDescription) TextView txtDescription;
        @BindView(R.id.item_list_subtitles_imgHighDefinition) ImageView imgHighDefinition;
        @BindView(R.id.item_list_subtitles_imgHearingImpaired) ImageView imgHearingImpaired;
        @BindView(R.id.item_list_subtitles_imgCorrected) ImageView imgCorrected;

        public SubtitlesRecyclerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
