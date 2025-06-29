package com.univr.glicontrol.dao;

import com.univr.glicontrol.bll.Medico;
import com.univr.glicontrol.bll.Paziente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccessoListaUtentiImpl implements AccessoListaUtenti {
    private final String url = "jdbc:mysql://localhost:3306/glicontrol";
    private final String user = "root";
    private final String pwd = "Sitecom12";


    @Override
    public List<Medico> recuperaTuttiIMedici() {
        String sql = "select * from Medico m join Utente u on m.id_medico = u.id";
        List<Medico> Medici = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement stmt = conn.prepareStatement(sql);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Medico med = new Medico(
                            rs.getInt("id_medico"),
                            rs.getString("codice_fiscale"),
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            rs.getString("password"),
                            rs.getString("ruolo"),
                            rs.getString("email")
                    );
                    Medici.add(med);
                }
            }

            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("[ERRORE RECUPERO MEDICI]: " + e.getMessage());
        }

        return Medici;
    }

    @Override
    public List<Paziente> recuperaTuttiIPazienti() {
        String sql = "select * from Paziente p join Utente u on p.id_paziente = u.id";
        List<Paziente> Pazienti = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement stmt = conn.prepareStatement(sql);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Paziente paz = new Paziente(
                            rs.getInt("id_paziente"),
                            rs.getString("codice_fiscale"),
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            rs.getString("password"),
                            rs.getString("ruolo"),
                            rs.getInt("medico_riferimento"),
                            rs.getDate("data_nascita"),
                            rs.getString("sesso"),
                            rs.getString("email"),
                            rs.getString("allergie"),
                            rs.getInt("primo_accesso")
                    );
                    paz.setAltezza(rs.getInt("altezza"));
                    paz.setPeso(rs.getFloat("peso"));
                    Pazienti.add(paz);
                }
            }

            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("[ERRORE RECUPERO PAZIENTI]: " + e.getMessage());
        }

        return Pazienti;
    }

    @Override
    public boolean updateMedico(int idMedico, String codiceFiscale, String nome, String cognome, String password, String email) {
        boolean success = false;
        String updateMedicoInUtenteSql = "update Utente set codice_fiscale = ?, nome = ?, cognome = ?, ruolo = ?, password = ? where id = ?";
        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement stmt = conn.prepareStatement(updateMedicoInUtenteSql);
            stmt.setString(1, codiceFiscale);
            stmt.setString(2, nome);
            stmt.setString(3, cognome);
            stmt.setString(4, "MEDICO");
            stmt.setString(5, password);
            stmt.setInt(6, idMedico);

            if (stmt.executeUpdate() != 0) {
                String updateMedicoInMedicoSql = "update Medico set email = ? where id_medico = ?";
                PreparedStatement stmt2 = conn.prepareStatement(updateMedicoInMedicoSql);
                stmt2.setString(1, email);
                stmt2.setInt(2, idMedico);

                success = stmt2.executeUpdate() != 0;
                stmt2.close();
            }

            if (success) {
                conn.commit();
            } else {
                conn.rollback();
                System.out.println("[ERRORE UPDATE LISTA MEDICI]: Impossibile aggiornare il medico selezionato nel database");
            }

            stmt.close();
            conn.setAutoCommit(true);
            conn.close();
        } catch (SQLException e) {
            System.out.println("[ERRORE UPDATE LISTA MEDICI]: " + e.getMessage());
        }

        return success;
    }

    @Override
    public boolean updatePaziente(int idPaziente, String codiceFiscale, String nome, String cognome, String password, int medico, Date nascita, String sesso, String email, String allergie, float altezza, float peso, int primoAccesso) {
        boolean success = false;
        String updatePazienteInUtenteSql = "update Utente set utente.codice_fiscale = ?, nome = ?, cognome = ?, ruolo = ?, password = ? where id = ?";
        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement stmt = conn.prepareStatement(updatePazienteInUtenteSql);
            stmt.setString(1, codiceFiscale);
            stmt.setString(2, nome);
            stmt.setString(3, cognome);
            stmt.setString(4, "PAZIENTE");
            stmt.setString(5, password);
            stmt.setInt(6, idPaziente);

            if (stmt.executeUpdate() != 0) {
                String updatePazienteInPazienteSql = "update Paziente set medico_riferimento = ?, data_nascita = ?, sesso = ?, email = ?, allergie = ?, altezza = ?, peso = ?, primo_accesso = ? where id_paziente = ?";
                PreparedStatement stmt2 = conn.prepareStatement(updatePazienteInPazienteSql);
                stmt2.setInt(1, medico);
                stmt2.setDate(2, nascita);
                stmt2.setString(3, sesso);
                stmt2.setString(4, email);
                stmt2.setString(5, allergie);
                stmt2.setFloat(6, altezza);
                stmt2.setFloat(7, peso);
                stmt2.setInt(8, primoAccesso);
                stmt2.setInt(9, idPaziente);

                success = stmt2.executeUpdate() != 0;
                stmt2.close();
            }

            if (success) {
                conn.commit();
            } else {
                conn.rollback();
                System.out.println("[ERRORE UPDATE LISTA PAZIENTI]: Impossibile aggiornare il paziente selezionato nel database");
            }

            stmt.close();
            conn.setAutoCommit(true);
            conn.close();
        } catch (SQLException e) {
            System.out.println("[ERRORE UPDATE LISTA PAZIENTI]: " + e.getMessage());
        }

        return success;
    }

    @Override
    public boolean insertNuovoMedico(String codiceFiscale, String nome, String cognome, String email, String password) {
        boolean success = false;
        String inserimentoMedicoInUtenteSql = "insert into Utente (codice_fiscale, nome, cognome, ruolo, password) values (?, ?, ?, ?, ?)";
        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement stmt = conn.prepareStatement(inserimentoMedicoInUtenteSql, Statement.RETURN_GENERATED_KEYS );
            stmt.setString(1, codiceFiscale);
            stmt.setString(2, nome);
            stmt.setString(3, cognome);
            stmt.setString(4, "MEDICO");
            stmt.setString(5, password);

            if (stmt.executeUpdate() != 0) {
                try (ResultSet id = stmt.getGeneratedKeys()) {
                    if (!id.next()) {
                        throw new SQLException("[ERRORE INSERIMENTO NUOVO MEDICO]: Impossibile recuperare l'ID generato");
                    } else {
                        int idMedico = id.getInt(1);
                        String inserimentoMedicoInMedicoSql = "insert into Medico (id_medico, email) values (?, ?)";
                        PreparedStatement stmt2 = conn.prepareStatement(inserimentoMedicoInMedicoSql);
                        stmt2.setInt(1, idMedico);
                        stmt2.setString(2, email);

                        success = stmt2.executeUpdate() != 0;
                        stmt2.close();
                    }
                }
            }

            if (success) {
                conn.commit();
            } else {
                conn.rollback();
                System.out.println("[ERRORE INSERIMENTO NUOVO MEDICO]: Impossibile inserire il nuovo medico nel database");
            }

            stmt.close();
            conn.setAutoCommit(true);
            conn.close();
        } catch (SQLException e) {
            System.out.println("[ERRORE INSERIMENTO NUOVO MEDICO]: " + e.getMessage());
        }

        return success;
    }

    @Override
    public boolean insertNuovoPaziente(String codiceFiscale, String nome, String cognome, String password, int medico, Date nascita, String sesso, String email, String allergie, int primoAccesso) {
        boolean success = false;
        String inserimentoPazienteInUtenteSql = "insert into Utente (codice_fiscale, nome, cognome, ruolo, password) values (?, ?, ?, ?, ?)";
        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement stmt = conn.prepareStatement(inserimentoPazienteInUtenteSql, Statement.RETURN_GENERATED_KEYS );
            stmt.setString(1, codiceFiscale);
            stmt.setString(2, nome);
            stmt.setString(3, cognome);
            stmt.setString(4, "PAZIENTE");
            stmt.setString(5, password);

            if (stmt.executeUpdate() != 0) {
                try (ResultSet id = stmt.getGeneratedKeys()) {
                    if (!id.next()) {
                        throw new SQLException("[ERRORE INSERIMENTO NUOVO MEDICO]: Impossibile recuperare l'ID generato");
                    } else {
                        int idPaziente = id.getInt(1);
                        String inserimentoPazienteInPazienteSql = "insert into Paziente (id_paziente, medico_riferimento, data_nascita, sesso, email, allergie, altezza, peso, primo_accesso) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        PreparedStatement stmt2 = conn.prepareStatement(inserimentoPazienteInPazienteSql);
                        stmt2.setInt(1, idPaziente);
                        stmt2.setInt(2, medico);
                        stmt2.setDate(3, nascita);
                        stmt2.setString(4, sesso);
                        stmt2.setString(5, email);
                        stmt2.setString(6, allergie);
                        stmt2.setInt(7, 1);
                        stmt2.setFloat(8, 1);
                        stmt2.setInt(9, primoAccesso);

                        success = stmt2.executeUpdate() != 0;
                        stmt2.close();
                    }
                }
            }

            if (success) {
                conn.commit();
            } else {
                conn.rollback();
                System.out.println("[ERRORE INSERIMENTO NUOVO PAZIENTE]: Impossibile inserire il nuovo paziente nel database");
            }

            stmt.close();
            conn.setAutoCommit(true);
            conn.close();
        } catch (SQLException e) {
            System.out.println("[ERRORE INSERIMENTO NUOVO PAZIENTE]: " + e.getMessage());
        }

        return success;
    }

    @Override
    public boolean deleteMedico(int idMedico) {
        boolean success = false;
        String deleteMedicoInMedicoSql = "delete from Medico where id_medico = ?";
        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement stmt = conn.prepareStatement(deleteMedicoInMedicoSql);
            stmt.setInt(1, idMedico);

            if (stmt.executeUpdate() != 0) {
                String deleteMedicoInUtenteSql = "delete from Utente where id = ?";
                PreparedStatement stmt2 = conn.prepareStatement(deleteMedicoInUtenteSql);
                stmt2.setInt(1, idMedico);

                success = stmt2.executeUpdate() != 0;
                stmt2.close();
            }

            if (success) {
                conn.commit();
            } else {
                conn.rollback();
                System.out.println("[ERRORE DELETE MEDICO]: Impossibile eliminare il medico selezionato nel database");
            }

            stmt.close();
            conn.setAutoCommit(true);
            conn.close();
        } catch (SQLException e) {
            System.out.println("[ERRORE DELETE MEDICO]: " + e.getMessage());
        }

        return success;
    }

    @Override
    public boolean deletePaziente(int idPaziente) {
        boolean success = false;
        String deletePazienteInPazienteSql = "delete from Paziente where id_paziente = ?";
        try {
            Connection conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);
            PreparedStatement stmt = conn.prepareStatement(deletePazienteInPazienteSql);
            stmt.setInt(1, idPaziente);

            if (stmt.executeUpdate() != 0) {
                String deletePazienteInUtenteSql = "delete from Utente where id = ?";
                PreparedStatement stmt2 = conn.prepareStatement(deletePazienteInUtenteSql);
                stmt2.setInt(1, idPaziente);

                success = stmt2.executeUpdate() != 0;
                stmt2.close();
            }

            if (success) {
                conn.commit();
            } else {
                conn.rollback();
            }

            stmt.close();
            conn.setAutoCommit(true);
            conn.close();
        } catch (SQLException e) {
            System.out.println("[ERRORE DELETE PAZIENTE]: " + e.getMessage());
        }

        return success;
    }
}
