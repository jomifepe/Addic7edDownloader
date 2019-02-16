package com.jomifepe.addic7eddownloader.ui.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.jomifepe.addic7eddownloader.R;
import com.jomifepe.addic7eddownloader.model.Show;
import com.jomifepe.addic7eddownloader.ui.adapter.listener.RecyclerViewItemLongClick;
import com.jomifepe.addic7eddownloader.ui.adapter.listener.RecyclerViewItemShortClick;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowsRecyclerAdapter
        extends BaseRecyclerAdapter<Show, ShowsRecyclerAdapter.ViewHolder>
        implements Filterable {

    private RecyclerViewItemLongClick itemLongClickListener;

    public static final int RESULTS_PER_PAGE = 30;
    private List<Show> originalList;
    private List<Show> filteredList;

    public ShowsRecyclerAdapter(RecyclerViewItemShortClick itemShortClickListener,
                                RecyclerViewItemLongClick itemLongClickListener) {
        super(itemShortClickListener);
        this.itemLongClickListener = itemLongClickListener;
    }

    public List<Show> getOriginalList() {
        return originalList;
    }

    public ShowsRecyclerAdapter setOriginalList(List<Show> originalList) {
        this.originalList = originalList;
        return this;
    }

    @Override
    public void setList(List<Show> shows) {
        int endRange = Math.min(RESULTS_PER_PAGE, shows.size());
        this.listData = new ArrayList<>(shows.subList(0, endRange));
        this.originalList = new ArrayList<>(shows);
        this.filteredList = new ArrayList<>(shows);
        notifyDataSetChanged();
    }

    public void loadNewItems() {
        int startRange = listData.size();
        int endRange = Math.min(startRange + RESULTS_PER_PAGE, filteredList.size());
        List<Show> newItems = filteredList.subList(startRange, endRange);
        listData.addAll(newItems);
        notifyItemRangeInserted(startRange, endRange);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_shows, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(v -> itemClickListener
                .onItemShortClick(v, viewHolder.getLayoutPosition()));
        view.setOnLongClickListener(v -> itemLongClickListener
            .onItemLongClick(v, viewHolder.getLayoutPosition()));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Show show = listData.get(position);
        holder.txtTitle.setText(show.getTitle());
        holder.txtId.setText(String.format(Locale.getDefault(), "%d", show.getAddic7edId()));
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String searchQuery = charSequence.toString().trim();
                List<Show> filteredList = new ArrayList<>();
                for (Show item : originalList) {
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
                filteredList = (List<Show>) filterResults.values;
                listData = new ArrayList<>(filteredList
                        .subList(0, Math.min(RESULTS_PER_PAGE, filteredList.size())));
                notifyDataSetChanged();
            }
        };
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_list_tvshows_txtId) TextView txtId;
        @BindView(R.id.item_list_tvshows_txtTitle) TextView txtTitle;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
