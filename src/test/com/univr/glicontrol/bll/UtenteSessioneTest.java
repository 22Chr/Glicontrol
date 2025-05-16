package com.univr.glicontrol.bll;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtenteSessioneTest {

    @Test
    void getInstance() {
        UtenteSessione.getInstance();
        assertNotNull(UtenteSessione.getInstance());
    }

    @Test
    void verificaUtente() {
        UtenteSessione utenteSessione = UtenteSessione.getInstance();
        assertTrue(utenteSessione.verificaUtente("CHCCRS03L22F861M", "SagradaFam", "ADMIN"));
    }

    @Test
    void creaAdminConnesso() {
        UtenteSessione utenteSessione = UtenteSessione.getInstance();
        Admin admin = utenteSessione.creaAdminConnesso("CHCCRS03L22F861M", "SagradaFam");
        assertNotNull(admin);
        assertInstanceOf(Admin.class, admin);
        assertEquals("CHCCRS03L22F861M", admin.getCodiceFiscale());
        assertEquals("Christian", admin.getNome());
        assertEquals("ADMIN", admin.getRuolo());
    }

    @Test
    void creaMedicoConnesso() {
        UtenteSessione utenteSessione = UtenteSessione.getInstance();
        Medico medico = utenteSessione.creaMedicoConnesso("MBRSFO03H51E349E", "Gelato9876");

        assertNotNull(medico);
        assertInstanceOf(Medico.class, medico);
        assertEquals("sofiaambrosi670@gmail.com", medico.getEmail());
    }

    @Test
    void creaPazienteConnesso() {
        UtenteSessione utenteSessione = UtenteSessione.getInstance();
        Paziente paziente = utenteSessione.creaPazienteConnesso("MRTNNA04A62H612X", "CalDigit04");

        assertNotNull(paziente);
        assertInstanceOf(Paziente.class, paziente);
        assertEquals("Anna", paziente.getNome());
        assertEquals(60, paziente.getPeso());
        //indagare colonna peso della tabella paziente
    }
}