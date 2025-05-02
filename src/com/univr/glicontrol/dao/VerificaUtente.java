package com.univr.glicontrol.dao;
import java.sql.*;

public class VerificaUtente {
    private final String codiceFiscale;
    private final String url = "jdbc:mysql://localhost:3306/glicontrol";
    private final String pwd;

    public VerificaUtente(String codiceFiscale, String password) {
        this.codiceFiscale = codiceFiscale;
        this.pwd = password;
    }

    public boolean verificaCredenziali() {
        String sql = "select * from Utente where codice_fiscale = ? and password = ?";

        try (Connection conn = DriverManager.getConnection(url, "root", "Sitecom12");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codiceFiscale);
            stmt.setString(2, pwd);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Se esiste almeno una riga, credenziali corrette

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
