package com.insat.ghazi.iac.Activitys;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.insat.ghazi.iac.Membre;
import com.insat.ghazi.iac.R;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class InscriptionActivity extends AppCompatActivity{


    private final int INTENT_IMAGE_PICKER = 1;


    private ScrollView scrollView = null;

    private Button valider = null;
    private Button annuler = null;
    private AutoCompleteTextView email = null;
    private EditText password = null;
    private EditText nom = null;
    private EditText prenom = null;
    private EditText tel = null;

    private CheckBox insatien = null;
    private LinearLayout listes = null;
    private Spinner filliere = null;
    private Spinner niveau = null;



    private Button choisirPhotoProfil = null;
    private de.hdodenhof.circleimageview.CircleImageView photoProfil = null;
    private Bitmap photoProfilBitmap = null;
    private InputStream photoProfilInputStream = null;



    private String erreur = "";


    private File repPhotoProfil = null;
    private File compteConnecter = null;


    private Membre membre = null;
    private boolean emailDejaExiste = false;
    private boolean telDejaExiste = false;
    private DatabaseReference tableMembreFirebase = null;
    private ProgressDialog progressDialog = null;
    private Query queryTell = null;
    private Query queryEmail = null;
    private EmailGetValueListener emailGetValueListener = null;
    private TelGetValueListener telGetValueListener = null;
    private  AlertDialog dialog = null;
    private boolean inscriptionReussite = false;



    private String[] f1 = {"Filliére","MPI","CBA","CH","RT","GL","BIO","IMI","IIA"};





    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        getSupportActionBar().setTitle("Inscription");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        scrollView = (ScrollView) findViewById(R.id.login_scroll);

        valider = (Button) findViewById(R.id.inscrireTerminer);
        annuler = (Button) findViewById(R.id.annulerInscrire);

        email = (AutoCompleteTextView) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        nom = (EditText) findViewById(R.id.nom);
        prenom = (EditText) findViewById(R.id.prenom);
        tel = (EditText) findViewById(R.id.tel);

        insatien = (CheckBox) findViewById(R.id.insatien);
        listes = (LinearLayout) findViewById(R.id.les_listes);
        filliere = (Spinner) findViewById(R.id.filliere);
        niveau = (Spinner) findViewById(R.id.niveau);


        photoProfil = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.photo_profil_inscription);
        choisirPhotoProfil = (Button) findViewById(R.id.button_choisirPhotoProfil_inscription);







         /*--------------------------- Les fichiers de l'application------------------------------------------*/

        String rootPath = getFilesDir().getAbsolutePath();

         repPhotoProfil = new File(rootPath+"/photo_profil");
         compteConnecter = new File(rootPath+"/compte.txt");

          /*---------------------------------------------------------------------------------------------------*/






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



        //clique sur le bouton choisir photo de profil
        choisirPhotoProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(InscriptionActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(InscriptionActivity.this,"Il faut autoriser la lecture des fichiers dans les paramétres de l'application",Toast.LENGTH_LONG).show();
                }else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(Intent.createChooser(intent, "Choisir une photo de profil"), INTENT_IMAGE_PICKER);
                }

            }
        });




        ///clique sur le bouton annuler
        annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                annulerTous();
            }
        });





/// clique sur le bouton s'inscrire
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String emailString = email.getText().toString();
                String passwordString = password.getText().toString();
                String nomString = nom.getText().toString();
                String prenomString = prenom.getText().toString();
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
                if(emailValid(emailString) && passwordValide(passwordString) && nomOuPrenomValide(nomString) && nomOuPrenomValide(prenomString) && telValide(telString) && filliereEtNiveauValide()) {





                    //enfin verification de la cnx pour s'inscrir
                    ConnectivityManager manager = (ConnectivityManager) InscriptionActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = manager.getActiveNetworkInfo();

                    if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        progressDialog = ProgressDialog.show(InscriptionActivity.this, "", "En cours...");


                        String passwordCyrpte = Base64.encodeToString(passwordString.getBytes(),Base64.DEFAULT);

                        //creation du nouveau membre
                        membre = new Membre(emailString, passwordCyrpte, nomString, prenomString, telString, insatien.isChecked(), filliereString, niveauString, true, "rien", 1, 0,false);


                        //Verifier si le compte existe déja (tel et email doivent ètre uniques) sinon créer le nouveau membre
                        tableMembreFirebase = FirebaseDatabase.getInstance().getReference("membre");

                        emailGetValueListener = new EmailGetValueListener();
                        telGetValueListener = new TelGetValueListener();
                        queryTell = tableMembreFirebase.orderByChild("tel").equalTo(membre.getTel());
                        queryTell.addValueEventListener(telGetValueListener);
                    }else{  // pas de cnx

                         AlertDialog.Builder dialog = new AlertDialog.Builder(InscriptionActivity.this);
                        dialog.setTitle("  Impossible de s'inscrire");
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







                }else{


                    //En cas d'erreur
                    AlertDialog.Builder dialog = new AlertDialog.Builder(InscriptionActivity.this);
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












    /*----------------------Methode utile------------------------------------------------------------------------------------*/


    private void annulerTous(){

        email.setText("");
        password.setText("");
        nom.setText("");
        prenom.setText("");
        tel.setText("");
        insatien.setChecked(false);
        filliere.setSelection(0);
        niveau.setSelection(0);
        photoProfil.setImageResource(R.drawable.anonyme);
        scrollView.fullScroll(ScrollView.FOCUS_UP);

    }


    //enregistrer les données du compte connécter actuellement
    private void enregistrerDonneesCompteConnecte(long id){

        try {
            FileWriter fw = new FileWriter(compteConnecter, false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(id+"");
            bw.newLine();
            bw.write(membre.getEmail());



            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }





    //enregistrer la photo de profil recupérer dans un emplacement (path)
    private void enregistrerPhotoProfilToPath(String path){

        try {

            FileOutputStream fichierImage = new FileOutputStream(path);


            if(photoProfilBitmap == null)
                photoProfilBitmap = BitmapFactory.decodeResource(InscriptionActivity.this.getResources(),R.drawable.anonyme);


            photoProfilBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fichierImage);
            fichierImage.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }






    //enregistrer photo de profil dans firebase
    private void enregisterPhotoProfilInFirebase(String path){


            StorageReference rootRef = FirebaseStorage.getInstance().getReference();
            StorageReference photoProfilReference = rootRef.child("photo_profil/"+membre.getId()+".jpg");


            File photoF = new File(path);


                BitmapFactory.Options bmpFactory = new BitmapFactory.Options();
                bmpFactory.inSampleSize = 3;

            Bitmap bitmap = BitmapFactory.decodeFile(photoF.getAbsolutePath(),bmpFactory);


            UploadTask uploadTask = photoProfilReference.putBytes(bitmapCompressed(bitmap));
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.e("NoNEnvoyer",e.getMessage());

                }
            });








    }






    //compter l'occurence d'un char dans un string
    private int calculCaractereDansUnString(char c,String s){
        int nbr = 0;


        for(int i=0;i<s.length();i++){

            if(s.charAt(i) == c)
                nbr++;


        }

        return nbr;
    }



    //savoir si un string contient au moins un nombre
    private boolean stringContientUnNombre(String s){
        boolean ok = false;

        for(int i=0;i<s.length();i++){

            char c = s.charAt(i);

            if(c == '0' || c == '1' || c == '2' || c == '3' || c == '4'|| c == '5' || c == '6'|| c == '7' || c == '8'|| c == '9'){
                ok = true; break;
            }

        }

        return ok;
    }
    /*---------------------------------------------------------------------------------------------------------------------*/














    /*--------------------------Les methodes de verification de validité des inputs------------------------*/


                        //verification email
    private boolean emailValid(String email){

        boolean ok = false;


      //il faut que email contient un seul "@"
      if(calculCaractereDansUnString('@',email) == 1 && email.charAt(0) != '@' && email.charAt(email.length()-1) != '@'){

          //on coupe l'email en deux morceaux  m1@m2
          String[] morceaux = email.split("@");
         if(morceaux[0].length() > 0 && morceaux[1].length() >= 3){

             //il faut que morceaux 2 contient un seul "."
             if(calculCaractereDansUnString('.',morceaux[1]) == 1 && morceaux[1].charAt(0) != '.' && morceaux[1].charAt(morceaux[1].length()-1) != '.'){

                 ok = true;

             }


         }




      }



        //donner l'erreur si l'email est invalide
        if(!ok)
         erreur += "Votre email est invalide";


        return ok;
    }



//Verification password
    private boolean passwordValide(String password){
        boolean ok = false;

        if(password.length() >= 8 && stringContientUnNombre(password)){
            ok = true;
        }


        if(!ok)
         erreur += "Password doit contenir au moins 8 caractéres et un nombre";
        return ok;
    }


    //verification nom ou prenom
    private boolean nomOuPrenomValide(String s){
        boolean ok = false;

        if(s.length() >= 2)
            ok = true;

        if(!ok)
          erreur += "Le nom et le prénom doivent contenir au moins 2 caractéres";

        return ok;
    }




    //verification numéro telephone
    private boolean telValide(String tel){
        boolean ok = false;

        if(tel.length() == 8)
            ok = true;

        if(!ok)
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






    private byte[] bitmapCompressed(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOS);
        byte[] byteArray = byteArrayOS.toByteArray();
        return byteArray;
    }

     /*----------------------------------------------------------------------------------------------------*/









    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(data != null){

        if(requestCode == INTENT_IMAGE_PICKER && resultCode == RESULT_OK ){



            Uri imageUri = data.getData();


            InputStream imageStream = null;


            try {
                imageStream = getContentResolver().openInputStream(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            BitmapFactory.Options bmpFactory = new BitmapFactory.Options();
            bmpFactory.inSampleSize = 3;




                photoProfilBitmap = BitmapFactory.decodeStream(imageStream,null,bmpFactory);
                photoProfil.setImageBitmap(photoProfilBitmap);




        }

        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()){

            case android.R.id.home :
                InscriptionActivity.this.finish();
                return true;



            default:return super.onOptionsItemSelected(item);
        }


    }













//----------------------------------------------------------------------Listners



         class TelGetValueListener implements ValueEventListener{
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






    class EmailGetValueListener implements ValueEventListener{
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for(DataSnapshot val : dataSnapshot.getChildren()) {
            emailDejaExiste = true;
            break;
        }

        if((telDejaExiste || emailDejaExiste) && !inscriptionReussite){    //compte existe


            progressDialog.dismiss();


            dialog = new AlertDialog.Builder(InscriptionActivity.this).create();
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


        //avant tout on doit savoir combient de user y'ont a
        tableMembreFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (!inscriptionReussite){


                    Membre m = null;
                    for(DataSnapshot val : dataSnapshot.getChildren()){ m = val.getValue(Membre.class);}


                    long nbrUsers = 0;
                    if(m != null)
                        nbrUsers = m.getId();


                membre.setId(nbrUsers + 1);membre.setPhoto(nbrUsers+1);


                //remplir le fichier du compte connecter
                enregistrerDonneesCompteConnecte(membre.getId());





         /*---------envoyer les données dans SQLLite databse---------------------*/

                //enregistrer la photo de profil dans le terminal
                enregistrerPhotoProfilToPath(repPhotoProfil.getAbsolutePath() + "/" + membre.getId() + ".jpg");

                membre.enregistrerDansSQLiteDatabase(InscriptionActivity.this);







                    /*----------envoyer les données à Firebase-----------------------------*/

                //enregistrer la photo de profil dans firebase
                enregisterPhotoProfilInFirebase(repPhotoProfil.getAbsolutePath() + "/" + membre.getId() + ".jpg");

                tableMembreFirebase.child(membre.getId() + "").setValue(membre);
                  /*--------------------------------------------------------------------*/


                //Enfin lancer les Evenements dans (MainInConnection)
                progressDialog.dismiss();



                inscriptionReussite = true;
                Intent intent = new Intent(InscriptionActivity.this, MainInConnectionActivity.class);
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
        if(queryEmail != null){
        queryEmail.removeEventListener(emailGetValueListener);
        queryTell.removeEventListener(telGetValueListener);
    }}





}

