package com.univr.glicontrol.bll;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class InputChecker {

    private InputChecker() {}

    private static class Holder {
        private static final InputChecker INSTANCE = new InputChecker();
    }

    public static InputChecker getInstance() {
        return InputChecker.Holder.INSTANCE;
    }

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
        return verificaNome(nome) && verificaCognome(cognome) && verificaCodiceFiscale(codiceFiscale) && verificaPassword(password) && verificaEmail(email) && sesso != null && verificaNascita(dataNascita);
    }

    public boolean verificaDataInizioTerapiaConcomitante(Date dataInizioTerapia, Date dataInizioPatologia) {
        return !dataInizioTerapia.before(dataInizioPatologia);
    }

    public boolean verificaDosaggioFarmaco(String dosaggio, String nomeFarmaco, boolean inserimento) {

        if (inserimento) {
            return dosaggio.matches("^[0-9]\\d*(\\.\\d{1,2})?$");
        } else {
            int limit = -1;
            for (int i = 0; i < dosaggio.length(); i++) {
                if (dosaggio.charAt(i) == ' ') {
                    limit = i + 1;
                    break;
                }
            }
            if (limit == -1) {
                return false;
            }

            Farmaco farmaco = GestioneFarmaci.getInstance().getFarmacoByName(nomeFarmaco);

            return dosaggio.substring(0, limit).matches("^[0-9]\\d*(\\.\\d{1,2})?\\s$") && dosaggio.substring(limit).equals(farmaco.getUnitaMisura());
        }
    }

    public boolean verificaOrariTerapia(String orari) {
        return orari.matches("^(0\\d|1\\d|2[0-3]):[0-5]\\d(, (0\\d|1\\d|2[0-3]):[0-5]\\d)*$");
    }

    public boolean campoVuoto(String campo) {
        return campo.matches("^[A-Za-z0-9].*");
    }

    public boolean verificaAltezza(String altezza) {
        return altezza.matches("^\\d{2,3}\\scm$");
    }
}
