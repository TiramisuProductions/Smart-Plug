package smartplug.app.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final int REQUEST_CODE_BLUETOOTH_ON = 1313;
    private ProgressDialog deviceDialog;
    FirebaseFirestore db;
    FirebaseAuth auth1;


    private NavigationView navigationView;
    private View navHeader;
    private TextView nameHeader, emailHeader;

    /**
     * Bluetooth device discovery time，second。
     */
    private static final int BLUETOOTH_DISCOVERABLE_DURATION = 250;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView)findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);
        nameHeader = (TextView)navHeader.findViewById(R.id.username_nav_header);
        emailHeader = (TextView)navHeader.findViewById(R.id.email_nav_header);

        setSupportActionBar(toolbar);
        toolbar.setTitle("My Devices");

        db = FirebaseFirestore.getInstance();
        auth1 = FirebaseAuth.getInstance();


        nameHeader.setText(auth1.getCurrentUser().getDisplayName());
        emailHeader.setText(auth1.getCurrentUser().getEmail());

        //displayFirstTimeUserDialog();
        deviceList();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    public void deviceList() {
        deviceDialog = new ProgressDialog(this);
        deviceDialog.setMessage("Getting Your Devices");
        deviceDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        deviceDialog.setIndeterminate(false);
        deviceDialog.setProgressNumberFormat(null);
        deviceDialog.setProgressPercentFormat(null);
        deviceDialog.setCancelable(false);
        deviceDialog.show();

        db.collection("flash").document(auth1.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                Log.d("hola", documentSnapshot.getData().get("hasDevices").toString());
                if (Boolean.valueOf(documentSnapshot.getData().get("hasDevices").toString()) == false) {
                    deviceDialog.cancel();
                    displayFirstTimeUserDialog();
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
                        // TODO

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        return true;
    }
}
