package com.univr.glicontrol.pl.Models;

import com.univr.glicontrol.bll.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UtilityPortaliTest {
    private Paziente paziente;
    private UtilityPortali utility;
    private int idReferente;

    @BeforeEach
    void setUp() {
        // Prepara un paziente dal DB. Assicurati che l'ID esista.
        paziente = new Paziente(6, "MRTNNA04A62H612X",
                "Anna",
                "Martini",
                "CarloDiana",
                "Paziente",
                8,
                Date.valueOf("2004-01-22"),
                "F",
                "anna.martini2004@gmail.com",
                "amoxicillina",
                0);
        UtenteSessione.getInstance().creaPazienteConnesso("MRTNNA04A62H612X", "CarloDiana");
        Medico medico = new Medico(2,
                "CHCCRS03L22F861M",
                "Christian",
                "Checchetti",
                "Gio_Roccia",
                "Medico",
                "chris22checchetti@gmail.com");
        UtenteSessione.getInstance().creaMedicoConnesso("CHCCRS03L22F861M", "Gio_Roccia");

        utility = new UtilityPortali(paziente);

        idReferente = paziente.getMedicoRiferimento();
    }

    @Test
    void testConvertiOra() {
        Time time = utility.convertiOra(14, 30);
        assertEquals("14:30:00", time.toString());
    }

    @Test
    void testGetListaPasti() {
        List<String> pasti = utility.getListaPasti();
        assertNotNull(pasti);
        assertFalse(pasti.isEmpty(), "La lista dei pasti non dovrebbe essere vuota");
    }

    @Test
    void testGetNomePastoPerPastoFormattato() {
        List<String> pasti = utility.getListaPasti();
        if (!pasti.isEmpty()) {
            String pastoFormattato = pasti.get(0);
            String nome = utility.getNomePastoPerPastoFormattato(pastoFormattato);
            assertNotNull(nome);
        }
    }

    @Test
    void testGetListaSintomiPazienti() {
        List<String> sintomi = utility.getListaSintomiPazienti();
        assertNotNull(sintomi);
    }

    @Test
    void testGetSintomoPerDescrizioneFormattata() {
        List<String> sintomi = utility.getListaSintomiPazienti();
        if (!sintomi.isEmpty()) {
            String descrizioneFormattata = sintomi.get(0);
            Sintomo s = utility.getSintomoPerDescrizioneFormattata(descrizioneFormattata);
            assertNotNull(s);
        }
    }

    @Test
    void testGetListaPatologieConcomitantiPaziente() {
        List<String> patologie = utility.getListaPatologieConcomitantiPaziente();
        assertNotNull(patologie);
    }

    @Test
    void testGetPazienteSessione() {
        Paziente p = utility.getPazienteSessione();
        assertNotNull(p);
        assertEquals(paziente.getIdUtente(), p.getIdUtente());
    }

    @Test
    void testGetMedicoSessione() {
        Medico m = utility.getMedicoSessione();
        assertNotNull(m);
    }

    @Test
    void testGetListaRilevazioniGlicemichePazienti() {
        List<String> rilevazioni = utility.getListaRilevazioniGlicemichePazienti();
        assertNotNull(rilevazioni);
        assertFalse(rilevazioni.isEmpty(), "La lista delle rilevazioni glicemiche non dovrebbe essere vuota");
    }

//    @Test
//    void testGetRilevazioneGlicemicaPerValoreFormattata() {
//        List<String> rilevazioni = utility.getListaRilevazioniGlicemichePazienti();
//        String prima = rilevazioni.getFirst();
//        RilevazioneGlicemica rilevazione = utility.getRilevazioneGlicemicaPerValoreFormattata(prima);
//        assertNotNull(rilevazione);
//        assertEquals(prima, utility.getRilevazioneGlicemicaPerValoreFormattata(rilevazione.toString()));
//
//    }

    @Test
    void testGetListaTerapiePaziente() {
        List<String> terapie = utility.getListaTerapiePaziente();
        assertNotNull(terapie);
        assertFalse(terapie.isEmpty(), "La lista delle terapie non dovrebbe essere vuota");
    }

    @Test
    void testGetTerapiaPerNomeFormattata() {
        String nomeTerapia = utility.getListaTerapiePaziente().get(0);
        Terapia terapia = utility.getTerapiaPerNomeFormattata(nomeTerapia);
        assertNotNull(terapia);
        assertEquals(nomeTerapia, terapia.getNome());
    }

    @Test
    void testGetListaFarmaciDaAssumere() {
        List<String> farmaci = utility.getListaFarmaciDaAssumere();
        assertNotNull(farmaci);
        // Non testiamo isEmpty perché può essere vuota se sono tutti stati assunti
    }

    @Test
    void testGetListaFarmaciAssuntiOggi() {
        List<String> assunti = utility.getListaFarmaciAssuntiOggi();
        assertNotNull(assunti);
        // Come sopra, può essere vuota
    }

    @Test
    void testGetListaCompletaAssunzioneFarmaciDiabete() {
        List<String> farmaciDiabete = utility.getListaCompletaAssunzioneFarmaciDiabete();
        assertNotNull(farmaciDiabete);
    }

    @Test
    void testGetListaCompletaAssunzioneFarmaciTerapieConcomitanti() {
        List<String> farmaciConcomitanti = utility.getListaCompletaAssunzioneFarmaciTerapieConcomitanti();
        assertNotNull(farmaciConcomitanti);
    }

    @Test
    void testGetFarmacoPerNomeFormattato() {
        List<String> assunti = utility.getListaFarmaciAssuntiOggi();
        if (!assunti.isEmpty()) {
            Farmaco f = utility.getFarmacoPerNomeFormattato(assunti.get(0));
            assertNotNull(f);
        }
    }

    @Test
    void testGetListaPatologieConcomitanti() {
        List<String> patologie = utility.getListaPatologieConcomitantiPaziente();
        assertNotNull(patologie);
    }

    @Test
    void testGetPatologiaConcomitantePerNomeFormattata() {
        List<String> patologie = utility.getListaPatologieConcomitantiPaziente();
        if (!patologie.isEmpty()) {
            PatologiaConcomitante p = utility.getPatologiaConcomitantePerNomeFormattata(patologie.get(0));
            assertNotNull(p);
        }
    }

    @Test
    void testGetPazientiAssociatiAlReferente() {
        int idReferente = paziente.getMedicoRiferimento();
        List<String> associati = utility.getPazientiAssociatiAlReferente(idReferente);
        assertNotNull(associati);
        assertTrue(associati.stream().anyMatch(p -> p.contains(paziente.getCodiceFiscale())));
    }

    @Test
    void testGetIndicazioniTemporaliTerapia() {
        List<String> terapie = utility.getListaTerapiePaziente();
        if (!terapie.isEmpty()) {
            Terapia terapia = utility.getTerapiaPerNomeFormattata(terapie.get(0));
            String indicazioni = utility.getIndicazioniTemporaliTerapia(terapia);
            assertNotNull(indicazioni);
            assertTrue(indicazioni.contains("-"));
        }
    }

    @Test
    void testGetPazientiNonAssociatiAlReferente() {
        List<String> nonAssociati = utility.getPazientiNonAssociatiAlReferente(idReferente);
        assertNotNull(nonAssociati);

        for (String s : nonAssociati) {
            Paziente p = utility.getPazienteNonAssociatoDaNomeFormattato(s);
            assertNotNull(p);
            assertNotEquals(idReferente, p.getMedicoRiferimento());
        }
    }

    @Test
    void testGetPazienteNonAssociatoDaNomeFormattato() {
        List<String> nonAssociati = utility.getPazientiNonAssociatiAlReferente(idReferente);
        if (!nonAssociati.isEmpty()) {
            String nomeFormattato = nonAssociati.get(0);
            Paziente p = utility.getPazienteNonAssociatoDaNomeFormattato(nomeFormattato);
            assertNotNull(p);
            String expected = p.getCognome() + " " + p.getNome() + " (" + p.getCodiceFiscale() + ")";
            assertEquals(nomeFormattato, expected);
        }
    }

    @Test
    void testGetPazienteAssociatoDaNomeFormattato() {
        List<String> associati = utility.getPazientiAssociatiAlReferente(idReferente);
        if (!associati.isEmpty()) {
            String nomeFormattato = associati.get(0);
            Paziente p = utility.getPazienteAssociatoDaNomeFormattato(nomeFormattato);
            assertNotNull(p);
            String expected = p.getCognome() + " " + p.getNome() + " (" + p.getCodiceFiscale() + ")";
            assertEquals(nomeFormattato, expected);
        }
    }

    @Test
    void testConvertiDosaggio() {
        assertEquals(12.5f, utility.convertiDosaggio("12.5"));
        assertEquals(100.0f, utility.convertiDosaggio("100"));
        assertEquals(0.75f, utility.convertiDosaggio("0.75"));
    }

    @Test
    void testGetNotificheFormattate() {
        List<String> notifiche = utility.getNotificheFormattate();
        assertNotNull(notifiche);
        for (String n : notifiche) {
            assertTrue(n.contains("[") && n.contains("]"));
            assertTrue(n.contains("(") && n.contains(")"));
        }
    }

    @Test
    void testGetNotificaNonVisualizzateDaNomeFormattato() {
        List<String> notifiche = utility.getNotificheFormattate();
        if (!notifiche.isEmpty()) {
            String formattata = notifiche.get(0);
            Notifica notifica = utility.getNotificaNonVisualizzateDaNomeFormattato(formattata);
            assertNotNull(notifica);
            assertEquals(formattata, buildExpectedFormat(notifica));
        }
    }

    private String buildExpectedFormat(Notifica n) {
        return "[" + n.getTitolo() + "]\n\n(" +
                n.getPazienteAssociato().getCodiceFiscale() + ")\n\n" +
                n.getMessaggio() + "\n\n(" +
                n.getDataNotifica().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")) +
                ")\n\n\n\n";
    }

    @Test
    void testGetListaLog() {
        List<String> log = utility.getListaLog();
        assertNotNull(log);
        assertTrue(log.stream().anyMatch(s -> !s.isBlank()));
    }

}