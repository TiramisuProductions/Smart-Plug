package smartplug.app.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.viewanimator.AnimationListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import smartplug.app.myapplication.Models.Device;
import smartplug.app.myapplication.Models.User;

public class ConnectToBluetoothActivity extends AppCompatActivity {
    static Handler bluetoothIn;

    final int handlerState = 0;        				 //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();
    @BindView(R.id.progressBar)
    CircularProgressBar progressBar;
    private ConnectedThread mConnectedThread;
    private int stage;
    private boolean firstDevice;
    @BindView(R.id.led)
    ImageView led;
    @BindView(R.id.done) Button doneButton;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.error) ImageView errorButton;
    int animationDuration = 2500;
    boolean finalAcknowlegement = false;
    String deviceId ="";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to_bluetooth);
        ButterKnife.bind(this);
        status.setText("Configuring Flash Device");
        stage=1;
        ledAnimation();

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(ConnectToBluetoothActivity.this,HomeActivity.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(newIntent);
            }
        });

        // 2500ms = 2,5s
        progressBar.setProgressWithAnimation(30, animationDuration);
        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {                                        //if message is what we want
                    String readMessage = (String) msg.obj;
                    Log.d("wow", readMessage);// msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);                                      //keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        Log.d("hoola", dataInPrint);
                        if(stage==1){
                            deviceId=dataInPrint;
                            handleStage();
                        }
                        else {
                            if (dataInPrint.equals("1")) {
                                handleStage();
                                Log.d("reached",""+stage);
                            }
                        }
                        int dataLength = dataInPrint.length();                          //get length of data received
                        Log.d("Length", "" + dataLength);
                        recDataString.delete(0, recDataString.length());                    //clear all string data
                        // strIncom =" ";
                        //handleStage();
                    }
                }
            }
        };
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();
    }

    private void ledAnimation(){
        com.github.florent37.viewanimator.ViewAnimator.animate(led).fadeIn().thenAnimate(led).fadeOut().start().onStop(new AnimationListener.Stop() {
            @Override
            public void onStop() {
                ledAnimation();
            }
        });
    }

    private void handleStage(){
        switch (stage){
            case 1:
                stage=2;
                progressBar.setProgressWithAnimation(45, animationDuration);
                status.setText("Processing...");
                deviceValidationFirebase();
                break;
            case 2:
                stage = 3;
                status.setText("Just 1 Second..");
                progressBar.setProgressWithAnimation(60, animationDuration);
                addDevice();
                break;
            case 3:
                doneButton.setVisibility(View.VISIBLE);
                status.setText("Sucessfully Setted Up...");
                progressBar.setProgressWithAnimation(100, animationDuration);

                break;
        }
    }

    private void deviceValidationFirebase() {

        FlashApplication.userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                if(!user.isHasDevices())
                {
                    firstDevice = true;
                    handleStage();
                }
                else{
                    firstDevice = false;
                    handleStage();
                }
            }
        });
    }

    private void SendResponse(){
        mConnectedThread.write("ACCEPTING_CHANNEL_TAG"+"~");
    }

    private void addDevice(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setTitle("What Would you like to name your device");
        builder.setView(input);
        builder.setPositiveButton("Done",null);
        builder.setNegativeButton("Cancel",null);
        builder.setCancelable(false);
        final AlertDialog alert = builder.create();
        FlashApplication.devicesRef
                .whereEqualTo("deviceId", deviceId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                status.setText("Flash Device Already Linked !");
                                errorButton.setVisibility(View.VISIBLE);
                                doneButton.setText("Abort");
                                doneButton.setVisibility(View.VISIBLE);


                            } else {
                                alert.show();
                            }
                        }
                    }
                });

        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button b = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        if (input.getText().toString().equals("")) {
                            input.setError("Enter a name");
                        } else {
                            Device device = new Device(deviceId, input.getText().toString(), false);
                            if (firstDevice) {
                                FlashApplication.devicesRef.add(device).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {

                                        FlashApplication.userRef.update("hasDevices", true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                SendResponse();
                                            }
                                        });
                                    }
                                });
                            } else {
                                FlashApplication.devicesRef.add(device).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {

                                        SendResponse();
                                    }
                                });
                            }


                            alert.dismiss();


                        }

                        Button n = alert.getButton(AlertDialog.BUTTON_NEGATIVE);
                        n.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alert.dismiss();
                            }
                        });

                    }
                });


            }}

        );


    }


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }
    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }

    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(BluetoothDevices.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        mConnectedThread.write("REQUEST_DEVICE_ID~");
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);        	//read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }
}
