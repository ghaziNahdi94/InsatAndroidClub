package com.insat.ghazi.iac;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.insat.ghazi.iac.Activitys.InscriptionActivity;
import com.insat.ghazi.iac.Activitys.MainActivity;
import com.insat.ghazi.iac.SQLiteDatabase.MemberDAO;
import com.insat.ghazi.iac.SQLiteDatabase.MessageDAO;

import java.sql.PreparedStatement;

/**
 * Created by ozil_ on 29/01/2017.
 */

public class Membre {


    private String email = null;
    private String password = null;
    private String nom = null;
    private String prenom = null;
    private String tel = null;
    private boolean insatien = false;
    private String filliere = null;
    private String niveau = null;
    private boolean connecter = false;
    private String poste = null;
    private long photo = -1;
    private long id = -1;
    private Parametres parametres = null;
    private boolean blocage = false;



    public  Membre(){}

    public Membre(String email, String password, String nom, String prenom, String tel, boolean insatien, String filliere, String niveau,boolean connecter,String poste,long photo,long id,boolean blocage) {
        this.email = email;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.tel = tel;
        this.insatien = insatien;
        this.filliere = filliere;
        this.niveau = niveau;
        this.connecter = connecter;
        this.poste = poste;
        this.photo = photo;
        this.id = id;

        this.parametres = new Parametres(1,1);
        this.blocage = blocage;

    }


    public String getEmail() {
        return email;
    }

    public String getPoste() {
        return poste;
    }

    public long getPhoto() {
        return photo;
    }

    public void setPhoto(long photo) {
        this.photo = photo;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public boolean isConnecter() {
        return connecter;
    }

    public void setConnecter(boolean connecter) {
        this.connecter = connecter;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public boolean isInsatien() {
        return insatien;
    }

    public void setInsatien(boolean insatien) {
        this.insatien = insatien;
    }

    public String getFilliere() {
        return filliere;
    }

    public void setFilliere(String filliere) {
        this.filliere = filliere;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Parametres getParametres() {
        return parametres;
    }

    public void setParametres(Parametres parametres) {
        this.parametres = parametres;
    }

    public boolean isBlocage() {
        return blocage;
    }


    //methode utiles pour un membre

    public boolean existInSQLiteDatabase(Context context){

        MemberDAO memberDAO = new MemberDAO(context);
        SQLiteDatabase database = memberDAO.open();
        Cursor cursor = database.rawQuery("SELECT * FROM "+MemberDAO.TABLE_NAME+" WHERE "+MemberDAO.EMAIL+"=?",new String[]{this.getEmail()});

        if(cursor.getCount() == 0) {
            memberDAO.close();
            return false;
        }else {
            memberDAO.close();
            return true;
        }

    }



    public void setMembreBloquerSQLite(Context context){

        MemberDAO tableMembreSQLite = new MemberDAO(context);
        SQLiteDatabase database = tableMembreSQLite.open();

        database.execSQL("UPDATE "+MemberDAO.TABLE_NAME+" SET "+MemberDAO.BLOCAGE+"=1 WHERE "+MemberDAO.ID+"="+this.getId());

        database.close();

    }

    public void enregistrerDansSQLiteDatabase(Context context){

        MemberDAO tableMembreSQLite = new MemberDAO(context);
        tableMembreSQLite.open();

        ContentValues values = new ContentValues();
        values.put(MemberDAO.TEL, this.getTel());
        values.put(MemberDAO.EMAIL, this.getEmail());
        values.put(MemberDAO.PASSWORD, this.getPassword());
        values.put(MemberDAO.NOM, this.getNom());
        values.put(MemberDAO.PRENOM, this.getPrenom());
        values.put(MemberDAO.INSATIEN, Boolean.toString(this.isInsatien()));
        values.put(MemberDAO.FILLIERE, this.getFilliere());
        values.put(MemberDAO.NIVEAU, this.getNiveau());
        values.put(MemberDAO.CONNECTER, Boolean.toString(this.isConnecter()));
        values.put(MemberDAO.POSTE, this.getPoste());
        values.put(MemberDAO.PHOTO, this.getPhoto());
        values.put(MemberDAO.ID, this.getId());
        if(this.isBlocage())
            values.put(MemberDAO.BLOCAGE,1);
        else
            values.put(MemberDAO.BLOCAGE,0);

        tableMembreSQLite.getDb().insert(MemberDAO.TABLE_NAME, null, values);
        tableMembreSQLite.close();


    }






}
