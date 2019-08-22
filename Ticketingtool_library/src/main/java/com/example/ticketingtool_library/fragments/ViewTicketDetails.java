package com.example.ticketingtool_library.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ticketingtool_library.MainActivity;
import com.example.ticketingtool_library.R;
import com.example.ticketingtool_library.api.RegisterAPI;
import com.example.ticketingtool_library.api.RetroClient;
import com.example.ticketingtool_library.invoke.FTPAPI;
import com.example.ticketingtool_library.model.TicketDetails;
import com.example.ticketingtool_library.values.FileOpen;
import com.example.ticketingtool_library.values.FunctionCall;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.ticketingtool_library.values.constant.DOWNLOAD_FILE_ERROR;
import static com.example.ticketingtool_library.values.constant.DOWNLOAD_FILE_FAILURE;
import static com.example.ticketingtool_library.values.constant.DOWNLOAD_FILE_SUCCESS;
import static com.example.ticketingtool_library.values.constant.REQUEST_RESULT_FAILURE;
import static com.example.ticketingtool_library.values.constant.REQUEST_RESULT_SUCCESS;


public class ViewTicketDetails extends Fragment implements View.OnClickListener {
    private static final int DLG_DOWNLOAD_PREVIEW = 1;
    private Button edit, details;
    private TextView tv_tic_id, tv_subdiv_code, tv_tic_file, tv_tic_gen_by, tv_tic_gen_on, tv_tic_closed_on, tv_tic_priority, tv_tic_severity, tv_tic_assign_to,
            tv_tic_dept, tv_tic_status, tv_tic_title, tv_tic_desc, tv_tic_narr, tv_tic_comm;
    private ArrayList<TicketDetails> ticketList;
    private List<TicketDetails> detailsList;
    private ImageView imageView;
    private FunctionCall functionCall;
    private ProgressDialog progressDialog;
    private FTPAPI ftpapi;
    private LinearLayout layout;
    EditText editText;

    //---------------------------------------------------------------------------------------------------------------------
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case DOWNLOAD_FILE_SUCCESS:
                    progressDialog.dismiss();
                    showdialog(DLG_DOWNLOAD_PREVIEW);
                    break;

                case DOWNLOAD_FILE_FAILURE:
                    progressDialog.dismiss();
                    functionCall.setSnackBar(Objects.requireNonNull(getActivity()),layout,"File Not Found");
                    break;

                case DOWNLOAD_FILE_ERROR:
                    progressDialog.dismiss();
                    functionCall.setSnackBar(Objects.requireNonNull(getActivity()),layout,"File is not downloading please check it");
                    break;

                case REQUEST_RESULT_SUCCESS:
                    progressDialog.dismiss();
                    ((MainActivity) Objects.requireNonNull(getActivity())).switchContent(MainActivity.Steps.FORM5,
                            getResources().getString(R.string.view_update_tickets), detailsList);
                    break;

                case REQUEST_RESULT_FAILURE:
                    progressDialog.dismiss();
                    functionCall.setSnackBar(Objects.requireNonNull(getActivity()),layout,"No Data Found");
                    break;
            }
            return false;
        }
    });

    public ViewTicketDetails() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_details, container, false);
        initialize(view);
        return view;
    }

    //-----------------------------------------------------------------------------------------------------------------------------------
    private void initialize(View view) {
        editText = ((MainActivity) Objects.requireNonNull(getActivity())).getEditText();
        editText.setVisibility(View.GONE);
        layout=view.findViewById(R.id.lin_basic);
        ftpapi = new FTPAPI();
        progressDialog = new ProgressDialog(getActivity());
        functionCall = new FunctionCall();
        Bundle bundle = getArguments();
        ticketList = (ArrayList<TicketDetails>) bundle.getSerializable("ticketList");
        edit = view.findViewById(R.id.btn_edit);
        edit.setOnClickListener(this);
        details = view.findViewById(R.id.btn_details);
        details.setOnClickListener(this);
        tv_tic_id = view.findViewById(R.id.txt_tic_id);
        tv_subdiv_code = view.findViewById(R.id.txt_tic_subdiv_code);
        tv_tic_file = view.findViewById(R.id.txt_tic_file);
        tv_tic_gen_by = view.findViewById(R.id.txt_tic_gen_by);
        tv_tic_gen_on = view.findViewById(R.id.txt_tic_gen_on);
        tv_tic_closed_on = view.findViewById(R.id.txt_tic_close);
        tv_tic_priority = view.findViewById(R.id.txt_tic_priority);
        tv_tic_severity = view.findViewById(R.id.txt_tic_severity);
        tv_tic_assign_to = view.findViewById(R.id.txt_tic_assign_to);
        tv_tic_dept = view.findViewById(R.id.txt_tic_department);
        tv_tic_status = view.findViewById(R.id.txt_tic_status);
        tv_tic_title = view.findViewById(R.id.txt_tic_title);
        tv_tic_desc = view.findViewById(R.id.txt_tic_desc);
        tv_tic_narr = view.findViewById(R.id.txt_tic_narr);
        tv_tic_comm = view.findViewById(R.id.txt_tic_comm);
        imageView = view.findViewById(R.id.file_download);
        imageView.setOnClickListener(this);
        setData();
    }

    //------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_edit) {
            editTicket();
        } else if (i == R.id.btn_details) {
            functionCall.showprogressdialog("Please wait to complete.", "Downloading Data", progressDialog);
            ticketData(ticketList.get(0).getTIC_ID());
        } else if (i == R.id.file_download) {
            if (!TextUtils.isEmpty(ticketList.get(0).getTIC_FILE())) {
                functionCall.showprogressdialog("Please wait to complete.", "Downloading file", progressDialog);
                FTPAPI.Download_file download_file = ftpapi.new Download_file(ticketList.get(0).getTIC_FILE(), handler);
                download_file.execute();
            } else {
                imageView.setEnabled(false);
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------
    private void editTicket() {
        ((MainActivity) Objects.requireNonNull(getActivity())).switchContent(MainActivity.Steps.FORM1,
                getResources().getString(R.string.upadte), ticketList);
    }

    //------------------------------------------------------------------------------------------------------------------------------
    private void setData() {
        tv_tic_id.setText(ticketList.get(0).getTIC_ID());
        tv_subdiv_code.setText(ticketList.get(0).getTIC_SUBCODE());
        tv_tic_file.setText(ticketList.get(0).getTIC_FILE());
        tv_tic_gen_by.setText(ticketList.get(0).getTIC_GENBY());
        tv_tic_gen_on.setText(ticketList.get(0).getTIC_GENON());
        tv_tic_closed_on.setText(ticketList.get(0).getCLOSED_ON());
        tv_tic_priority.setText(ticketList.get(0).getPRIORITY());
        tv_tic_severity.setText(ticketList.get(0).getSEVIRITY());
        tv_tic_assign_to.setText(ticketList.get(0).getASSIGN());
        tv_tic_dept.setText(ticketList.get(0).getHESCOM());
        tv_tic_status.setText(ticketList.get(0).getTIC_STATUS());
        tv_tic_title.setText(ticketList.get(0).getTITLE());
        tv_tic_desc.setText(ticketList.get(0).getDESCRIPTION());
        tv_tic_narr.setText(ticketList.get(0).getTIC_NARR());
        tv_tic_comm.setText(ticketList.get(0).getCOMMENT());
    }

    //---------------------------------------------------------------------------------------------------------------------
    private void showdialog(int id) {
        AlertDialog dialog;
        if (id == DLG_DOWNLOAD_PREVIEW) {
            AlertDialog.Builder download = new AlertDialog.Builder(getActivity());
            download.setCancelable(false);
            download.setTitle("View file");
            download.setMessage("Do you want to view this file?");
            download.setPositiveButton("YES", (dialog1, which) -> {
                File myFile = new File(functionCall.filepath("Documents") + File.separator + ticketList.get(0).getTIC_FILE());
                FileOpen.openFile(getActivity(), myFile);
            });
            download.setNeutralButton("NO", null);
            dialog = download.create();
            dialog.show();
        }
    }

    //---------------------------------------------------------------------------------------------------------------------
    private void ticketData(String TIC_ID) {
        RetroClient retroClient=new RetroClient();
        RegisterAPI api = retroClient.getApiService();
        api.getUpdateDetails(TIC_ID).enqueue(new Callback<List<TicketDetails>>() {
            @Override
            public void onResponse(@NonNull Call<List<TicketDetails>> call, @NonNull Response<List<TicketDetails>> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    detailsList = response.body();
                    handler.sendEmptyMessage(REQUEST_RESULT_SUCCESS);
                } else handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
            }

            @Override
            public void onFailure(@NonNull Call<List<TicketDetails>> call, @NonNull Throwable t) {
                handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
            }
        });
    }
}
