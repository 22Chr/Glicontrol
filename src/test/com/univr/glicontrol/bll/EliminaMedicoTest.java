package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EliminaMedicoTest {

    private EliminaMedico eliminaMedico;

    @Test
    void deleteMedicoSuccess() {
        InserisciMedico inserisciTest = new InserisciMedico();
        inserisciTest.insertMedico("ZNTPLO98G13H765M", "Paolo", "Zanotto", "paolozany_falseXXX@gmail.com", "1234567890");
        ListaMedici trovaMedico = new ListaMedici();
        int el = -5;
        for (Medico m : trovaMedico.getListaCompletaMedici()) {
            if (m.getCodiceFiscale().equals("ZNTPLO98G13H765M")) {
                eliminaMedico = new EliminaMedico(m);
                el = eliminaMedico.deleteMedico();
                break;
            }
        }
        assertEquals(1, el);
    }

    @Test
    void notificaEliminazioneMedico() {
        EliminaMedico eliminaMedico = new EliminaMedico(null);

        String emailDestinatario = "anna.martini2004@gmail.com";
        boolean risultato = eliminaMedico.notificaEliminazioneMedico(emailDestinatario);

        assertTrue(risultato, "L'invio dell'email dovrebbe andare a buon fine");
    }
}