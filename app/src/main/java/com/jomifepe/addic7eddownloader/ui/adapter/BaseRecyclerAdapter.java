package com.jomifepe.addic7eddownloader.ui.adapter;

import android.support.v7.widget.RecyclerView;

import com.jomifepe.addic7eddownloader.ui.adapter.listener.RecyclerViewItemShortClick;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseRecyclerAdapter<E, V extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<V> {

    protected List<E> listData;
    protected RecyclerViewItemShortClick itemClickListener;

    public BaseRecyclerAdapter(RecyclerViewItemShortClick itemClickListener) {
        this.listData = new ArrayList<>();
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void addItems(List<E> es) {
        final int previousCount = listData.size();
        this.listData.addAll(es);
        notifyItemRangeInserted(previousCount, listData.size());
    }

    public void addItem(E e) {
        this.listData.add(e);
        notifyDataSetChanged();
    }

    public void setList(List<E> es) {
        this.listData = es;
        notifyDataSetChanged();
    }

    public List<E> getList() {
        return listData;
    }

    public E getItem(int index) {
        return listData.get(index);
    }
}
