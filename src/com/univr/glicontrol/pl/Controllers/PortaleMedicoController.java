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

            FinestraTerapiePazienteController gestoreTerapiaController = fxmlLoader.getController();
            gestoreTerapiaController.setInstance(this);
            gestoreTerapiaController.setNomeBottoneInserimentoTerapia();

            Stage gestoreTerapiePaziente = new Stage();
            gestoreTerapiePaziente.setTitle("Terapie paziente");
            gestoreTerapiePaziente.setScene(new Scene(root));

            gestoreTerapiePaziente.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


}
