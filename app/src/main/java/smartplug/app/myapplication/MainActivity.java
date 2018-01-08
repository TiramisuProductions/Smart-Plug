package smartplug.app.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedpreferences = getSharedPreferences(TAGS.sharedPref, Context.MODE_PRIVATE);
        auth = FirebaseAuth.getInstance();
        if (sharedpreferences.getBoolean(TAGS.firsttime, true)) {
            startActivity(new Intent(this, AppIntro.class));
            finish();
        }
        else if(auth.getCurrentUser()!=null){
            Toast.makeText(MainActivity.this,"User is Logged in ",Toast.LENGTH_LONG).show();
            startActivity(new Intent(MainActivity.this,HomeActivity.class));
            finish();
        }

        else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
