package smartplug.app.myapplication;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;

/**
 * Created by Sarvesh Palav on 2/3/2018.
 */

public class FlashApplication extends Application {

    public static CollectionReference rootRef;
    public static CollectionReference devicesRef;
    public static DocumentReference userRef;
    public static  PNConfiguration pnConfiguration;
    @Override
    public void onCreate() {
        super.onCreate();
        rootRef = FirebaseFirestore.getInstance().collection("flash");
        userRef = rootRef.document(FirebaseAuth.getInstance().getUid());
        devicesRef = rootRef.document(FirebaseAuth.getInstance().getUid()).collection("devices");
         pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey("sub-c-a6bfe88c-09db-11e8-937d-c24c0beb948b");
        pnConfiguration.setPublishKey("pub-c-f70f0ecd-2496-46ba-a304-96519949bd89");
        pnConfiguration.setSecure(false);


    }
}
