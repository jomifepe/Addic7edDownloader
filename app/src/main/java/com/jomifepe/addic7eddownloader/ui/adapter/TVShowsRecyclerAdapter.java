package com.jomifepe.addic7eddownloader.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.model.TVShow;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TVShowsRecyclerAdapter
        extends BaseRecyclerAdapter<TVShow, TVShowsRecyclerAdapter.TVShowsRecyclerViewHolder>
        implements Filterable {

    public TVShowsRecyclerAdapter(RecyclerViewItemClick itemClickListener) {
        super(itemClickListener);
    }

    @NonNull
    @Override
    public TVShowsRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_tvshows, parent, false);
        TVShowsRecyclerViewHolder viewHolder = new TVShowsRecyclerViewHolder(view);
        view.setOnClickListener(v -> itemClickListener.onItemClick(v, viewHolder.getLayoutPosition()));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TVShowsRecyclerViewHolder holder, int position) {
        TVShow show = listData.get(position);
        holder.txtTitle.setText(show.getTitle());
        holder.txtId.setText(String.format(Locale.getDefault(), "%d", show.getAddic7edId()));
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String searchQuery = charSequence.toString();
                ArrayList<TVShow> filteredList = new ArrayList<>();
                for (TVShow item : listData) {
                    if (item.getTitle().toLowerCase().contains(searchQuery.toLowerCase())) {
                        filteredList.add(item);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listData = (ArrayList<TVShow>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    static class TVShowsRecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_list_tvshows_txtId) TextView txtId;
        @BindView(R.id.item_list_tvshows_txtTitle) TextView txtTitle;

        public TVShowsRecyclerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
