package com.mg.rfid.java.daos;

import com.google.cloud.firestore.Firestore;

import java.util.Optional;

public interface Dao <T> {

    void insert(T t, Firestore db);

    Optional<T> get(String rfidEPC, Firestore db);

}
