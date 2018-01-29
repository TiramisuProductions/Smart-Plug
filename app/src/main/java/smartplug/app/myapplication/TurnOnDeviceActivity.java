package smartplug.app.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.github.florent37.viewanimator.AnimationListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TurnOnDeviceActivity extends AppCompatActivity {

@BindView(R.id.led)
    ImageView led;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_on_device);
        ButterKnife.bind(this);
        ledAnimation();
    }

    public void done(View view){
       startActivity(new Intent(TurnOnDeviceActivity.this,BluetoothDevices.class));
    }

    private void ledAnimation(){
        com.github.florent37.viewanimator.ViewAnimator.animate(led).fadeIn().thenAnimate(led).fadeOut().start().onStop(new AnimationListener.Stop() {
            @Override
            public void onStop() {
                ledAnimation();
            }
        });
    }



}
