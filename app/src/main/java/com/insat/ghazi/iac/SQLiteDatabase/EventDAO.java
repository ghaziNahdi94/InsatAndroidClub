package com.insat.ghazi.iac.SQLiteDatabase;

import android.content.Context;

/**
 * Created by ozil_ on 31/01/2017.
 */

public class EventDAO extends DAOBase {






    //EVENTS
    public static final String ID = "id";
    public static final String TITRE= "titre";
    public static final String DESCRIPTION = "description";
    public static final String DATE = "date";
    public static final String TABLE_NAME = "events";





    public EventDAO(Context context) {
        super(context);
    }
}
