package com.example.metafinanceiraapp.model;

import com.google.firebase.Timestamp;
import java.util.Date;

public class Meta {

    private String nome;
    private String descricao;
    private double metaTotal;
    private double valorAcumulado;
    private Timestamp dataFinal;

    // Novos campos
    private boolean metaFinalizada = false;
    private Timestamp dataConclusao;

    // Construtor vazio (Firebase exige)
    public Meta() {}

    // Construtor principal
    public Meta(String nome, String descricao, double metaTotal, double valorAcumulado, Timestamp dataFinal) {
        this.nome = nome;
        this.descricao = descricao;
        this.metaTotal = metaTotal;
        this.valorAcumulado = valorAcumulado;
        this.dataFinal = dataFinal;
        this.metaFinalizada = false;
        this.dataConclusao = null;
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getMetaTotal() { return metaTotal; }
    public void setMetaTotal(double metaTotal) { this.metaTotal = metaTotal; }

    public double getValorAcumulado() { return valorAcumulado; }
    public void setValorAcumulado(double valorAcumulado) { this.valorAcumulado = valorAcumulado; }

    public Timestamp getDataFinal() { return dataFinal; }
    public void setDataFinal(Timestamp dataFinal) { this.dataFinal = dataFinal; }

    public boolean isMetaFinalizada() { return metaFinalizada; }
    public void setMetaFinalizada(boolean metaFinalizada) { this.metaFinalizada = metaFinalizada; }

    public Timestamp getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(Timestamp dataConclusao) { this.dataConclusao = dataConclusao; }

    // Métodos auxiliares
    public Date getDataFinalAsDate() {
        return dataFinal != null ? dataFinal.toDate() : null;
    }

    public Date getDataConclusaoAsDate() {
        return dataConclusao != null ? dataConclusao.toDate() : null;
    }

    public double getPorcentagem() {
        if (metaTotal == 0) return 0;
        return (valorAcumulado / metaTotal) * 100.0;
    }
}
