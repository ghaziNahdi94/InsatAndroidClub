package com.insat.ghazi.iac.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;


public class BootReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {



        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){



            //services notifications-----------------------------------------------------------------------------------
            Intent serviceInt = new Intent(context, NotificationService.class);



            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(serviceInt);
            else
                context.startService(serviceInt);



        }




    }
}
