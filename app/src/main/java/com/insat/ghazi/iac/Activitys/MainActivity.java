package com.insat.ghazi.iac.Activitys;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.insat.ghazi.iac.Membre;
import com.insat.ghazi.iac.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {



    private Button inscrire = null;
    private Button login = null;



    private FirebaseAuth fAuth = null;
    private FirebaseAuth.AuthStateListener fAuthListener = null;




    private GoogleApiClient gClient = null;


    private CallbackManager callbackManager = null;

    private FirebaseUser user = null;

    private final int GOOGLE = 1;
    private String methodeInscription = "";
    private String methodeCnx = "";
    private Button facebook = null;
    private Button google = null;
    private Button manuelle = null;
    private String photoURL = null;


    private EditText emailOrTel = null;
    private EditText passwordConecter = null;
    private String attributUtiliserEnCnx = null;
    private ProgressDialog progressDialog = null;
    private Membre membre = null;
    private File compteConnecter = null;
    private File repPhotoEvent = null;
    private File repPhotoProfil = null;
    private File repPhotoMessages = null;
    private File repPhotoCouverture = null;
    private File parametres = null;


    private ImageButton facebookLogin = null;
    private ImageButton googleLogin = null;

    private boolean inscription = false;

    private boolean loginTerminer = false;


    boolean entrer = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        getPermissions();


        /*--------------------------- Les fichiers de l'application------------------------------------------*/

        String rootPath = getFilesDir().getAbsolutePath();

        compteConnecter = new File(rootPath+"/compte.txt");
        repPhotoProfil = new File(rootPath+"/photo_profil");
        repPhotoEvent = new File(rootPath+"/photo_events");
        repPhotoMessages = new File(rootPath+"/photo_messages");
        repPhotoCouverture = new File(rootPath+"/photo_couverture");
        parametres = new File(rootPath+"/param.txt");



        if(!repPhotoMessages.exists())
            repPhotoMessages.mkdir();

        if(!compteConnecter.exists())
            try {
                compteConnecter.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        if(!repPhotoProfil.exists())
            repPhotoProfil.mkdir();



        if(!repPhotoEvent.exists())
            repPhotoEvent.mkdir();

        if(!repPhotoCouverture.exists()) {
            repPhotoCouverture.mkdir();


        if(!parametres.exists()) {
            try {
                parametres.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                PrintWriter pw = new PrintWriter(parametres.getAbsolutePath());
                pw.println("1");
                pw.println("1");
                pw.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.bache2015);
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,new FileOutputStream(repPhotoCouverture.getAbsolutePath()+"/1.jpg"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }


         /*---------------------------------------------------------------------------------------------------*/





        //on vérifie si il ya un compte déja connécté pour connécter directement
        if(compteConnecter.length() != 0){


            Intent intent = new Intent(MainActivity.this,MainInConnectionActivity.class);
            startActivity(intent);
        }





        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("");
        View bar = getLayoutInflater().inflate(R.layout.action_bar_home,null);
        TextView barTitle = (TextView) bar.findViewById(R.id.barTitle);
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/RobotoCondensed-Bold.ttf");
        barTitle.setTypeface(myTypeface);
        actionBar.setCustomView(bar,new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,Gravity.CENTER));









        //definir mon firebase auth
        fAuth = FirebaseAuth.getInstance();

        fAuth.signOut();


        //definir listener auth (google+facebook)
        fAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                 user = firebaseAuth.getCurrentUser();

                if (user != null) {


                    //pour connaitre  inscription ou login
                    if (inscription) {


                        if (methodeInscription.equalsIgnoreCase("facebook")) {

                            photoURL = Profile.getCurrentProfile().getProfilePictureUri(200,200).toString();



                        }

                        Intent intent = new Intent(MainActivity.this, TerminerInscriptionActivity.class);
                        intent.putExtra("userName", user.getDisplayName());
                        intent.putExtra("userEmail", user.getEmail());
                        intent.putExtra("userPhoto", photoURL);
                        intent.putExtra("methode", methodeInscription);

                        startActivityForResult(intent, 0);


                    } else { //login (facebook+google)




                        final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "", "En cours...");


                        //on cherche si le compte existe
                        DatabaseReference tableMembre = FirebaseDatabase.getInstance().getReference("membre");
                        tableMembre.orderByChild("email").equalTo(user.getEmail()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {

                                if(!loginTerminer){


                                if (dataSnapshot.getChildrenCount() == 0) {  //compte n'est pas inscrit !!



                                    progressDialog.dismiss();

                                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                                    dialog.setTitle("Le compte n'est pas inscrit");
                                    dialog.setMessage("Voulez vous inscrire avec ce compte ?");
                                    dialog.setNegativeButton("NON", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            LoginManager.getInstance().logOut();
                                            dialog.dismiss();
                                        }
                                    });

                                    dialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {


                                            if (methodeCnx.equals("facebook")) {
                                                photoURL = Profile.getCurrentProfile().getProfilePictureUri(200,200).toString();

                                            }

                                            Intent intent = new Intent(MainActivity.this, TerminerInscriptionActivity.class);
                                            intent.putExtra("userName", user.getDisplayName());
                                            intent.putExtra("userEmail", user.getEmail());
                                            intent.putExtra("userPhoto", photoURL);
                                            intent.putExtra("methode", methodeCnx);
                                            loginTerminer = true;
                                            startActivity(intent);

                                        }
                                    });


                                    dialog.show();


                                } else { // le compte existe


                                    for (DataSnapshot val : dataSnapshot.getChildren()) {

                                        membre = (Membre) val.getValue(Membre.class);


                                    }


                                    //Vérifier si le compte est bloqué ou non


                                    if (membre.isBlocage()) {
                                        Log.e("hhh",membre.isBlocage()+"...");
                                        progressDialog.dismiss();
                                        membreBloquer();


                                    } else {

                                        //sinon
                                        //verifier si le compte est enregistré dans SQLite database sinon on l'enregistre
                                        if (!membre.existInSQLiteDatabase(getApplicationContext())) {

                                            membre.enregistrerDansSQLiteDatabase(getApplicationContext());
                                            photoURL = user.getPhotoUrl().toString();
                                            enregistrerPhotoMembreFromURL(photoURL, membre.getPhoto());


                                        }

                                        enregistrerDonneesCompteConnecte(membre);


                                        Intent intent = new Intent(MainActivity.this, MainInConnectionActivity.class);
                                        intent.putExtra("PHOTO", membre.getPhoto());
                                        loginTerminer = true;
                                        startActivity(intent);
                                        progressDialog.dismiss();
                                    }
                                }

                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }





            }





            }
        };














        inscrire = (Button) findViewById(R.id.inscrireButtonId);
        login = (Button) findViewById(R.id.loginButtonId);



        /*------------------------------------- clique sur inscrire ---------------------------------------------------*/
        inscrire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

                View inscrireView = getLayoutInflater().inflate(R.layout.dialog_inscription,null);



                dialog.setTitle("Inscription");
                dialog.setView(inscrireView);


                dialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


                dialog.show();


                // les boutons du dialog d'inscription
                facebook = (Button) inscrireView.findViewById(R.id.facebook_inscrire);
                google = (Button) inscrireView.findViewById(R.id.google_inscrire);
                manuelle = (Button) inscrireView.findViewById(R.id.manuelle_inscrire);



                //----- clique sur manuelle
                manuelle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this,InscriptionActivity.class);
                        startActivity(intent);
                    }
                });







                //---- clique sur google
                        google.setOnClickListener(new GoogleInscriptionListener());








                //---- clique sur facebook

                FacebookSdk.sdkInitialize(getApplicationContext());
                callbackManager = CallbackManager.Factory.create();
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        firebaseAuthWithFacebook(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.e("hhh","facebook cancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                        dialog.setTitle("  Impossible de s'inscrire");
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
                });


                facebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        methodeInscription = "facebook";
                        inscription =  true;
                        LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("email", "public_profile"));

                    }
                });




            }
        });










        /*------------------------------------- clique sur connecter ---------------------------------------------------*/
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {






                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                View loginView  = getLayoutInflater().inflate(R.layout.dialog_login,null);


                emailOrTel = (EditText) loginView.findViewById(R.id.emailOrTel);
                passwordConecter = (EditText) loginView.findViewById(R.id.passwordConnecter);
                facebookLogin = (ImageButton) loginView.findViewById(R.id.facebook_login);
                googleLogin = (ImageButton) loginView.findViewById(R.id.google_login);



                dialog.setTitle("Authentification");

                dialog.setView(loginView);



                dialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });




                //---------------------------clique sur connecter (manuelle)
                dialog.setPositiveButton("Connecter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {


                        progressDialog = ProgressDialog.show(MainActivity.this, "", "En cours...");


                        //verification de la cnx
                        ConnectivityManager manager = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

                        if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED){
                        //detecter si le membre a utiliser Tel ou email pour connecter
                        attributUtiliserEnCnx = "";
                        String resultat = emailOrTel.getText().toString();
                        if (emailOrTel.getText().toString().contains("@"))
                            attributUtiliserEnCnx = "email";
                        else
                            attributUtiliserEnCnx = "tel";


                        //verifier si le compte existe
                        DatabaseReference tableMembre = FirebaseDatabase.getInstance().getReference("membre");
                        tableMembre.orderByChild(attributUtiliserEnCnx).equalTo(resultat).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(entrer){

                                if (dataSnapshot.getChildrenCount() == 0) {
                                    //compte n'existe pas

                                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                                    dialog.setTitle("Compte n'existe pas");
                                    dialog.setMessage("Email ou Numéro Tel n'appartient pas a un compte existant");
                                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });


                                    progressDialog.dismiss();
                                    dialog.show();

                                } else { // le compte existe


                                    for (DataSnapshot val : dataSnapshot.getChildren()) {

                                        membre = (Membre) val.getValue(Membre.class);

                                    }


                                    if (!membre.getPassword().equals("facebook") && !membre.getPassword().equals("google")) {
                                        String passwordDecrypte = new String(Base64.decode(membre.getPassword().getBytes(),Base64.DEFAULT));

                                        if (passwordDecrypte.equals(passwordConecter.getText().toString())) {


                                            //verification si le membre est bloqué


                                            if(membre.isBlocage()) {

                                                progressDialog.dismiss();
                                                membreBloquer();

                                            }else{

                                                //verifier compte enregistrer dans SQLite database
                                                if (!membre.existInSQLiteDatabase(getApplicationContext())) {
                                                    membre.enregistrerDansSQLiteDatabase(getApplicationContext());
                                                    enregistrerPhotoProfilFromFirebase(membre.getPhoto());
                                                }

                                                //on enregistre le compte actuellement connécter
                                                enregistrerDonneesCompteConnecte(membre);

                                                progressDialog.dismiss();
                                                Intent intent = new Intent(MainActivity.this, MainInConnectionActivity.class);
                                                intent.putExtra("PHOTO", membre.getPhoto());
                                                startActivity(intent);
                                                progressDialog.dismiss();
                                            }

                                        } else {

                                            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                                            dialog.setTitle("Mot de passe incorrecte");
                                            dialog.setMessage("Le mot de passe est incorrecte");
                                            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });


                                            progressDialog.dismiss();
                                            dialog.show();


                                        }

                                    }


                                }


                                entrer = false;
                            }}

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }else{ // pas de cnx

                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("  Impossible de connecter");
                            builder.setIcon(R.drawable.invalide_icon);
                            builder.setMessage("Vérifier votre connexion internet Svp");
                            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            progressDialog.dismiss();
                            builder.show();

                        }

                    }


                });




                //------------------------------------------------------clique sur facebook login
                FacebookSdk.sdkInitialize(getApplicationContext());
                callbackManager = CallbackManager.Factory.create();
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        firebaseAuthWithFacebook(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.e("hhh","facebook cancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                        dialog.setTitle("  Impossible de connecter");
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
                });

                facebookLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        methodeCnx = "facebook";
                        inscription = false;
                        LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("email", "public_profile"));

                    }
                });



                //------------------------------------------------------clique sur facebook login
                googleLogin.setOnClickListener(new GoogleLoginListener());



                dialog.show();













            }
        });





    }










    //facebook et google authFirebase
    private void firebaseAuthWithFacebook(AccessToken token) {


        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("hhh", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.e("hhh", "cnx facebook échoué : "+task.getException());

                        }else{
                            Log.e("hhh","cnx facebook avec succés");


                        }

                        // ...
                    }
                });
    }







    private void firebaseAuthWithGoogle(GoogleSignInAccount account){

        AuthCredential ac = GoogleAuthProvider.getCredential(account.getIdToken(),null);

        fAuth.signInWithCredential(ac).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(!task.isSuccessful()) {

                    Log.e("hhh", "cnx google echoué : " + task.getException());

                }else {

                    Log.e("hhh", "cnx google avec succés");



                }

            }
        });

    }








    @Override
    protected void onStart() {
        super.onStart();

        fAuth.addAuthStateListener(fAuthListener);





    }


    @Override
    protected void onStop() {
        super.onStop();

        if(fAuthListener != null)
            fAuth.removeAuthStateListener(fAuthListener);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if(callbackManager != null)
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GOOGLE && resultCode == RESULT_OK){

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);


            //Si l'operation réussit
            if(result.isSuccess()){

                GoogleSignInAccount account = result.getSignInAccount();
                photoURL = account.getPhotoUrl().toString();
                firebaseAuthWithGoogle(account);

            }else{

                //cnx a google échoue

                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("Impossible de connecter");
                dialog.setIcon(R.drawable.invalide_icon);
                dialog.setMessage("Connexion à votre compte google+ impossible vérifier votre connexion svp");
                dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }



        }else if(requestCode == 0){


            LoginManager.getInstance().logOut();

            if(gClient != null)
            gClient.disconnect();

            FirebaseAuth.getInstance().signOut();

        }


        super.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if(gClient != null) {
            gClient.stopAutoManage(MainActivity.this);
            gClient.disconnect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if(gClient != null) {
            gClient.stopAutoManage(MainActivity.this);
            gClient.disconnect();
        }

    }





    //methode utile


    private void viderFichierConnexion(){

        try {
            PrintWriter writer = new PrintWriter(compteConnecter);
            writer.write("");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void membreBloquer(){

        Toast.makeText(MainActivity.this,"Désolé votre compte est bloqué",Toast.LENGTH_LONG).show();

                viderFichierConnexion();
                membre.setMembreBloquerSQLite(MainActivity.this);


    }

    private void enregistrerPhotoProfilFromFirebase(long photo){


        StorageReference reference = FirebaseStorage.getInstance().getReference();
        StorageReference pathImage = reference.child("photo_profil/"+photo+".jpg");
        File imageFile = new File(repPhotoProfil.getAbsoluteFile()+"/"+photo+".jpg");

        pathImage.getFile(imageFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                Log.e("hhh","photo de profil c'est bon");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("hhh","photo de profil c'est pas bon");
            }
        });


    }





    private void enregistrerPhotoMembreFromURL(String photoUrl,long photo){

       EnregistrerPhotoInTerminalFromURL enregistrerPhotoInTerminalFromURL = new EnregistrerPhotoInTerminalFromURL(photoUrl,photo);
        Thread thread = new Thread(enregistrerPhotoInTerminalFromURL);
        thread.start();

        }


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

//-----------------------------------------------------Thread
    class EnregistrerPhotoInTerminalFromURL implements Runnable{

    private String photoUrl = null;
    private long photo = -1;

    public EnregistrerPhotoInTerminalFromURL(String photoUrl, long photo) {
        this.photoUrl = photoUrl;
        this.photo = photo;
    }

    @Override
    public void run() {
        try {


            File file = new File(repPhotoProfil.getAbsoluteFile()+"/"+photo+".jpg");

            if(!file.exists()) {
                FileOutputStream fichierImage = new FileOutputStream(file);
                URL url = new URL(photoUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                Bitmap photoProfilBitmap = BitmapFactory.decodeStream(inputStream);
                photoProfilBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fichierImage);
                fichierImage.close();
            }




        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}





//-----------------------------------------------------Listener

    class GoogleInscriptionListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            ConnectivityManager manager = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {

            inscription = true;
            methodeInscription = "google";

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
            gClient = new GoogleApiClient.Builder(MainActivity.this).enableAutoManage(MainActivity.this,new GoogleApiClient.OnConnectionFailedListener(){
                @Override
                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                }
            }).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();

            Intent intent = Auth.GoogleSignInApi.getSignInIntent(gClient);


            //On récupére les données google+ dans onActivityResult()
            startActivityForResult(intent,GOOGLE);

            }else{

                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
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
        }
    }






    class GoogleLoginListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {


            ConnectivityManager manager = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {

                methodeCnx = "google";

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
                gClient = new GoogleApiClient.Builder(MainActivity.this).enableAutoManage(MainActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }


                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

                Intent intent = Auth.GoogleSignInApi.getSignInIntent(gClient);


                //On récupére les données google+ dans onActivityResult()
                startActivityForResult(intent, GOOGLE);


            }else{

                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("  Impossible de connecter");
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
        }
    }







//Permissions
    private void getPermissions(){
        // The request code used in ActivityCompat.requestPermissions()
// and returned in the Activity's onRequestPermissionsResult()
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        if(!hasPermissions(this, PERMISSIONS)){
            Toast.makeText(MainActivity.this,"Pour que l'application puisse fonctionner correctement veuillez donner ses permissions",Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

    }



    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }








}
