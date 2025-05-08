package com.univr.glicontrol.bll;

import java.sql.Date;

public class InputChecker {

    public boolean verificaCodiceFiscale(String codiceFiscale) {
        return codiceFiscale.matches("[A-Z]{6}[0-9]{2}[A-Z][0-9]{2}[A-Z][0-9]{3}[A-Z]");
    }

    public boolean verificaPassword(String password) {
        return password.length() == 10;
    }

    public boolean verificaEmail(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    public boolean verificaAllergie(String allergie) {
        return !allergie.isEmpty();
    }

    public boolean verificaPeso(double peso) {
        return peso >= 0;
    }

    public boolean verificaSesso(String sesso) {
        return sesso.equals("M") || sesso.equals("F");
    }

    public boolean verificaMedico(int idMedico) {
        return idMedico > 0;
    }

    public boolean verificaNascita(Date dataNascita) {
        Date today = new Date(System.currentTimeMillis());
        return dataNascita != null && dataNascita.before(today);
    }

    public boolean verificaNome(String nome) {
        return nome.matches("[a-zA-Z]+") && nome.length() > 1;
    }

    public boolean verificaCognome(String cognome) {
        return cognome.matches("[a-zA-Z]+") && cognome.length() > 1;
    }
}
