package com.mg.rfid.java.daos;

import com.mg.rfid.java.beans.Equipment;
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
            System.out.println("[Equipos] Added document with ID: " + addedDocRef.get().getId());

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

    }
}
