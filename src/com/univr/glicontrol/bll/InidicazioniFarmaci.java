package com.univr.glicontrol.bll;

public class InidicazioniFarmaci {
    private final int idIndicazioniFarmaci;
    private final int idTerapiaDiabeteAnnessa;
    private final int idTerapiaConcomitanteAnnessa;
    private final int idFarmacoAnnesso;
    private float dosaggio;
    private String frequenzaAssunzione;
    private String orariAssunzione;

    public InidicazioniFarmaci(int idIndicazioniFarmaci, int idTerapiaDiabeteAnnessa, int idTerapiaConcomitanteAnnessa, int idFarmacoAnnesso, float dosaggio, String frequenzaAssunzione, String orariAssunzione) {
        this.idIndicazioniFarmaci = idIndicazioniFarmaci;
        this.idTerapiaDiabeteAnnessa = idTerapiaDiabeteAnnessa;
        this.idTerapiaConcomitanteAnnessa = idTerapiaConcomitanteAnnessa;
        this.idFarmacoAnnesso = idFarmacoAnnesso;
        this.dosaggio = dosaggio;
        this.frequenzaAssunzione = frequenzaAssunzione;
        this.orariAssunzione = orariAssunzione;
    }

    public int getIdIndicazioniFarmaci() {
        return idIndicazioniFarmaci;
    }

    public int getIdTerapiaDiabeteAnnessa() {
        return idTerapiaDiabeteAnnessa;
    }

    public int getIdTerapiaConcomitanteAnnessa() {
        return idTerapiaConcomitanteAnnessa;
    }

    public int getIdFarmacoAnnesso() {
        return idFarmacoAnnesso;
    }

    public float getDosaggio() {
        return dosaggio;
    }
    public void setDosaggio(float dosaggio) {
        this.dosaggio = dosaggio;
    }

    public String getFrequenzaAssunzione() {
        return frequenzaAssunzione;
    }
    public void setFrequenzaAssunzione(String frequenzaAssunzione) {
        this.frequenzaAssunzione = frequenzaAssunzione;
    }

    public String getOrariAssunzione() {
        return orariAssunzione;
    }
    public void setOrariAssunzione(String orariAssunzione) {
        this.orariAssunzione = orariAssunzione;
    }

}
