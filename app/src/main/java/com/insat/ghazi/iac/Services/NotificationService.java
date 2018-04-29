package com.insat.ghazi.iac.Services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.insat.ghazi.iac.Activitys.MainActivity;
import com.insat.ghazi.iac.Events;
import com.insat.ghazi.iac.Fragments.MessagesFragment;
import com.insat.ghazi.iac.Membre;
import com.insat.ghazi.iac.Message;
import com.insat.ghazi.iac.MessageInMessageFrgment;
import com.insat.ghazi.iac.R;
import com.insat.ghazi.iac.SQLiteDatabase.MemberDAO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationService extends Service {

    private Context context = this;
    private File compteConnecter = null;

    private long msgCounted = 0;
    private long nbrMsgs = 0;


    private  String email = null;
    private long id = -1;
    private String rootFile = null;

    private Membre m = null;





    private final String[] months = {"septembre","octobre","novembre","décembre","janvier","février","mars","avril","mai","juin","juillet","août"};

    private ArrayList<Message> messages = null;



    private DatabaseReference messagesThisMember;
    private ValueEventListener vv;
    private DatabaseReference reference;
    private ValueEventListener v;

    public NotificationService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        rootFile = getFilesDir().getAbsolutePath();
        compteConnecter = new File(rootFile+"/compte.txt");
        email = recupererEmailFromFichierConecter();
        if(email != null)
        id = getMemberId();



        new Thread(new ConnectionVerif()).start();


        return START_STICKY;
    }





    @Override
    public void onTaskRemoved(Intent rootIntent) {




    }







    @Override
    public void onCreate() {

    }





    private void runNotification(){

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();


        if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED)
        {

            final DatabaseReference tableMembreFireBase = FirebaseDatabase.getInstance().getReference("membre");
            tableMembreFireBase.orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot d : dataSnapshot.getChildren())
                        m = d.getValue(Membre.class);



                    DatabaseReference tableEventsFireBase = FirebaseDatabase.getInstance().getReference("events");
                    tableEventsFireBase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {



                            //vider la sauvegarde s'il n'ya pas d'event
                            if(dataSnapshot.getChildrenCount() == 0)
                             clearAllEvents();





                            for(DataSnapshot data : dataSnapshot.getChildren())
                            {

                                Events event = (Events) data.getValue(Events.class);


                                long eventId = getEventsNotif();






                                if( eventId != -1 ) {




                                        if(event.getId() > eventId) {








                                            //lancer la notification
                                            if(m == null) {
                                                stopSelf();
                                            }else if(m.isConnecter() && !m.isBlocage() &&goodTimeNotif(event.getDate())) {
                                                notif(event);
                                            }


                                            saveEventsNotif(event);

                                        }

                                    }else{






                                        //lancer la notification
                                        if(m == null) {
                                            stopSelf();
                                        }else if(m.isConnecter() && !m.isBlocage()&& goodTimeNotif(event.getDate())) {
                                            notif(event);
                                        }


                                        saveEventsNotif(event);


                                    }



                            }



                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });





                }







                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }

    }









    @Override
    public void onDestroy() {
        super.onDestroy();
    }






    private String recupererEmailFromFichierConecter() {

        String result = "";
        try {
            FileReader fr = new FileReader(compteConnecter);
            BufferedReader br = new BufferedReader(fr);

            br.readLine();
            result = br.readLine();

            br.close();
            fr.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void notif(Events event){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.logo)
                .setContentTitle(event.getTitre())
                .setContentText(event.getDescription());


        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.robot);
        builder.setSound(sound);

        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(event.getId(), notification);

    }




    private void notifMsg(Message message){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.logo)
                .setContentTitle("Message réçu")
                .setContentText(message.getTexte());

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.robot);
        builder.setSound(sound);

        Notification notification = builder.build();


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify((int) message.getSource(), notification);
    }










    private boolean goodTimeNotif(String dateStr){


        String[] all = dateStr.split(" ");


        String jour = all[1].trim(); int jr = Integer.parseInt(jour);

        String nomMois = all[2].trim();


        String currentMonth = getNomMois();
        int indexCurrentMonth = getIndexMonth(currentMonth);
        int indexEventMonth = getIndexMonth(nomMois);


        Log.e("hhhh","i : "+indexCurrentMonth+"/"+indexEventMonth);
        if(indexCurrentMonth < indexEventMonth) {
            return true;
        }else if(indexCurrentMonth == indexEventMonth){

            Date d = new Date();
            if(d.getDate() <= jr){
                Log.e("hhhh","d : "+d.getDate()+"/"+jr);
                return true;
            }else{Log.e("hhhh","!d : "+d.getDate()+"/"+jr);return false;}

        }else{

            return false;
        }



    }







    private String getNomMois(){



        Calendar c = Calendar.getInstance();

        return  c.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.FRANCE).toLowerCase();

    }



    public int getIndexMonth(String m){

        int res = -1;

        final Collator instance = Collator.getInstance();

        // This strategy mean it'll ignore the accents
        instance.setStrength(Collator.NO_DECOMPOSITION);


        for(int i=0;i<months.length;i++){

            String s = months[i];

            if(instance.compare(s,m) == 0) {

                res = i; break;

            }
        }


        return res;
    }































    //verif cnx for notification and
    class ConnectionVerif implements Runnable{


        @Override
        public void run() {



            while (true){


                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();


                if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED){


                    new Thread(new messageNotification()).start();

                    runNotification();




                }


                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }





        }
    }







    //message notifications
    class messageNotification implements Runnable{

        @Override
        public void run() {




                if(email != null){


                messages = new ArrayList<Message>();
                msgCounted = 0;
                 messagesThisMember = FirebaseDatabase.getInstance().getReference("messages/messages de membre " + id);

                vv = messagesThisMember.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        nbrMsgs = dataSnapshot.getChildrenCount();

                        for (DataSnapshot val : dataSnapshot.getChildren()) {


                            //pour accéder a chaque conversation avec les autres membres    (val.getKey() nous donne le membre visé)
                            reference = FirebaseDatabase.getInstance().getReference("messages/messages de membre " + id + "/" + val.getKey());


                            //on récupére les derniérs messages de chaque conversation
                            v = reference.limitToLast(1).addValueEventListener(new AllLastMessagesGetter());
                        }

                        messagesThisMember.removeEventListener(vv);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


        }}
    }



    //Listeneres
    class AllLastMessagesGetter implements ValueEventListener{
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {




            Message message = null;

            for(DataSnapshot val : dataSnapshot.getChildren())
                message = (Message) val.getValue(Message.class);




            if(message != null){

                msgCounted++;
                messages.add(message);





                if(msgCounted == nbrMsgs){ //Tous les messages sont ici

                    ArrayList<Message> msgs = new ArrayList<Message>();


                    msgs = getMsgNotif();



                for(Message msg : messages){

                    if(msg.getSource() != id){


                Message m = getMsgBySource(msgs,msg.getSource());

                if(m == null){
                    msgs.add(msg);
                    if(!appIsRunning())
                    notifMsg(msg);

                }else{


                    if(!m.getDate().equals(msg.getDate())){

                        msgs.remove(m);
                        msgs.add(msg);


                        if(!appIsRunning())
                            notifMsg(msg);

                    }




                }


                    }

                }


                saveMsgNotif(msgs);


                }


            }



            reference.removeEventListener(v);




        }
        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }



    private Message getMsgBySource(ArrayList<Message> msgs,long source){
        Message msg = null;
        for(Message m : msgs){ if(m.getSource() == source){ msg = m; break;}}
        return msg;
    }



    private long getMemberId(){

        MemberDAO dao = new MemberDAO(context);
        SQLiteDatabase database = dao.open();
        Cursor cursor = database.rawQuery("SELECT "+dao.ID+" FROM membre WHERE "+dao.EMAIL+"=?",new String[]{email});


        cursor.moveToNext();

        long id = cursor.getLong(0);



        cursor.close();
        database.close();

        return id;
    }



    private void saveEventsNotif(Events event){


                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putLong("Events"+email,event.getId());
                editor.commit();



    }



    private long getEventsNotif(){


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        long id = sharedPreferences.getLong("Events"+email,-1);



        return id;


    }

    private void clearAllEvents(){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("Events"+email).commit();

    }

    private ArrayList<Message> getMsgNotif(){


        ArrayList<Message> messages = new ArrayList<Message>();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Msg"+email,"");
        Type type = new TypeToken<List<Message>>(){}.getType();
        messages = gson.fromJson(json,type);


        if(messages == null)
            messages = new ArrayList<Message>();

        return messages;
    }



    private void saveMsgNotif(ArrayList<Message> messages){

        final ArrayList<Message> m = messages;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(m);
                editor.putString("Msg"+email,json);
                editor.commit();
            }
        }).start();


    }




    private boolean appIsRunning(){

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager
                .getRunningTasks(Integer.MAX_VALUE);
        boolean isActivityFound = false;

        if (services.get(0).topActivity.getPackageName().toString()
                .equalsIgnoreCase(context.getPackageName().toString())) {
            isActivityFound = true;
        }

        if (isActivityFound) {
            return true;
        } else {
            return false;
        }

    }












}



