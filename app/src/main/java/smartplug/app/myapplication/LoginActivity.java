package smartplug.app.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import smartplug.app.myapplication.Models.User;


public class LoginActivity extends AppCompatActivity {

    int RC_SIGN_IN = 7;
    GoogleSignInClient mGoogleSignInClient;

    FirebaseAuth auth;
    FirebaseUser user;

    private SignInButton signin;
    @BindView(R.id.email) TextInputEditText emailEditText;
    @BindView(R.id.password) TextInputEditText passwordEditText;
    @BindView(R.id.logIn) Button loginButton;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db;
    public static String name = "name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login3);
        ButterKnife.bind(this);

        signin = (SignInButton) findViewById(R.id.sign_in_button);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getting the google signin intent
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();

                //starting the activity for result
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 loginUser();

            }
        });


    }

    public void loginUser(){
        String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        //authenticate user
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        progressDialog.dismiss();
                        if (!task.isSuccessful()) {
                            // there was an error

                                Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();

                        } else {
                            user = auth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "User Signed In\n" + user.getUid()
                                    + "\n" + user.getEmail() + "\n" + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(LoginActivity.this,"Logged In",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //if the user is already signed in
        //we will close this activity
        //and take the user to profile activity
        if (auth.getCurrentUser() != null) {
            finish();

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN) {

            //Getting the GoogleSignIn Task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                progressDialog.show();
                progressDialog.setMessage("Authenticating...");
                //authenticating with firebase
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }





    private void checkIfUserExist(String email){
        progressDialog.show();
        progressDialog.setMessage("Verifying...");
        db.collection("flash")
                .whereEqualTo("email",email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.cancel();
                        if (task.isSuccessful()) {
                            if(task.getResult().size()>0){

                                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                                finish();
                            }
                            else{
                       startActivity(new Intent(LoginActivity.this,NewUserRegistration.class).putExtra(name,auth.getCurrentUser().getDisplayName()));
                            }


                        } else {
                          //  Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("cola1", "firebaseAuthWithGoogle:" + acct.getId());

        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //Now using firebase we are signing in the user here
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("cola2", "signInWithCredential:success");
                            user = auth.getCurrentUser();
                           // progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "User Signed In\n" + user.getUid()
                                    + "\n" + user.getEmail() + "\n" + user.getDisplayName(), Toast.LENGTH_SHORT).show();


                            checkIfUserExist(user.getEmail());



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("cola3", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }
}
