package com.insat.ghazi.iac;

import android.widget.ImageView;

/**
 * Created by ozil_ on 26/01/2017.
 */

public class Events {

   private int id = 0;
    private String titre = null;
    private String description = null;
    private String date = null;



    public Events(){}

    public Events(int imageId, String title, String description, String date) {
        this.id = imageId;
        this.description = description;
        this.titre = title;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
