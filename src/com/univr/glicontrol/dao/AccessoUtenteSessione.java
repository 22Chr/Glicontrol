package com.univr.glicontrol.dao;

import java.sql.ResultSet;

public interface AccessoUtenteSessione {
    ResultSet recuperaUtente(String codiceFiscale, String ruolo);
}
