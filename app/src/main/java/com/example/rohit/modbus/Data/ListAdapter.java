package com.example.rohit.modbus.Data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.rohit.modbus.R;

import java.util.ArrayList;

/**
 * Created by Rohit on 20/03/16.
 */
public class ListAdapter extends ArrayAdapter<RegisterValue> {
    private ArrayList<RegisterValue> objects;
    public ListAdapter(Context context, int textViewResourceId, ArrayList<RegisterValue> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.recyclerview_item, null);
        }
        RegisterValue reg = objects.get(position);
        TextView regId = (TextView) v.findViewById(R.id.regId);
        regId.setText(String.valueOf(reg.getRegId()));
        TextView regVal = (TextView) v.findViewById(R.id.regVal);
        regVal.setText(String.valueOf(reg.getRegValue()));
        return v;
    }

}