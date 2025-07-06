package com.univr.glicontrol.bll;

import com.univr.glicontrol.dal.AccessoUtenteSessione;
import com.univr.glicontrol.dal.AccessoUtenteSessioneImpl;

public class UtenteSessione {
    private AccessoUtenteSessione accessoUtenteSessione;

    // Costruttore privato per impedire istanziazione esterna
    private UtenteSessione() {

    }

    // Classe statica interna che contiene l'istanza Singleton
    private static class Holder {
        private static final UtenteSessione INSTANCE = new UtenteSessione();
    }

    // Metodo di accesso pubblico
    public static UtenteSessione getInstance() {
        return Holder.INSTANCE;
    }

    private Medico medicoSessione;
    private Paziente pazienteSessione;

    // Metodo di autenticazione utente
    public boolean verificaUtente(String codiceFiscale, String pwd, String ruolo) {
        return new AccessoUtenteSessioneImpl(pwd).verificaUtente(codiceFiscale, ruolo);
    }

    public Admin creaAdminConnesso(String codiceFiscale, String pwd) {
        return new AccessoUtenteSessioneImpl(pwd).getAdmin(codiceFiscale);
    }

    public Medico creaMedicoConnesso(String codiceFiscale, String pwd) {
        medicoSessione = new AccessoUtenteSessioneImpl(pwd).getMedico(codiceFiscale);
        return medicoSessione;
    }

    public Paziente creaPazienteConnesso(String codiceFiscale, String pwd) {
        pazienteSessione = new AccessoUtenteSessioneImpl(pwd).getPaziente(codiceFiscale);
        return pazienteSessione;
    }

    // Restituisce gli utenti connessi alla loro sessione per categoria
    // MEDICO
    public Medico getMedicoSessione() {
        return medicoSessione;
    }

    // PAZIENTE
    public Paziente getPazienteSessione() {
        return pazienteSessione;
    }
}
