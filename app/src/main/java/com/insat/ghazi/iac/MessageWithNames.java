package com.insat.ghazi.iac;

/**
 * Created by ozil_ on 22/03/2017.
 */

public class MessageWithNames extends Message{


    private String nom = null;
    private String prenom = null;
    private String email = null;
    private String tel = null;
    private int appel = -1;
    private int sms = -1;


    public MessageWithNames() {
        super();

    }

    public MessageWithNames(long id, String date, String texte, long source, long destination, boolean vu,String nom,String prenom,String email,String tel,int appel,int sms) {
        super(id, date, texte, source, destination, vu);
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.tel = tel;
        this.appel = appel;
        this.sms = sms;
    }


    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }


    public String getEmail() {
        return email;
    }

    public String getTel() {
        return tel;
    }


    public int getAppel() {
        return appel;
    }

    public int getSms() {
        return sms;
    }
}
