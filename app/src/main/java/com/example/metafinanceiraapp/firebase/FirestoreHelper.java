package com.example.metafinanceiraapp.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.metafinanceiraapp.model.Deposito;
import com.example.metafinanceiraapp.model.Meta;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FirestoreHelper {

    private static final String TAG = "FIREBASE";
    private static final String META_ID = "meta_unica"; // id fixo já que não temos login ainda

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final DocumentReference metaRef = db.collection("metas").document(META_ID);
    private final CollectionReference depositosRef = metaRef.collection("depositos");

    // Salvar nova meta no Firestore
    public void salvarMeta(Meta meta) {
        db.collection("metas").document(META_ID)
                .set(meta)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "✅ Meta salva com sucesso"))
                .addOnFailureListener(e -> Log.e(TAG, "❌ Erro ao salvar meta", e));
    }

    // Adicionar um novo depósito e atualizar o valor acumulado
    public void adicionarDeposito(double valor, @NonNull Runnable onSuccess, @NonNull Runnable onFailure) {
        // Cria objeto de depósito com data atual e ID da meta
        Deposito deposito = new Deposito();
        deposito.setValor(valor);
        deposito.setData(new Timestamp(new Date()));
        deposito.setIdMeta(META_ID);  // essencial para filtro posterior

        // Adiciona o depósito na coleção RAIZ "depositos"
        db.collection("depositos")
                .add(deposito)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "✅ Depósito adicionado: " + documentReference.getId());

                    // Atualiza valor acumulado
                    metaRef.get().addOnSuccessListener(snapshot -> {
                        Meta meta = snapshot.toObject(Meta.class);
                        if (meta != null) {
                            double novoValor = meta.getValorAcumulado() + valor;
                            meta.setValorAcumulado(novoValor);
                            metaRef.set(meta)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "✅ Valor acumulado atualizado");
                                        onSuccess.run();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "❌ Falha ao atualizar valor acumulado", e);
                                        onFailure.run();
                                    });
                        }
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "❌ Falha ao buscar meta", e);
                        onFailure.run();
                    });

                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Erro ao adicionar depósito", e);
                    onFailure.run();
                });
    }


    // A partir daqui, futuramente podemos adicionar métodos para buscar histórico e meta atual
}
