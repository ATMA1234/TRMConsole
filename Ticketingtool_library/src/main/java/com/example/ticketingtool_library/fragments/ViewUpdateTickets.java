package com.example.ticketingtool_library.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ticketingtool_library.MainActivity;
import com.example.ticketingtool_library.R;
import com.example.ticketingtool_library.adapter.ViewTicketAdapter;
import com.example.ticketingtool_library.adapter.ViewUpdateTicketAdapter;
import com.example.ticketingtool_library.api.RegisterAPI;
import com.example.ticketingtool_library.api.RetroClient;
import com.example.ticketingtool_library.model.LoginDetails;
import com.example.ticketingtool_library.model.TicketDetails;
import com.example.ticketingtool_library.values.FunctionCall;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.ticketingtool_library.values.constant.REQUEST_RESULT_FAILURE;
import static com.example.ticketingtool_library.values.constant.REQUEST_RESULT_SUCCESS;

public class ViewUpdateTickets extends Fragment {

    public ViewUpdateTickets() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_ticket, container, false);
        initialize(view);
        return view;
    }

    //---------------------------------------------------------------------------------------------------------------
    @SuppressLint("RestrictedApi")
    private void initialize(View view) {
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.simpleSwipeRefreshLayout);
        EditText editText = ((MainActivity) Objects.requireNonNull(getActivity())).getEditText();
        editText.setVisibility(View.GONE);
        RecyclerView recyclerView = view.findViewById(R.id.rec_view_ticket);
        FloatingActionButton floatingActionButton = view.findViewById(R.id.fab_new_ticket);
        floatingActionButton.setVisibility(View.GONE);
        Bundle bundle = getArguments();
        List<TicketDetails> ticketDetails = (ArrayList<TicketDetails>) Objects.requireNonNull(bundle).getSerializable("ticketList");
        ViewUpdateTicketAdapter viewTicketAdapter = new ViewUpdateTicketAdapter(ticketDetails, getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(viewTicketAdapter);
        viewTicketAdapter.notifyDataSetChanged();

        //--------------------------------------------------------------------------------------------------------------------------
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.color_section, R.color.indigo);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
            }, 2000L);
        });
    }
}