package com.univr.glicontrol.bll;

import org.junit.jupiter.api.*;

import java.sql.Date;
import java.sql.Time;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GestioneAssunzioneFarmaciTest {
    private static GestioneAssunzioneFarmaci gestione;
    private static Paziente paziente;
    private static Farmaco farmaco;
    private static List<Float> dosaggi;

    @BeforeAll
    static void setupOnce() {
        paziente = GestionePazienti.getInstance().getPazientePerCodiceFiscale("MRTNNA04A62H612X");

        dosaggi = Arrays.asList(5.0f, 10.0f, 20.0f);
        farmaco = new Farmaco(50,
                "Tachipirina 500 mg",
                "Paracetamolo",
                dosaggi,
                "mg",
                "Angelini",
                "orale",
                "rash cutanei, rari casi di epatossicità",
                "alcol, warfarin",
                "Analgesico");
    }
    @BeforeEach
    public void setUP(){
        gestione = new GestioneAssunzioneFarmaci(paziente);

    }

    @Test
    @Order(1)
    void testRegistraAssunzioniFarmaco() {
        Date data = Date.valueOf("2025-07-05");
        Time ora = Time.valueOf("08:00:00");

        int result = gestione.registraAssunzioneFarmaco(farmaco, data, ora, 5.0f);
        assertTrue(result == 1 || result == -1);
    }

    @Test
    @Order(2)
    void testListaAssunzioni() {
        List<AssunzioneFarmaco> lista = gestione.getListaAssunzioneFarmaci();
        assertNotNull(lista);
        assertFalse(lista.isEmpty());
    }

    @Test
    @Order(3)
    void testAssunzioniOggi() {
        Date data = new Date(System.currentTimeMillis());

        List<AssunzioneFarmaco> lista = gestione.getListaFarmaciAssuntiOggi(data, "Tachipirina 500 mg");
        assertNotNull(lista);
    }

    @Test
    @Order(4)
    void testEliminaAssunzione() {
        List<AssunzioneFarmaco> lista = gestione.getListaAssunzioneFarmaci();
        int idAssunzione = lista.get(0).getIdAssunzioneFarmaco();

        boolean esito = gestione.eliminaAssunzioneFarmaco(idAssunzione);
        assertTrue(esito);
    }
}