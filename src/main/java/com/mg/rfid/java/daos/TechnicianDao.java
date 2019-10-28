package com.mg.rfid.java.daos;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.mg.rfid.java.beans.Technician;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TechnicianDao implements Dao<Technician> {

    @Override
    public void insert(Technician technician, Firestore db) {
        try {

            CollectionReference ref = db.collection("temporal");
            Map<String, Object> tecnico = new HashMap<>();
            tecnico.put("EPC",technician.getEpc());
            //tecnico.put("creationdate", FieldValue.serverTimestamp());
            tecnico.put("name",technician.getName());
            ApiFuture<DocumentReference> addedDocRef = ref.add(tecnico);
            System.out.println("[Technician] Added document to temporal with ID: " + addedDocRef.get().getId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Technician> get(String rfidEPC, Firestore db) {

        Optional<Technician> optionalTechnician = Optional.empty();

        try {

            CollectionReference ref = db.collection("tecnicos");
            Query query = ref.whereEqualTo("EPC",rfidEPC);
            ApiFuture<QuerySnapshot> future = query.get();
            int allDocsSize = future.get().getDocuments().size();

            if( allDocsSize > 0){
                DocumentSnapshot doc = future.get().getDocuments().get(0);

                if (doc.exists()){
                    String epc = (String) doc.getData().get("EPC");
                    String name = (String) doc.getData().get("name");
                    Technician  tech = new Technician();
                    tech.setEpc(epc);
                    tech.setName(name);
                    return Optional.of(tech);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return optionalTechnician;
    }
}
