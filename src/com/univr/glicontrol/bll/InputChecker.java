package com.univr.glicontrol.bll;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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

    public boolean verificaPeso(String peso) {
        //return peso > 0.0 && peso <= 700.0;
        if (!peso.endsWith(" kg")) {
            return false;
        }

        float pesoFloat = Float.parseFloat(peso.substring(0, peso.length() - 3));
        return pesoFloat > 0.0 && pesoFloat <= 700.0;
    }

    public boolean verificaSesso(String sesso) {
        return sesso.equals("M") || sesso.equals("F") || sesso.equals("m") || sesso.equals("f") ;
    }

    public boolean verificaMedico(int idMedico) {
        return idMedico > 0;
    }

    public boolean verificaNascita(Date dataNascita) {
        Date today = new Date(System.currentTimeMillis());
        boolean dataValida;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            LocalDate.parse(dataNascita.toString(), formatter);
            dataValida = true;
        } catch (DateTimeParseException e) {
            System.out.println(e.getMessage());
            dataValida = false;
        }

        return dataNascita.before(today) && dataValida && dataNascita.after(Date.valueOf("1900-01-01"));
    }

    public boolean verificaNome(String nome) {
        return nome.matches("^[A-Z][a-z]+(?: [A-Z][a-z]+){0,2}$") && nome.length() > 1;
    }

    public boolean verificaCognome(String cognome) {
        return cognome.matches("^[A-Z][a-z]+(?: [A-Z][a-z]+){0,2}$") && cognome.length() > 1;
    }

    public boolean allCheckForMedico(String nome, String cognome, String codiceFiscale, String password, String email) {
        return verificaNome(nome) && verificaCognome(cognome) && verificaCodiceFiscale(codiceFiscale) && verificaPassword(password) && verificaEmail(email);
    }

    public boolean allCheckForPaziente(String nome, String cognome, String codiceFiscale, String password, String email, String sesso, Date dataNascita) {
        return verificaNome(nome) && verificaCognome(cognome) && verificaCodiceFiscale(codiceFiscale) && verificaPassword(password) && verificaEmail(email) && verificaSesso(sesso) && verificaNascita(dataNascita);
    }
}
