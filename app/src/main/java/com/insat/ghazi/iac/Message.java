package com.insat.ghazi.iac;

import android.util.Log;

import java.util.Date;

/**
 * Created by ozil_ on 29/01/2017.
 */

public class Message implements Comparable<Message>{

    private long  id = -1;
    private String date = null;
    private String texte = null;
    private long source = -1;
    private long destination = -1;
    private boolean vu = false;


    public Message(){}

    public Message(long id, String date, String texte,long source,long destination ,boolean vu) {
        this.id = id;
        this.date = date;
        this.texte = texte;
        this.source = source;
        this.destination = destination;
        this.vu = vu;
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

    public long getSource() {
        return source;
    }

    public void setSource(long source) {
        this.source = source;
    }

    public long getDestination() {
        return destination;
    }

    public void setDestination(long destination) {
        this.destination = destination;
    }

    public boolean isVu() {
        return vu;
    }

    public void setVu(boolean vu) {
        this.vu = vu;
    }





    public boolean egale(Message other) {



 if( id == other.getId() && date.equals(other.getDate()) && texte.equals(other.getTexte()) && source == other.getSource() && destination == other.getDestination()&& vu == other.isVu())
   return true;
            else
   return false;

    }


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Message){
            Message other = (Message) obj;
            return id == other.getId() && date.equals(other.getDate()) && texte.equals(other.getTexte()) && source == other.getSource() && destination == other.getDestination();

        }else{
            return false;
        }
    }

    @Override
    public int compareTo(Message msg) {



        //Recupérer date 1 pour comparer
        String date1String = this.date.split("à")[0].trim();
        int day1 = Integer.parseInt(date1String.split("/")[0].trim());
        int month1 = Integer.parseInt(date1String.split("/")[1].trim());
        int year1 = Integer.parseInt(date1String.split("/")[2].trim());
        String time1String = this.date.split("à")[1].trim();
        int hour1 = Integer.parseInt(time1String.split(":")[0].trim());
        int minute1 = Integer.parseInt(time1String.split(":")[1].trim());
        Date date1 = new Date(year1,month1,day1,hour1,minute1);



        //Recupérer date 2 pour comparer
        String date2String = msg.date.split("à")[0].trim();
        int day2 = Integer.parseInt(date2String.split("/")[0].trim());
        int month2 = Integer.parseInt(date2String.split("/")[1].trim());
        int year2 = Integer.parseInt(date2String.split("/")[2].trim());
        String time2String = msg.date.split("à")[1].trim();
        int hour2 = Integer.parseInt(time2String.split(":")[0].trim());
        int minute2 = Integer.parseInt(time2String.split(":")[1].trim());
        Date date2 = new Date(year2,month2,day2,hour2,minute2);



        return date1.compareTo(date2);
    }
}
