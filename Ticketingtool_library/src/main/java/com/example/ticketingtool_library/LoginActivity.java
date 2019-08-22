package com.example.ticketingtool_library;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ticketingtool_library.adapter.RoleAdapter;
import com.example.ticketingtool_library.api.RegisterAPI;
import com.example.ticketingtool_library.api.RetroClient;
import com.example.ticketingtool_library.invoke.FTPAPI;
import com.example.ticketingtool_library.model.LoginDetails;
import com.example.ticketingtool_library.model.TicketDetails;
import com.example.ticketingtool_library.values.FunctionCall;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.ticketingtool_library.values.constant.APK_FILE_DOWNLOADED;
import static com.example.ticketingtool_library.values.constant.APK_FILE_NOT_FOUND;
import static com.example.ticketingtool_library.values.constant.LOGIN_FAILURE;
import static com.example.ticketingtool_library.values.constant.LOGIN_SUCCESS;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int DLG_APK_UPDATE_SUCCESS = 1;
    TicketDetails ticket;
    ArrayList<TicketDetails> ticketList;
    RoleAdapter roleAdapter;
    Button login;
    Spinner sp_role;
    EditText userId, password;
    FunctionCall functionCall;
    LinearLayout layout;
    List<LoginDetails> loginList;
    ProgressDialog progressDialog;
    FTPAPI ftpapi;
    String main_curr_version = "";
    TextView tv_version;

    @SuppressLint("SetTextI18n")
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case LOGIN_SUCCESS:
                    progressDialog.dismiss();
                    moveToNext();
                   /* if (functionCall.compare(main_curr_version, loginList.get(0).getTIC_VERSION()))
                        show_Dialog(DLG_APK_UPDATE_SUCCESS);
                    else {
                        moveToNext();
                    }*/
                    break;

                case LOGIN_FAILURE:
                    progressDialog.dismiss();
                    functionCall.setSnackBar(LoginActivity.this, layout, "Invalid Credentials!!");
                    break;

                case APK_FILE_DOWNLOADED:
                    progressDialog.dismiss();
                    functionCall.updateApp(LoginActivity.this, new File(functionCall.filepath("ApkFolder") +
                            File.separator + "bigdata_" + loginList.get(0).getTIC_VERSION() + ".apk"));
                    break;

                case APK_FILE_NOT_FOUND:
                    progressDialog.dismiss();
                    break;

            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_login);
        initialize();
        userRole();
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    @SuppressLint("SetTextI18n")
    public void initialize() {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (pInfo != null) {
            main_curr_version = pInfo.versionName;
        }
        ftpapi = new FTPAPI();
        progressDialog = new ProgressDialog(this);
        ticketList = new ArrayList<>();
        loginList = new ArrayList<>();
        sp_role = findViewById(R.id.spinner);
        userId = findViewById(R.id.et_user_name);
        userId.setText("");
        password = findViewById(R.id.et_password);
        password.setText("");
        login = findViewById(R.id.btn_login);
        login.setOnClickListener(this);
        functionCall = new FunctionCall();
        layout = findViewById(R.id.lin_login);
        tv_version = findViewById(R.id.version_code);
        tv_version.setText("Version :" + main_curr_version);
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    public void userRole() {
        ticketList.clear();
        for (int i = 0; i < getResources().getStringArray(R.array.user_role).length; i++) {
            ticket = new TicketDetails();
            ticket.setUSER_ROLE(getResources().getStringArray(R.array.user_role)[i]);
            ticketList.add(ticket);
            roleAdapter = new RoleAdapter(ticketList, this);
            roleAdapter.notifyDataSetChanged();
            sp_role.setAdapter(roleAdapter);
        }
        sp_role.setSelection(0);
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_login) {
            loginValidation();
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    public void loginValidation() {
        TextView tv_role = findViewById(R.id.spinner_txt);
        if (tv_role.getText().toString().equals("SELECT")) {
            functionCall.setSnackBar(this, layout, "Please Select Role");
            return;
        }
        if (TextUtils.isEmpty(userId.getText())) {
            userId.setError("Please Enter UserID");
            return;
        }
        if (TextUtils.isEmpty(password.getText())) {
            password.setError("Please Enter Password");
            return;
        }
        functionCall.showprogressdialog("Please wait to complete.", "Log in", progressDialog);
        loginDetails(userId.getText().toString(), password.getText().toString());
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    public void moveToNext() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("loginList", (Serializable) loginList);
        startActivity(intent);
        finish();
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    public void loginDetails(String USERNAME, String PASSWORD) {
        RetroClient retroClient = new RetroClient();
        RegisterAPI api = retroClient.getApiService();
        api.getData(USERNAME, PASSWORD).enqueue(new Callback<List<LoginDetails>>() {
            @Override
            public void onResponse(@NonNull Call<List<LoginDetails>> call, @NonNull Response<List<LoginDetails>> response) {
                if (response.isSuccessful()) {
                    loginList = response.body();
                    handler.sendEmptyMessage(LOGIN_SUCCESS);
                } else
                    handler.sendEmptyMessage(LOGIN_FAILURE);
            }

            @Override
            public void onFailure(@NonNull Call<List<LoginDetails>> call, @NonNull Throwable t) {
                handler.sendEmptyMessage(LOGIN_FAILURE);
            }
        });
    }

    //------------------------------------------------------------------------------------------------------------------------------
    private void show_Dialog(int id) {
        Dialog dialog;
        if (id == DLG_APK_UPDATE_SUCCESS) {
            AlertDialog.Builder appupdate = new AlertDialog.Builder(this);
            appupdate.setTitle("App Updates");
            appupdate.setCancelable(false);
            appupdate.setMessage("Your current version number : " + main_curr_version + "\n" + "\n" +
                    "New version is available : " + loginList.get(0).getTIC_VERSION() + "\n");
            appupdate.setPositiveButton("UPDATE", (dialog1, which) -> {
                FTPAPI.Download_apk downloadApk = ftpapi.new Download_apk(handler, progressDialog, loginList.get(0).getTIC_VERSION());
                downloadApk.execute();
            });
            dialog = appupdate.create();
            dialog.show();
        }
    }
}
