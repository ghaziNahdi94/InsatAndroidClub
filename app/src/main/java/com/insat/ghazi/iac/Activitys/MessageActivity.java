package com.insat.ghazi.iac.Activitys;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.insat.ghazi.iac.Message;
import com.insat.ghazi.iac.R;
import com.insat.ghazi.iac.SQLiteDatabase.MessageDAO;
import com.insat.ghazi.iac.ViewWithJava.MessageEnvoyerView;
import com.insat.ghazi.iac.ViewWithJava.MessageRecuView;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class MessageActivity extends AppCompatActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {


    private ScrollView scrollView = null;
    private EmojiconEditText taperMessage = null;
    private ImageButton envoieMessage = null;
    private ImageView emoticons = null;
    private boolean emoticonsShow = false;


    private long idMembreConversation = -1;
    private long idMembreActuel = -1;
    private String nomPrenomMembreConversation = null;
    private String emailMembreConversation = null;


    private File compteConnecter = null;


    private LinearLayout layoutMessages = null;
    private ArrayList<Message> listMsg = null;
    private ArrayList<Message> messageDejaAffiche = new ArrayList<Message>();
    private File repPhotoMessages = null;

    private boolean dernierMsgIsMsgRecu = false;
    private boolean msg1Envoyer = false;
    private boolean msg2Envoyer = false;
    private boolean premierMsgAafficher = true;
    private String dateCompare = null;
    private String time1Compare = null;
    private String time2Compare = null;
    private char etat = 0;

    private Message msgVu = null;


    private MessageEnvoyerView msgEnvoyer = null;
    private boolean msgEnvoyerIsLast = false;


    private String telMember = null;


    private ImageButton telChat = null;
    private ImageButton smsChat = null;
    private TextView nomBarMsg = null;
    private TextView emailBarMsg = null;


    private boolean refrech = false;
    private boolean setAllVu = false;


    private MsgVuListener msgVuListener;

    DatabaseReference tableMessages;
    private AddedMsgsListener addedMsgsListener = new AddedMsgsListener();
    DatabaseReference tableMessagesAutre;





    private Bitmap photoCoversation = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);





          /*--------------------------- Les fichiers de l'application------------------------------------------*/

        String rootPath = getFilesDir().getAbsolutePath();

        compteConnecter = new File(rootPath + "/compte.txt");
        repPhotoMessages = new File(rootPath + "/photo_messages");

          /*---------------------------------------------------------------------------------------------------*/


        //on a besoin du  id(membre actuel + membre de la conversation)
        Intent intent = getIntent();
        idMembreConversation = intent.getLongExtra("idMembreConversation", -1);
        nomPrenomMembreConversation = intent.getStringExtra("prenomNomMembreConversation");
        emailMembreConversation = intent.getStringExtra("emailMembreConversation");
        telMember = intent.getStringExtra("telMembreConversation");
        idMembreActuel = recupererIdFromFichierConecter();
        int appel = intent.getIntExtra("appelMembreConversation", -1);
        int sms = intent.getIntExtra("smsMembreConversation", -1);


        photoCoversation = BitmapFactory.decodeFile(repPhotoMessages.getAbsolutePath() + "/" + idMembreConversation + ".jpg");



        //reffs

         tableMessages = FirebaseDatabase.getInstance().getReference("messages/messages de membre " + idMembreActuel + "/avec membre " + idMembreConversation);
         tableMessagesAutre = FirebaseDatabase.getInstance().getReference("messages/messages de membre " + idMembreConversation + "/avec membre " + idMembreActuel);






        /*********************Custom ActionBar***********************************/

        View bar = LayoutInflater.from(this).inflate(R.layout.message_toolbar, null);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3CB371")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(bar);

        telChat = (ImageButton) findViewById(R.id.tel_chat);
        smsChat = (ImageButton) findViewById(R.id.sms_chat);
        nomBarMsg = (TextView) findViewById(R.id.nom_barMsg);
        emailBarMsg = (TextView) findViewById(R.id.email_barMsg);


        if (nomPrenomMembreConversation.length() > 20)
            nomPrenomMembreConversation = nomPrenomMembreConversation.substring(0, 20) + "...";


        if(emailMembreConversation.length() > 25)
            emailMembreConversation = emailMembreConversation.substring(0,20)+"...";


        nomBarMsg.setText(nomPrenomMembreConversation);
        emailBarMsg.setText(emailMembreConversation);


        if (appel == 0)
            telChat.setVisibility(View.GONE);


        if (sms == 0)
            smsChat.setVisibility(View.GONE);


        telChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    telChat.setBackgroundColor(Color.parseColor("#FF85C2A0"));

                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telMember));


                    if (ActivityCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        
                    }

                    startActivity(intent);

                }else{
                    telChat.setBackgroundColor(Color.parseColor("#3CB371"));
                }

                return false;
            }
        });

        smsChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){

                    smsChat.setBackgroundColor(Color.parseColor("#FF85C2A0"));

                    ActivityCompat.requestPermissions(MessageActivity.this,new String[]{Manifest.permission.SEND_SMS},1);

                    final AlertDialog.Builder dialog = new AlertDialog.Builder(MessageActivity.this);

                    String nom = nomPrenomMembreConversation;





                    if (nom.length() < 20)
                        dialog.setTitle("SMS à "+nom);
                    else
                        dialog.setTitle("SMS à "+nom.substring(0, 20) + "...");





                    final EditText editText = new EditText(MessageActivity.this);
                    dialog.setView(editText);

                    dialog.setPositiveButton("Envoyer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(telMember,null,editText.getText().toString(),null,null);
                            dialogInterface.dismiss();
                        }
                    });

                    dialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    dialog.show();


                }else{
                    smsChat.setBackgroundColor(Color.parseColor("#3CB371"));
                }

                return false;
            }
        });

        /*********************Custom ActionBar***********************************/


        scrollView = (ScrollView) findViewById(R.id.scroll_messages_activity);
        emoticons = (ImageView) findViewById(R.id.emoticons_msg);
        taperMessage = (EmojiconEditText) findViewById(R.id.taper_msg);
        envoieMessage = (ImageButton) findViewById(R.id.envoie_msg);
        layoutMessages = (LinearLayout) findViewById(R.id.layout_messages);




        //lose focus message
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);







        taperMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideEmojiPopUp();
                showKeyboard(taperMessage);

                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });


                setAllVu = true;

                if(msgVu != null) {
                   // setMessagesVu();
                }


            }
        });

        emoticons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                showEmojiPopUp();

                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });



        setEmojiconFragment(false);

    }










    @Override
    protected void onStart() {
        super.onStart();



   //-------------------------------------------Partie affichage des messages

        if(layoutMessages.getChildCount() == 0) {
            layoutMessages.addView(new View(MessageActivity.this), LinearLayout.LayoutParams.MATCH_PARENT, 10);
        }

        if(telMember == null) {

            refrech = false;
            premierMsgAafficher = true;
            dateCompare = null;
            afficheMsgFromFirebase();

        }else{

            refrech = true;
            afficheMsgFromSQLite();


        }













//-------------------------------------------Partie pour envoyer des messages
        envoieMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();


                if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED){



                if (!taperMessage.getText().toString().equals("")) {


                    //on récupére le nbr de msg pour placer le nouveau msg à sa place


                    Date date = new Date();
                    String jour = date.getDate() + "";
                    String mois = (date.getMonth() + 1) + "";
                    String annee = (date.getYear() + 1900) + "";
                    String heure = date.getHours() + "";
                    String minute = date.getMinutes() + "";
                    String dateComplet = jour + "/" + mois + "/" + annee + " à " + heure + ":" + minute;


                    Message message = new Message(55, dateComplet, taperMessage.getText().toString(), idMembreActuel, idMembreConversation, false);



                    messageDejaAffiche.add(message);
                    String key1 = tableMessages.push().getKey();
                    HashMap<String,Object> map1 = new HashMap<String,Object>();
                    map1.put(key1,message);
                    tableMessages.updateChildren(map1);

                            //De méme pour la table message de l'autre membre
                            if (idMembreConversation != idMembreActuel) {
                                Date date1 = new Date();
                                String jour1 = date1.getDate() + "";
                                String mois1 = (date1.getMonth() + 1) + "";
                                String annee1 = (date1.getYear() + 1900) + "";
                                String heure1 = date1.getHours() + "";
                                String minute1 = date1.getMinutes() + "";
                                String dateComplet1 = jour1 + "/" + mois1 + "/" + annee1 + " à " + heure1 + ":" + minute1;



                                Message message1 = new Message(55, dateComplet1, taperMessage.getText().toString(), idMembreActuel, idMembreConversation, false);


                                String key2 = tableMessagesAutre.push().getKey();
                                HashMap<String,Object> map2 = new HashMap<String,Object>();
                                map2.put(key2,message1);
                                tableMessagesAutre.updateChildren(map2);
                            }

                                //enfin vider le EditText de taper message
                                taperMessage.setText("");

                            }










            }else{



                    AlertDialog.Builder dialog = new AlertDialog.Builder(MessageActivity.this);
                    dialog.setTitle("  Impossible d'envoyer");
                    dialog.setIcon(R.drawable.invalide_icon);
                    dialog.setMessage("  Vérifier votre connexion internet Svp");
                    dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();


                        }
                    });


                    dialog.show();

                }

            }
        });



        new Thread(new ConnectionVerif()).start();


    }










    @Override
    public void onBackPressed() {

        if(emoticonsShow)
            hideEmojiPopUp();
        else
            super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);



        getMenuInflater().inflate(R.menu.menu_chat,menu);


        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()){

            case android.R.id.home :
                MessageActivity.this.finish();
                return true;

            case R.id.supprimer :

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {


                    final AlertDialog.Builder dialog = new AlertDialog.Builder(MessageActivity.this);
                    dialog.setTitle("Supprimer la conversation");
                    dialog.setMessage("Voulez vous vraiment supprimer la conversation");
                    dialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    dialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            DatabaseReference tableMessages = FirebaseDatabase.getInstance().getReference("messages/messages de membre " + idMembreActuel);
                            tableMessages.child("avec membre " + idMembreConversation).removeValue();

                            SQLiteDatabase database = new MessageDAO(MessageActivity.this).open();
                            database.execSQL("DELETE FROM "+MessageDAO.TABLE_NAME+" WHERE avec="+idMembreConversation);
                            database.close();

                            dialogInterface.dismiss();
                            MessageActivity.this.finish();
                        }
                    });

                    dialog.show();
                }else{


                    AlertDialog.Builder dialog = new AlertDialog.Builder(MessageActivity.this);
                    dialog.setTitle("  Impossible de supprimer");
                    dialog.setIcon(R.drawable.invalide_icon);
                    dialog.setMessage("  Vérifier votre connexion internet Svp");
                    dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                });
                dialog.show();

                }
                return true;


            default:return super.onOptionsItemSelected(item);
        }
    }






    //-----------------------------------------------methode utiles

    private void afficheMsgFromSQLite(){

        premierMsgAafficher = true;
        dateCompare = null;
        layoutMessages.removeAllViews();
        listMsg = new ArrayList<Message>();
        time1Compare = null;
        time2Compare = null;
        etat = 0;
        msgEnvoyerIsLast = false;

        SQLiteDatabase database = new MessageDAO(MessageActivity.this).open();
        Cursor cursor =  database.rawQuery("SELECT * FROM "+ MessageDAO.TABLE_NAME+" WHERE avec="+idMembreConversation,new String[]{});

        while (cursor.moveToNext()){

            long id = cursor.getLong(0);
            String date = cursor.getString(1);
            String texte = cursor.getString(2);
            long source = cursor.getLong(4);
            long dest = cursor.getLong(5);
            //Log.e("hhh",cursor.getString(6));
            boolean vu = true;
            //String nom = cursor.getString(7);
            //String prenom = cursor.getString(8);
            //String email = cursor.getString(9);
            //String tel = cursor.getString(10);

            Message message = new Message(id,date,texte,source,dest,vu);
            listMsg.add(message);

        }


            afficherMsgs();


        database.close();

    }





    private void afficheMsgFromFirebase() {





        time1Compare = null;
        time2Compare = null;
        etat = 0;
        msgEnvoyerIsLast = false;


        tableMessages.addChildEventListener(addedMsgsListener);


    }


    @Override
    protected void onPause() {
        super.onPause();

        tableMessages.removeEventListener(addedMsgsListener);

    }

    @Override
    protected void onStop() {
        super.onStop();

        tableMessages.removeEventListener(addedMsgsListener);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();






    }





    class AddedMsgsListener implements ChildEventListener{


        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Message m = null;


            Iterator i = dataSnapshot.getChildren().iterator();

            while(i.hasNext()){

                String date = (String) ((DataSnapshot) i.next()).getValue();
                long destination = (long) ((DataSnapshot) i.next()).getValue();
                long id = (long) ((DataSnapshot) i.next()).getValue();
                long source = (long) ((DataSnapshot) i.next()).getValue();
                String texte = (String) ((DataSnapshot) i.next()).getValue();
                boolean vu = (boolean) ((DataSnapshot) i.next()).getValue();

                m = new Message(id,date,texte,source,destination,vu);



                afficherOneMsg(m);
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
















/////////partie Vu des messages///////////

    private void setMessagesVu(){


        msgVuListener = new MsgVuListener(tableMessagesAutre);

        tableMessagesAutre.addValueEventListener(msgVuListener);



    }


class MsgVuListener implements ValueEventListener{

    private DatabaseReference tableMessagesAutre = null;

    public MsgVuListener(DatabaseReference tableMessagesAutre){this.tableMessagesAutre = tableMessagesAutre;}

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        for(DataSnapshot val : dataSnapshot.getChildren()){


            if(setAllVu) {
                Message m = (Message) val.getValue(Message.class);

                m.setVu(true);

                tableMessagesAutre.child(m.getId() + "").setValue(m);
            }

        }


        setAllVu  = false;

        tableMessagesAutre.removeEventListener(msgVuListener);

    }
    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}



//////////
    //////////////
    //////////////////////////////






    private String correctNumber(int s){

        if(s < 10)
            return "0"+s;
        else
            return ""+s;
    }

    private String reglerDate(String date){

        int day = Integer.parseInt(date.split("/")[0]); String dayString = correctNumber(day);
        int month = Integer.parseInt(date.split("/")[1]); String monthString = correctNumber(month);
        String yearString = date.split("/")[2];



        return dayString+"/"+monthString+"/"+yearString;
    }



        private boolean biggerDate(String date1,String date2){

            int day1 = Integer.parseInt(date1.split("/")[0]);
            int day2 = Integer.parseInt(date2.split("/")[0]);

            int month1 = Integer.parseInt(date1.split("/")[1]);
            int month2 = Integer.parseInt(date2.split("/")[1]);

            int year1 = Integer.parseInt(date1.split("/")[2]);
            int year2 = Integer.parseInt(date2.split("/")[2]);


            Date d1 = new Date(year1,month1,day1);
            Date d2 = new Date(year2,month2,day2);


            if(d1.compareTo(d2) > 0)
                return true;
            else
                return false;


        }


    private long recupererIdFromFichierConecter(){

        String result = "";
        try {
            FileReader fr = new FileReader(compteConnecter);
            BufferedReader br = new BufferedReader(fr);

            result = br.readLine();

            br.close(); fr.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



        return Long.parseLong(result);
    }




    private void afficherOneMsg(final Message m){




        new Handler().post(new Runnable() {
            @Override
            public void run() {
                //affichage de la date
                String dateOnly = m.getDate().trim().split("à")[0].trim();
                dateOnly = reglerDate(dateOnly);
                String timeOnly = m.getDate().trim().split("à")[1].trim();


                TextView dateView = new TextView(MessageActivity.this);

                //param date
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;

                dateView.setLayoutParams(layoutParams);
                dateView.setGravity(Gravity.CENTER);
                dateView.setTextColor(Color.BLACK);


                //c'est le premier msg
                if (premierMsgAafficher) {

                    dateView.setText(dateOnly);
                    layoutMessages.addView(dateView);
                    dateCompare = dateOnly;
                    premierMsgAafficher = false;


                } else {


                    if (biggerDate(dateOnly, dateCompare)) {


                        dateView.setText(dateOnly);
                        layoutMessages.addView(new View(MessageActivity.this), LinearLayout.LayoutParams.MATCH_PARENT, 20);
                        dateCompare = dateOnly;
                        layoutMessages.addView(new View(MessageActivity.this), LinearLayout.LayoutParams.MATCH_PARENT, 20);
                        layoutMessages.addView(dateView);

                    }


                }


                if (m.getSource() == idMembreActuel) {


                    MessageEnvoyerView messageEnvoyerView = null;


                    if (time1Compare == null) {
                        messageEnvoyerView = new MessageEnvoyerView(MessageActivity.this, m.getTexte(), m.getDate(), true, m.isVu());

                    } else {


                        if (time1Compare.equals(timeOnly)) {
                            messageEnvoyerView = new MessageEnvoyerView(MessageActivity.this, m.getTexte(), m.getDate(), false, m.isVu());
                        } else {
                            messageEnvoyerView = new MessageEnvoyerView(MessageActivity.this, m.getTexte(), m.getDate(), true, m.isVu());
                        }


                    }


                    time1Compare = timeOnly;


                    if (etat == 0 || etat == 2)
                        layoutMessages.addView(new View(MessageActivity.this), LinearLayout.LayoutParams.MATCH_PARENT, 30);
                    else
                        layoutMessages.addView(new View(MessageActivity.this), LinearLayout.LayoutParams.MATCH_PARENT, 2);


                    etat = 1;

                    layoutMessages.addView(messageEnvoyerView);
                    dernierMsgIsMsgRecu = false;
                    msgEnvoyer = messageEnvoyerView;
                    msgEnvoyerIsLast = true;

                } else {




                    //si le dernier message ce n'est pas de membre actuel on n'affiche pas la photo
                    MessageRecuView messageRecuView = null;



                    if (time2Compare == null) {
                        messageRecuView = new MessageRecuView(MessageActivity.this, photoCoversation, m.getTexte(), m.getDate(), true);

                    } else {


                        if (time2Compare.equals(timeOnly))
                            messageRecuView = new MessageRecuView(MessageActivity.this, photoCoversation, m.getTexte(), m.getDate(), false);
                        else
                            messageRecuView = new MessageRecuView(MessageActivity.this, photoCoversation, m.getTexte(), m.getDate(), true);


                    }

                    time2Compare = timeOnly;


                    if (etat == 0 || etat == 1)
                        layoutMessages.addView(new View(MessageActivity.this), LinearLayout.LayoutParams.MATCH_PARENT, 30);
                    else
                        layoutMessages.addView(new View(MessageActivity.this), LinearLayout.LayoutParams.MATCH_PARENT, 2);

                    etat = 2;
                    layoutMessages.addView(messageRecuView);
                    dernierMsgIsMsgRecu = true;

                    msgVu = m;
                    msgEnvoyerIsLast = false;

                }


                //si le dernier message afficher est notre msg et il est vu on doit afficher Vu
                if (msgEnvoyerIsLast) {
                    if (msgEnvoyer != null)
                        msgEnvoyer.showVu();
                }


                //scroll to bottom
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });


        }






    private boolean contient(ArrayList<Message> messages, Message message){

        boolean ok = false;
        for(Message m : messages){

            if(m.egale(message)){ok = true; break;}

        }

        return ok;
    }



    private void afficherMsgs(){



        for (Message m : listMsg) {



            if(!messageDejaAffiche.contains(m)) {

                messageDejaAffiche.add(m);
                afficherOneMsg(m);
            }
        }



        //si le dernier message afficher est notre msg et il est vu on doit afficher Vu
        if (msgEnvoyerIsLast) {
            if (msgEnvoyer != null)
                msgEnvoyer.showVu();
        }

        //scroll to bottom
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });


    }






    /*-----------------------------------------------Emojicons----------------------------------*/
    private void setEmojiconFragment(boolean useSystemDefault) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons_msg, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }
    // Hiding the FrameLayout containing the list of Emoticons
    public void hideEmojiPopUp() {
        emoticonsShow = false;
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.emojicons_msg);
        frameLayout.setVisibility(View.GONE);
    }

    //Show the soft keyboard
    public void showKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        hideEmojiPopUp();
    }
    // Hiding the keyboard
    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void showEmojiPopUp() {
        emoticonsShow = true;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int deviceHeight = size.y;
        Log.e("Device Height", String.valueOf(deviceHeight));
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.emojicons_msg);
        frameLayout.getLayoutParams().height = (int) (deviceHeight / 2.5); // Setting the height of FrameLayout
        frameLayout.requestLayout();
        frameLayout.setVisibility(View.VISIBLE);

    }
    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(taperMessage, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(taperMessage);
    }










    class ConnectionVerif implements Runnable{


        @Override
        public void run() {



            while (true){

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();


                if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED){

                    if(refrech){
                        premierMsgAafficher = true;
                        dateCompare = null;
                        afficheMsgFromFirebase();
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












}



