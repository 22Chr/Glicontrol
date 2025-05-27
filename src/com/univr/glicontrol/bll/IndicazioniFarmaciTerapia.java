package com.univr.glicontrol.bll;

public class IndicazioniFarmaciTerapia {
    private int idIndicazioniFarmaci;
    private final int idTerapiaDiabeteAnnessa;
    private final int idFarmacoAnnesso;
    private float dosaggio;
    private String frequenzaAssunzione;
    private String orariAssunzione;

    public IndicazioniFarmaciTerapia(int idTerapiaDiabeteAnnessa, int idFarmacoAnnesso, float dosaggio, String frequenzaAssunzione, String orariAssunzione) {
        this.idTerapiaDiabeteAnnessa = idTerapiaDiabeteAnnessa;
        this.idFarmacoAnnesso = idFarmacoAnnesso;
        this.dosaggio = dosaggio;
        this.frequenzaAssunzione = frequenzaAssunzione;
        this.orariAssunzione = orariAssunzione;
    }

    public int getIdIndicazioniFarmaci() {
        return idIndicazioniFarmaci;
    }
    public void setIdIndicazioniFarmaci(int idIndicazioniFarmaci) {
        this.idIndicazioniFarmaci = idIndicazioniFarmaci;
    }

    public int getIdTerapiaDiabeteAnnessa() {
        return idTerapiaDiabeteAnnessa;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        IndicazioniFarmaciTerapia other = (IndicazioniFarmaciTerapia) obj;
        return idIndicazioniFarmaci == other.idIndicazioniFarmaci &&
                idTerapiaDiabeteAnnessa == other.idTerapiaDiabeteAnnessa &&
                idFarmacoAnnesso == other.idFarmacoAnnesso &&
                Float.floatToIntBits(dosaggio) == Float.floatToIntBits(other.dosaggio) &&
                frequenzaAssunzione.equals(other.frequenzaAssunzione) &&
                orariAssunzione.equals(other.orariAssunzione);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result *= prime + idIndicazioniFarmaci;
        result *= prime + idTerapiaDiabeteAnnessa;
        result *= prime + idFarmacoAnnesso;
        result *= prime + Float.floatToIntBits(dosaggio);
        result *= prime + frequenzaAssunzione.hashCode();
        result *= prime + orariAssunzione.hashCode();
        return result;
    }

}
