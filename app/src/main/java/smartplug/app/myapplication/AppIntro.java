package smartplug.app.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

import smartplug.app.myapplication.fragments.BlueToothPermissionFragment;
import smartplug.app.myapplication.fragments.IntroFragment;

/**
 * Created by Sarvesh Palav on 1/4/2018.
 */

public class AppIntro extends IntroActivity {
    SharedPreferences sharedpreferences;

    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        sharedpreferences = getSharedPreferences(TAGS.sharedPref, Context.MODE_PRIVATE);
        IntroFragment introFragment = new IntroFragment();
        setFullscreen(true);
        addSlide(new SimpleSlide.Builder()
                .title("Thank You For Buying Flash")
                .description("Lets Set It Up")
                .image(R.drawable.logo)
                .background(R.color.lightgrey)
                .backgroundDark(R.color.colorPrimary)
                .scrollable(false)
                .build());



        addSlide(new SimpleSlide.Builder()
                .title("We Need Permissions")
                .background(R.color.lightgrey)
                .backgroundDark(R.color.colorPrimary)
                .scrollable(false)
                .permission(Manifest.permission.CAMERA)
                .build());



    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putBoolean(TAGS.firsttime,false);
        editor.commit();
        startActivity(new Intent(this,MainActivity.class));
    }
}
