package smartplug.app.myapplication;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import smartplug.app.myapplication.Adapters.DevicesAdapter;
import smartplug.app.myapplication.Models.Device;
import smartplug.app.myapplication.Models.User;
import smartplug.app.myapplication.fragments.MyDevicesFragment;
import smartplug.app.myapplication.fragments.StatisticsFragment;
import smartplug.app.myapplication.fragments.TimerFragment;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ProgressDialog progressDialog;
    FirebaseAuth auth;
    Fragment fragment;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.devices) RecyclerView devicesRecylerView;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    private  TextView nameHeader;
    private  TextView emailHeader;
    private View navHeader;
    private DevicesAdapter devicesAdapter;
    public ArrayList<Device> deviceList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        auth = FirebaseAuth.getInstance();
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle("My Devices");
        navHeader = navigationView.getHeaderView(0);
        nameHeader = (TextView)navHeader.findViewById(R.id.username_nav_header);
        emailHeader = (TextView)navHeader.findViewById(R.id.email_nav_header);
        nameHeader.setText(auth.getCurrentUser().getDisplayName());
        emailHeader.setText(auth.getCurrentUser().getEmail());
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        fragment = new MyDevicesFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_home, fragment);
        ft.commit();
        deviceList();

    }


    public void deviceList() {
        //function on how to get device list
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting Your Devices");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressNumberFormat(null);
        progressDialog.setProgressPercentFormat(null);
        progressDialog.setCancelable(false);
        progressDialog.show();

        FlashApplication.userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (!documentSnapshot.toObject(User.class).isHasDevices()) {
                    progressDialog.cancel();
                    displayFirstTimeUserDialog();

                }
                else {
                    FlashApplication.devicesRef
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (DocumentSnapshot document : task.getResult()) {
                                            Device device = document.toObject(Device.class);
                                            deviceList.add(device);
                                        }
                                        progressDialog.cancel();
                                        devicesAdapter = new DevicesAdapter(HomeActivity.this, deviceList);
                                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(HomeActivity.this);
                                        devicesRecylerView.setLayoutManager(mLayoutManager);
                                        devicesRecylerView.setItemAnimator(new DefaultItemAnimator());
                                        devicesRecylerView.setAdapter(devicesAdapter);
                                    } else {

                                    }
                                }
                            });
                }
            }
        });
    }


    private void turnOnBluetoothDialog() {
        new MaterialDialog.Builder(this).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }).cancelable(false)
                .title("Your Bluetooth seems to be off")
                .content("Lets turn it On")
                .positiveText("Turn It on")
                .negativeText("No")
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth enabled", Toast.LENGTH_LONG).show();
                startActivity(new Intent(HomeActivity.this, TurnOnDeviceActivity.class));


            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "User canceled", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void displayFirstTimeUserDialog() {
        new MaterialDialog.Builder(this)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {


                        if ((BluetoothManager.isBluetoothSupported())
                                && (!BluetoothManager.isBluetoothEnabled())) {
                            dialog.dismiss();
                            turnOnBluetoothDialog();
                        } else {
                            startActivity(new Intent(HomeActivity.this, TurnOnDeviceActivity.class));
                        }

                    }
                })
                .cancelable(false)
                .icon(getResources().getDrawable(R.drawable.nav_mydevices))
                .title(R.string.no_devices_popup)
                .content(R.string.no_devices_content)
                .positiveText("Okay")
                .negativeText("Skip")

                .show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }





    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

         fragment = null;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_devices) {
            fragment = new MyDevicesFragment();
            toolbar.setTitle("My Devices");
            // Handle the camera action
        } else if (id == R.id.nav_timers) {
            fragment = new TimerFragment();

            toolbar.setTitle("Timers");
        } else if (id == R.id.nav_statistics) {
            toolbar.setTitle("Statistics");
            fragment = new StatisticsFragment();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_info) {

        }
        else if(id==R.id.nav_logout){
            auth.signOut();
            startActivity(new Intent(this,MainActivity.class));
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_home, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        return true;
    }
}
