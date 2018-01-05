package smartplug.app.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedpreferences = getSharedPreferences(TAGS.sharedPref, Context.MODE_PRIVATE);

        if(sharedpreferences.getBoolean(TAGS.firsttime,true)){
            startActivity(new Intent(this,AppIntro.class));
            finish();
        }
        else{
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }






    }
}
