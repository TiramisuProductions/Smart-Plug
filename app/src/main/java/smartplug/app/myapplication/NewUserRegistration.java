package smartplug.app.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import butterknife.BindView;
import butterknife.ButterKnife;
import smartplug.app.myapplication.Models.User;

public class NewUserRegistration extends AppCompatActivity {

    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.done)
    ImageView done;
    FirebaseAuth auth;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_registration);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Setting Up..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        if(getIntent().getStringExtra(LoginActivity.name)!=null)
        {
           name.setText(getIntent().getStringExtra(LoginActivity.name));
        }

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!name.getText().toString().equals(""))
                {
                    progressDialog.show();
                    saveUser();
                }
                else {
                    name.setError("Enter A Name");
                }

            }
        });


    }

    private void saveUser(){
        User user = new User(auth.getCurrentUser().getEmail(),name.getText().toString(),false);




        FlashApplication.userRef
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.cancel();
                        startActivity(new Intent(NewUserRegistration.this,MainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Newuser", "Error writing document", e);
                    }
                });



    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.signOut();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        auth.signOut();
    }
}
