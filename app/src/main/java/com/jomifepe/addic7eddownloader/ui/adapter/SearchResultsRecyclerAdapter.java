package com.jomifepe.addic7eddownloader.ui.adapter;

import android.os.Build;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.model.MediaType;
import com.jomifepe.addic7eddownloader.model.SearchResult;
import com.jomifepe.addic7eddownloader.ui.adapter.listener.RecyclerViewItemShortClick;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchResultsRecyclerAdapter
        extends BaseRecyclerAdapter<SearchResult, SearchResultsRecyclerAdapter.ViewHolder> {

    public SearchResultsRecyclerAdapter(RecyclerViewItemShortClick itemClickListener) {
        super(itemClickListener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_search_results, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(v -> itemClickListener.onItemShortClick(v, viewHolder.getLayoutPosition()));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchResult searchResult = listData.get(position);
        holder.tvDescription.setText(searchResult.getDescription());
        final int iconRes = searchResult.getType() == MediaType.SHOW ?
                R.drawable.round_ic_live_tv_24dp : R.drawable.round_ic_movie_24dp;
        holder.imgIcon.setImageResource(iconRes);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_item_list_search_icon) ImageView imgIcon;
        @BindView(R.id.tv_item_list_search_results_description) TextView tvDescription;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tvDescription.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
            }
        }
    }
}
