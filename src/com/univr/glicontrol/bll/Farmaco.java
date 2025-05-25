package com.univr.glicontrol.bll;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Farmaco farmaco = (Farmaco) o;
        return idFarmaco == farmaco.idFarmaco &&
                Objects.equals(nome, farmaco.nome) &&
                Objects.equals(principioAttivo, farmaco.principioAttivo) &&
                Objects.equals(dosaggio, farmaco.dosaggio) &&
                Objects.equals(unitaMisura, farmaco.unitaMisura) &&
                Objects.equals(produttore, farmaco.produttore) &&
                Objects.equals(viaSomministrazione, farmaco.viaSomministrazione) &&
                Objects.equals(effettiCollaterali, farmaco.effettiCollaterali) &&
                Objects.equals(interazioniNote, farmaco.interazioniNote) &&
                Objects.equals(tipologia, farmaco.tipologia);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idFarmaco, nome, principioAttivo, dosaggio, unitaMisura, produttore, viaSomministrazione, effettiCollaterali, interazioniNote, tipologia);
    }

}
