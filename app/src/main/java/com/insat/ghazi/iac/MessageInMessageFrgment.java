package com.insat.ghazi.iac;

import java.util.Date;

/**
 * Created by ozil_ on 24/02/2017.
 */

public class MessageInMessageFrgment {

    private long image = -1; // id du membre de la conversation
    private String nomPrenom = null;
    private String date = null;
    private String time = null;
    private String texte = null;
    private String[] infos = null;
    private int[] parametres = null;
    private long destination = -1;

    public MessageInMessageFrgment(long image, String nomPrenom, String date, String time, String texte,long destination) {
        this.image = image;
        this.nomPrenom = nomPrenom;
        this.date = date;
        this.time = time;
        this.texte = texte;
        this.destination = destination;
        infos = null;
        parametres = null;
    }

    public MessageInMessageFrgment(long image, String nomPrenom, String date, String time, String texte,String nom,String prenom,String email,String tel,int appel,int sms,long destination) {
        this.image = image;
        this.nomPrenom = nomPrenom;
        this.date = date;
        this.time = time;
        this.texte = texte;
        this.destination = destination;
        infos = new String[]{prenom,nom,email,tel};
        parametres = new int[]{appel,sms};
    }


    public long getDestination() {
        return destination;
    }

    public long getImage() {
        return image;
    }

    public void setImage(long image) {
        this.image = image;
    }

    public String getNomPrenom() {
        return nomPrenom;
    }

    public void setNomPrenom(String nomPrenom) {
        this.nomPrenom = nomPrenom;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    public String[] getInfos() {
        return infos;
    }

    public int[] getParametres() {
        return parametres;
    }











}
