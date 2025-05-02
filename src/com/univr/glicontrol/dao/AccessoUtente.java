package com.univr.glicontrol.dao;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.*;

public class AccessoUtente {
    private final TextField codiceFiscale;
    private final String url = "jdbc:mysql://localhost:3306/glicontrol";
    private final PasswordField pwd;
    private final Enum ruolo;

    public AccessoUtente(TextField codiceFiscale, PasswordField password, Enum ruolo) {
        this.codiceFiscale = codiceFiscale;
        this.pwd = password;
        this.ruolo = ruolo;
    }

    public boolean verificaCredenziali() {
        String sql = "use glicontrol; select * from Utente where codice_fiscale = ? and password = ? and ruolo = ?";

        try (Connection conn = DriverManager.getConnection(url, "root", "Sitecom12");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codiceFiscale.getText());
            stmt.setString(2, pwd.getText());
            stmt.setString(3, ruolo.toString());

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Se esiste almeno una riga, credenziali corrette

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
