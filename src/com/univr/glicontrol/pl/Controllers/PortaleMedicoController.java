package com.univr.glicontrol.pl.Controllers;

import com.univr.glicontrol.bll.*;
import com.univr.glicontrol.pl.Models.UtilityPortali;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PortaleMedicoController implements Portale, Controller {

    private final UtilityPortali up = new UtilityPortali();
    private final Medico medico = up.getMedicoSessione();
    private Paziente pazienteSelezionato;
    private GestioneRilevazioniGlicemia gestione;
    Map<Paziente, List<Notifica>> mappaPazientiAssociatiNotifiche = new HashMap<>();
    Map<Paziente, List<Notifica>> mappaPazientiNonAssociatiNotifiche = new HashMap<>();



    @FXML
    private Circle badgeC;
    @FXML
    private ListView<String> pazientiReferenteLV, pazientiGenericiLV, notificheLV;
    @FXML
    private TextField pazienteSelezionatoTF;
    @FXML
    private LineChart<String, Number> rilevazioniGiornaliereLC, rilevazioniSettimanaliLC, rilevazioniMensiliLC;
    @FXML
    private VBox centerVB, rightVB, centroNotificheVB;
    @FXML
    private Button centroNotificheB;

    @FXML
    private void initialize(){
        pazienteSelezionatoTF.setText("Seleziona un paziente per iniziare");

        // Inizializza l'avatar con le iniziali del medico
        badgeC.setFill(new ImagePattern(up.getBadge()));
        badgeC.setSmooth(true);
        badgeC.setStyle("-fx-border-color: #ff0404;");

        //Inizializzazione delle due listview
        ObservableList<String> pazientiReferenti = FXCollections.observableArrayList();
        pazientiReferenti.addAll(up.getPazientiAssociatiAlReferente(medico.getIdUtente()));
        pazientiReferenteLV.setItems(pazientiReferenti);

        ObservableList<String> pazientiGenerici = FXCollections.observableArrayList();
        pazientiGenerici.addAll(up.getPazientiNonAssociatiAlReferente(medico.getIdUtente()));
        pazientiGenericiLV.setItems(pazientiGenerici);


        // popola la UI con le informazioni del paziente
        pazientiReferenteLV.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                    }
                }
            };


            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !cell.isEmpty()) {
                    PauseTransition mostraUI = new PauseTransition(Duration.millis(300));
                    mostraUI.setOnFinished(e -> {
                        centerVB.setVisible(true);
                        rightVB.setVisible(true);
                        pazienteSelezionatoTF.setText(cell.getItem());
                        pazienteSelezionato = up.getPazienteAssociatoDaNomeFormattato(pazientiReferenteLV.getSelectionModel().getSelectedItem());
                        gestione = new GestioneRilevazioniGlicemia(pazienteSelezionato);
                        aggiornaGraficoGlicemiaOdierna();
                        aggiornaGraficoGlicemiaSettimanale();
                        aggiornaGraficoGlicemiaMensile();
                    });
                    mostraUI.play();
                }
            });

            return cell;
        });

        pazientiGenericiLV.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                    }
                }
            };

            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !cell.isEmpty()) {
                    centerVB.setVisible(true);
                    rightVB.setVisible(true);
                    pazienteSelezionatoTF.setText(cell.getItem()); //carica il nome al centro
                    pazienteSelezionato = up.getPazienteNonAssociatoDaNomeFormattato(pazientiGenericiLV.getSelectionModel().getSelectedItem());
                    gestione = new GestioneRilevazioniGlicemia(pazienteSelezionato);
                    aggiornaGraficoGlicemiaOdierna();
                    aggiornaGraficoGlicemiaSettimanale();
                    aggiornaGraficoGlicemiaMensile();
                }
            });

            return cell;
        });

        notificheLV.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };

            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !cell.isEmpty()) {
                    try {
                        FXMLLoader dettaglioNotificaLoader = new FXMLLoader(getClass().getResource("../uiElements/DettaglioNotifica.fxml"));
                        Parent root = dettaglioNotificaLoader.load();

                        DettaglioNotificaController dnc = dettaglioNotificaLoader.getController();
                        dnc.setInstance(this, cell.getItem());

                        Stage dettaglioNotificaStage = new Stage();
                        dettaglioNotificaStage.setScene(new Scene(root));
                        dettaglioNotificaStage.setResizable(false);
                        dettaglioNotificaStage.setTitle("System Notification Service");

                        dettaglioNotificaStage.show();

                    } catch (IOException e) {
                        System.err.println("Si è verificato un errore durante il caricamento del dettaglio della notifica: " + e.getMessage());
                    }
                }
            });

            return cell;
        });


        // Avvia il core system
        GlicontrolCoreSystem.getInstance().setPortaleMedicoInstance(this);
        GlicontrolCoreSystem.getInstance().setConnessoComeMedico();
        GlicontrolCoreSystem.getInstance().monitoraLivelliGlicemici();
        GlicontrolCoreSystem.getInstance().monitoraSospensioneFarmaci();
        GlicontrolCoreSystem.getInstance().monitoraPresenzaNotificheNonVisualizzate();

    }

    //3 metodi, uno per ogni grafico, che vanno chiamati nel momento in cui si schiaccia sul paziente
    public void aggiornaGraficoGlicemiaOdierna(){
        rilevazioniGiornaliereLC.getData().clear();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();

        LocalDate oggi = LocalDate.now();
        serie.setName("Andamento odierno");

        List<RilevazioneGlicemica> rilevazioniOggi = gestione.getRilevazioniPerData(oggi);
        for (RilevazioneGlicemica rilevazione : rilevazioniOggi) {
            String orario = rilevazione.getOra().toString().substring(0,5);
            serie.getData().add(new XYChart.Data<>(orario, rilevazione.getValore()));
        }

        rilevazioniGiornaliereLC.getData().add(serie);

    }

    public void aggiornaGraficoGlicemiaSettimanale(){
        rilevazioniSettimanaliLC.getData().clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        XYChart.Series<String, Number> serie = new XYChart.Series<>();

        LocalDate oggi = LocalDate.now();
        serie.setName("Andamento settimanale");

        int anno = oggi.getYear();
        WeekFields wf = WeekFields.of(Locale.getDefault());
        int settimana = oggi.get(wf.weekOfWeekBasedYear());

        Map<String, Double> mediaGiornaliera = gestione.getMediaGiornalieraGlicemia(anno, settimana);
        for (var entry : mediaGiornaliera.entrySet()) {
            LocalDate data = LocalDate.parse(entry.getKey());
            String dataFormattata = data.format(formatter);
            serie.getData().add(new XYChart.Data<>(dataFormattata, entry.getValue()));
        }
        rilevazioniSettimanaliLC.getData().add(serie);

    }

    public void aggiornaGraficoGlicemiaMensile(){
        rilevazioniMensiliLC.getData().clear();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();

        LocalDate oggi = LocalDate.now();
        serie.setName("Andamento mensile");

        int anno = oggi.getYear();
        int mese = oggi.getMonthValue();

        Map<String, Double> mediaSettimanale = gestione.getMediaMensileGlicemiaPerMeseCorrente(anno, mese);

        for (var entry : mediaSettimanale.entrySet()) {
            String settimanaLabel = entry.getKey();
            serie.getData().add(new XYChart.Data<>(settimanaLabel, entry.getValue()));
        }

        rilevazioniMensiliLC.getData().add(serie);

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

    public void openGestoreTerapie() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../uiElements/FinestraTerapiePaziente.fxml"));
            Parent root = fxmlLoader.load();

            FinestraTerapiePazienteController ftpc = fxmlLoader.getController();
            ftpc.setInstance(this, pazienteSelezionato);
            ftpc.setNomeBottoneInserimentoTerapia();


            Stage gestoreTerapiePaziente = new Stage();
            gestoreTerapiePaziente.setTitle("Terapie paziente");
            gestoreTerapiePaziente.setResizable(false);
            gestoreTerapiePaziente.setScene(new Scene(root, 1200, 720));

            gestoreTerapiePaziente.show();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void openPatologiePaziente() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../uiElements/FinestraPatologieConcomitantiPaziente.fxml"));
            Parent root = fxmlLoader.load();
            root.setOpacity(0);
            FadeTransition fade = new FadeTransition(Duration.millis(300), root);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

            FinestraPatologieConcomitantiPazienteController fpcp = fxmlLoader.getController();
            fpcp.setInstance(this, pazienteSelezionato);

            Stage patologiePaziente = new Stage();
            patologiePaziente.setWidth(700);
            patologiePaziente.setHeight(500);
            patologiePaziente.setResizable(false);
            patologiePaziente.setTitle("Patologie concomitanti");
            patologiePaziente.setScene(new Scene(root));

            patologiePaziente.show();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void openSintomiPaziente(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../uiElements/FinestraSintomiPaziente.fxml"));
            Parent root = fxmlLoader.load();
            root.setOpacity(0);
            FadeTransition fade = new FadeTransition(Duration.millis(300), root);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

            FinestraSintomiPazienteController fspc = fxmlLoader.getController();
            fspc.setInstance(this, pazienteSelezionato);

            Stage sintomiPaziente = new Stage();
            sintomiPaziente.setWidth(600);
            sintomiPaziente.setHeight(500);
            sintomiPaziente.setResizable(false);
            sintomiPaziente.setTitle("Sintomi");
            sintomiPaziente.setScene(new Scene(root));

            sintomiPaziente.show();

        } catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

    public void visualizzaInfoPaziente() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../uiElements/ModificaInformazioniPaziente.fxml"));
            Parent root = fxmlLoader.load();
            root.setOpacity(0);
            FadeTransition fade = new FadeTransition(Duration.millis(150), root);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

            ModificaInformazioniPazienteController mipc = fxmlLoader.getController();
            mipc.setInstance(this, pazienteSelezionato);

            Stage infoPaziente = new Stage();
            infoPaziente.setWidth(1200);
            infoPaziente.setHeight(600);
            infoPaziente.setResizable(false);
            infoPaziente.setTitle("Informazioni personali");
            infoPaziente.setScene(new Scene(root));

            infoPaziente.show();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void visualizzaRilevazioniGlicemiche() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../uiElements/FinestraRilevazioniGlicemichePaziente.fxml"));
            Parent root = fxmlLoader.load();
            root.setOpacity(0);
            FadeTransition fade = new FadeTransition(Duration.millis(150), root);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

            FinestraRilevazioniGlicemichePazienteController frgpc = fxmlLoader.getController();
            frgpc.setInstance(this, pazienteSelezionato);

            Stage rilevazioniGlicemiche = new Stage();
            rilevazioniGlicemiche.setWidth(650);
            rilevazioniGlicemiche.setHeight(500);
            rilevazioniGlicemiche.setResizable(false);
            rilevazioniGlicemiche.setTitle("Rilevazioni glicemiche");
            rilevazioniGlicemiche.setScene(new Scene(root));

            rilevazioniGlicemiche.show();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void visualizzaAssunzioneFarmaciPaziente() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../uiElements/FinestraAssunzioneFarmaciPaziente.fxml"));
            Parent root = fxmlLoader.load();

            FinestraAssunzioneFarmaciPazienteController fafpc = fxmlLoader.getController();
            fafpc.setInstance(this, pazienteSelezionato);

            Stage assunzioneFarmaciPaziente = new Stage();
            assunzioneFarmaciPaziente.setTitle("Storico assunzioni");
            assunzioneFarmaciPaziente.setScene(new Scene(root));
            assunzioneFarmaciPaziente.setHeight(650);
            assunzioneFarmaciPaziente.setWidth(700);
            assunzioneFarmaciPaziente.setResizable(false);

            assunzioneFarmaciPaziente.show();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    //GESTIONE DEL CENTRO NOTIFICHE

    public void openCentroNotifiche(){

        GlicontrolCoreSystem.getInstance().centroNotificheIsOpened();

        UtilityPortali upCentroNotifiche = new UtilityPortali();

        Task<Void> loadNotificheTask = new Task<>() {
            @Override
            protected Void call() {
                ObservableList<String> notifiche = FXCollections.observableArrayList();
                notifiche.addAll(upCentroNotifiche.getNotificheFormattate().reversed());
                Platform.runLater(()->{
                    notificheLV.setItems(notifiche);
                });

                return  null;
            }

            @Override
            protected void succeeded() {
                centroNotificheVB.setTranslateX(300);
                centroNotificheVB.setVisible(true);
                centroNotificheB.setVisible(false);
                badgeC.setVisible(false);
                rightVB.setVisible(false);

                TranslateTransition transizione = new TranslateTransition(Duration.millis(200), centroNotificheVB);
                transizione.setToX(0);
                transizione.play();
            }
        };

        new Thread(loadNotificheTask).start();
    }


    public void resetListaNotifiche() {
        UtilityPortali upCentroNotifiche = new UtilityPortali();
        ObservableList<String> notifiche = FXCollections.observableArrayList();
        notifiche.addAll(upCentroNotifiche.getNotificheFormattate().reversed());
        Platform.runLater(()->{
            notificheLV.setItems(notifiche);
        });
    }


    public void closeCentroNotifiche(){
        TranslateTransition transizione = new TranslateTransition(Duration.millis(200), centroNotificheVB);
        transizione.setToX(300);
        transizione.setOnFinished(e -> {
            centroNotificheVB.setVisible(false);
            centroNotificheVB.setTranslateX(0);
            centroNotificheB.setVisible(true);
            badgeC.setVisible(true);
            if(pazienteSelezionato!=null) {
                rightVB.setVisible(true);
            }
        });
        transizione.play();

        GlicontrolCoreSystem.getInstance().centroNotificheIsClosed();
    }
}