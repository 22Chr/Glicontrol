package com.univr.glicontrol.pl.Models;

import com.univr.glicontrol.bll.GestionePasti;
import com.univr.glicontrol.bll.Pasto;
import com.univr.glicontrol.bll.Paziente;
import com.univr.glicontrol.bll.UtenteSessione;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UtilityPortalePaziente {
    private final Paziente paziente;
    private Map<String, Pasto> mappaPasti = new HashMap<>();

    public UtilityPortalePaziente() {
        this.paziente = UtenteSessione.getInstance().getPazienteSessione();
    }

    public Time convertiOraPasto(int ora, int minuti) {
        LocalTime localTime = LocalTime.of(ora, minuti);
        return Time.valueOf(localTime);
    }

    public List<String> getListaPasti() {
        List<String> listaPasti = new ArrayList<>();
        GestionePasti gp = new GestionePasti(paziente);
        for (Pasto pasto : gp.getPasti()) {
            String pastoFormattato = pasto.getNomePasto() + " - " + pasto.getOrario().toString().substring(0, 5);
            listaPasti.add(pastoFormattato);
            mappaPasti.put(pastoFormattato, pasto);
        }

        return listaPasti;
    }

    public Paziente getPazienteSessione() {
        return paziente;
    }

    public Image getBadge() {
        double width = 40;
        double height = 40;
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Sfondo rosso
        gc.setFill(Color.valueOf("#ff0404"));
        gc.fillRect(0, 0, width, height);

        // Font grande e grassetto
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Avenir Next", FontWeight.BOLD, 20));

        // Iniziali (gestione sicura)
        String iniziali = "";
        if (paziente.getNome() != null && !paziente.getNome().isEmpty()) {
            iniziali += paziente.getNome().substring(0, 1).toUpperCase();
        }
        if (paziente.getCognome() != null && !paziente.getCognome().isEmpty()) {
            iniziali += paziente.getCognome().substring(0, 1).toUpperCase();
        }

        // Calcolo per centrare il testo
        Text text = new Text(iniziali);
        text.setFont(gc.getFont());
        double textWidth = text.getLayoutBounds().getWidth();
        double textHeight = text.getLayoutBounds().getHeight();

        double x = (width - textWidth) / 2;
        double y = (height + textHeight / 2) / 2;

        // Disegno testo
        gc.fillText(iniziali, x, y);

        return canvas.snapshot(null, null);
    }
}
