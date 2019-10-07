package daos;

import beans.Temporal;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TemporalDao implements Dao<Temporal> {

    @Override
    public void insert(Temporal temporal, Firestore db) {

        try {

            CollectionReference ref = db.collection("temporal");
            Map<String, Object> equipo = new HashMap<>();
            equipo.put("EPC",temporal.getEpc());
            ApiFuture<DocumentReference> addedDocRef = ref.add(equipo);
            System.out.println("Added document with ID: " + addedDocRef.get().getId());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Optional<Temporal> get(String rfidEPC, Firestore db) {
        return Optional.empty();
    }
}
