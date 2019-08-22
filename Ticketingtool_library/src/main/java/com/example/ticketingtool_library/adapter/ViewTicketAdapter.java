package com.example.ticketingtool_library.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketingtool_library.MainActivity;
import com.example.ticketingtool_library.R;
import com.example.ticketingtool_library.fragments.ViewTicketDetails;
import com.example.ticketingtool_library.model.TicketDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ViewTicketAdapter extends RecyclerView.Adapter<ViewTicketAdapter.TicketHolder> implements Filterable {
    private List<TicketDetails> arrayList;
    private List<TicketDetails> filteredList;
    private Context context;

    public ViewTicketAdapter(List<TicketDetails> arrayList, Context context) {
        this.arrayList = arrayList;
        this.filteredList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public TicketHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_ticket_adapter, parent, false);
        return new TicketHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketHolder holder, int i) {
        holder.tv_tic_no.setText(arrayList.get(i).getTIC_ID());
        holder.tv_tic_gen_on.setText(arrayList.get(i).getTIC_GENON());
        holder.tv_tic_status.setText(arrayList.get(i).getTIC_STATUS());
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    //*********************************************** Filter *************************************************************
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String search = constraint.toString();
                if (search.isEmpty())
                    filteredList = arrayList;
                else {
                    List<TicketDetails> filterlist = new ArrayList<>();
                    for (int i = 0; i < arrayList.size(); i++) {
                        TicketDetails ticketDetails = arrayList.get(i);
                        if (ticketDetails.getTIC_ID().contains(search)) {
                            filterlist.add(ticketDetails);
                        }
                    }
                    filteredList = filterlist;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<TicketDetails>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    //********************************* TicketHolder ************************************************************
    public class TicketHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_tic_no, tv_tic_gen_on, tv_tic_status;

        private TicketHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tv_tic_no = itemView.findViewById(R.id.tic_no);
            tv_tic_gen_on = itemView.findViewById(R.id.tic_gen_on);
            tv_tic_status = itemView.findViewById(R.id.tic_status);
        }

        //*****************************************************************************************************************************
        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            final TicketDetails details = arrayList.get(pos);
            arrayList.clear();
            arrayList.add(details);
            ((MainActivity) context).switchContent(MainActivity.Steps.FORM4, context.getResources().getString(R.string.view_ticket_details), arrayList);
        }
    }
}
