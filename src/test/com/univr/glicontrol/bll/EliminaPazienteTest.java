package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class EliminaPazienteTest {

    EliminaPaziente eliminaPaziente;
    @Test
    void deletePaziente() {
        InserisciPaziente inserisciTest = new InserisciPaziente();
        inserisciTest.insertPaziente("GBTAGO98A78I972P", "Agostino", "Geberto", "1234567890", 3, Date.valueOf("1987-04-05"), "M", "gebertoago@gmail.com", null, 67 );
        ListaPazienti trovaPaziente = new ListaPazienti();
        boolean el = false;
        for (Paziente p : trovaPaziente.getListaCompletaPazienti()) {
            if (p.getCodiceFiscale().equals("GBTAGO98A78I972P")) {
                eliminaPaziente = new EliminaPaziente(p);
                el = eliminaPaziente.deletePaziente();
                break;
            }
        }
        assertEquals(true, el);
    }

    @Test
    void notificaEliminazionePaziente() {
    }
}