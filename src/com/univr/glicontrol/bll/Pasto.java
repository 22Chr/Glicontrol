package com.univr.glicontrol.bll;

import java.sql.Time;

public class Pasto {
    private final int idPasto;
    private final int idPaziente;
    private final String nomePasto;
    private Time orario;

    public Pasto(int idPasto, int idPaziente, String nomePasto, Time orario) {
        this.idPasto = idPasto;
        this.idPaziente = idPaziente;
        this.nomePasto = nomePasto;
        this.orario = orario;
    }

    public int getIdPasto() {
        return idPasto;
    }

    public int getIdPaziente() {
        return idPaziente;
    }

    public String getNomePasto() {
        return nomePasto;
    }

    public Time getOrario() {
        return orario;
    }
    public void setOrario(Time orario) {
        this.orario = orario;
    }

}
