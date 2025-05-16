package com.univr.glicontrol.pl.Models;

import com.univr.glicontrol.bll.GestionePasti;
import com.univr.glicontrol.bll.Pasto;
import com.univr.glicontrol.bll.Paziente;
import com.univr.glicontrol.bll.UtenteSessione;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

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

    public Image getAvatar() {
        Canvas canvas = new Canvas(100, 100);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.valueOf("#ff0404"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.BLACK);
        gc.fillText(paziente.getCognome().substring(0, 1) + ".", 25, 50);
        gc.fillText(paziente.getNome().substring(0, 1) + ".", 50, 75);
        return canvas.snapshot(null, null);
    }
}
