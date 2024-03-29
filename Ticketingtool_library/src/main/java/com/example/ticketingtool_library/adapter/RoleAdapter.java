package com.example.ticketingtool_library.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ticketingtool_library.R;
import com.example.ticketingtool_library.model.TicketDetails;

import java.util.ArrayList;


public class RoleAdapter extends BaseAdapter {
    private ArrayList<TicketDetails> arrayList;
    private LayoutInflater inflater;

    public RoleAdapter(ArrayList<TicketDetails> arrayList, Context context) {
        this.arrayList = arrayList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.spinner_items, null);
        TextView tv_role = convertView.findViewById(R.id.spinner_txt);
        tv_role.setText(arrayList.get(position).getUSER_ROLE());
        return convertView;
    }
}
