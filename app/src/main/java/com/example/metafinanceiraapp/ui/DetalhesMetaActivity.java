package com.example.metafinanceiraapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.metafinanceiraapp.R;
import com.example.metafinanceiraapp.model.Meta;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetalhesMetaActivity extends AppCompatActivity {

    private TextView txtNomeMeta, txtDescricao, txtMetaTotal, txtValorAcumulado, txtValorRestante,
            txtDataFinal, txtDiasRestantes, txtValorPorDia, txtPorcentagem;
    private ProgressBar progressoBarra;
    private MaterialButton btnAdicionarDeposito, btnEditarMeta, btnVerHistorico, btnExcluirMeta;
    private FirebaseFirestore db;
    private Meta meta;
    private String metaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_meta);

        metaId = getIntent().getStringExtra("metaId");
        if (metaId == null) {
            Toast.makeText(this, "Meta não encontrada!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbarDetalhesMeta);
        toolbar.setNavigationOnClickListener(v -> finish());

        // UI
        txtNomeMeta = findViewById(R.id.txtNomeMeta);
        txtDescricao = findViewById(R.id.txtDescricao);
        txtMetaTotal = findViewById(R.id.txtMetaTotal);
        txtValorAcumulado = findViewById(R.id.txtValorAcumulado);
        txtValorRestante = findViewById(R.id.txtValorRestante);
        txtDataFinal = findViewById(R.id.txtDataFinal);
        txtDiasRestantes = findViewById(R.id.txtDiasRestantes);
        txtValorPorDia = findViewById(R.id.txtValorPorDia);
        txtPorcentagem = findViewById(R.id.txtPorcentagem);
        progressoBarra = findViewById(R.id.progressoBarra);
        btnAdicionarDeposito = findViewById(R.id.btnAdicionarDeposito);
        btnEditarMeta = findViewById(R.id.btnEditarMeta);
        btnVerHistorico = findViewById(R.id.btnVerHistorico);
        btnExcluirMeta = findViewById(R.id.btnExcluirMeta);

        db = FirebaseFirestore.getInstance();

        carregarMeta();

        btnAdicionarDeposito.setOnClickListener(v -> {
            Intent intent = new Intent(this, NovoDepositoActivity.class);
            intent.putExtra("metaId", metaId);
            startActivity(intent);
        });

        btnVerHistorico.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoricoActivity.class);
            intent.putExtra("metaId", metaId);
            startActivity(intent);
        });

        btnEditarMeta.setOnClickListener(v -> {
            if (meta != null) {
                Intent intent = new Intent(this, NovaMetaActivity.class);
                intent.putExtra("modoEdicao", true);
                intent.putExtra("metaId", metaId);
                intent.putExtra("nome", meta.getNome());
                intent.putExtra("descricao", meta.getDescricao());
                intent.putExtra("valor", meta.getMetaTotal());
                intent.putExtra("dataFinal", meta.getDataFinal().toDate().getTime());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Meta ainda não carregada", Toast.LENGTH_SHORT).show();
            }
        });

        btnExcluirMeta.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Excluir Meta")
                    .setMessage("Tem certeza que deseja excluir esta meta? Isso removerá todos os depósitos também.")
                    .setPositiveButton("Sim", (dialog, which) -> excluirMeta())
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void carregarMeta() {
        db.collection("metas").document(metaId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        meta = documentSnapshot.toObject(Meta.class);
                        if (meta != null) {
                            exibirMeta(meta);

                            // 🎉 Exibe mensagem se meta foi concluída há pouco
                            if (meta.isMetaFinalizada() && meta.getDataConclusao() != null) {
                                long tempoDesdeConclusao = System.currentTimeMillis() - meta.getDataConclusao().toDate().getTime();
                                if (tempoDesdeConclusao < 5000) {
                                    new AlertDialog.Builder(this)
                                            .setTitle("🎉 Parabéns!")
                                            .setMessage("Você concluiu sua meta com sucesso!\nContinue assim! 💪")
                                            .setPositiveButton("Fechar", null)
                                            .show();
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, "Meta não encontrada", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao buscar meta", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void exibirMeta(Meta meta) {
        txtNomeMeta.setText(meta.getNome());
        txtDescricao.setText(meta.getDescricao());

        NumberFormat formatoBR = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        txtMetaTotal.setText("Meta: " + formatoBR.format(meta.getMetaTotal()));
        txtValorAcumulado.setText("Acumulado: " + formatoBR.format(meta.getValorAcumulado()));

        double restante = meta.getMetaTotal() - meta.getValorAcumulado();
        txtValorRestante.setText("Falta: " + formatoBR.format(Math.max(0, restante)));

        int porcentagem = (int) meta.getPorcentagem();
        progressoBarra.setProgress(porcentagem);
        txtPorcentagem.setText("Progresso: " + porcentagem + "%");

        Date dataFinal = meta.getDataFinalAsDate();
        if (dataFinal != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            txtDataFinal.setText("Data final: " + sdf.format(dataFinal));

            long diff = dataFinal.getTime() - System.currentTimeMillis();
            int dias = (int) Math.ceil(diff / (1000.0 * 60 * 60 * 24));
            txtDiasRestantes.setText("Dias restantes: " + Math.max(0, dias));

            double valorPorDia = dias > 0 ? restante / dias : 0;
            txtValorPorDia.setText("Valor sugerido por dia: " + formatoBR.format(Math.max(0, valorPorDia)));
        } else {
            txtDataFinal.setText("Data final: --/--/----");
            txtDiasRestantes.setText("Dias restantes: -");
            txtValorPorDia.setText("Valor sugerido por dia: --");
        }

        if (meta.isMetaFinalizada()) {
            btnAdicionarDeposito.setEnabled(false);
            btnAdicionarDeposito.setText("Meta Concluída ✅");
            btnAdicionarDeposito.setAlpha(0.7f);

            // 👉 Exibe a data da conclusão
            if (meta.getDataConclusao() != null) {
                Date dataConclusao = meta.getDataConclusao().toDate();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                txtDataFinal.setText("Concluída em: " + sdf.format(dataConclusao));
            }
        } else {
            btnAdicionarDeposito.setEnabled(true);
            btnAdicionarDeposito.setText("Adicionar Depósito");
            btnAdicionarDeposito.setAlpha(1f);
        }
    }

    private void excluirMeta() {
        db.collection("metas").document(metaId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Meta excluída com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro ao excluir meta", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarMeta();
    }
}
