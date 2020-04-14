package com.breezytechdevelopers.healthapp.ui.auth;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.breezytechdevelopers.healthapp.database.entities.PingHistory;
import com.breezytechdevelopers.healthapp.databinding.ItemPingHistoryBinding;

import java.util.List;

public class PingHistoryAdapter extends RecyclerView.Adapter<PingHistoryAdapter.PingHistoryViewHolder> {

    private List<? extends PingHistory> mPingHistoryList;

    public void setPingHistoryList(final List<? extends PingHistory> pingHistoryList) {
        mPingHistoryList = pingHistoryList;
        notifyDataSetChanged();
    }

    @Override
    @NonNull
    public PingHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPingHistoryBinding binding = ItemPingHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PingHistoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PingHistoryViewHolder holder, int position) {
        holder.binding.pingHeader.setText(mPingHistoryList.get(position).getCustomDate());
        holder.binding.pingBody.setText(mPingHistoryList.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return mPingHistoryList == null ? 0 : mPingHistoryList.size();
    }

    static class PingHistoryViewHolder extends RecyclerView.ViewHolder {

        final ItemPingHistoryBinding binding;

        PingHistoryViewHolder(ItemPingHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
