package com.example.metafinanceiraapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metafinanceiraapp.R;
import com.example.metafinanceiraapp.adapter.DepositoAdapter;
import com.example.metafinanceiraapp.model.Deposito;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistoricoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DepositoAdapter adapter;
    private List<Deposito> listaDepositos = new ArrayList<>();
    private FirebaseFirestore db;
    private String metaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        // Toolbar com botão de voltar
        MaterialToolbar toolbar = findViewById(R.id.toolbarHistorico);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerDepositos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DepositoAdapter(listaDepositos);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // Recebe ID da meta
        metaId = getIntent().getStringExtra("metaId");
        if (metaId == null) {
            Toast.makeText(this, "Meta não encontrada!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        carregarDepositos();
    }

    private void carregarDepositos() {
        Log.d("HISTORICO_DEBUG", "Buscando depósitos com metaId: " + metaId);

        db.collection("depositos")
                .whereEqualTo("idMeta", metaId)
                .orderBy("data")
                .get()
                .addOnSuccessListener(this::processarResultado)
                .addOnFailureListener(e -> {
                    Log.e("HISTORICO_DEBUG", "Erro ao buscar depósitos", e);
                    Toast.makeText(this, "Erro ao carregar depósitos", Toast.LENGTH_SHORT).show();
                });
    }


    private void processarResultado(QuerySnapshot snapshots) {
        listaDepositos.clear();

        for (DocumentSnapshot doc : snapshots.getDocuments()) {
            Deposito deposito = doc.toObject(Deposito.class);
            if (deposito != null) {
                listaDepositos.add(deposito);
            }
        }

        if (listaDepositos.isEmpty()) {
            Toast.makeText(this, "Nenhum depósito registrado ainda.", Toast.LENGTH_SHORT).show();
        }

        adapter.notifyDataSetChanged();
        Log.d("HISTORICO_DEBUG", "Documentos retornados: " + snapshots.size());
    }

}
