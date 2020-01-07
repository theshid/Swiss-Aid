package com.shid.swissaid.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shid.swissaid.Model.Draft;
import com.shid.swissaid.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class DraftListAdapter extends RecyclerView.Adapter<DraftListAdapter.ViewHolder> {

    private int draftItemLayout;
    private List<Draft> draftList;

    public DraftListAdapter(int layoutId) {
        draftItemLayout = layoutId;
    }

    public void setDraftList(List<Draft> drafts) {
        draftList = drafts;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return draftList == null ? 0 : draftList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()).inflate(draftItemLayout, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
        TextView item = holder.item;
        item.setText(draftList.get(listPosition).getNameProjectStep());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView item;

        ViewHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.draft_row);
        }
    }
}
