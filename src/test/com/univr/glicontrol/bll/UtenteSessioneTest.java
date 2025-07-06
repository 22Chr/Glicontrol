package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtenteSessioneTest {

    //TEST DI INTEGRAZIONE
    //TODO: il test accedere direttamente al DB sulla macchina, quindi i campi vanno cambiati in base al pc su cui si lavora

    @Test
    void testVerificaUtenteMedico() {
        UtenteSessione us = UtenteSessione.getInstance();
        String cf = "CHCCRS03L22F861M";
        String password = "Gio_Roccia";
        String ruolo = "MEDICO";

        boolean risultato = us.verificaUtente(cf, password, ruolo);

        assertTrue(risultato, "Il medico è stato trovato nel database");
    }

    @Test
    void testCreaMedicoConnesso() {
        UtenteSessione us = UtenteSessione.getInstance();
        String cf = "CHCCRS03L22F861M";
        String password = "Gio_Roccia";

        Medico medico = us.creaMedicoConnesso(cf, password);

        assertNotNull(medico, "Il medico non è null");
        assertEquals("Christian", medico.getNome());
        assertEquals("Checchetti", medico.getCognome());
        assertEquals("chris22checchetti@gmail.com", medico.getEmail());
        assertEquals("CHCCRS03L22F861M", medico.getCodiceFiscale());
    }

    @Test
    void testCreaAdminConnesso() {
        UtenteSessione us = UtenteSessione.getInstance();
        Admin admin = us.creaAdminConnesso("CHCCRS03L22F861M", "SagradaFam");

        assertNotNull(admin);
        assertEquals("ADMIN", admin.getRuolo());
    }

    @Test
    void testCreaPazienteConnesso() {
        UtenteSessione sessione = UtenteSessione.getInstance();
        Paziente paziente = sessione.creaPazienteConnesso("MRTNNA04A62H612X", "CarloDiana");

        assertNotNull(paziente);
        assertEquals("Anna", paziente.getNome());
        assertEquals("anna.martini2004@gmail.com", paziente.getEmail());
    }

}