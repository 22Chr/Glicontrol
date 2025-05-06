package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.Medico;
import com.univr.glicontrol.bll.Paziente;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccessoListaUtentiImplTest {

    @Test
    void recuperaTuttiIMedici() {
        AccessoListaUtenti accessoListaUtenti = new AccessoListaUtentiImpl();
        List<Medico> Medici = accessoListaUtenti.recuperaTuttiIMedici();
        assertEquals(2, Medici.size());

        for (Medico m : Medici) {
            assertNotNull(m);
            assertInstanceOf(Medico.class, m);
            assertNotNull(m.getCodiceFiscale());
            assertNotNull(m.getNome());
            assertNotNull(m.getCognome());
            assertNotNull(m.getRuolo());
            assertNotNull(m.getEmail());
        }
    }

    @Test
    void recuperaTuttiIPazienti() {
        AccessoListaUtenti accessoListaUtenti = new AccessoListaUtentiImpl();
        List<Paziente> Pazienti = accessoListaUtenti.recuperaTuttiIPazienti();
        assertEquals(1, Pazienti.size());

        for (Paziente p : Pazienti) {
            assertNotNull(p);
            assertInstanceOf(Paziente.class, p);
            assertNotNull(p.getCodiceFiscale());
            assertNotNull(p.getNome());
            assertNotNull(p.getCognome());
            assertNotNull(p.getRuolo());
            assertNotNull(p.getEmail());
            assertNotEquals(0, p.getPeso());
        }
    }
}