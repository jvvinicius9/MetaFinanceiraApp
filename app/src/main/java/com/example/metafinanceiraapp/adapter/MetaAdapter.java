package com.example.metafinanceiraapp.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metafinanceiraapp.R;
import com.example.metafinanceiraapp.model.Meta;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MetaAdapter extends RecyclerView.Adapter<MetaAdapter.MetaViewHolder> {

    private final List<Meta> metas;
    private final List<String> metaIds;
    private final OnMetaClickListener listener;

    public interface OnMetaClickListener {
        void onMetaClick(String metaId);
    }

    public MetaAdapter(List<Meta> metas, List<String> metaIds, OnMetaClickListener listener) {
        this.metas = metas;
        this.metaIds = metaIds;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MetaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meta, parent, false);
        return new MetaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MetaViewHolder holder, int position) {
        Meta meta = metas.get(position);
        String metaId = metaIds.get(position);

        NumberFormat formatoBR = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        double porcentagem = (meta.getMetaTotal() > 0)
                ? (meta.getValorAcumulado() / meta.getMetaTotal()) * 100
                : 0;

        // Selo e cor para meta finalizada
        if (meta.isMetaFinalizada()) {
            holder.txtNomeMeta.setText(meta.getNome() + " ✅");
            holder.itemView.setAlpha(0.85f); // leve transparência
            holder.itemView.setBackgroundColor(Color.parseColor("#E8F5E9")); // verde claro
        } else {
            holder.txtNomeMeta.setText(meta.getNome());
            holder.itemView.setAlpha(1f);
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        holder.txtResumoMeta.setText(formatoBR.format(meta.getValorAcumulado())
                + " de " + formatoBR.format(meta.getMetaTotal()));
        holder.progressoMeta.setProgress((int) porcentagem);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMetaClick(metaId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return metas != null ? metas.size() : 0;
    }

    static class MetaViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomeMeta, txtResumoMeta;
        ProgressBar progressoMeta;

        MetaViewHolder(View itemView) {
            super(itemView);
            txtNomeMeta = itemView.findViewById(R.id.txtNomeMeta);
            txtResumoMeta = itemView.findViewById(R.id.txtResumoMeta);
            progressoMeta = itemView.findViewById(R.id.progressoMeta);
        }
    }
}
