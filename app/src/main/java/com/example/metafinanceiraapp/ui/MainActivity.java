package com.example.metafinanceiraapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metafinanceiraapp.R;
import com.example.metafinanceiraapp.adapter.MetaAdapter;
import com.example.metafinanceiraapp.model.Meta;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MetaAdapter adapter;
    private List<Meta> listaMetas = new ArrayList<>();
    private List<String> listaIds = new ArrayList<>();
    private FirebaseFirestore db;

    private boolean mostrarSomenteAtivas = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewMetas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MetaAdapter(listaMetas, listaIds, metaId -> {
            Intent intent = new Intent(MainActivity.this, DetalhesMetaActivity.class);
            intent.putExtra("metaId", metaId);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        FloatingActionButton fabNovaMeta = findViewById(R.id.fabNovaMeta);
        fabNovaMeta.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NovaMetaActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnFiltroMetas).setOnClickListener(v -> {
            mostrarSomenteAtivas = !mostrarSomenteAtivas;
            carregarMetas();
        });
    }

    private void carregarMetas() {
        db.collection("metas").get()
                .addOnSuccessListener(this::processarResultado)
                .addOnFailureListener(e -> {
                    // Tratamento de erro
                });
    }

    private void processarResultado(QuerySnapshot querySnapshot) {
        listaMetas.clear();
        listaIds.clear();

        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
            Meta meta = doc.toObject(Meta.class);
            if (meta != null) {
                boolean finalizada = meta.isMetaFinalizada();
                if (!mostrarSomenteAtivas || !finalizada) {
                    listaMetas.add(meta);
                    listaIds.add(doc.getId());
                }
            }
        }

        // Atualiza texto do botão
        MaterialButton btnFiltro = findViewById(R.id.btnFiltroMetas);
        if (mostrarSomenteAtivas) {
            btnFiltro.setText("Ver todas");
        } else {
            btnFiltro.setText("Ver somente ativas");
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarMetas();
    }
}

