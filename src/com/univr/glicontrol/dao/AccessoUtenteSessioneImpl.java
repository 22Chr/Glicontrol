package com.univr.glicontrol.dao;

import java.sql.*;

public class AccessoUtenteSessioneImpl implements AccessoUtenteSessione {

    private final String pwd;

    public AccessoUtenteSessioneImpl(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public ResultSet recuperaUtente(String codiceFiscale, String ruolo) {
        String sql = """
            select 
                u.id AS id_utente, u.nome, u.cognome, u.codice_fiscale, u.password, u.ruolo,
                p.data_nascita, p.sesso, p.email AS email_paziente, p.allergie, p.medico_riferimento, p.altezza, p.peso, p.primo_accesso,
                m.email AS email_medico
            from Utente u
            left join Paziente p on u.id = p.id_paziente
            left join Medico m on u.id = m.id_medico
            where u.codice_fiscale = ? and u.password = ? and u.ruolo = ?
        """;

        String url = "jdbc:mysql://localhost:3306/glicontrol";

        try {
            Connection conn = DriverManager.getConnection(url, "root", "Sitecom12");
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, codiceFiscale);
            stmt.setString(2, pwd);
            stmt.setString(3, ruolo);

            return stmt.executeQuery();

        } catch (SQLException e) {
            System.out.println("[ERRORE ACCESSO DB]: " + e.getMessage());
            return null;
        }
    }
}
