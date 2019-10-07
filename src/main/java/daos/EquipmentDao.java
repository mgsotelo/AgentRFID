package daos;

import beans.Equipment;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EquipmentDao implements Dao<Equipment>  {

    @Override
    public void insert(Equipment equipment, Firestore db) {

        try {

            CollectionReference ref = db.collection("equipos");
            Map<String, Object> equipo = new HashMap<>();
            equipo.put("EPC",equipment.getEpc());
            equipo.put("creationdate",FieldValue.serverTimestamp());
            ApiFuture<DocumentReference> addedDocRef = ref.add(equipo);
            System.out.println("Added document with ID: " + addedDocRef.get().getId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Equipment> get(String rfidEPC, Firestore db) {

        Optional<Equipment> optionalequipment = Optional.empty();

        try {

            CollectionReference ref = db.collection("equipos");
            Query query = ref.whereEqualTo("EPC",rfidEPC);
            ApiFuture<QuerySnapshot> future = query.get();
            int allDocsSize = future.get().getDocuments().size();

            if( allDocsSize > 0){
                DocumentSnapshot doc = future.get().getDocuments().get(0);

                if (doc.exists()){
                    String epc = (String) doc.getData().get("EPC");
                    String id = doc.getId();
                    Equipment eqp = new Equipment();
                    eqp.setEpc(epc);
                    eqp.setId(id);
                    return Optional.of(eqp);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return optionalequipment;

        /*
        Optional<User> optional = findUserById("667290");
        en este caso el "findUserById" seria este get

        Y luego lo accedo mediante este metodo y si no hay nada, ejecuto una inserción en el dato.
        optional.ifPresent(user -> {
            System.out.println("User's name = " + user.getName());
        })
        */
    }
}
