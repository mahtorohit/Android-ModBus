package com.example.rohit.modbus.Data;

/**
 * Created by Rohit on 20/03/16.
 */


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rohit.modbus.R;

import java.util.ArrayList;

public class ModListAdapter extends RecyclerView
        .Adapter<ModListAdapter.RegObjectHolder> {
    private static String LOG_TAG = "ModListAdapter";
    private ArrayList<RegisterValue> mDataset;
    private static ModClickListener modClickListener;

    public static class RegObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView regId;
        TextView regValue;

        public RegObjectHolder(View itemView) {
            super(itemView);
            regId = (TextView) itemView.findViewById(R.id.regId);
            regValue = (TextView) itemView.findViewById(R.id.regVal);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            modClickListener.onItemClick(getPosition(), v);
        }
    }

    public void setOnItemClickListener(ModClickListener modClickListener) {
        this.modClickListener = modClickListener;
    }

    public ModListAdapter(ArrayList<RegisterValue> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public RegObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);

        RegObjectHolder dataObjectHolder = new RegObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(RegObjectHolder holder, int position) {
        holder.regId.setText("" + mDataset.get(position).getRegId());
        holder.regValue.setText("" + mDataset.get(position).getRegValue());
    }

    public void addItem(RegisterValue dataObj, int index) {
        mDataset.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface ModClickListener {
        public void onItemClick(int position, View v);
    }
}