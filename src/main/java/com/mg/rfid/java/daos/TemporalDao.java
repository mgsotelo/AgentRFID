package com.mg.rfid.java.daos;

import com.google.cloud.firestore.*;
import com.mg.rfid.java.beans.Entity;
import com.mg.rfid.java.beans.Temporal;
import com.google.api.core.ApiFuture;

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
            System.out.println("[Temporal] Added document with ID: " + addedDocRef.get().getId());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Optional<Temporal> get(String rfidEPC, Firestore db) {
        Optional<Temporal> optionalequipment = Optional.empty();

        try {

            CollectionReference ref = db.collection("temporal");
            Query query = ref.whereEqualTo("EPC",rfidEPC);
            ApiFuture<QuerySnapshot> future = query.get();
            int allDocsSize = future.get().getDocuments().size();

            if( allDocsSize > 0){
                DocumentSnapshot doc = future.get().getDocuments().get(0);

                if (doc.exists()){
                    String epc = (String) doc.getData().get("EPC");
                    Temporal tmp = new Temporal();
                    tmp.setEpc(epc);
                    return Optional.of(tmp);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return optionalequipment;
    }

    @Override
    public void inserttmp(Entity ent, Firestore fdb) {

    }
}
