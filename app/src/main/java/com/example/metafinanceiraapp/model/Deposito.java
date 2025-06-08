package com.example.metafinanceiraapp.model;

import com.google.firebase.Timestamp;
import java.util.Date;

public class Deposito {
    private double valor;
    private Timestamp data;
    private String idMeta;

    public Deposito() {}  // obrigatório para Firestore

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public Timestamp getData() { return data; }
    public void setData(Timestamp data) { this.data = data; }

    public String getIdMeta() { return idMeta; }
    public void setIdMeta(String idMeta) { this.idMeta = idMeta; }

    public Date getDataAsDate() {
        return data != null ? data.toDate() : null;
    }
}

