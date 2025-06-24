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
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PortaleMedicoController implements Portale {

    private final UtilityPortali upm = new UtilityPortali();
    private final Medico medico = upm.getMedicoSessione();
    private Paziente pazienteSelezionato;
    private GestioneRilevazioniGlicemia gestione;

    @FXML
    private Circle badgeC;

    @FXML
    private ListView<String> pazientiReferenteLV, pazientiGenericiLV;

    @FXML
    private TextField pazienteSelezionatoTF;

    @FXML
    private LineChart<String, Number> rilevazioniGiornaliereLC, rilevazioniSettimanaliLC, rilevazioniMensiliLC;

    @FXML
    private VBox centerVB, rightVB;

    @FXML
    private void initialize(){

        // Inizializza l'avatar con le iniziali del medico
        badgeC.setFill(new ImagePattern(upm.getBadge()));
        badgeC.setSmooth(true);
        badgeC.setStyle("-fx-border-color: #ff0404;");

        //Inizializzazione delle due listview
        ObservableList<String> pazientiReferenti = FXCollections.observableArrayList();
        pazientiReferenti.addAll(upm.getPazientiAssociatiAlReferente(medico.getIdUtente()));
        pazientiReferenteLV.setItems(pazientiReferenti);

        ObservableList<String> pazientiGenerici = FXCollections.observableArrayList();
        pazientiGenerici.addAll(upm.getPazientiNonAssociatiAlReferente(medico.getIdUtente()));
        pazientiGenericiLV.setItems(pazientiGenerici);

        //Se si schiaccia due volte su un paziente succedono cose
        pazientiReferenteLV.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };

            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !cell.isEmpty()) {
                    centerVB.setVisible(true);
                    rightVB.setVisible(true);
                    pazienteSelezionatoTF.setText(cell.getItem()); //carica il nome al centro
                    pazienteSelezionato = upm.getPazienteAssociatoDaNomeFormattato(pazientiReferenteLV.getSelectionModel().getSelectedItem());
                    gestione = new GestioneRilevazioniGlicemia(pazienteSelezionato);
                    aggiornaGraficoGlicemiaOdierna();
                    aggiornaGraficoGlicemiaSettimanale();
                    aggiornaGraficoGlicemiaMensile();
                }
            });

            return cell;
        });

        pazientiGenericiLV.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };

            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !cell.isEmpty()) {
                    centerVB.setVisible(true);
                    rightVB.setVisible(true);
                    pazienteSelezionatoTF.setText(cell.getItem()); //carica il nome al centro
                    pazienteSelezionato = upm.getPazienteNonAssociatoDaNomeFormattato(pazientiGenericiLV.getSelectionModel().getSelectedItem());
                    gestione = new GestioneRilevazioniGlicemia(pazienteSelezionato);
                    aggiornaGraficoGlicemiaOdierna();
                    aggiornaGraficoGlicemiaSettimanale();
                    aggiornaGraficoGlicemiaMensile();
                }
            });

            return cell;
        });

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
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
            alert.setHeaderText("Sei sicuro di voler uscire?");

            if (alert.showAndWait().get() == ButtonType.OK) {
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
            gestoreTerapiePaziente.setScene(new Scene(root));

            gestoreTerapiePaziente.show();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void openPatologiePaziente() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../uiElements/FinestraPatologieConcomitantiPaziente.fxml"));
            Parent root = fxmlLoader.load();

            FinestraPatologieConcomitantiPazienteController fpcp = fxmlLoader.getController();
            fpcp.setInstance(this, pazienteSelezionato);

            Stage patologiePaziente = new Stage();
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

            FinestraSintomiPazienteController fspc = fxmlLoader.getController();
            fspc.setInstance(this, pazienteSelezionato);

            Stage sintomiPaziente = new Stage();
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

            ModificaInformazioniPazienteController mipc = fxmlLoader.getController();
            mipc.setInstance(this, pazienteSelezionato);

            Stage infoPaziente = new Stage();
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

            FinestraRilevazioniGlicemichePazienteController frgpc = fxmlLoader.getController();
            frgpc.setInstance(this, pazienteSelezionato);

            Stage rirevazioniGlicemiche = new Stage();
            rirevazioniGlicemiche.setTitle("Rilevazioni glicemiche");
            rirevazioniGlicemiche.setScene(new Scene(root));

            rirevazioniGlicemiche.show();

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

            assunzioneFarmaciPaziente.show();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

}
