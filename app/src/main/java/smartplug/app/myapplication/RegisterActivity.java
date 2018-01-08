package smartplug.app.myapplication;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private Button register;
    private EditText UnameR, PassR;
    ProgressDialog progressDialog;
    FirebaseUser user;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register = (Button) findViewById(R.id.register_btn);
        UnameR = (EditText) findViewById(R.id.username_edR);
        PassR = (EditText) findViewById(R.id.password_edR);

        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String email = UnameR.getText().toString().trim();
        String password = PassR.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog.show();
        progressDialog.setMessage("Registering...");

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if (task.isSuccessful()) {
                            //display some message here
                            progressDialog.dismiss() ;
                            user = auth.getCurrentUser();
                            Toast.makeText(RegisterActivity.this,"\n" + user.getUid()
                                    + "\n" + user.getEmail() + "\n" + user.getDisplayName(), Toast.LENGTH_SHORT).show();

                            Toast.makeText(RegisterActivity.this, "Successfully registered", Toast.LENGTH_LONG).show();
                            Toast.makeText(RegisterActivity.this, "remove this toast", Toast.LENGTH_LONG).show();
                        } else {

                            //display some message here
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Registration Error", Toast.LENGTH_LONG).show();
                            Log.w("coolio", "createUserWithEmail:failure", task.getException());
                        }
                        //progressDialog.dismiss();
                    }
                });
    }
}
