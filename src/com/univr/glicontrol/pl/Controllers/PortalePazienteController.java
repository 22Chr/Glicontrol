package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.*;
import com.univr.glicontrol.pl.Models.UtilityPortalePaziente;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;

public class PortalePazienteController {
    //ultimeRilevazioniLW dovrà contenere il sunto delle ultime rilevazioni
    //andamentoGlicemia dovrà mostrare una rappresentazione grafica -> cercare come fare
    //l'avatar deve mostrare le iniziali dell'utente
    //i vari textfield e la text area contengono le info sul medico di riferimento
    //il profileB deve rimandare a una paginetta con le info del paziente e il bottone di logout

    private final UtilityPortalePaziente upp = new UtilityPortalePaziente();
    private final Paziente paziente = upp.getPazienteSessione();
    private final Medico medicoRiferimento = new ListaMedici().ottieniMedicoPerId(paziente.getMedicoRiferimento());
    private GestioneRilevazioniGlicemia gestione;
    private boolean visualizzazioneMensile = true;


    @FXML
    private TextField nomeMedicoRiferimentoTF, cognomeMedicoRiferimentoTF, emailMedicoRiferimentoTF;

    @FXML
    private Circle badgeCircle;

    @FXML
    private LineChart<String, Number> andamentoGlicemiaLC;

    @FXML
    private ToggleButton visualizzazioneT;

    @FXML
    private ListView<String> ultimeRilevazioniLV;

    @FXML
    private void initialize() {
        // Popola box con i dati di contatto del medico di riferimento
        nomeMedicoRiferimentoTF.setText(medicoRiferimento.getNome());
        cognomeMedicoRiferimentoTF.setText(medicoRiferimento.getCognome());
        emailMedicoRiferimentoTF.setText(medicoRiferimento.getEmail());

        // Inizializza l'avatar con le iniziali del paziente
        badgeCircle.setFill(new ImagePattern(upp.getBadge()));
        badgeCircle.setSmooth(true);
        badgeCircle.setStyle("-fx-border-color: #ff0404;");

        // cercare metodo per risolvere il problema
        visualizzazioneT.setOnAction(e -> aggiornaGrafico());

        // Se hai un paziente corrente salvato, crea la gestione
        gestione = new GestioneRilevazioniGlicemia(paziente);

        aggiornaGrafico();

        //Inizializza la lista delle ultime rilevazioni glicemiche
        ObservableList<String> ultimeRilevazioni = FXCollections.observableArrayList();
        ultimeRilevazioni.addAll(upp.getListaRilevazioniGlicemicheOdierne());
        ultimeRilevazioniLV.setItems(ultimeRilevazioni);

        PauseTransition pause = new PauseTransition(javafx.util.Duration.seconds(0.5));
        pause.setOnFinished(e -> {
            if (GlicontrolCoreSystem.getInstance().presenzaFarmaciNonRegistrati(paziente)) {
                Platform.runLater(() -> {
                    ServizioNotifiche promemoriaFarmaci = new ServizioNotifiche();
                    promemoriaFarmaci.mostraNotifichePromemoriaAssunzioneFarmaci();
                });
            }
        });
        pause.play();
    }

    public void openProfile() {
        // Carica il file FXML della nuova finestra
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../uiElements/ModificaInformazioniPaziente.fxml"));
            Parent root = fxmlLoader.load();

            // Crea una nuova finestra (Stage)
            Stage infoPaziente = new Stage();
            infoPaziente.setTitle("Informazioni personali");
            infoPaziente.setScene(new Scene(root));

            // Mostra la nuova finestra SENZA chiudere quella attuale
            infoPaziente.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void openSintomi() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../uiElements/FinestraSintomiPaziente.fxml"));
            Parent root = fxmlLoader.load();

            Stage sintomiPaziente = new Stage();
            sintomiPaziente.setTitle("I miei sintomi");
            sintomiPaziente.setScene(new Scene(root));

            sintomiPaziente.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void logout(Stage stage) {
        stage.setOnCloseRequest(event -> {
            event.consume();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Sei sicuro di voler uscire?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                stage.close();
            }
        });
    }

    public void openPatologie() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../uiElements/FinestraPatologieConcomitantiPaziente.fxml"));
            Parent root = fxmlLoader.load();

            Stage patologiePaziente = new Stage();
            patologiePaziente.setTitle("Le mie patologie");
            patologiePaziente.setScene(new Scene(root));

            patologiePaziente.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void openRilevazioniGlicemia() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../uiElements/FinestraRilevazioniGlicemichePaziente.fxml"));
            Parent root = fxmlLoader.load();

            FinestraRilevazioniGlicemichePazienteController frgpc =  fxmlLoader.getController();
            frgpc.setInstance(this);

            Stage rilevazioniGlicemiaPaziente = new Stage();
            rilevazioniGlicemiaPaziente.setTitle("Le mie rilevazioni glicemiche");
            rilevazioniGlicemiaPaziente.setScene(new Scene(root));

            rilevazioniGlicemiaPaziente.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void openGestoreTerapie() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../uiElements/FinestraTerapiePaziente.fxml"));
            Parent root = fxmlLoader.load();

            Stage gestoreTerapiePaziente = new Stage();
            gestoreTerapiePaziente.setTitle("Le mie terapie");
            gestoreTerapiePaziente.setScene(new Scene(root));

            gestoreTerapiePaziente.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void aggiornaGrafico() {
        andamentoGlicemiaLC.getData().clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        XYChart.Series<String, Number> serie = new XYChart.Series<>();

        LocalDate oggi = LocalDate.now();
        int anno = oggi.getYear();

        if (visualizzazioneMensile) {
            // Visualizzazione mensile: media settimanale del mese corrente
            int mese = oggi.getMonthValue();
            serie.setName("Media Settimanale del Mese");

            // Recupera media settimanale filtrata per mese corrente
            Map<String, Double> mediaSettimanale = gestione.getMediaMensileGlicemiaPerMeseCorrente(anno, mese);

            for (var entry : mediaSettimanale.entrySet()) {
                LocalDate data = LocalDate.parse(entry.getKey());
                String dataFormattata = data.format(formatter);
                serie.getData().add(new XYChart.Data<>(dataFormattata, entry.getValue()));
            }

        } else {
            // Visualizzazione settimanale: media giornaliera della settimana corrente
            WeekFields wf = WeekFields.of(Locale.getDefault());
            int settimana = oggi.get(wf.weekOfWeekBasedYear());
            serie.setName("Media Giornaliera della Settimana ");

            // Recupera media giornaliera filtrata per settimana corrente
            Map<String, Double> mediaGiornaliera = gestione.getMediaGiornalieraGlicemia(anno, settimana);

            for (var entry : mediaGiornaliera.entrySet()) {
                LocalDate data = LocalDate.parse(entry.getKey());
                String dataFormattata = data.format(formatter);
                serie.getData().add(new XYChart.Data<>(dataFormattata, entry.getValue()));
            }
        }

        andamentoGlicemiaLC.getData().add(serie);
        visualizzazioneMensile = !visualizzazioneMensile;
    }

    public void openAssunzioneFarmaci() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../uiElements/FinestraAssunzioneFarmaciPaziente.fxml"));
            Parent root = fxmlLoader.load();

            Stage assunzioneFarmaciPaziente = new Stage();
            assunzioneFarmaciPaziente.setTitle("Registra i farmaci");
            assunzioneFarmaciPaziente.setScene(new Scene(root));

            assunzioneFarmaciPaziente.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void aggiornaListaRilevazioniGlicemicheOdierne(){
        UtilityPortalePaziente newUpp = new UtilityPortalePaziente();
        ObservableList<String> newRilevazioni = FXCollections.observableArrayList();
        newRilevazioni.addAll(newUpp.getListaRilevazioniGlicemicheOdierne());
        ultimeRilevazioniLV.setItems(newRilevazioni);
    }
}
