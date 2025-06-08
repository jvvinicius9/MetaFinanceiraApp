package com.example.metafinanceiraapp.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.metafinanceiraapp.R;
import com.example.metafinanceiraapp.model.Deposito;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class NovoDepositoActivity extends AppCompatActivity {

    private TextInputEditText edtValor;
    private MaterialButton btnSalvarDeposito;
    private String metaId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_deposito);

        // Toolbar com botão de voltar
        MaterialToolbar toolbar = findViewById(R.id.toolbarNovoDeposito);
        if (toolbar != null) toolbar.setNavigationOnClickListener(v -> finish());

        // Inicializa os componentes
        edtValor = findViewById(R.id.edtValorDeposito);
        btnSalvarDeposito = findViewById(R.id.btnSalvarDeposito);
        db = FirebaseFirestore.getInstance();

        // Recebe o ID da meta
        metaId = getIntent().getStringExtra("metaId");
        if (metaId == null) {
            Toast.makeText(this, "Meta não encontrada!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnSalvarDeposito.setOnClickListener(v -> salvarDeposito());
    }

    private void salvarDeposito() {
        String valorStr = edtValor.getText().toString().trim();
        if (TextUtils.isEmpty(valorStr)) {
            edtValor.setError("Informe o valor");
            return;
        }

        double valor;
        try {
            valor = Double.parseDouble(valorStr.replace(",", "."));
        } catch (Exception e) {
            edtValor.setError("Valor inválido");
            return;
        }

        if (valor <= 0) {
            edtValor.setError("Informe um valor maior que zero");
            return;
        }

        // Cria o objeto do depósito
        Deposito deposito = new Deposito();
        deposito.setValor(valor);
        deposito.setData(new Timestamp(new Date()));
        deposito.setIdMeta(metaId);

        // Salva o depósito na coleção raiz
        db.collection("depositos")
                .add(deposito)
                .addOnSuccessListener(documentReference -> atualizarValorAcumulado(valor))
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro ao salvar depósito", Toast.LENGTH_SHORT).show());
    }

    private void atualizarValorAcumulado(double valorDeposito) {
        DocumentReference metaRef = db.collection("metas").document(metaId);

        db.runTransaction(transaction -> {
            double acumulado = transaction.get(metaRef).getDouble("valorAcumulado") != null
                    ? transaction.get(metaRef).getDouble("valorAcumulado") : 0.0;

            double metaTotal = transaction.get(metaRef).getDouble("metaTotal") != null
                    ? transaction.get(metaRef).getDouble("metaTotal") : 0.0;

            acumulado += valorDeposito;
            transaction.update(metaRef, "valorAcumulado", acumulado);

            // Finaliza a meta automaticamente se atingir ou ultrapassar o total
            if (acumulado >= metaTotal) {
                transaction.update(metaRef, "metaFinalizada", true);
                transaction.update(metaRef, "dataConclusao", new Timestamp(new Date()));
            }

            return null;
        }).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Depósito salvo com sucesso!", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Erro ao atualizar meta", Toast.LENGTH_SHORT).show());
    }
}
