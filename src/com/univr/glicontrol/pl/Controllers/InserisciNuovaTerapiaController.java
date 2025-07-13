package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.*;
import com.univr.glicontrol.pl.Models.UtilityPortali;
import jakarta.mail.MessagingException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;


public class InserisciNuovaTerapiaController implements Controller {
    UtilityPortali up;
    private Paziente paziente;
    GestioneTerapie gt;
    private FinestraTerapiePazienteController ftpc = new FinestraTerapiePazienteController();
    private int terapiaSelezionata;
    private String ruoloAccesso, noteTerapia = null;
    private Date dataInizio;

    @FXML
    private ComboBox<String>  patologiaCB;
    @FXML
    private DatePicker dataInizioDP, dataFineDP;
    @FXML
    private HBox finestraSceltaTerapia;
    @FXML
    private VBox finestraInserimentoTerapia;
    @FXML
    private Button inserisciNoteB;
    @FXML
    private Label patologiaL;

    @FXML
    private void initialize(){

        Platform.runLater(()->{
            List<String> listaPatologieInCorso = new ArrayList<>();
            for (String patologia : up.getListaPatologieConcomitantiPaziente()) {
                if (patologia.contains("in corso")) {
                    listaPatologieInCorso.add(patologia);
                }
            }

            patologiaCB.getItems().addAll(listaPatologieInCorso);
        });
    }

    public void setInstance(FinestraTerapiePazienteController ftpc, String ruoloAccesso, Paziente pazienteSelezionato) {
        this.ftpc = ftpc;
        this.ruoloAccesso = ruoloAccesso;

        this.paziente = pazienteSelezionato;
        up = new UtilityPortali(pazienteSelezionato);
        gt = new GestioneTerapie(pazienteSelezionato);

        Platform.runLater(()->{
           if (ruoloAccesso.equals("paziente")) {
               finestraInserimentoTerapia.setVisible(true);
               finestraSceltaTerapia.setVisible(false);
               // unica opzione valida per il paziente
               terapiaSelezionata = 1;
               inserisciNoteB.setVisible(false);
           } else {
               finestraSceltaTerapia.setVisible(true);
               finestraInserimentoTerapia.setVisible(false);
               inserisciNoteB.setVisible(true);
           }
        });
    }

    public void inserisciFarmaco(){
        String nomePatologia = patologiaCB.getSelectionModel().getSelectedItem();
        Date dataInizio = ottieniDataInizioTerapia();

        if (dataInizio == null) {
            Alert erroreDataMancataAlert = new Alert(Alert.AlertType.ERROR);
            erroreDataMancataAlert.setTitle("System Notification Service");
            erroreDataMancataAlert.setHeaderText("Data mancante");
            erroreDataMancataAlert.setContentText("Prima di poter selezionare dei farmaci per la tua terapia è necessario precisarne la data di inizio. Riprova");
            erroreDataMancataAlert.showAndWait();
            dataInizioDP.setValue(null);
            return;
        }

        PatologiaConcomitante patologia = up.getPatologiaConcomitantePerNomeFormattata(nomePatologia);
        if (terapiaSelezionata == 1 && dataInizio.before(patologia.getDataInizio())) {
            Alert erroreDiscrepanzaData = new Alert(Alert.AlertType.ERROR);
            erroreDiscrepanzaData.setTitle("System Notification Service");
            erroreDiscrepanzaData.setHeaderText("Data non valida");
            erroreDiscrepanzaData.setContentText("La data di inizio della terapia non può essere antecedente a quella della patologia.\nInserisci una data compatibile e riprova");
            erroreDiscrepanzaData.showAndWait();
            dataInizioDP.setValue(null);
            return;
        }

        if (terapiaSelezionata == 1 && patologiaCB.getSelectionModel().getSelectedItem() == null || dataInizioDP.getValue() == null) {
            Alert datiTerapiaMancantiAlert = new Alert(Alert.AlertType.ERROR);
            datiTerapiaMancantiAlert.setTitle("System Notification Service");
            datiTerapiaMancantiAlert.setHeaderText("Dati mancanti");
            datiTerapiaMancantiAlert.setContentText("Per procedere all'inserimento dei farmaci è necessario selezionare patologie e data di inizio.\nRiprova");
            datiTerapiaMancantiAlert.showAndWait();
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../uiElements/DettaglioNuovoFarmaco.fxml"));
                Parent root = loader.load();

                DettaglioNuovoFarmacoController dettaglioNuovoFarmacoController = loader.getController();
                dettaglioNuovoFarmacoController.setInstance(paziente, gt);

                Stage dettaglio = new Stage();
                dettaglio.setTitle("Aggiungi farmaco");
                dettaglio.setScene(new Scene(root));
                dettaglio.show();

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

    }

    public void inserisciNote() {

        dataInizio = ottieniDataInizioTerapia();
        
        if (dataInizio == null) {
            Alert dataInizioNonDefinitaALert = new Alert(Alert.AlertType.ERROR);
            dataInizioNonDefinitaALert.setTitle("System Notification Service");
            dataInizioNonDefinitaALert.setHeaderText("Data inizio mancante");
            dataInizioNonDefinitaALert.setContentText("Per poter inserire delle note è  necessario precisare una data di inizio per la terapia");
            dataInizioNonDefinitaALert.showAndWait();

            return;
        }

        try {
            FXMLLoader inserisciNoteTerapiaLoader = new FXMLLoader(getClass().getResource("../uiElements/FinestraNoteTerapia.fxml"));
            Parent root = inserisciNoteTerapiaLoader.load();

            FinestraNoteTerapiaController fntc = inserisciNoteTerapiaLoader.getController();
            fntc.setInstance(this, ruoloAccesso.equals("medico"));

            Stage finestraNoteTerapia = new Stage();
            finestraNoteTerapia.setTitle("Note terapia");
            finestraNoteTerapia.setScene(new Scene(root));

            finestraNoteTerapia.show();

        } catch (IOException e) {
            System.err.println("Si è verificato un errore durante il caricamento della finestra delle note: " + e.getMessage());
        }
    }

    public void setNoteTerapia(String noteTerapia) {
        this.noteTerapia = noteTerapia;
    }

    public void aggiungiNuovaTerapia() throws MessagingException {
        Medico medicoUltimaModifica = up.getMedicoSessione();
        String nomePatologia = patologiaCB.getSelectionModel().getSelectedItem();

        if (nomePatologia == null && terapiaSelezionata == 1) {
            Alert nomePatologiaAssenteAlert = new Alert(Alert.AlertType.ERROR);
            nomePatologiaAssenteAlert.setTitle("System Notification Service");
            nomePatologiaAssenteAlert.setHeaderText("Nome patologia assente");
            nomePatologiaAssenteAlert.setContentText("Per poter inserire una terapia concomitante è necessario precisare la patologia concomitante cui fa riferimento.\nSeleziona una patologia valida e riprova");
            nomePatologiaAssenteAlert.showAndWait();

            return;
        }

        dataInizio = ottieniDataInizioTerapia();
        Date dataFine = ottieniDataFineTerapia();

        if (dataInizio == null) {
            Alert dataInizioNonSpecificataAlert = new Alert(Alert.AlertType.ERROR);
            dataInizioNonSpecificataAlert.setTitle("System Notification Service");
            dataInizioNonSpecificataAlert.setHeaderText("Data inizio mancante");
            dataInizioNonSpecificataAlert.setContentText("Per specificare una terapia è necessario impostare una data di inizio");
            dataInizioNonSpecificataAlert.showAndWait();

            return;
        }

        PatologiaConcomitante patologia = up.getPatologiaConcomitantePerNomeFormattata(nomePatologia);

        if (terapiaSelezionata == 1) {
            if (!InputChecker.getInstance().verificaDataInizioTerapiaConcomitante(dataInizio, patologia.getDataInizio())) {
                Alert dataInizioAntecedenteAllaPatologiaAlert = new Alert(Alert.AlertType.ERROR);
                dataInizioAntecedenteAllaPatologiaAlert.setTitle("System Notification Service");
                dataInizioAntecedenteAllaPatologiaAlert.setHeaderText("Data di inizio non valida");
                dataInizioAntecedenteAllaPatologiaAlert.setContentText("La data di inizio della terapia non può precedere quella della diagnosi della patologia che va a trattare. Riprova");
                dataInizioAntecedenteAllaPatologiaAlert.showAndWait();

                return;
            }
        }

        List<FarmacoTerapia> farmaciConIndicazioni = gt.getFarmaciSingolaTerapia();
        if (farmaciConIndicazioni == null || farmaciConIndicazioni.isEmpty()) {
            Alert farmaciMancantiAlert = new Alert(Alert.AlertType.ERROR);
            farmaciMancantiAlert.setTitle("System Notification Service");
            farmaciMancantiAlert.setHeaderText("Nessun farmaco precisato");
            farmaciMancantiAlert.setContentText("Per creare una terapia è necessario associarvi dei farmaci");
            farmaciMancantiAlert.showAndWait();

            return;
        }

        int status;
        if (terapiaSelezionata == 0) {
            //status dovrà corrispondere a gt.inserisciTerapiaDiabete
            status = gt.inserisciTerapiaDiabete(medicoUltimaModifica.getIdUtente(), dataInizio, dataFine, noteTerapia, farmaciConIndicazioni);
        } else {
            status = gt.inserisciTerapiaConcomitante(patologia, paziente.getMedicoRiferimento(), dataInizio, dataFine, noteTerapia, farmaciConIndicazioni);
        }

        if (status == 1) {


            Alert successoInserimentoTerapiaAlert = new Alert(Alert.AlertType.INFORMATION);
            successoInserimentoTerapiaAlert.setTitle("System Notification Service");
            successoInserimentoTerapiaAlert.setHeaderText("Terapia inserita con successo");
            successoInserimentoTerapiaAlert.setContentText("La nuova terapia è stata inserita con successo");
            successoInserimentoTerapiaAlert.showAndWait();

            new Thread(() -> ftpc.resetListaTerapie()).start();

            boolean inseritaDalMedico;
            if (ruoloAccesso.equals("medico")) {
                inseritaDalMedico = true;

                new Thread(() -> {
                    try {
                        informaPazienteInserimentoTerapia();
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                }).start();

            } else {
                inseritaDalMedico = false;
            }

            GlicontrolCoreSystem.getInstance().generaLog(Log.TERAPIA, paziente, medicoUltimaModifica, true, inseritaDalMedico);

            Window currentWindow = dataInizioDP.getScene().getWindow();
            if (currentWindow instanceof Stage) {
                ((Stage) currentWindow).close();
            }

        } else if (status == 0) {

            Alert erroreInserimentoTerapiaAlert = new Alert(Alert.AlertType.ERROR);
            erroreInserimentoTerapiaAlert.setTitle("System Notification Service");
            erroreInserimentoTerapiaAlert.setHeaderText("Errore durante l'inserimento della nuova terapia");
            erroreInserimentoTerapiaAlert.setContentText("Non è stato possibile inserire la nuova terapia.\nAssicurati di aver inserito correttamente tutti i dati e riprova");
            erroreInserimentoTerapiaAlert.showAndWait();

        } else {
            Alert erroreDuplicazioneTerapiaAlert = new Alert(Alert.AlertType.ERROR);
            erroreDuplicazioneTerapiaAlert.setTitle("System Notification Service");
            erroreDuplicazioneTerapiaAlert.setHeaderText("Errore durante l'inserimento della nuova terapia");
            erroreDuplicazioneTerapiaAlert.setContentText("Esiste già una terapia associata a questa patologia nel sistema");
            erroreDuplicazioneTerapiaAlert.showAndWait();
            dataInizioDP.setValue(null);
            dataFineDP.setValue(null);
        }
    }

    private Date ottieniDataInizioTerapia() {
        return dataInizioDP.getValue() == null ? null : Date.valueOf(dataInizioDP.getValue());
    }

    private Date ottieniDataFineTerapia() {
        Date dataFine;
        if (dataFineDP.getValue() == null) {
            dataFine = null;
        } else {
            dataFine = Date.valueOf(dataFineDP.getValue());
        }
        return dataFine;
    }

    public void caricaFinestraTerapiaDiabete() {
        finestraSceltaTerapia.setVisible(false);
        finestraInserimentoTerapia.setVisible(true);
        patologiaL.setVisible(false);
        patologiaCB.setVisible(false);
        terapiaSelezionata = 0;
    }

    public void caricaFinestraTerapiaPatologiaConcomitante() {
        finestraSceltaTerapia.setVisible(false);
        finestraInserimentoTerapia.setVisible(true);
        terapiaSelezionata = 1;
    }

    public void informaPazienteInserimentoTerapia() throws MessagingException {
        String oggetto = "Hai una nuova terapia";

        String nomePatologia = "", corpo = "";
        if (terapiaSelezionata == 1) {
            // Verificare il controllo sulla data di inizio
            nomePatologia = up.getPatologiaConcomitantePerNomeFormattata(patologiaCB.getSelectionModel().getSelectedItem()).getNomePatologia();
            corpo = "Ti è stata registrata una nuova terapia per la patologia " + nomePatologia + ".\nAccedi all'applicazione per maggiori dettagli.\n\nGlicontrol Medical System";
        } else {
            corpo = "Ti è stata registrata una nuova terapia per il diabete.\nAccedi all'applicazione per maggiori dettagli.\n\nGlicontrol Medical System";
        }

        ServizioNotifiche.getInstance().sendEmail(paziente.getEmail(), oggetto, corpo);
    }
}
