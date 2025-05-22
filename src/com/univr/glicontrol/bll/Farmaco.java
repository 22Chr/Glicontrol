package com.univr.glicontrol.bll;

import java.util.List;

public class Farmaco {
    private final int idFarmaco;
    private String nome;
    private String principioAttivo;
    private List<Float> dosaggio;
    private String unitaMisura;
    private String produttore;
    private String viaSomministrazione;
    private String effettiCollaterali;
    private String interazioniNote;
    private String tipologia;

    public Farmaco(int idFarmaco, String nome, String principioAttivo, List<Float> dosaggio, String unitaMisura, String produttore, String viaSomministrazione, String effettiCollaterali, String interazioniNote, String tipologia) {
        this.idFarmaco = idFarmaco;
        this.nome = nome;
        this.principioAttivo = principioAttivo;
        this.dosaggio = dosaggio;
        this.unitaMisura = unitaMisura;
        this.produttore = produttore;
        this.viaSomministrazione = viaSomministrazione;
        this.effettiCollaterali = effettiCollaterali;
        this.interazioniNote = interazioniNote;
        this.tipologia = tipologia;
    }

    public int getIdFarmaco() {
        return idFarmaco;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPrincipioAttivo() {
        return principioAttivo;
    }
    public void setPrincipioAttivo(String principioAttivo) {
        this.principioAttivo = principioAttivo;
    }

    public List<Float> getDosaggio() {
        return dosaggio;
    }
    public void setDosaggio(List<Float> dosaggio) {
        this.dosaggio = dosaggio;
    }

    public String getUnitaMisura() {
        return unitaMisura;
    }
    public void setUnitaMisura(String unitaMisura) {
        this.unitaMisura = unitaMisura;
    }

    public String getProduttore() {
        return produttore;
    }
    public void setProduttore(String produttore) {
        this.produttore = produttore;
    }

    public String getViaSomministrazione() {
        return viaSomministrazione;
    }
    public void setViaSomministrazione(String viaSomministrazione) {
        this.viaSomministrazione = viaSomministrazione;
    }

    public String getEffettiCollaterali() {
        return effettiCollaterali;
    }
    public void setEffettiCollaterali(String effettiCollaterali) {
        this.effettiCollaterali = effettiCollaterali;
    }

    public String getInterazioniNote() {
        return interazioniNote;
    }
    public void setInterazioniNote(String interazioniNote) {
        this.interazioniNote = interazioniNote;
    }

    public String getTipologia() {
        return tipologia;
    }
    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
    }
}
