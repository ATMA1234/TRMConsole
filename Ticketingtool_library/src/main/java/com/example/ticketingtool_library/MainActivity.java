package com.example.ticketingtool_library;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ticketingtool_library.fragments.GenerateTicket;
import com.example.ticketingtool_library.fragments.Settings;
import com.example.ticketingtool_library.fragments.UpdateTicket;
import com.example.ticketingtool_library.fragments.ViewAllTickets;
import com.example.ticketingtool_library.fragments.ViewTicketDetails;
import com.example.ticketingtool_library.fragments.ViewUpdateTickets;
import com.example.ticketingtool_library.model.LoginDetails;
import com.example.ticketingtool_library.model.TicketDetails;
import com.example.ticketingtool_library.receiver.New_Ticket_Notification;
import com.example.ticketingtool_library.services.NewTicketService;
import com.example.ticketingtool_library.values.FunctionCall;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.example.ticketingtool_library.receiver.New_Ticket_Notification.COUNT;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    private Fragment fragment;
    private boolean isFirstBackPressed = false;
    List<LoginDetails> loginList;
    ArrayList<TicketDetails> ticketList;
    EditText editText;
    TextView tv_count;
    FunctionCall functionCall;
    New_Ticket_Notification notification;

    //--------------------------------------------------------------------------------------------------------------------------
    public enum Steps {
        FORM0(GenerateTicket.class),
        FORM1(UpdateTicket.class),
        FORM2(ViewAllTickets.class),
        FORM3(Settings.class),
        FORM4(ViewTicketDetails.class),
        FORM5(ViewUpdateTickets.class);

        private Class clazz;

        Steps(Class clazz) {
            this.clazz = clazz;
        }

        public Class getFragClass() {
            return clazz;
        }
    }


    //--------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.generate);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if (intent != null) {
            loginList = (ArrayList<LoginDetails>) intent.getSerializableExtra("loginList");
        }
        ticketList = new ArrayList<>();
        functionCall = new FunctionCall();
        notification = new New_Ticket_Notification();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView user_name = header.findViewById(R.id.nav_user_name);
        TextView subdiv_code = header.findViewById(R.id.nav_subdiv_code);
        TextView tv_close = header.findViewById(R.id.nav_ic_close);
        tv_close.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.START);
        });

        user_name.setText(loginList.get(0).getUSERNAME());
        subdiv_code.setText(loginList.get(0).getSUBDIVCODE());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //--------------------------------------------------------------------------------------------------------------------------
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String main_curr_version = null;
        if (pInfo != null) {
            main_curr_version = pInfo.versionName;
        }

        NavigationView logout_navigationView = findViewById(R.id.nav_drawer_bottom);
        logout_navigationView.setNavigationItemSelectedListener(this);
        logout_navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.black)));
        Menu menu = logout_navigationView.getMenu();
        MenuItem nav_login = menu.findItem(R.id.nav_version);
        nav_login.setTitle("Version" + " : " + main_curr_version);
        nav_login.setOnMenuItemClickListener(item -> true);

        switchContent(Steps.FORM0, getResources().getString(R.string.generate), ticketList);
    }

    //--------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            super.onBackPressed();
        } else {
            if (isFirstBackPressed) {
                super.onBackPressed();
            } else {
                isFirstBackPressed = true;
                Toast.makeText(this, "Press again to close app", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> isFirstBackPressed = false, 2000);
            }
        }
    }

    //--------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_generate) {
            switchContent(Steps.FORM0, getResources().getString(R.string.generate), ticketList);
        } else if (id == R.id.nav_update) {
            switchContent(Steps.FORM2, getResources().getString(R.string.view_all), ticketList);
        } else if (id == R.id.nav_view) {
            switchContent(Steps.FORM2, getResources().getString(R.string.view_all), ticketList);
        } else if (id == R.id.nav_settings) {
            switchContent(Steps.FORM3, getResources().getString(R.string.setting), ticketList);
        } else if (id == R.id.nav_logout) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //----------------------------------------------------------------------------------------------------------------------------------
    public void switchContent(Steps currentForm, String title, List<TicketDetails> details) {
        try {
            fragment = (Fragment) currentForm.getFragClass().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("loginList", (Serializable) loginList);
        bundle.putSerializable("ticketList", (Serializable) details);
        fragment.setArguments(bundle);
        toolbar.setTitle(title);
        ft.replace(R.id.container_main, fragment, currentForm.name());
        ft.commit();
    }

    //----------------------------------------------------------------------------------------------------------------------------------
    public EditText getEditText() {
        editText = findViewById(R.id.et_search);
        return editText;
    }

    //---------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        if (!isMyServiceRunning(NewTicketService.class)) {
            functionCall.logStatus("NewTicketService not running");
            Intent service = new Intent(MainActivity.this, NewTicketService.class);
            service.putExtra("subdiv_code", loginList.get(0).getSUBDIVCODE());
            service.putExtra("comp_id", loginList.get(0).getCOMPANY_LEVEL_ID());
            startService(service);
        } else functionCall.logStatus("NewTicketService Running in background");
    }

    //---------------------------------------------------------------------------------------------------------------------------------
    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    //*************search record in a list*********************************************************************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.notification, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_notification);
        View actionview = MenuItemCompat.getActionView(menuItem);
        tv_count = actionview.findViewById(R.id.cart_badge);
        tv_count.setText(String.valueOf(COUNT));
        actionview.setOnClickListener(v -> onOptionsItemSelected(menuItem));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_notification) {
            switchContent(Steps.FORM2, getResources().getString(R.string.view_all), ticketList);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
