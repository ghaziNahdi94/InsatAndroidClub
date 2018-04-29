package com.insat.ghazi.iac.SQLiteDatabase;

import android.content.Context;

/**
 * Created by ozil_ on 24/02/2017.
 */

public class MessageDAO extends DAOBase {



    public static final String ID = "id";
    public static final String DATE = "date";
    public static final String TEXTE = "texte";
    public static final String AVEC = "avec";
    public static final String EMAIL_SOURCE = "source";
    public static final String EMAIL_DESTINATION = "destination";
    public static final String TABLE_NAME = "message";
    public static final String VU = "vu";
    public static final String NOM = "nom";
    public static final String PRENOM = "prenom";
    public static final String EMAIL = "email";
    public static final String TEL = "tel";
    public static final String APPEL = "appel";
    public static final String SMS = "sms";






    public MessageDAO(Context context) {
        super(context);
    }




}
