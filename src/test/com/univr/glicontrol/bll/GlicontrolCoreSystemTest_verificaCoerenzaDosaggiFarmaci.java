package com.univr.glicontrol.bll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlicontrolCoreSystemTest_verificaCoerenzaDosaggiFarmaci {
    private Paziente paziente;
    private GlicontrolCoreSystem gcs;

    @BeforeEach
    public void setup() {
        paziente = new Paziente(6, "MRTNNA04A62H612X", "Anna", "Martini",
                "CarloDiana", "Paziente", 8,
                Date.valueOf("2004-01-22"),
                "F", "anna.martini2004@gmail.com", "Amoxicillina", 0);

        gcs = GlicontrolCoreSystem.getInstance();
    }

    @Test
    public void testVerificaCoerenzaDosaggioFarmaci_true() {
        String nomeFarmaco = "Jardiance 10 mg";
        float dose = 9f;

        boolean risultato = gcs.verificaCoerenzaDosaggioFarmaci(paziente, nomeFarmaco, dose);

        assertTrue(risultato, "La dose dovrebbe essere coerente col dosaggio massimo");
    }

    @Test
    public void testVerificaCoerenzaDosaggioFarmaci_Sovradosaggio() {
        String nomeFarmaco = "Jardiance 10 mg";
        float dose = 3000f;

        boolean risultato = gcs.verificaCoerenzaDosaggioFarmaci(paziente, nomeFarmaco, dose);

        assertFalse(risultato, "La dose dovrebbe essere un sovradosaggio");
    }


}