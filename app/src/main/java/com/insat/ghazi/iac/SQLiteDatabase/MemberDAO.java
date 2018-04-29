package com.insat.ghazi.iac.SQLiteDatabase;

import android.content.Context;

/**
 * Created by ozil_ on 29/01/2017.
 */

public class MemberDAO extends DAOBase{


    public static final String ID = "id";
    public static final String TEL = "tel";
    public static final String EMAIL = "email";
    public static final String NOM = "nom";
    public static final String PRENOM = "prenom";
    public static final String PASSWORD = "password";
    public static final String INSATIEN = "insatien";
    public static final String FILLIERE = "filliere";
    public static final String NIVEAU = "niveau";
    public static final String CONNECTER = "connecter";
    public static final String POSTE = "poste";
    public static final String PHOTO = "photo";
    public static final String BLOCAGE = "blocage";
    public static final String TABLE_NAME = "membre";




    public MemberDAO(Context context) {
        super(context);
    }




}
