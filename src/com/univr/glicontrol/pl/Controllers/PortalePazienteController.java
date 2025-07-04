package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.*;
import com.univr.glicontrol.pl.Models.UtilityPortali;
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

public class PortalePazienteController implements Portale, Controller {
    //ultimeRilevazioniLW dovrà contenere il sunto delle ultime rilevazioni
    //andamentoGlicemia dovrà mostrare una rappresentazione grafica -> cercare come fare
    //l'avatar deve mostrare le iniziali dell'utente
    //i vari textfield e la text area contengono le info sul medico di riferimento
    //il profileB deve rimandare a una paginetta con le info del paziente e il bottone di logout

    private final UtilityPortali upp = new UtilityPortali();
    private final Paziente paziente = upp.getPazienteSessione();
    private final Medico medicoRiferimento = GestioneMedici.getInstance().getMedicoPerId(paziente.getMedicoRiferimento());
    private GestioneRilevazioniGlicemia gestione;
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
        badgeCircle.setFill(new ImagePattern(upp.getBadge()));
        badgeCircle.setSmooth(true);
        badgeCircle.setStyle("-fx-border-color: #ff0404;");

        //Gestione del bottone visulizzazione per switchare tra i vari grafici
        visualizzazioneT.setOnAction(e -> cambiaModalitàGrafico());

        // Gestione colorazione livelli glicemici per il paziente su base odierna
        gestione = new GestioneRilevazioniGlicemia(paziente);
        List<Integer> codiciRilevazioni = GlicontrolCoreSystem.getInstance().verificaLivelliGlicemici(paziente, true, false);

        aggiornaGrafico();

        //Inizializza la lista delle ultime rilevazioni glicemiche
        ObservableList<String> ultimeRilevazioni = FXCollections.observableArrayList();
        ultimeRilevazioni.addAll(upp.getListaRilevazioniGlicemicheOdierne());
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
                        case 1 -> style = "-fx-background-color: #ffdd00; -fx-text-fill: black;";
                        case 2 -> style = "-fx-background-color: #ff9900; -fx-text-fill: black;";
                        case 3 -> style = "-fx-background-color: #ff0000; -fx-text-fill: white;";
                        case 4 -> style = "-fx-background-color: #6b0c8a; -fx-text-fill: white;";
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
        GlicontrolCoreSystem.getInstance().promemoriaRegistrazioneGlicemica(paziente);
        GlicontrolCoreSystem.getInstance().monitoraInserimentoRilevazioniGlicemiche(paziente);
    }

    public void openProfile() {
        // Carica il file FXML della nuova finestra
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../uiElements/ModificaInformazioniPaziente.fxml"));
            Parent root = fxmlLoader.load();

            ModificaInformazioniPazienteController mipc = fxmlLoader.getController();
            mipc.setInstance(this, paziente);

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

            FinestraSintomiPazienteController fspc = fxmlLoader.getController();
            fspc.setInstance(this, paziente);

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

            FinestraPatologieConcomitantiPazienteController fpcp = fxmlLoader.getController();
            fpcp.setInstance(this, paziente);

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
            frgpc.setInstance(this, paziente);

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

            FinestraTerapiePazienteController ftpc =  fxmlLoader.getController();
            ftpc.setInstance(this, paziente);

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

        switch (visualizzazioneAttuale) {
            case MENSILE -> {
                int mese = oggi.getMonthValue();
                serie.setName("Andamento mensile");

                Map<String, Double> mediaSettimanale = gestione.getMediaMensileGlicemiaPerMeseCorrente(anno, mese);

                for (var entry : mediaSettimanale.entrySet()) {
                    String settimanaLabel = entry.getKey();
                    serie.getData().add(new XYChart.Data<>(settimanaLabel, entry.getValue()));
                }
            }

            case SETTIMANALE -> {
                WeekFields wf = WeekFields.of(Locale.getDefault());
                int settimana = oggi.get(wf.weekOfWeekBasedYear());
                serie.setName("Andamento settimanale");

                Map<String, Double> mediaGiornaliera = gestione.getMediaGiornalieraGlicemia(anno, settimana);
                for (var entry : mediaGiornaliera.entrySet()) {
                    LocalDate data = LocalDate.parse(entry.getKey());
                    String dataFormattata = data.format(formatter);
                    serie.getData().add(new XYChart.Data<>(dataFormattata, entry.getValue()));
                }
            }

            case GIORNALIERA -> {
                serie.setName("Andamento odierno");

                List<RilevazioneGlicemica> rilevazioniOggi = gestione.getRilevazioniPerData(oggi);

                rilevazioniOggi.sort(Comparator.comparing(RilevazioneGlicemica::getOra));

                for (RilevazioneGlicemica rilevazione : rilevazioniOggi) {
                    String orario = rilevazione.getOra().toString().substring(0,5); // es. "15:30"
                    serie.getData().add(new XYChart.Data<>(orario, rilevazione.getValore()));
                }
            }
        }

        andamentoGlicemiaLC.getData().add(serie);

    }

    public void cambiaModalitàGrafico(){

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
            assunzioneFarmaciPaziente.setTitle("Registra i farmaci");
            assunzioneFarmaciPaziente.setScene(new Scene(root));

            assunzioneFarmaciPaziente.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void aggiornaListaRilevazioniGlicemicheOdierne(){
        UtilityPortali newUpp = new UtilityPortali();
        ObservableList<String> newRilevazioni = FXCollections.observableArrayList();
        newRilevazioni.addAll(newUpp.getListaRilevazioniGlicemicheOdierne());
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
                        case 1 -> style = "-fx-background-color: #ffdd00; -fx-text-fill: black;";
                        case 2 -> style = "-fx-background-color: #ff9900; -fx-text-fill: black;";
                        case 3 -> style = "-fx-background-color: #ff0000; -fx-text-fill: white;";
                        case 4 -> style = "-fx-background-color: #6b0c8a; -fx-text-fill: white;";
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
