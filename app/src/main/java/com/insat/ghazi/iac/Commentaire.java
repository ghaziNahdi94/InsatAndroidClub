package com.insat.ghazi.iac;

import java.util.Date;

/**
 * Created by ozil_ on 29/01/2017.
 */

public class Commentaire {

    private long id = -1;
    private String date = null;
    private String texte = null;
    private String emailMembre = null;
    private int idEvenement = -1;


    public Commentaire(){}

    public Commentaire(long id, String date, String texte, String emailMembre, int idEvenement) {
        this.id = id;
        this.date = date;
        this.texte = texte;
        this.emailMembre = emailMembre;
        this.idEvenement = idEvenement;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    public String getEmailMembre() {
        return emailMembre;
    }

    public void setEmailMembre(String emailMembre) {
        this.emailMembre = emailMembre;
    }

    public int getIdEvenement() {
        return idEvenement;
    }

    public void setIdEvenement(int idEvenement) {
        this.idEvenement = idEvenement;
    }
}
