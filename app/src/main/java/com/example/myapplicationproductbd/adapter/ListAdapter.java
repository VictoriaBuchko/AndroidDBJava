package com.example.myapplicationproductbd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationproductbd.R;
import com.example.myapplicationproductbd.model.ShoppingList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {
    public interface OnListClickListener {
        void onListClick(ShoppingList list);
        void onListLongClick(ShoppingList list);
        void onListDelete(ShoppingList list);
    }

    private List<ShoppingList> lists;
    private final LayoutInflater inflater;
    private final OnListClickListener listener;

    public ListAdapter(Context context, List<ShoppingList> lists, OnListClickListener listener) {
        this.lists = lists;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    public void updateData(List<ShoppingList> newLists) {
        this.lists = newLists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_list, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        ShoppingList list = lists.get(position);

        holder.tvName.setText(list.getName());

        //дата
        String dateStr = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                .format(new Date(list.getDate() * 1000));
        holder.tvDate.setText(dateStr);

        //опис
        if (list.getDescription() != null && !list.getDescription().isEmpty()) {
            holder.tvDesc.setText(list.getDescription());
            holder.tvDesc.setVisibility(View.VISIBLE);
        } else {
            holder.tvDesc.setVisibility(View.GONE);
        }

        //куплено / всього
        holder.tvCount.setText(list.getCheckedCount() + " / " + list.getTotalCount());

        //натискання на списокпереходить до покупок, а затискання відкриває редагування списку
        holder.itemView.setOnClickListener(v -> listener.onListClick(list));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onListLongClick(list);
            return true;
        });

        //видалення
        holder.btnDelete.setOnClickListener(v -> listener.onListDelete(list));
    }

    @Override
    public int getItemCount() { return lists.size(); }

    static class ListViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName, tvDate, tvDesc, tvCount;
        final Button btnDelete;

        ListViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvListName);
            tvDate = view.findViewById(R.id.tvListDate);
            tvDesc = view.findViewById(R.id.tvListDesc);
            tvCount = view.findViewById(R.id.tvListCount);
            btnDelete = view.findViewById(R.id.btnDeleteList);
        }
    }
}