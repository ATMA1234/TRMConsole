package com.example.ticketingtool_library.fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.ticketingtool_library.MainActivity;
import com.example.ticketingtool_library.R;
import com.example.ticketingtool_library.adapter.RoleAdapter;
import com.example.ticketingtool_library.api.RegisterAPI;
import com.example.ticketingtool_library.api.RetroClient;
import com.example.ticketingtool_library.model.LoginDetails;
import com.example.ticketingtool_library.model.TicketDetails;
import com.example.ticketingtool_library.values.FunctionCall;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.example.ticketingtool_library.MainActivity.Steps.FORM2;
import static com.example.ticketingtool_library.values.FunctionCall.getPath;
import static com.example.ticketingtool_library.values.constant.REQUEST_RESULT_FAILURE;
import static com.example.ticketingtool_library.values.constant.REQUEST_RESULT_SUCCESS;

public class UpdateTicket extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private EditText title, description, narration, comment, editText;
    private TextView tv_browse_file, tv_ticket_id;
    private Button submit;
    private Spinner priority, severity, assign_to, department, status;
    private ArrayList<TicketDetails> priority_list, severity_list, assign_list, dept_list, status_list;
    private RoleAdapter priority_adapter, severity_adapter, assigned_adapter, dept_adapter, status_adapter;
    private TicketDetails ticketDetails;
    private ArrayList<TicketDetails> ticketList;
    private ArrayList<LoginDetails> loginList;
    private String PRIORITY, SEVERITY, ASSIGN_TO, DEPARTMENT, STATUS;
    private TextInputLayout txt_input_comment;
    private LinearLayout lin_ticket_id;
    private static final int RESULT_CANCELED = 3;
    private static final int DLG_TICKET_UPDATE = 4;
    private static final int FILE_MANAGER = 5;
    private final int CAMERA = 2, GALLERY = 1;
    private ProgressDialog progressDialog;
    private FunctionCall functionCall;
    private String regex = "!'~@#$%^&*:;<>.,/}";
    private String ImageDecode = "", IMAGENAME = "", filepathImage = "";

    public UpdateTicket() {
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @SuppressLint("RestrictedApi")
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case REQUEST_RESULT_SUCCESS:
                    progressDialog.dismiss();
                    showdialog(DLG_TICKET_UPDATE);
                    break;

                case REQUEST_RESULT_FAILURE:
                    progressDialog.dismiss();
                    functionCall.setSnackBar(getActivity(), lin_ticket_id, "Ticket not updated");
                    break;
            }
            return false;
        }
    });

    //------------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_generate_ticket, container, false);
        initialize(view);
        SetSpinnerValues();
        setData();
        return view;
    }

    //------------------------------------------------------------------------------------------------
    @SuppressLint("SetTextI18n")
    private void initialize(View view) {
        editText = ((MainActivity) Objects.requireNonNull(getActivity())).getEditText();
        editText.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(getActivity());
        functionCall = new FunctionCall();
        Bundle bundle = getArguments();
        ticketList = (ArrayList<TicketDetails>) bundle.getSerializable("ticketList");
        loginList = (ArrayList<LoginDetails>) bundle.getSerializable("loginList");
        priority = view.findViewById(R.id.priority);
        priority.setOnItemSelectedListener(this);
        assign_to = view.findViewById(R.id.assign_to);
        assign_to.setOnItemSelectedListener(this);
        department = view.findViewById(R.id.department);
        department.setOnItemSelectedListener(this);
        status = view.findViewById(R.id.status);
        status.setOnItemSelectedListener(this);
        severity = view.findViewById(R.id.severity);
        severity.setOnItemSelectedListener(this);
        title = view.findViewById(R.id.et_title);
        title.setEnabled(false);
        description = view.findViewById(R.id.et_description);
        description.requestFocus();
        description.setFilters(new InputFilter[]{filter});
        narration = view.findViewById(R.id.et_narration);
        narration.setFilters(new InputFilter[]{filter});
        comment = view.findViewById(R.id.et_comment);
        comment.setFilters(new InputFilter[]{filter});
        txt_input_comment = view.findViewById(R.id.txt_input_comment);
        txt_input_comment.setVisibility(View.VISIBLE);
        tv_ticket_id = view.findViewById(R.id.txt_ticket_id);
        lin_ticket_id = view.findViewById(R.id.lin_ticket_id);
        lin_ticket_id.setVisibility(View.VISIBLE);
        submit = view.findViewById(R.id.btn_submit);
        submit.setText("Update");
        submit.setOnClickListener(this);
        tv_browse_file = view.findViewById(R.id.txt_browse_file);
        tv_browse_file.setOnClickListener(this);
        priority_list = new ArrayList<>();
        severity_list = new ArrayList<>();
        assign_list = new ArrayList<>();
        dept_list = new ArrayList<>();
        status_list = new ArrayList<>();
    }

    private void SetSpinnerValues() {
        //**************************Setting priority spinner*********************************************
        for (int i = 0; i < getResources().getStringArray(R.array.priority).length; i++) {
            ticketDetails = new TicketDetails();
            ticketDetails.setUSER_ROLE(getResources().getStringArray(R.array.priority)[i]);
            priority_list.add(ticketDetails);
            priority_adapter = new RoleAdapter(priority_list, getActivity());
            priority.setAdapter(priority_adapter);
            priority_adapter.notifyDataSetChanged();
        }
        for (int i = 0; i < priority_list.size(); i++) {
            TicketDetails details = priority_list.get(i);
            String value = ticketList.get(0).getPRIORITY();
            String value1 = details.getUSER_ROLE();
            if (value.equals(value1)) {
                priority.setSelection(i);
                break;
            }
        }
        //******************************Setting Severity_to spinner************************************************
        for (int i = 0; i < getResources().getStringArray(R.array.severity).length; i++) {
            ticketDetails = new TicketDetails();
            ticketDetails.setUSER_ROLE(getResources().getStringArray(R.array.severity)[i]);
            severity_list.add(ticketDetails);
            severity_adapter = new RoleAdapter(severity_list, getActivity());
            severity.setAdapter(severity_adapter);
            severity_adapter.notifyDataSetChanged();
        }
        for (int i = 0; i < severity_list.size(); i++) {
            TicketDetails details = severity_list.get(i);
            String value = ticketList.get(0).getSEVIRITY();
            String value1 = details.getUSER_ROLE();
            if (value.equals(value1)) {
                severity.setSelection(i);
                break;
            }
        }
        //*******************************Setting assign_to spinner***********************************************
        for (int i = 0; i < getResources().getStringArray(R.array.assign_to).length; i++) {
            ticketDetails = new TicketDetails();
            ticketDetails.setUSER_ROLE(getResources().getStringArray(R.array.assign_to)[i]);
            assign_list.add(ticketDetails);
            assigned_adapter = new RoleAdapter(assign_list, getActivity());
            assign_to.setAdapter(assigned_adapter);
            assigned_adapter.notifyDataSetChanged();
        }
        for (int i = 0; i < assign_list.size(); i++) {
            TicketDetails details = assign_list.get(i);
            String value = ticketList.get(0).getASSIGN();
            String value1 = details.getUSER_ROLE();
            if (value.equals(value1)) {
                assign_to.setSelection(i);
                break;
            }
        }
        //***********************************************Select department Assign***********************************************
        for (int i = 0; i < getResources().getStringArray(R.array.department).length; i++) {
            ticketDetails = new TicketDetails();
            ticketDetails.setUSER_ROLE(getResources().getStringArray(R.array.department)[i]);
            dept_list.add(ticketDetails);
            dept_adapter = new RoleAdapter(dept_list, getActivity());
            department.setAdapter(dept_adapter);
            dept_adapter.notifyDataSetChanged();
        }
        for (int i = 0; i < dept_list.size(); i++) {
            TicketDetails details = dept_list.get(i);
            String value = ticketList.get(0).getHESCOM();
            String value1 = details.getUSER_ROLE();
            if (value.equals(value1)) {
                department.setSelection(i);
                break;
            }
        }
        //***********************************************Setting status spinner***********************************************
        for (int i = 0; i < getResources().getStringArray(R.array.status).length; i++) {
            ticketDetails = new TicketDetails();
            ticketDetails.setUSER_ROLE(getResources().getStringArray(R.array.status)[i]);
            status_list.add(ticketDetails);
            status_adapter = new RoleAdapter(status_list, getActivity());
            status.setAdapter(status_adapter);
            status_adapter.notifyDataSetChanged();
        }
        for (int i = 0; i < status_list.size(); i++) {
            TicketDetails details = status_list.get(i);
            String value = ticketList.get(0).getTIC_STATUS();
            String value1 = details.getUSER_ROLE();
            if (value.equals(value1)) {
                status.setSelection(i);
                break;
            }
        }
    }

    //--------------------------------------------------------------------------------------------------------------------
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_submit) {
            validation();
        }
        if (view.getId() == R.id.txt_browse_file) {
            showPictureDialog();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        TicketDetails role;
        if (adapterView.getId() == R.id.priority) {
            role = priority_list.get(i);
            PRIORITY = role.getUSER_ROLE();
        }
        if (adapterView.getId() == R.id.severity) {
            role = severity_list.get(i);
            SEVERITY = role.getUSER_ROLE();
        }
        if (adapterView.getId() == R.id.assign_to) {
            role = assign_list.get(i);
            ASSIGN_TO = role.getUSER_ROLE();
        }
        if (adapterView.getId() == R.id.department) {
            role = dept_list.get(i);
            DEPARTMENT = role.getUSER_ROLE();
        }
        if (adapterView.getId() == R.id.status) {
            role = status_list.get(i);
            STATUS = role.getUSER_ROLE();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
        pictureDialog.setTitle("Select Option");
        String[] pictureDialogItems = {"Gallery", "Camera", "File Manager", "Cancel"};
        pictureDialog.setCancelable(false);
        pictureDialog.setItems(pictureDialogItems, (dialog, which) -> {
            switch (which) {
                case 0:
                    choosePhotoFromGallary();
                    break;
                case 1:
                    takePhotoFromCamera();
                    break;
                case 2:
                    takeFromFileManager();
                    break;
                case 3:
                    cancel_file();
                    break;
            }
        });
        pictureDialog.show();
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    private void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    private void takeFromFileManager() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("*/*");
        startActivityForResult(intent, FILE_MANAGER);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        try {
            if (requestCode == GALLERY) {
                if (data != null) {
                    Uri URI = data.getData();
                    String[] FILE = {MediaStore.Images.Media.DATA};
                    Cursor cursor = Objects.requireNonNull(getActivity()).getContentResolver().query(Objects.requireNonNull(URI), FILE, null, null, null);
                    Objects.requireNonNull(cursor).moveToFirst();
                    int columnIndex = cursor.getColumnIndex(FILE[0]);
                    ImageDecode = cursor.getString(columnIndex);
                    cursor.close();
                    filepathImage = getPath(getActivity(), URI);
                    File file = new File(Objects.requireNonNull(filepathImage));
                    IMAGENAME = file.getName();
                    tv_browse_file.setText(IMAGENAME);

                }
            } else if (requestCode == CAMERA && resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                Uri tempUri = getImageUri(Objects.requireNonNull(getActivity()), Objects.requireNonNull(photo));
                filepathImage = (getRealPathFromURI(tempUri));
                File file = new File(Objects.requireNonNull(filepathImage));
                IMAGENAME = file.getName();
                tv_browse_file.setText(IMAGENAME);

            } else if (requestCode == FILE_MANAGER) {
                if (data != null) {
                    Uri URI = data.getData();
                    String[] FILE = {MediaStore.Images.Media.DATA};
                    ContentResolver cr = Objects.requireNonNull(getActivity()).getContentResolver();
                    Cursor cursor = cr.query(Objects.requireNonNull(URI), FILE, null, null, null);
                    Objects.requireNonNull(cursor).moveToFirst();
                    int columnIndex = cursor.getColumnIndex(FILE[0]);
                    ImageDecode = cursor.getString(columnIndex);
                    cursor.close();
                    filepathImage = getPath(getActivity(), URI);
                    File file = new File(Objects.requireNonNull(filepathImage));
                    IMAGENAME = file.getName();
                    tv_browse_file.setText(IMAGENAME);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            functionCall.setSnackBar(Objects.requireNonNull(getActivity()), lin_ticket_id, "File not attached, try again...");
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------
    @SuppressLint("SetTextI18n")
    private void cancel_file() {
        tv_browse_file.setText("Browse File");
        IMAGENAME = "";
        filepathImage = "";
    }

    //-------------------------------------------get ImageUri & compress-----------------------------------------------------------
    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private String getRealPathFromURI(Uri uri) {
        @SuppressLint("Recycle") Cursor cursor = Objects.requireNonNull(getContext()).getContentResolver().query(uri,
                null, null, null, null);
        Objects.requireNonNull(cursor).moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    //-------------------------------------------------------------------------------------------------------------------------------
    private void validation() {
        if (PRIORITY.equals("SELECT")) {
            functionCall.setSnackBar(Objects.requireNonNull(getActivity()), lin_ticket_id, "Please Select Priority");
            return;
        }
        if (SEVERITY.equals("SELECT")) {
            functionCall.setSnackBar(Objects.requireNonNull(getActivity()), lin_ticket_id, "Please Select Severity");
            return;
        }
        if (ASSIGN_TO.equals("SELECT")) {
            functionCall.setSnackBar(Objects.requireNonNull(getActivity()), lin_ticket_id, "Please Select AssignTo");
            return;
        }
        if (DEPARTMENT.equals("SELECT")) {
            functionCall.setSnackBar(Objects.requireNonNull(getActivity()), lin_ticket_id, "Please Select Department");
            return;
        }
        if (TextUtils.isEmpty(description.getText())) {
            functionCall.setSnackBar(Objects.requireNonNull(getActivity()), lin_ticket_id, "Please Enter Description");
            return;
        }
        if (TextUtils.isEmpty(narration.getText())) {
            functionCall.setSnackBar(Objects.requireNonNull(getActivity()), lin_ticket_id, "Please Enter Narration");
            return;
        }
        if (TextUtils.isEmpty(comment.getText())) {
            functionCall.setSnackBar(Objects.requireNonNull(getActivity()), lin_ticket_id, "Please Enter Comment");
            return;
        }
        functionCall.showprogressdialog("Please wait to complete", "Updating Ticket", progressDialog);
        updateTicketData(ticketList.get(0).getTIC_ID(), narration.getText().toString(), IMAGENAME, ticketList.get(0).getTIC_GENBY(),
                ticketList.get(0).getTIC_SUBCODE(), STATUS, PRIORITY, SEVERITY, title.getText().toString(), description.getText().toString(),
                ASSIGN_TO, DEPARTMENT, "0", comment.getText().toString(), functionCall.encoded(filepathImage));
    }

    //---------------------------------------------------------------------------------------------------------------------
    private void updateTicketData(String TIC_ID, String NARRATION, String TIC_FILE, String TIC_GENBY, String TIC_SUBCODE, String TIC_STATUS, String PRIORITY,
                                  String SEVIRITY, String TITLE, String DESCRIPTION, String ASSIGN, String HESCOM, String MR_CODE, String COMMENT, String Encodefile) {
        RetroClient retroClient = new RetroClient();
        RegisterAPI api = retroClient.getApiService();
        api.updateTicket(TIC_ID, NARRATION, TIC_FILE, TIC_GENBY, TIC_SUBCODE, TIC_STATUS, PRIORITY, SEVIRITY, TITLE, DESCRIPTION, ASSIGN,
                HESCOM, MR_CODE, COMMENT, Encodefile).enqueue(new Callback<List<TicketDetails>>() {
            @Override
            public void onResponse(@NonNull Call<List<TicketDetails>> call, @NonNull Response<List<TicketDetails>> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    handler.sendEmptyMessage(REQUEST_RESULT_SUCCESS);
                } else handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
            }

            @Override
            public void onFailure(@NonNull Call<List<TicketDetails>> call, @NonNull Throwable t) {
                handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
            }
        });
    }

    //---------------------------------------------------------------------------------------------------------------------
    private void showdialog(int id) {
        if (id == DLG_TICKET_UPDATE) {
            AlertDialog.Builder ticket_id = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            ticket_id.setTitle("Ticket Result");
            ticket_id.setCancelable(false);
            ticket_id.setMessage("Your Ticket ID " + ticketList.get(0).getTIC_ID() + " is Updated successfully.");
            ticket_id.setPositiveButton("OK", (dialog, which) -> {
                ((MainActivity) Objects.requireNonNull(getActivity())).switchContent(FORM2, getResources().getString(R.string.view_all), ticketList);
                email();
            });
            AlertDialog dialog = ticket_id.create();
            dialog.show();
        }
    }

    //---------------------------------------------------------------------------------------------------------------------------------------
    private InputFilter filter = (source, start, end, dest, dstart, dend) -> {
        if (source != null && regex.contains(("" + source))) {
            return "";
        }
        return null;
    };

    //-----------------------------------------------------------------------------------------------------------------------------------
    private void setData() {
        tv_ticket_id.setText(ticketList.get(0).getTIC_ID());
        title.setText(ticketList.get(0).getTITLE());
        description.setText(ticketList.get(0).getDESCRIPTION());
        narration.setText(ticketList.get(0).getTIC_NARR());
    }

    //-----------------------------------------------------------------------------------------------------------------------------------
    private void email() {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"notification@hescomtrm.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, "Ticket ID " + ticketList.get(0).getTIC_ID() + " is Updated successfully.");
        email.putExtra(Intent.EXTRA_TEXT, "Dear user, \n\n Title : " + ticketList.get(0).getTITLE() + "\n" +
                "Comment : " + comment.getText().toString() + "\n" + "File : " + IMAGENAME);
        email.setType("plain/text");
        try {
            startActivity(Intent.createChooser(email, "Choose an Email client :"));
            Objects.requireNonNull(getActivity()).finish();
        } catch (android.content.ActivityNotFoundException ex) {
            functionCall.setSnackBar(Objects.requireNonNull(getActivity()), lin_ticket_id, "There is no email client installed.");
        }
    }
}
