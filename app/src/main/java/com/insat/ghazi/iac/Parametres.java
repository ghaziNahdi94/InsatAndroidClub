package com.insat.ghazi.iac;

/**
 * Created by ozil_ on 26/03/2017.
 */

public class Parametres {

    private int tel = 1;
    private int sms = 1;


    public Parametres(){}

    public Parametres(int tel, int sms) {
        this.tel = tel;
        this.sms = sms;
    }

    public int getTel() {
        return tel;
    }

    public void setTel(int tel) {
        this.tel = tel;
    }

    public int getSms() {
        return sms;
    }

    public void setSms(int sms) {
        this.sms = sms;
    }
}
