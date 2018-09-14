package com.jomifepe.addic7eddownloader.ui.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class BaseRecyclerAdapter<E, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V>{
    protected ArrayList<E> listData;
    protected RecyclerViewItemClickListener itemClickListener;

    public BaseRecyclerAdapter(RecyclerViewItemClickListener itemClickListener) {
        this.listData = new ArrayList<>();
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void addItems(ArrayList<E> es) {
        this.listData.addAll(es);
        notifyDataSetChanged();
    }

    public void addItem(E... es) {
        this.listData.addAll(Arrays.asList(es));
        notifyDataSetChanged();
    }

    public void setList(ArrayList<E> es) {
        this.listData = es;
        notifyDataSetChanged();
    }

    public ArrayList<E> getList() {
        return listData;
    }

    public E getItem(int index) {
        return listData.get(index);
    }
}
