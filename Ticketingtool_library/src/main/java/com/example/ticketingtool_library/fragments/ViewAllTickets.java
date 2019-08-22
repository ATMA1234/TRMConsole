package com.example.ticketingtool_library.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ticketingtool_library.MainActivity;
import com.example.ticketingtool_library.R;
import com.example.ticketingtool_library.adapter.ViewTicketAdapter;
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

public class ViewAllTickets extends Fragment implements View.OnClickListener {
    private RecyclerView recyclerView;
    private ViewTicketAdapter viewTicketAdapter;
    private List<TicketDetails> ticketList;
    private FloatingActionButton floatingActionButton;
    private ProgressDialog progressDialog;
    private List<LoginDetails> loginList;
    private FunctionCall functionCall;
    private EditText editText;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView imageView;

    public ViewAllTickets() {
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @SuppressLint("RestrictedApi")
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case REQUEST_RESULT_SUCCESS:
                    progressDialog.dismiss();
                    break;

                case REQUEST_RESULT_FAILURE:
                    progressDialog.dismiss();
                    imageView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.GONE);
                    floatingActionButton.setVisibility(View.GONE);
                    break;
            }
            return false;
        }
    });


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_ticket, container, false);
        initialize(view);
        setHasOptionsMenu(true);
        return view;
    }

    //---------------------------------------------------------------------------------------------------------------
    private void initialize(View view) {
        swipeRefreshLayout = view.findViewById(R.id.simpleSwipeRefreshLayout);
        editText = ((MainActivity) Objects.requireNonNull(getActivity())).getEditText();
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                viewTicketAdapter.getFilter().filter(c.toString());
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
            }

            public void afterTextChanged(Editable c) {
                viewTicketAdapter.getFilter().filter(c.toString());
            }
        });
        imageView=view.findViewById(R.id.img_no_data);
        functionCall = new FunctionCall();
        progressDialog = new ProgressDialog(getActivity());
        recyclerView = view.findViewById(R.id.rec_view_ticket);
        floatingActionButton = view.findViewById(R.id.fab_new_ticket);
        floatingActionButton.setOnClickListener(this);
        Bundle bundle = getArguments();
        loginList = (ArrayList<LoginDetails>) Objects.requireNonNull(bundle).getSerializable("loginList");
        functionCall.showprogressdialog("Please wait to complete", "Data Loading", progressDialog);
        ticketData(loginList.get(0).getSUBDIVCODE(), loginList.get(0).getCOMPANY_LEVEL_ID());

        //--------------------------------------------------------------------------------------------------------------------------
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.color_section, R.color.indigo);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
                ticketList.clear();
                ticketData(loginList.get(0).getSUBDIVCODE(), loginList.get(0).getCOMPANY_LEVEL_ID());
            }, 2000L);
        });
    }

    //---------------------------------------------------------------------------------------------------------------------
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_new_ticket) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("loginList", (Serializable) loginList);
            startActivity(intent);
            Objects.requireNonNull(getActivity()).finish();
        }
    }

    //---------------------------------------------------------------------------------------------------------------------
    private void ticketData(String subdiv_code,String comp_id) {
        RetroClient retroClient = new RetroClient();
        RegisterAPI api = retroClient.getApiService();
        api.getTicketDetails(subdiv_code, comp_id).enqueue(new Callback<List<TicketDetails>>() {
            @Override
            public void onResponse(@NonNull Call<List<TicketDetails>> call, @NonNull Response<List<TicketDetails>> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    ticketList = response.body();
                    viewTicketAdapter = new ViewTicketAdapter(ticketList, getActivity());
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(viewTicketAdapter);
                    viewTicketAdapter.notifyDataSetChanged();
                    handler.sendEmptyMessage(REQUEST_RESULT_SUCCESS);
                } else handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
            }

            @Override
            public void onFailure(@NonNull Call<List<TicketDetails>> call, @NonNull Throwable t) {
                handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
            }
        });
    }

    //-------------------------------------------------------------------------------------------------------------------------
}
