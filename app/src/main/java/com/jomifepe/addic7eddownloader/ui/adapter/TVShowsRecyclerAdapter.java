package com.jomifepe.addic7eddownloader.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.model.TVShow;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TVShowsRecyclerAdapter
        extends BaseRecyclerAdapter<TVShow, TVShowsRecyclerAdapter.TVShowsRecyclerViewHolder>
        implements Filterable {

    public static final int RESULTS_PER_PAGE = 30;
    private List<TVShow> originalList;
    private List<TVShow> filteredList;

    public TVShowsRecyclerAdapter(RecyclerViewItemClick itemClickListener) {
        super(itemClickListener);
    }

    public List<TVShow> getOriginalList() {
        return originalList;
    }

    public TVShowsRecyclerAdapter setOriginalList(List<TVShow> originalList) {
        this.originalList = originalList;
        return this;
    }

    @Override
    public void setList(List<TVShow> tvShows) {
        int endRange = Math.min(RESULTS_PER_PAGE, tvShows.size());
        this.listData = new ArrayList<>(tvShows.subList(0, endRange));
        this.originalList = new ArrayList<>(tvShows);
        this.filteredList = new ArrayList<>(tvShows);
        notifyDataSetChanged();
    }

    public void loadNewItems() {
        int startRange = listData.size();
        int endRange = Math.min(startRange + RESULTS_PER_PAGE, filteredList.size());
        List<TVShow> newItems = filteredList.subList(startRange, endRange);
        listData.addAll(newItems);
        notifyItemRangeInserted(startRange, endRange);
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

                String searchQuery = charSequence.toString().trim();
                List<TVShow> filteredList = new ArrayList<>();
                for (TVShow item : originalList) {
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
                filteredList = (List<TVShow>) filterResults.values;
                listData = new ArrayList<>(filteredList
                        .subList(0, Math.min(RESULTS_PER_PAGE, filteredList.size())));
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
