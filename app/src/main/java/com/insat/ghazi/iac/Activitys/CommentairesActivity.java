package com.insat.ghazi.iac.Activitys;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.insat.ghazi.iac.AsynckTasks.AjouterCommentaireTask;
import com.insat.ghazi.iac.Commentaire;
import com.insat.ghazi.iac.Membre;
import com.insat.ghazi.iac.R;
import com.insat.ghazi.iac.ViewWithJava.CommentaireView;
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
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

public class CommentairesActivity extends AppCompatActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener{




    private DatabaseReference tableCommentaire = null;
    private ChildEventListener commListener;
    private DatabaseReference tableMembre = null;
    private GetCommentairesVuesListner getCommentairesVuesListner;
    private ArrayList<Commentaire> listeCommentaires = null;


    private StorageReference reference = null;
    private StorageReference pathPhotoProfil = null;



    private ImageView imageEvent = null;
    private LinearLayout commentairesLayout = null;
    private String pathImageEvent = null;
    private int eventId = -1;
    private String eventTitle = null;
    private com.rockerhieu.emojicon.EmojiconEditText taperCommentaire = null;
    private ImageButton envoieCommentaire = null;
    boolean commentaireAjouter = false;



    private File compteConnecter = null;
    private String emailMembreActuel = null;
    private ImageView emoticons = null;
    private boolean emoticonsShow = false;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentaires);



         /*--------------------------- Les fichiers de l'application------------------------------------------*/

        String rootPath = getFilesDir().getAbsolutePath();

        compteConnecter = new File(rootPath+"/compte.txt");

          /*---------------------------------------------------------------------------------------------------*/




        //lose focus commentaire
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);



        imageEvent = (ImageView) findViewById(R.id.event_image_commentaires);
        commentairesLayout = (LinearLayout) findViewById(R.id.layout_commentaires);

        emoticons = (ImageView) findViewById(R.id.emoticons_comm);
        taperCommentaire = (com.rockerhieu.emojicon.EmojiconEditText) findViewById(R.id.taper_commentaire);
        envoieCommentaire = (ImageButton) findViewById(R.id.envoie_commentaire);
        envoieCommentaire.setOnClickListener(new EnvoieCommentaireListener());


        Intent intent = getIntent();
        pathImageEvent = intent.getStringExtra("pathImageEvent");
        eventId = intent.getIntExtra("eventId",-1);
        eventTitle = intent.getStringExtra("eventTitle");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(eventTitle.length() < 30)
        getSupportActionBar().setTitle(eventTitle);
        else
        getSupportActionBar().setTitle(eventTitle.substring(0,29)+"...");






        Bitmap bitmap = BitmapFactory.decodeFile(pathImageEvent);
        imageEvent.setImageBitmap(bitmap);


        tableCommentaire = FirebaseDatabase.getInstance().getReference("commentaire");
        tableMembre = FirebaseDatabase.getInstance().getReference("membre");

        reference = FirebaseStorage.getInstance().getReference();

        emailMembreActuel = recupererEmailFromFichierConecter();



        taperCommentaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideEmojiPopUp();
                showKeyboard(taperCommentaire);
            }
        });


        emoticons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                showEmojiPopUp();
            }
        });

        setEmojiconFragment(false);


    }


    @Override
    protected void onStop() {
        super.onStop();
        if(tableCommentaire != null)
        tableCommentaire.removeEventListener(commListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(tableCommentaire != null)
            tableCommentaire.removeEventListener(commListener);
    }

    @Override
    protected void onStart() {
        super.onStart();




        //on récupére les commentaires depuis firebase en temps réel
        commListener = tableCommentaire.orderByChild("idEvenement").equalTo(eventId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {



                Commentaire c = null;

                Iterator i = dataSnapshot.getChildren().iterator();


                while(i.hasNext()){

                    String date = (String) ((DataSnapshot) i.next()).getValue();
                    String email = (String) ((DataSnapshot) i.next()).getValue();
                    long id = (long) ((DataSnapshot) i.next()).getValue();
                    long evnt = (long) ((DataSnapshot) i.next()).getValue();
                    String texte = (String) ((DataSnapshot) i.next()).getValue();

                    c = new Commentaire(id,date,texte,email,(int)evnt);




                }


                    /*-----------------jointure------------------*/
                //pour chaque commentaire on cherche son propriétre (le membre qui a écrit ce commentaire)
                getCommentairesVuesListner = new GetCommentairesVuesListner(c,"add");
                Log.e("hhhhhhhhhhhhhhh1",c.getTexte()+"///"+c.getEmailMembre());
                tableMembre.orderByChild("email").equalTo(c.getEmailMembre()).addValueEventListener(getCommentairesVuesListner);


                }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


                Commentaire c = null;

                Iterator i = dataSnapshot.getChildren().iterator();


                while(i.hasNext()){

                    String date = (String) ((DataSnapshot) i.next()).getValue();
                    String email = (String) ((DataSnapshot) i.next()).getValue();
                    long id = (long) ((DataSnapshot) i.next()).getValue();
                    long evnt = (long) ((DataSnapshot) i.next()).getValue();
                    String texte = (String) ((DataSnapshot) i.next()).getValue();

                    c = new Commentaire(id,date,texte,email,(int)evnt);



                }

                                        /*-----------------jointure------------------*/
                //pour chaque commentaire on cherche son propriétre (le membre qui a écrit ce commentaire)
                getCommentairesVuesListner = new GetCommentairesVuesListner(c,"change");
                tableMembre.orderByChild("email").equalTo(c.getEmailMembre()).addValueEventListener(getCommentairesVuesListner);


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                Commentaire c = null;

                Iterator i = dataSnapshot.getChildren().iterator();


                while(i.hasNext()){

                    String date = (String) ((DataSnapshot) i.next()).getValue();
                    String email = (String) ((DataSnapshot) i.next()).getValue();
                    long id = (long) ((DataSnapshot) i.next()).getValue();
                    long evnt = (long) ((DataSnapshot) i.next()).getValue();
                    String texte = (String) ((DataSnapshot) i.next()).getValue();

                    c = new Commentaire(id,date,texte,email,(int)evnt);



                }


                    /*-----------------jointure------------------*/
                //pour chaque commentaire on cherche son propriétre (le membre qui a écrit ce commentaire)
                getCommentairesVuesListner = new GetCommentairesVuesListner(c,"remove");
                tableMembre.orderByChild("email").equalTo(c.getEmailMembre()).addValueEventListener(getCommentairesVuesListner);


            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }


    @Override
    public void onBackPressed() {

        if(emoticonsShow)
            hideEmojiPopUp();
        else
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()){

            case android.R.id.home :
                CommentairesActivity.this.finish();
                return true;


            default:return super.onOptionsItemSelected(item);
        }
}






    //-----------------------------------------------methode utiles
    private String recupererEmailFromFichierConecter(){

        String result = "";
        try {
            FileReader fr = new FileReader(compteConnecter);
            BufferedReader br = new BufferedReader(fr);

            br.readLine();
            result = br.readLine();

            br.close(); fr.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



        return result;
    }








    /*-----------------------------------------------Emojicons----------------------------------*/
    private void setEmojiconFragment(boolean useSystemDefault) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons_comm, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }
    // Hiding the FrameLayout containing the list of Emoticons
    public void hideEmojiPopUp() {
        emoticonsShow = false;
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.emojicons_comm);
        frameLayout.setVisibility(View.GONE);
    }

    //Show the soft keyboard
    public void showKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        hideEmojiPopUp();
        //setHeightOfEmojiEditText();
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
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.emojicons_comm);
        frameLayout.getLayoutParams().height = (int) (deviceHeight / 2.5); // Setting the height of FrameLayout
        frameLayout.requestLayout();
        frameLayout.setVisibility(View.VISIBLE);

    }
    @Override
    public void onEmojiconClicked(Emojicon emojicon) {

        EmojiconsFragment.input(taperCommentaire, emojicon);



    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {

        EmojiconsFragment.backspace(taperCommentaire);


    }





















    //Listeners classes

    class EnvoieCommentaireListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            if(!taperCommentaire.getText().toString().equals("")){

            final DatabaseReference tableCommentaire = FirebaseDatabase.getInstance().getReference("commentaire");


            //Avoir combient de commentaire
            commentaireAjouter = false;
            tableCommentaire.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!commentaireAjouter) {

                        long idCommentaire = 0;

                        for (DataSnapshot val : dataSnapshot.getChildren()) {
                            idCommentaire = ((Commentaire) val.getValue(Commentaire.class)).getId();
                        }

                        Date date = new Date();
                        String jour = date.getDate() + "";
                        String mois = (date.getMonth() + 1) + "";
                        String annee = (date.getYear() + 1900) + "";
                        String heure = date.getHours() + "";
                        String minute = date.getMinutes() + "";
                        String dateComplet = jour + "/" + mois + "/" + annee + " à " + heure + ":" + minute;

                        Commentaire commentaire = new Commentaire(idCommentaire + 1, dateComplet, taperCommentaire.getText().toString(), emailMembreActuel, eventId);

                        tableCommentaire.child(commentaire.getId() + "").setValue(commentaire);


                        taperCommentaire.setText("");

                        InputMethodManager inputManager =
                                (InputMethodManager) CommentairesActivity.this.
                                        getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(
                                taperCommentaire.getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);

                        hideEmojiPopUp();

                        commentaireAjouter = true;

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
        }
    }










    class GetCommentairesVuesListner implements ValueEventListener{

        private Commentaire commentaire = null;
        private String state;

        public GetCommentairesVuesListner(Commentaire commentaire,String state){ this.commentaire = commentaire; Log.e("hhhhhhhhhhh:'(",this.commentaire.getTexte()); this.state = state;}



        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {


            for(DataSnapshot val : dataSnapshot.getChildren()){

                Membre membre = (Membre) val.getValue(Membre.class);


                //on récupére l'image du membre
                pathPhotoProfil = reference.child("photo_profil/"+membre.getPhoto()+".jpg");



                //savoir si le membre actuel est le propriétaire du commentaire
                boolean prop = false;
                if(emailMembreActuel.equals(membre.getEmail()))
                    prop = true;



                String nomPrenom = membre.getPrenom()+" "+membre.getNom();
                Log.e("hhhhhhhhhhhh555",commentaire.getTexte()+"////////"+membre.getEmail());
                AjouterCommentaireTask task = new AjouterCommentaireTask(CommentairesActivity.this,commentaire.getId(),eventId,membre.getEmail(),pathPhotoProfil,commentairesLayout,nomPrenom,commentaire.getTexte(),commentaire.getDate(),prop,state);

                task.execute();

                tableMembre.removeEventListener(getCommentairesVuesListner);

            }



        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
        }


    }






}
