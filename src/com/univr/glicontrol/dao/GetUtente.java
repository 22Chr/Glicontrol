package com.univr.glicontrol.dao;

import java.sql.ResultSet;

public interface GetUtente {
    ResultSet recuperaUtente(String codiceFiscale, String ruolo);
}
