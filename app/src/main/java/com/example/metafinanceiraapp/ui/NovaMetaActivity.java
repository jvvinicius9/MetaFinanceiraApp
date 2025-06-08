package com.example.metafinanceiraapp.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.metafinanceiraapp.R;
import com.example.metafinanceiraapp.model.Meta;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NovaMetaActivity extends AppCompatActivity {

    private TextInputEditText edtNome, edtDescricao, edtValor, edtDataFinal;
    private MaterialButton btnSalvar;
    private Calendar dataFinalSelecionada = null;

    private boolean modoEdicao = false;
    private String metaId = null;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_meta);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbarNovaMeta);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Inputs
        edtNome = findViewById(R.id.edtNomeMeta);
        edtDescricao = findViewById(R.id.edtDescricaoMeta);
        edtValor = findViewById(R.id.edtValorMeta);
        edtDataFinal = findViewById(R.id.edtDataFinal);
        btnSalvar = findViewById(R.id.btnSalvarMeta);

        // Verifica se é modo edição
        modoEdicao = getIntent().getBooleanExtra("modoEdicao", false);
        if (modoEdicao) {
            metaId = getIntent().getStringExtra("metaId");
            edtNome.setText(getIntent().getStringExtra("nome"));
            edtDescricao.setText(getIntent().getStringExtra("descricao"));
            edtValor.setText(String.valueOf(getIntent().getDoubleExtra("valor", 0.0)));

            long millis = getIntent().getLongExtra("dataFinal", -1);
            if (millis != -1) {
                dataFinalSelecionada = Calendar.getInstance();
                dataFinalSelecionada.setTimeInMillis(millis);
                edtDataFinal.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .format(dataFinalSelecionada.getTime()));
            }

            btnSalvar.setText("Salvar Alterações");
        }

        // Seletor de data
        edtDataFinal.setOnClickListener(v -> mostrarDatePicker());

        // Botão salvar
        btnSalvar.setOnClickListener(v -> salvarMeta());
    }

    private void mostrarDatePicker() {
        final Calendar hoje = Calendar.getInstance();
        int ano = hoje.get(Calendar.YEAR);
        int mes = hoje.get(Calendar.MONTH);
        int dia = hoje.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    dataFinalSelecionada = Calendar.getInstance();
                    dataFinalSelecionada.set(year, month, dayOfMonth, 0, 0, 0);
                    edtDataFinal.setText(String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year));
                },
                ano, mes, dia
        );
        datePickerDialog.show();
    }

    private void salvarMeta() {
        String nome = edtNome.getText().toString().trim();
        String descricao = edtDescricao.getText().toString().trim();
        String valorStr = edtValor.getText().toString().trim();

        if (TextUtils.isEmpty(nome)) {
            edtNome.setError("Obrigatório");
            return;
        }
        if (TextUtils.isEmpty(valorStr)) {
            edtValor.setError("Obrigatório");
            return;
        }
        if (dataFinalSelecionada == null) {
            edtDataFinal.setError("Escolha uma data");
            return;
        }

        double metaTotal;
        try {
            metaTotal = Double.parseDouble(valorStr.replace(",", "."));
        } catch (Exception e) {
            edtValor.setError("Valor inválido");
            return;
        }

        Timestamp dataFinal = new Timestamp(dataFinalSelecionada.getTime());

        if (modoEdicao && metaId != null) {
            // Atualiza meta existente
            db.collection("metas").document(metaId)
                    .update(
                            "nome", nome,
                            "descricao", descricao,
                            "metaTotal", metaTotal,
                            "dataFinal", dataFinal
                    )
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Meta atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Erro ao atualizar meta", Toast.LENGTH_SHORT).show());

        } else {
            // Cria nova meta (valorAcumulado inicia em 0)
            Meta novaMeta = new Meta(nome, descricao, metaTotal, 0.0, dataFinal);

            db.collection("metas")
                    .add(novaMeta)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Meta salva com sucesso!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Erro ao salvar meta", Toast.LENGTH_SHORT).show());
        }
    }
}
