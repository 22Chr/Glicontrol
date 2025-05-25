package com.univr.glicontrol.bll;

public class FarmacoTerapia {
    private final Farmaco farmaco;
    private IndicazioniFarmaciTerapia indicazioni;

    public FarmacoTerapia(Farmaco farmaco, IndicazioniFarmaciTerapia indicazioni) {
        this.farmaco = farmaco;
        this.indicazioni = indicazioni;
    }

    public Farmaco getFarmaco() {
        return farmaco;
    }

    public IndicazioniFarmaciTerapia getIndicazioni() {
        return indicazioni;
    }
    public void setIndicazioni(IndicazioniFarmaciTerapia indicazioni) {
        this.indicazioni = indicazioni;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        FarmacoTerapia other = (FarmacoTerapia) obj;
        if (farmaco == null) {
            if (other.farmaco != null) return false;
        } else if (!farmaco.equals(other.farmaco)) return false;
        if (indicazioni == null) {
            return other.indicazioni == null;
        } else return indicazioni.equals(other.indicazioni);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((farmaco == null) ? 0 : farmaco.hashCode());
        result = prime * result + ((indicazioni == null) ? 0 : indicazioni.hashCode());
        return result;
    }
}
