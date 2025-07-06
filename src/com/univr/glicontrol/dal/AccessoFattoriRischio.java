package com.univr.glicontrol.dal;

import com.univr.glicontrol.bll.FattoriRischio;

public interface AccessoFattoriRischio {
    FattoriRischio recuperaFattoreRischio(int idPaziente);
    boolean updateFattoreRischio(FattoriRischio fattore);
    boolean insertFattoreRischio(FattoriRischio fattore);
    boolean deleteFattoreRischio(int idPaziente);
}
