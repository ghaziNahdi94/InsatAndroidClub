package com.insat.ghazi.iac.SQLiteDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by ozil_ on 29/01/2017.
 */

public abstract class DAOBase {

    protected final static int VERSION = 1;
    protected final static String DBName = "IACAppDatabase.db";


    protected SQLiteDatabase db = null;
    protected DatabaseHandler handler = null;


    public DAOBase(Context context){

        this.handler = new DatabaseHandler(context,DBName,null,VERSION);

    }



    public SQLiteDatabase open(){

        db = handler.getWritableDatabase();
        return db;

    }



    public void close(){

        db.close();

    }



    public SQLiteDatabase getDb(){return db;}




}
