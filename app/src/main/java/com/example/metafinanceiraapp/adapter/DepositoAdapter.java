package com.example.metafinanceiraapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metafinanceiraapp.R;
import com.example.metafinanceiraapp.model.Deposito;
import com.google.firebase.Timestamp;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DepositoAdapter extends RecyclerView.Adapter<DepositoAdapter.DepositoViewHolder> {

    private final List<Deposito> depositos;

    public DepositoAdapter(List<Deposito> depositos) {
        this.depositos = depositos;
    }

    @NonNull
    @Override
    public DepositoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_deposito, parent, false);
        return new DepositoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DepositoViewHolder holder, int position) {
        Deposito deposito = depositos.get(position);

        // Formata valor no padrão brasileiro
        NumberFormat formatoBR = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        holder.txtValor.setText("Valor: " + formatoBR.format(deposito.getValor()));

        // Formata data
        Timestamp data = deposito.getData();
        String dataStr = "-";
        if (data != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dataStr = sdf.format(data.toDate());
        }
        holder.txtData.setText("Data: " + dataStr);
    }

    @Override
    public int getItemCount() {
        return depositos != null ? depositos.size() : 0;
    }

    static class DepositoViewHolder extends RecyclerView.ViewHolder {
        TextView txtValor, txtData;

        DepositoViewHolder(View itemView) {
            super(itemView);
            txtValor = itemView.findViewById(R.id.txtValorItem);
            txtData = itemView.findViewById(R.id.txtDataItem);
        }
    }
}
