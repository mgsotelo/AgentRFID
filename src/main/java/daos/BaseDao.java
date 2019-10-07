package daos;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;

public class BaseDao {

    public static Firestore getFirestoreDB() throws Exception {
        FileInputStream serviceAccount = new FileInputStream("./serviceAccountKey.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://myadmin-ca390.firebaseio.com")
                .setFirestoreOptions(FirestoreOptions.newBuilder()
                        .setTimestampsInSnapshotsEnabled(true)
                        .build())
                .build();
        FirebaseApp.initializeApp(options);
        return FirestoreClient.getFirestore();
    }
}
