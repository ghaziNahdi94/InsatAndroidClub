package com.insat.ghazi.iac.SQLiteDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ozil_ on 29/01/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {


    //MEMBER
    public static final String MEMBER_TEL = "tel";
    public static final String MEMBER_EMAIL = "email";
    public static final String MEMBER_NOM = "nom";
    public static final String MEMBER_PRENOM = "prenom";
    public static final String MEMBER_PASSWORD = "password";
    public static final String MEMBER_INSATIEN = "insatien";
    public static final String MEMBER_FILLIERE = "filliere";
    public static final String MEMBER_NIVEAU = "niveau";
    public static final String MEMBER_CONNECTER = "connecter";
    public static final String MEMBER_POSTE = "poste";
    public static final String MEMBER_PHOTO = "photo";
    public static final String  MEMBER_ID = "id";
    public static final String MEMBER_BLOCAGE = "blocage";
    public static final String MEMBER_TABLE_NAME = "membre";
    public static final String MEMBER_TABLE_CREATE = "CREATE TABLE "+MEMBER_TABLE_NAME+" ("+MEMBER_ID+" INTEGER,"+MEMBER_TEL+" TEXT,"
            +" TEXT,"+MEMBER_EMAIL+" TEXT,"+MEMBER_NOM+" TEXT,"+MEMBER_PRENOM+" TEXT,"+MEMBER_PASSWORD+" TEXT,"+MEMBER_INSATIEN+" TEXT,"
            +MEMBER_FILLIERE+" TEXT,"+MEMBER_NIVEAU+" TEXT,"+MEMBER_CONNECTER+" TEXT,"+MEMBER_POSTE+" TEXT,"+MEMBER_PHOTO+" INTEGER,"+MEMBER_BLOCAGE+" INTEGER);";
    public static final String MEMBER_TABLE_DROP = "DROP TABLE IF EXISTS "+MEMBER_TABLE_NAME+";";



    //EVENTS
    public static final String EVENT_ID = "id";
    public static final String EVENT_TITRE= "titre";
    public static final String EVENT_DESCRIPTION = "description";
    public static final String EVENT_DATE = "date";
    public static final String EVENT_TABLE_NAME = "events";
    public static final String EVENT_TABLE_CREATE = "CREATE TABLE "+EVENT_TABLE_NAME+" ("+EVENT_ID+" INTEGER,"
            +EVENT_TITRE+" TEXT,"+EVENT_DESCRIPTION+" TEXT,"+EVENT_DATE+" TEXT);";
    public static final String EVENT_TABLE_DROP = "DROP TABLE IF EXISTS "+EVENT_TABLE_NAME+";";




    //MESSAGE
    public static final String MESSAGE_ID = "id";
    public static final String MESSAGE_DATE = "date";
    public static final String MESSAGE_TEXTE = "texte";
    public static final String MESSAGE_AVEC = "avec";
    public static final String MESSAGE_EMAIL_SOURCE = "source";
    public static final String MESSAGE_EMAIL_DESTINATION = "destination";
    public static final String MESSAGE_VU = "vu";
    public static final String MESSAGE_NOM = "nom";
    public static final String MESSAGE_PRENOM = "prenom";
    public static final String MESSAGE_EMAIL = "email";
    public static final String MESSAGE_TEL = "tel";
    public static final String MESSAGE_APPEL = "appel";
    public static final String MESSAGE_SMS = "sms";
    public static final String MESSAGE_TABLE_NAME = "message";
    public static final String MESSAGE_TABLE_CREATE = "CREATE TABLE "+MESSAGE_TABLE_NAME+" ("+MESSAGE_ID+" INTEGER,"
            +MESSAGE_DATE+" TEXT,"+MESSAGE_TEXTE+" TEXT,"+MESSAGE_AVEC+" TEXT,"+MESSAGE_EMAIL_SOURCE+" INTEGER,"+MESSAGE_EMAIL_DESTINATION+" INTEGER,"+MESSAGE_VU+" BOOLEAN,"+MESSAGE_NOM+" TEXT, "+MESSAGE_PRENOM+" TEXT, "+MESSAGE_EMAIL+" TEXT, "+MESSAGE_TEL+" TEXT, "+MESSAGE_APPEL+" INTEGER, "+MESSAGE_SMS+" INTEGER);";
    public static final String MESSAGE_TABLE_DROP = "DROP TABLE IF EXISTS "+MESSAGE_TABLE_NAME+";";








    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL(MEMBER_TABLE_CREATE);
            db.execSQL(EVENT_TABLE_CREATE);
            db.execSQL(MESSAGE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(MEMBER_TABLE_DROP);
        db.execSQL(EVENT_TABLE_DROP);
        db.execSQL(MESSAGE_TABLE_DROP);
        onCreate(db);
    }
}
