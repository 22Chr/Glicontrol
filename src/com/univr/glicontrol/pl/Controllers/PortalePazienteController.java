package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.*;
import com.univr.glicontrol.pl.Models.UtilityPortali;
import javafx.animation.FadeTransition;
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
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;

public class PortalePazienteController implements Portale, Controller {

    private final UtilityPortali up = new UtilityPortali();
    private final Paziente paziente = up.getPazienteSessione();
    private final Medico medicoRiferimento = GestioneMedici.getInstance().getMedicoPerId(paziente.getMedicoRiferimento());
    private GestioneRilevazioniGlicemia grg;
    private enum ModalitaVisualizzazione { GIORNALIERA, SETTIMANALE, MENSILE }
    private ModalitaVisualizzazione visualizzazioneAttuale = ModalitaVisualizzazione.GIORNALIERA;




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
        badgeCircle.setFill(new ImagePattern(up.getBadge()));
        badgeCircle.setSmooth(true);
        badgeCircle.setStyle("-fx-border-color: #ff0404;");

        //Gestione del bottone visulizzazione per switchare tra i vari grafici
        visualizzazioneT.setOnAction(e -> cambiaModalitaGrafico());

        // Gestione colorazione livelli glicemici per il paziente su base odierna
        grg = new GestioneRilevazioniGlicemia(paziente);
        List<Integer> codiciRilevazioni = GlicontrolCoreSystem.getInstance().verificaLivelliGlicemici(paziente, true, false);

        aggiornaGrafico();

        //Inizializza la lista delle ultime rilevazioni glicemiche
        ObservableList<String> ultimeRilevazioni = FXCollections.observableArrayList();
        ultimeRilevazioni.addAll(up.getListaRilevazioniGlicemicheOdierne());
        ultimeRilevazioniLV.getItems().setAll(ultimeRilevazioni);

        ultimeRilevazioniLV.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);

                int index = getIndex();
                if (index >= 0 && index < codiciRilevazioni.size()) {
                    int severity = codiciRilevazioni.get(index);
                    String style;

                    switch (Math.abs(severity)) {
                        case 0 -> style = "";
                        case 1 -> style = "-fx-background-color: #ffdd00; -fx-text-fill: black; -fx-border-color: whitesmoke;";
                        case 2 -> style = "-fx-background-color: #ff9900; -fx-text-fill: black; -fx-border-color: whitesmoke;";
                        case 3 -> style = "-fx-background-color: #ff0000; -fx-text-fill: white; -fx-border-color: whitesmoke;";
                        case 4 -> style = "-fx-background-color: #6b0c8a; -fx-text-fill: white; -fx-border-color: whitesmoke;";
                        default -> style = "";
                    }

                    setStyle(style);
                } else {
                    setStyle("");
                }
            }
        });



        // Task automatici sempre attivi in background
        GlicontrolCoreSystem.getInstance().monitoraAssunzioneFarmaci(paziente);
        GlicontrolCoreSystem.getInstance().monitoraRegistrazioneGlicemica(paziente);
    }

    public void openProfile() {
        // Carica il file FXML della nuova finestra
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../uiElements/ModificaInformazioniPaziente.fxml"));
            Parent root = fxmlLoader.load();
            root.setOpacity(0);
            FadeTransition fade = new FadeTransition(Duration.millis(150), root);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

            ModificaInformazioniPazienteController mipc = fxmlLoader.getController();
            mipc.setInstance(this, paziente);

            // Crea una nuova finestra (Stage)
            Stage infoPaziente = new Stage();
            infoPaziente.setWidth(1200);
            infoPaziente.setHeight(600);
            infoPaziente.setResizable(false);
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
            root.setOpacity(0);
            FadeTransition fade = new FadeTransition(Duration.millis(300), root);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

            FinestraSintomiPazienteController fspc = fxmlLoader.getController();
            fspc.setInstance(this, paziente);

            Stage sintomiPaziente = new Stage();
            sintomiPaziente.setWidth(600);
            sintomiPaziente.setHeight(500);
            sintomiPaziente.setResizable(false);
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
            alert.setTitle("System Notification Service");
            alert.setHeaderText("Sei sicuro di voler uscire?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                GlicontrolCoreSystem.getInstance().stopScheduler();
                stage.close();
            }
        });
    }

    public void openPatologie() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../uiElements/FinestraPatologieConcomitantiPaziente.fxml"));
            Parent root = fxmlLoader.load();
            root.setOpacity(0);
            FadeTransition fade = new FadeTransition(Duration.millis(300), root);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

            FinestraPatologieConcomitantiPazienteController fpcp = fxmlLoader.getController();
            fpcp.setInstance(this, paziente);

            Stage patologiePaziente = new Stage();
            patologiePaziente.setWidth(700);
            patologiePaziente.setHeight(500);
            patologiePaziente.setResizable(false);
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
            root.setOpacity(0);
            FadeTransition fade = new FadeTransition(Duration.millis(150), root);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

            FinestraRilevazioniGlicemichePazienteController frgpc =  fxmlLoader.getController();
            frgpc.setInstance(this, paziente);

            Stage rilevazioniGlicemiaPaziente = new Stage();
            rilevazioniGlicemiaPaziente.setWidth(800);
            rilevazioniGlicemiaPaziente.setHeight(500);
            rilevazioniGlicemiaPaziente.setTitle("Le mie rilevazioni glicemiche");
            rilevazioniGlicemiaPaziente.setResizable(false);
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

            FinestraTerapiePazienteController ftpc =  fxmlLoader.getController();
            ftpc.setInstance(this, paziente);

            Stage gestoreTerapiePaziente = new Stage();
            gestoreTerapiePaziente.setTitle("Le mie terapie");
            gestoreTerapiePaziente.setResizable(false);
            gestoreTerapiePaziente.setScene(new Scene(root));

            gestoreTerapiePaziente.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void aggiornaGrafico() {
        andamentoGlicemiaLC.getData().clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        XYChart.Series<String, Number> serie = new XYChart.Series<>();

        LocalDate oggi = LocalDate.now();
        int anno = oggi.getYear();

        switch (visualizzazioneAttuale) {
            case MENSILE -> {
                int mese = oggi.getMonthValue();
                serie.setName("Andamento mensile");

                Map<String, Double> mediaSettimanale = grg.getMediaMensileGlicemiaPerMeseCorrente(anno, mese);

                for (var entry : mediaSettimanale.entrySet()) {
                    String settimanaLabel = entry.getKey();
                    serie.getData().add(new XYChart.Data<>(settimanaLabel, entry.getValue()));
                }
            }

            case SETTIMANALE -> {
                WeekFields wf = WeekFields.of(Locale.getDefault());
                int settimana = oggi.get(wf.weekOfWeekBasedYear());
                serie.setName("Andamento settimanale");

                Map<String, Double> mediaGiornaliera = grg.getMediaGiornalieraGlicemia(anno, settimana);
                for (var entry : mediaGiornaliera.entrySet()) {
                    LocalDate data = LocalDate.parse(entry.getKey());
                    String dataFormattata = data.format(formatter);
                    serie.getData().add(new XYChart.Data<>(dataFormattata, entry.getValue()));
                }
            }

            case GIORNALIERA -> {
                serie.setName("Andamento odierno");

                List<RilevazioneGlicemica> rilevazioniOggi = grg.getRilevazioniPerData(oggi);

                rilevazioniOggi.sort(Comparator.comparing(RilevazioneGlicemica::getOra));

                for (RilevazioneGlicemica rilevazione : rilevazioniOggi) {
                    String orario = rilevazione.getOra().toString().substring(0,5); // es. "15:30"
                    serie.getData().add(new XYChart.Data<>(orario, rilevazione.getValore()));
                }
            }
        }

        andamentoGlicemiaLC.getData().add(serie);

    }

    public void cambiaModalitaGrafico(){

        visualizzazioneAttuale = switch (visualizzazioneAttuale) {
            case MENSILE -> ModalitaVisualizzazione.GIORNALIERA;
            case GIORNALIERA -> ModalitaVisualizzazione.SETTIMANALE;
            case SETTIMANALE -> ModalitaVisualizzazione.MENSILE;
        };

        aggiornaGrafico();
    }

    public void openAssunzioneFarmaci() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../uiElements/FinestraAssunzioneFarmaciPaziente.fxml"));
            Parent root = fxmlLoader.load();

            FinestraAssunzioneFarmaciPazienteController fafpc = fxmlLoader.getController();
            fafpc.setInstance(this, paziente);

            Stage assunzioneFarmaciPaziente = new Stage();
            assunzioneFarmaciPaziente.setHeight(500);
            assunzioneFarmaciPaziente.setWidth(650);
            assunzioneFarmaciPaziente.setResizable(false);
            assunzioneFarmaciPaziente.setTitle("Registra i farmaci");
            assunzioneFarmaciPaziente.setScene(new Scene(root));

            assunzioneFarmaciPaziente.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void aggiornaListaRilevazioniGlicemicheOdierne(){
        UtilityPortali newUp = new UtilityPortali(paziente);
        ObservableList<String> newRilevazioni = FXCollections.observableArrayList();
        newRilevazioni.addAll(newUp.getListaRilevazioniGlicemicheOdierne());
        ultimeRilevazioniLV.getItems().setAll(newRilevazioni);

        List<Integer> codiciRilevazioni = GlicontrolCoreSystem.getInstance().verificaLivelliGlicemici(paziente, true, false);

        ultimeRilevazioniLV.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);

                int index = getIndex();
                if (index >= 0 && index < codiciRilevazioni.size()) {
                    int severity = codiciRilevazioni.get(index);
                    String style;

                    switch (Math.abs(severity)) {
                        case 0 -> style = "";
                        case 1 -> style = "-fx-background-color: #ffdd00; -fx-text-fill: black; -fx-border-color: whitesmoke;";
                        case 2 -> style = "-fx-background-color: #ff9900; -fx-text-fill: black; -fx-border-color: whitesmoke;";
                        case 3 -> style = "-fx-background-color: #ff0000; -fx-text-fill: white; -fx-border-color: whitesmoke;";
                        case 4 -> style = "-fx-background-color: #6b0c8a; -fx-text-fill: white; -fx-border-color: whitesmoke;";
                        default -> style = "";
                    }

                    setStyle(style);
                } else {
                    setStyle("");
                }
            }
        });
    }
}
