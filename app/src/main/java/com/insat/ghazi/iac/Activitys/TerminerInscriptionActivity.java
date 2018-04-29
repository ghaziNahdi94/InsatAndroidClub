package com.insat.ghazi.iac.Activitys;

import android.app.ProgressDialog;
import android.app.usage.NetworkStatsManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.insat.ghazi.iac.Membre;
import com.insat.ghazi.iac.R;
import com.insat.ghazi.iac.SQLiteDatabase.MemberDAO;
import com.insat.ghazi.iac.SQLiteDatabase.MessageDAO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


///Cette activity c'est pour terminer l'inscription lorsqu'on a fait l'inscription avec google ou facebook
public class TerminerInscriptionActivity extends AppCompatActivity {



    private String photoURL = null;



    private EditText tel = null;
    private CheckBox insatien = null;
    private Spinner filliere = null;
    private Spinner niveau = null;
    private LinearLayout listes = null;
    private Button terminer = null;
    private Button annuler = null;


    private File compteConnecter = null;



    private Membre membre = null;
    private boolean emailDejaExiste = false;
    private boolean telDejaExiste = false;
    private DatabaseReference tableMembreFirebase = null;
    private ProgressDialog progressDialog = null;
    private Query queryTell = null;
    private Query queryEmail = null;
    private EmailGetValueListenerTerminer emailGetValueListener = null;
    private TelGetValueListenerTerminer telGetValueListener = null;
    private  AlertDialog dialog = null;


    private String[] f1 = {"Filliére","MPI","CBA","CH","RT","GL","BIO","IMI","IIA"};


    private String erreur = null;

    private boolean inscriptionReussite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminer_inscription);
        getSupportActionBar().setTitle("Terminer inscription");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



         /*--------------------------- Les fichiers de l'application------------------------------------------*/

        String rootPath = getFilesDir().getAbsolutePath();

        compteConnecter = new File(rootPath+"/compte.txt");

          /*---------------------------------------------------------------------------------------------------*/


        tel = (EditText) findViewById(R.id.tel_terminerInscription);
        insatien = (CheckBox) findViewById(R.id.insatien_terminerInscription);
        filliere = (Spinner) findViewById(R.id.filliere_terminerInscription);
        niveau = (Spinner) findViewById(R.id.niveau_terminerInscription);
        listes = (LinearLayout) findViewById(R.id.les_listes_inscriptionTerminer);
        terminer = (Button) findViewById(R.id.inscrire_TerminerInscription);
        annuler = (Button) findViewById(R.id.annulerInscrire_terminerInscription);





        Intent intent = getIntent();
        String methodeInscription = intent.getStringExtra("methode");
        photoURL = intent.getStringExtra("userPhoto");

        String membreEmail = intent.getStringExtra("userEmail");
        String membreName = intent.getStringExtra("userName");
        String prenom = membreName.split(" ")[0];
        String nom = membreName.split(" ")[1];


        // verifier que nom et prénom commance par des Majuscule
        StringBuilder prenomBuilder = new StringBuilder(prenom);
        prenomBuilder.setCharAt(0, Character.toUpperCase(prenom.charAt(0)));
        prenom = prenomBuilder.toString();
        StringBuilder nomBuilder = new StringBuilder(nom);
        nomBuilder.setCharAt(0, Character.toUpperCase(nom.charAt(0)));
        nom = nomBuilder.toString();




        membre = new Membre(membreEmail,methodeInscription,nom,prenom,"rien",false,"rien","rien",false,"rien",-1,-1,false);





        //spinners
        niveau.setVisibility(View.INVISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,f1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filliere.setAdapter(adapter);

        filliere.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(i == 0 || i == 1 || i == 2)
                    niveau.setVisibility(View.INVISIBLE);
                else
                    niveau.setVisibility(View.VISIBLE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });






        // NB : si le chekbox "insatien" est coché les listes filliére et niveau apparaissent sinon le contraire ils disparaissent
        insatien.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    filliere.setSelection(0);
                    niveau.setSelection(0);
                    listes.setVisibility(View.VISIBLE);

                }else{

                    listes.setVisibility(View.INVISIBLE);

                }


            }
        });

    }





    @Override
    protected void onStart() {
        super.onStart();



        annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                annulerTous();
            }
        });



        terminer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String telString = tel.getText().toString();
                String filliereString = "rien";
                String niveauString = "rien";
                if(insatien.isChecked()){

                    filliereString = (String) filliere.getSelectedItem();

                    if(niveau.getVisibility() == View.VISIBLE)
                        niveauString = (String) niveau.getSelectedItem();
                    else
                        niveauString = "1";
                }






//controle d'erreur de saisie
                if(telValide(telString) && filliereEtNiveauValide()){



                    //enfin verification de la cnx pour s'inscrir
                    ConnectivityManager manager = (ConnectivityManager) TerminerInscriptionActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = manager.getActiveNetworkInfo();

                    if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        progressDialog = ProgressDialog.show(TerminerInscriptionActivity.this, "", "En cours...");
                        //terminer la creation du nouveau membre
                        membre.setTel(telString);
                        membre.setInsatien(insatien.isChecked());
                        membre.setFilliere(filliereString);
                        membre.setNiveau(niveauString);


                        //Verifier si le compte existe déja (tel et email doivent ètre uniques) sinon créer le nouveau membre
                        tableMembreFirebase = FirebaseDatabase.getInstance().getReference("membre");

                        emailGetValueListener = new EmailGetValueListenerTerminer();
                        telGetValueListener = new TelGetValueListenerTerminer();
                        queryTell = tableMembreFirebase.orderByChild("tel").equalTo(membre.getTel());
                        queryTell.addValueEventListener(telGetValueListener);

                             }else{ // pas de cnx

                        AlertDialog.Builder dialog = new AlertDialog.Builder(TerminerInscriptionActivity.this);
                        dialog.setTitle("  Impossible de s'inscrir");
                        dialog.setIcon(R.drawable.invalide_icon);
                        dialog.setMessage("Vérifier votre connexion internet Svp");
                        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });


                        dialog.show();

                              }


                }else{

                    //En cas d'erreur
                    AlertDialog.Builder dialog = new AlertDialog.Builder(TerminerInscriptionActivity.this);
                    dialog.setTitle("Données invalide");
                    dialog.setIcon(R.drawable.invalide_icon);
                    dialog.setMessage(erreur);

                    dialog.setCancelable(false);

                    dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            erreur = "";
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }


            }
        });




    }






    //---------------verification des données--------------------------------//
    //verification numéro telephone

    private boolean telValide(String tel) {
        boolean ok = false;

        erreur = "";

        if (tel.length() == 8)
            ok = true;

        if (!ok)
            erreur += "Le numéro de téléphone est invalide";

        return ok;
    }


    //verification de la filliére et le niveau
    private boolean filliereEtNiveauValide(){
        boolean ok = false;

        if(insatien.isChecked()){

            if(filliere.getSelectedItemPosition() != 0 && niveau.getVisibility() == View.VISIBLE && niveau.getSelectedItemPosition() != 0){ ok = true; }

            if(filliere.getSelectedItemPosition()!=0 && niveau.getVisibility() == View.INVISIBLE){ok = true;}

        }else{
            ok = true;
        }

        if(!ok)
            erreur += "Veuillez Choisir votre filliére et votre niveau Svp";

        return ok;
    }



    //--------------------methodes utiles---------------------------------------//
    private void annulerTous(){
        tel.setText("");
        insatien.setChecked(false);
        filliere.setSelection(0);
        niveau.setSelection(0);

    }


    //enregistrer les données du compte connécter actuellement
    private void enregistrerDonneesCompteConnecte(Membre membre){



        try {
            FileWriter fw = new FileWriter(compteConnecter, false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(membre.getId()+"");
            bw.newLine();
            bw.write(membre.getEmail());



            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }




//------------------------------------------------------------------------------//






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home :

                Intent intent = new Intent(TerminerInscriptionActivity.this,MainActivity.class);
                startActivity(intent);

                return true;

            default:return super.onOptionsItemSelected(item);
        }



    }






















//-------------------------------------Listeners----------------------------------------------------//




    class TelGetValueListenerTerminer implements ValueEventListener{
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {



            for(DataSnapshot val : dataSnapshot.getChildren()) {
                telDejaExiste = true;
                break;
            }

            //verification email
            queryEmail = tableMembreFirebase.orderByChild("email").equalTo(membre.getEmail());
            queryEmail.addValueEventListener(emailGetValueListener);



        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }







    class EmailGetValueListenerTerminer implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for(DataSnapshot val : dataSnapshot.getChildren()) {
                emailDejaExiste = true;
                break;
            }


            if((telDejaExiste || emailDejaExiste) && !inscriptionReussite){    //compte existe


                progressDialog.dismiss();


                dialog = new AlertDialog.Builder(TerminerInscriptionActivity.this).create();
                dialog.setTitle("Compte existe");
                dialog.setMessage("Email ou Numéro tél est utilisé dans un autre compte");
                dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


                dialog.show();


                telDejaExiste = false;
                emailDejaExiste = false;




            }else {   //compte n'existe pas encore => création du compte


                //avant tout on doit savoir combient de user y'on a
                tableMembreFirebase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        if (!inscriptionReussite){


                            Membre m = null;
                            for(DataSnapshot val : dataSnapshot.getChildren()){ m = val.getValue(Membre.class);}



                            long nbrUsers = 0;
                            if(m != null)
                             nbrUsers = m.getId();




                            membre.setId(nbrUsers + 1); membre.setPhoto(nbrUsers+1);


                            //remplir le fichier du compte connecter
                            enregistrerDonneesCompteConnecte(membre);





         /*---------envoyer les données dans SQLLite databse---------------------*/



                            membre.setConnecter(true);

                            MemberDAO tableMembreSQLite = new MemberDAO(TerminerInscriptionActivity.this);
                            tableMembreSQLite.open();

                            ContentValues values = new ContentValues();
                            values.put(MemberDAO.TEL, membre.getTel());
                            values.put(MemberDAO.EMAIL, membre.getEmail());
                            values.put(MemberDAO.PASSWORD, membre.getPassword());
                            values.put(MemberDAO.NOM, membre.getNom());
                            values.put(MemberDAO.PRENOM, membre.getPrenom());
                            values.put(MemberDAO.INSATIEN, Boolean.toString(membre.isInsatien()));
                            values.put(MemberDAO.FILLIERE, membre.getFilliere());
                            values.put(MemberDAO.NIVEAU, membre.getNiveau());
                            values.put(MemberDAO.CONNECTER, Boolean.toString(membre.isConnecter()));
                            values.put(MemberDAO.POSTE, membre.getPoste());
                            values.put(MemberDAO.PHOTO, membre.getPhoto());
                            values.put(MemberDAO.ID, membre.getId());
                            if(membre.isBlocage())
                            values.put(MemberDAO.BLOCAGE,1);
                            else
                             values.put(MemberDAO.BLOCAGE,0);

                            tableMembreSQLite.getDb().insert(MemberDAO.TABLE_NAME, null, values);

                            tableMembreSQLite.close();






                    /*----------envoyer les données à Firebase-----------------------------*/

                            tableMembreFirebase.child(membre.getId() + "").setValue(membre);
                  /*--------------------------------------------------------------------*/




                            //Enfin lancer les Evenements dans (MainInConnectionActivity)
                            progressDialog.dismiss();


                            inscriptionReussite = true;



                            Intent intent = new Intent(TerminerInscriptionActivity.this, MainInConnectionActivity.class);

                            intent.putExtra("photoURL",photoURL);
                            intent.putExtra("PHOTO",membre.getId());



                            startActivity(intent);

                        }




                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });








            }







        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(queryEmail != null && queryTell != null){
        queryEmail.removeEventListener(emailGetValueListener);
        queryTell.removeEventListener(telGetValueListener);

            if(FirebaseAuth.getInstance() != null)
            FirebaseAuth.getInstance().signOut();


    }}




































}
