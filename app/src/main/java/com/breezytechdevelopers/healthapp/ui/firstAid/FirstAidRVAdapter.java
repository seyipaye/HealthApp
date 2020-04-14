package com.breezytechdevelopers.healthapp.ui.firstAid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.breezytechdevelopers.healthapp.R;
import com.breezytechdevelopers.healthapp.database.entities.FirstAidTip;

import java.util.List;

public class FirstAidRVAdapter extends RecyclerView.Adapter<FirstAidRVAdapter.FirstAidRVAdapterViewHolder> {

    private final FragmentManager fragmentManager;
    private List<FirstAidTip> firstAidTips;
    private FirstAidInfoFragment firstAidInfoFragment;

    public FirstAidRVAdapter(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        this.firstAidInfoFragment = new FirstAidInfoFragment();
    }

    @NonNull
    @Override
    public FirstAidRVAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_first_aid, parent, false);
        return new FirstAidRVAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FirstAidRVAdapterViewHolder holder, int position) {

        if (firstAidTips != null) {
            final FirstAidTip current = firstAidTips.get(position);
            holder.title.setText(current.getAilment());

            final TextView textView = holder.body;
            textView.setText(
                    String.format("%s %s", current.getSymptoms(), current.getDos()));
            holder.card.setOnClickListener(v -> {
                if (firstAidInfoFragment == null) firstAidInfoFragment = new FirstAidInfoFragment();
                if (!(firstAidInfoFragment.isAdded())) {
                    firstAidInfoFragment.setContents(current);
                    firstAidInfoFragment.show(fragmentManager, "Info");
                }
            });
        } else {
            // Covers the case of data not being ready yet.
            holder.title.setText(R.string.noTip);
        }
    }

    public void setTips(List<FirstAidTip> firstAidTips) {
        this.firstAidTips = firstAidTips;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (firstAidTips != null)
            return firstAidTips.size();
        else
            return 0;
    }

    public class FirstAidRVAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView title, body;
        CardView card;

        public FirstAidRVAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            body = itemView.findViewById(R.id.body);
            card = itemView.findViewById(R.id.card);
        }
    }
}
