package com.insat.ghazi.iac.Fragments;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.insat.ghazi.iac.Adapters.RecycleMessagesAdapter;
import com.insat.ghazi.iac.Membre;
import com.insat.ghazi.iac.Message;
import com.insat.ghazi.iac.MessageInMessageFrgment;
import com.insat.ghazi.iac.MessageWithNames;
import com.insat.ghazi.iac.R;
import com.insat.ghazi.iac.SQLiteDatabase.MessageDAO;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment {


    private Context context = null;

    private ScrollView scrollView = null;
    private RecyclerView recyclerView = null;

    private MessageInMessageFrgment messageInMessageFrgment = null;
    private RecycleMessagesAdapter recycleMessagesAdapter = null;
    private TextView aucunMessage = null;



    private ProgressDialog progressDialog = null;
    private ArrayList<Message> messages = null;


    private long nbrOfLastMessages = -1;
    private long nbrOfProgress = -1;



    private long msgViewCreated = -1;
    private long nbrOfMessageViews = -1;
    private ArrayList<MessageInMessageFrgment> msgViews = null;



    private long memberActuelId = -1;



    private File repPhotoMessages = null;





    private  File path = null;
    private StorageReference pathFirebase = null;
    private SQLiteDatabase database = null;


    private boolean refrech = false;


    private DatabaseReference messagesThisMember;
    private ValueEventListener listener;
    DatabaseReference refChild;
    private ValueEventListener vListener;


    public MessagesFragment() {
        // Required empty public constructor
    }


    public void setArgs(Context context,long memberActuelId,File repPhotoMessages){

        this.memberActuelId = memberActuelId;
        this.repPhotoMessages = repPhotoMessages;
        this.context = context;
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


         View view = inflater.inflate(R.layout.fragment_messages, container, false);


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/coder.ttf").build());

        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_messages);
        aucunMessage = (TextView) view.findViewById(R.id.aucun_message);
        scrollView = (ScrollView) view.findViewById(R.id.scroll_recycler_messages);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);



        progressDialog = ProgressDialog.show(getContext(), "", "Chargement...");



        messagesThisMember  = FirebaseDatabase.getInstance().getReference("messages/messages de membre " + memberActuelId);


        //pour acéder a la table des messages du membre actuel
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {

           refrech = false;
            afficheViewsFromFirebase();


        }else{


            refrech = true;
            afficheViewsFromSQlite();

            progressDialog.dismiss();



        }








        //Refresh
        new Thread(new ConnectionVerif()).start();



        // Inflate the layout for this fragment
        return view;
    }










    //methodes utiles

    private void afficheViewsFromSQlite(){

        ArrayList<MessageWithNames> messageWithNames = getMsgFromSQLiteDb();


        if(messageWithNames.size() > 0){
            msgViews = new ArrayList<MessageInMessageFrgment>();


            for (MessageWithNames withNames : messageWithNames) {

                Message m = new Message(withNames.getId(), withNames.getDate(), withNames.getTexte(), withNames.getSource(), withNames.getDestination(), withNames.isVu());

                long idMember = -1;
                if (m.getDestination() == memberActuelId)
                    idMember = m.getSource();
                else
                    idMember = m.getDestination();

                String date = m.getDate().split("à")[0].trim();
                String time = m.getDate().split("à")[1].trim();


                MessageInMessageFrgment messageInMessageFrgment = new MessageInMessageFrgment(idMember, withNames.getPrenom() + " " + withNames.getNom(), date, time, m.getTexte(), withNames.getNom(), withNames.getPrenom(), withNames.getEmail(), withNames.getTel(),withNames.getAppel(),withNames.getSms(),m.getDestination());

                msgViews.add(messageInMessageFrgment);
            }





            recycleMessagesAdapter = new RecycleMessagesAdapter(context,msgViews,repPhotoMessages);
            scrollView.setVisibility(View.VISIBLE);
            aucunMessage.setVisibility(View.GONE);


            recyclerView.setAdapter(new RecycleMessagesAdapter(context,msgViews,repPhotoMessages));
        }else{
            aucunMessage.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }

    }


    @Override
    public void onStop() {
        super.onStop();

        if(messagesThisMember != null && listener != null)
            messagesThisMember.removeEventListener(listener);

        if(refChild != null && vListener != null)
            refChild.removeEventListener(vListener);

    }


    @Override
    public void onPause() {
        super.onPause();

        if(messagesThisMember != null && listener != null)
            messagesThisMember.removeEventListener(listener);


        if(refChild != null && vListener != null)
            refChild.removeEventListener(vListener);

    }

    private void afficheViewsFromFirebase(){





           listener = messagesThisMember.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //ouvrir la base SQLite
                database = new MessageDAO(context).open();
                database.execSQL("DELETE FROM "+MessageDAO.TABLE_NAME);
                database.close();

                if (dataSnapshot.getChildrenCount() == 0) {
                    aucunMessage.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.GONE);
                    progressDialog.dismiss();
                }

                if (dataSnapshot.getChildrenCount() > 0) {
                    messages = new ArrayList<Message>();
                    nbrOfProgress = 0;
                    nbrOfLastMessages = dataSnapshot.getChildrenCount();


                    for (DataSnapshot val : dataSnapshot.getChildren()) {


                        //pour accéder a chaque conversation avec les autres membres    (val.getKey() nous donne le membre visé)
                        refChild = messagesThisMember.child(val.getKey());


                        //on récupére les derniérs messages de chaque conversation
                        messages = new ArrayList<>();
                        msgViews = new ArrayList<MessageInMessageFrgment>();
                        vListener = refChild.addValueEventListener(new AllLastMessagesGetter());


                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void getPhotoMemberMessages(long idMember){


         pathFirebase = FirebaseStorage.getInstance().getReference("photo_profil/"+idMember+".jpg");


   path = new File(repPhotoMessages.getAbsoluteFile()+"/"+idMember+".jpg");


new Thread(new Runnable() {
    @Override
    public void run() {
        pathFirebase.getFile(path).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                Log.e("msg_profil","download member images succes");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("msg_profil","download member images failed !!!");
            }
        });
    }
}).start();




}



    private ArrayList<MessageWithNames> getMsgFromSQLiteDb(){

    messages = new ArrayList<Message>();


    String sqlQuery = "SELECT * FROM "+MessageDAO.TABLE_NAME+" as t1";
    sqlQuery += " GROUP BY avec HAVING id >= (SELECT MAX(id) FROM "+MessageDAO.TABLE_NAME+" as t2 WHERE t1.avec=t2.avec)";
    database = new MessageDAO(context).open();
    Cursor cursor = database.rawQuery(sqlQuery,new String[]{});



    ArrayList<MessageWithNames> messageWithNames = new ArrayList<MessageWithNames>();
    while (cursor.moveToNext()){


        long id = cursor.getLong(0);
        String date = cursor.getString(1);
        String texte = cursor.getString(2);
        long source = cursor.getLong(4);
        long dest = cursor.getLong(5);

        boolean vu = true;
        String nom = cursor.getString(7);
        String prenom = cursor.getString(8);
        String email = cursor.getString(9);
        String tel = cursor.getString(10);
        int appel = cursor.getInt(11);
        int sms = cursor.getInt(12);



        MessageWithNames message = new MessageWithNames(id,date,texte,source,dest,vu,nom,prenom,email,tel,appel,sms);

       messageWithNames.add(message);

    }
    cursor.close();
    database.close();



    Collections.sort(messageWithNames, new Comparator<MessageWithNames>() {
        @Override
        public int compare(MessageWithNames messageWithNames, MessageWithNames t1) {

            //Recupérer date 1 pour comparer
            String date1String = messageWithNames.getDate().split("à")[0].trim();
            int day1 = Integer.parseInt(date1String.split("/")[0].trim());
            int month1 = Integer.parseInt(date1String.split("/")[1].trim());
            int year1 = Integer.parseInt(date1String.split("/")[2].trim());
            String time1String = messageWithNames.getDate().split("à")[1].trim();
            int hour1 = Integer.parseInt(time1String.split(":")[0].trim());
            int minute1 = Integer.parseInt(time1String.split(":")[1].trim());
            Date date1 = new Date(year1,month1,day1,hour1,minute1);



            //Recupérer date 2 pour comparer
            String date2String = t1.getDate().split("à")[0].trim();
            int day2 = Integer.parseInt(date2String.split("/")[0].trim());
            int month2 = Integer.parseInt(date2String.split("/")[1].trim());
            int year2 = Integer.parseInt(date2String.split("/")[2].trim());
            String time2String = t1.getDate().split("à")[1].trim();
            int hour2 = Integer.parseInt(time2String.split(":")[0].trim());
            int minute2 = Integer.parseInt(time2String.split(":")[1].trim());
            Date date2 = new Date(year2,month2,day2,hour2,minute2);

            return date1.compareTo(date2);
        }
    });




    Collections.reverse(messageWithNames);


    return messageWithNames;

}


    private void insertMsgInSQLiteDatabase(final Message msg){


        long idMember = -1;
        if(msg.getDestination() == memberActuelId)
            idMember = msg.getSource();
        else
            idMember = msg.getDestination();



        DatabaseReference tableMembre = FirebaseDatabase.getInstance().getReference("membre");
        tableMembre.orderByChild("id").equalTo(idMember).addValueEventListener(new InsertMsgInFirebase(msg,idMember));


    }




    //Listeneres
    class AllLastMessagesGetter implements ValueEventListener{


        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Message message = null;

            for(DataSnapshot val : dataSnapshot.getChildren()){

                message = (Message) val.getValue(Message.class);
                //ouvrir la base SQLite
                insertMsgInSQLiteDatabase(message);


            }


            if(message != null) {

                //on récupére la photo du membre de la conversation

                long idMember = -1;
                if (message.getDestination() == memberActuelId)
                    idMember = message.getSource();
                else
                    idMember = message.getDestination();

                getPhotoMemberMessages(idMember);


                messages.add(message);
                nbrOfProgress++;


                if (nbrOfProgress == nbrOfLastMessages) {         //toutes les derniérs msg est là



                    msgViewCreated = 0;
                    nbrOfMessageViews = messages.size();

                    for (Message m : messages) {




                        if (m.getDestination() == memberActuelId)
                            idMember = m.getSource();
                        else
                            idMember = m.getDestination();


                        //récupérer le nom et prénom du membre de la conversation puis afficher les messages views
                        DatabaseReference tableMembre = FirebaseDatabase.getInstance().getReference("membre");
                        AfficheListMessages afficheListMessages = new AfficheListMessages(m);


                        tableMembre.orderByChild("id").equalTo(idMember).addValueEventListener(afficheListMessages);


                    }


                }
        }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }








    private MessageInMessageFrgment getMimfByMessage(ArrayList<MessageInMessageFrgment> messagesFrag,Message m){


        for(MessageInMessageFrgment inMessageFrgment : messagesFrag){

            if(inMessageFrgment.getDestination() == m.getDestination())
                return inMessageFrgment;

        }


        return null;
    }



    private ArrayList<MessageInMessageFrgment> getAllMessagesInMessageFragments(ArrayList<MessageInMessageFrgment> messagesFrag,ArrayList<Message> messages){


        ArrayList<MessageInMessageFrgment> result = new ArrayList<>();


        for(Message m : messages){

            MessageInMessageFrgment mimf = getMimfByMessage(messagesFrag,m);
            result.add(mimf);
            messagesFrag.remove(mimf);

        }

        return result;
    }



    private void sortMessages(ArrayList<MessageInMessageFrgment> msgs){

        Collections.sort(msgs, new Comparator<MessageInMessageFrgment>() {
            @Override
            public int compare(MessageInMessageFrgment m1, MessageInMessageFrgment m2) {

                int year1 = Integer.parseInt(m1.getDate().split("/")[2].trim());
                int year2 = Integer.parseInt(m2.getDate().split("/")[2].trim());

                if(year1 > year2)
                    return -1;
                else if(year2 > year1)
                    return 1;


                int month1 = Integer.parseInt(m1.getDate().split("/")[1].trim());
                int month2 = Integer.parseInt(m2.getDate().split("/")[1].trim());


                if(month1 > month2)
                    return -1;
                else if(month2 > month1)
                    return 1;


                int day1 = Integer.parseInt(m1.getDate().split("/")[0].trim());
                int day2 = Integer.parseInt(m2.getDate().split("/")[0].trim());


                if(day1 > day2)
                    return -1;
                else if(day2 > day1)
                    return 1;




                int hour1 = Integer.parseInt(m1.getTime().split(":")[0].trim());
                int hour2 = Integer.parseInt(m2.getTime().split(":")[0].trim());


                if(hour1 > hour2)
                    return -1;
                else if(hour2 > hour1)
                    return 1;



                int minute1 = Integer.parseInt(m1.getTime().split(":")[1].trim());
                int minute2 = Integer.parseInt(m1.getTime().split(":")[1].trim());


                if(minute1 > minute2)
                    return -1;
                else if(minute2 > minute1)
                    return 1;



                return 0;
            }
        });

    }


    class AfficheListMessages implements ValueEventListener{


        private Message message = null;

        public AfficheListMessages(Message message){

            this.message = message;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {


            Membre membre = null;

            for (DataSnapshot val : dataSnapshot.getChildren()) {

                membre = (Membre) val.getValue(Membre.class);


            }



                String date = message.getDate().split("à")[0].trim();
                String time = message.getDate().split("à")[1].trim();


                messageInMessageFrgment = new MessageInMessageFrgment(membre.getId(), membre.getPrenom() + " " + membre.getNom(), date, time, message.getTexte(),message.getDestination());

                msgViews.add(messageInMessageFrgment);
                msgViewCreated++;








                if (msgViewCreated == nbrOfMessageViews) {



                    ArrayList<MessageInMessageFrgment> messageInMessageFrgments = getAllMessagesInMessageFragments(msgViews,messages);
                    sortMessages(messageInMessageFrgments);

                    scrollView.setVisibility(View.VISIBLE);
                    aucunMessage.setVisibility(View.GONE);


                    recyclerView.setAdapter(new RecycleMessagesAdapter(context, messageInMessageFrgments, repPhotoMessages));


                    progressDialog.dismiss();

                }

            }





        @Override
        public void onCancelled(DatabaseError databaseError) {

        }




    }




    class InsertMsgInFirebase implements ValueEventListener{

        private Message msg = null;
        private long idMember = -1;

        public InsertMsgInFirebase(Message msg,long idMember) {

            this.msg = msg;
            this.idMember = idMember;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            ContentValues contentValues = new ContentValues();

            contentValues.put(MessageDAO.ID,msg.getId());
            contentValues.put(MessageDAO.DATE,msg.getDate());
            contentValues.put(MessageDAO.EMAIL_SOURCE,msg.getSource());
            contentValues.put(MessageDAO.EMAIL_DESTINATION,msg.getDestination());
            contentValues.put(MessageDAO.TEXTE,msg.getTexte());
            contentValues.put(MessageDAO.VU,msg.isVu());

            contentValues.put(MessageDAO.AVEC, idMember);

            for(DataSnapshot val : dataSnapshot.getChildren()){

                Membre membre = (Membre) val.getValue(Membre.class);

                contentValues.put(MessageDAO.NOM,membre.getNom());
                contentValues.put(MessageDAO.PRENOM,membre.getPrenom());
                contentValues.put(MessageDAO.EMAIL,membre.getEmail());
                contentValues.put(MessageDAO.TEL,membre.getTel());
                contentValues.put(MessageDAO.APPEL,membre.getParametres().getTel());
                contentValues.put(MessageDAO.SMS,membre.getParametres().getSms());

            }

            database = new MessageDAO(context).open();
            database.insert(MessageDAO.TABLE_NAME,null,contentValues);
            database.close();


        }
        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }







    class ConnectionVerif implements Runnable{


        @Override
        public void run() {



            while (true){

                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();


                if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED){

                    if(refrech){

                        afficheViewsFromFirebase();
                        refrech = false;

                    }

                }else{

                    refrech  = true;

                }


                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }





        }
    }



    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }




    private int getAnnee(String date){

        String s = date.split("à")[0].trim().split("/")[2];
        return Integer.parseInt(s);

    }

    private int getMois(String date){

        String s = date.split("à")[0].trim().split("/")[1];
        return Integer.parseInt(s);

    }

    private int getJour(String date){

        String s = date.split("à")[0].trim().split("/")[0];
        return Integer.parseInt(s);

    }


    private int getHours(String date){
        String s = date.split("à")[1].trim().split(":")[0];
        return Integer.parseInt(s);
    }



    private int getMinutes(String date){
        String s = date.split("à")[1].trim().split(":")[1];
        return Integer.parseInt(s);
    }


}
