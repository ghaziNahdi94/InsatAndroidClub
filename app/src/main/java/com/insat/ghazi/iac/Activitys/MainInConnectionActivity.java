package com.insat.ghazi.iac.Activitys;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.insat.ghazi.iac.Events;
import com.insat.ghazi.iac.Fragments.ChargementFragment;
import com.insat.ghazi.iac.Fragments.EvenentsFragment;
import com.insat.ghazi.iac.Fragments.ListMemberFragment;
import com.insat.ghazi.iac.Fragments.ListeStaffFragment;
import com.insat.ghazi.iac.Fragments.MessagesFragment;
import com.insat.ghazi.iac.Fragments.ParametreFragment;
import com.insat.ghazi.iac.Fragments.ProfilFragment;
import com.insat.ghazi.iac.Membre;
import com.insat.ghazi.iac.R;
import com.insat.ghazi.iac.SQLiteDatabase.EventDAO;
import com.insat.ghazi.iac.SQLiteDatabase.MemberDAO;
import com.insat.ghazi.iac.Services.NotificationService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainInConnectionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private Toolbar toolbar = null;
    private TextView titleToolbar = null;
    private NavigationView navigationView = null;


    private TextView drawerNameHeader = null;
    private TextView drawerEmailHeader = null;
    private de.hdodenhof.circleimageview.CircleImageView drawerImageHeader = null;


    private EvenentsFragment evenentsFragment = null;
    private ListeStaffFragment listeStaffFragment = null;
    private ProfilFragment profilFragment = null;
    private ChargementFragment chargementFragment = null;
    private MessagesFragment messagesFragment = null;
    private ParametreFragment parametreFragment = null;
    private static int nbrChargementPhotoEventComplete = 0;


    private File repPhotoProfil = null;
    private File repPhotoEvent = null;
    private File compteConnecter = null;
    private File repPhotoMessages = null;
    private File repPhotoCouverture = null;
    private File notification = null;
    private Intent serviceInt = null;


    private String email = null;
    private Membre member = null;


    private ArrayList<Events> eventsList = new ArrayList<Events>();


    private DrawerLayout drawer = null;


    private boolean refrech = false;
    private boolean besoinCnx = false;
    private int drawerPos = R.id.nav_actualite;


    boolean entrer = true;
    boolean deconnecter = false;


    //private InterstitialAd mInterstitialAd;
    //private AdRequest request;

    private Handler changeImgHeader = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            File file = new File(repPhotoProfil.getAbsolutePath() + "/" + member.getPhoto() + ".jpg");
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            drawerImageHeader.setImageBitmap(bitmap);
        }
    };

   /* private Handler afficherPubHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            mInterstitialAd.show();
        }
    };
    private Handler loadPubHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mInterstitialAd.loadAd(request);
        }
    };
*/

    private String callString = "";

    public void setCallString(String s) {
        this.callString = s;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_in_connection);


        getPermissions();


         /*--------------------------- Les fichiers de l'application------------------------------------------*/

        String rootPath = getFilesDir().getAbsolutePath();

        repPhotoProfil = new File(rootPath + "/photo_profil");
        repPhotoEvent = new File(rootPath + "/photo_events");
        compteConnecter = new File(rootPath + "/compte.txt");
        repPhotoMessages = new File(rootPath + "/photo_messages");
        repPhotoCouverture = new File(rootPath + "/photo_couverture");



          /*---------------------------------------------------------------------------------------------------*/


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                // check if no view has focus:
                View v = getCurrentFocus();
                if (v == null)
                    return;

                inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }

        });

        setSupportActionBar(toolbar);
        titleToolbar = (TextView) findViewById(R.id.titleToolbar);
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/RobotoCondensed-Bold.ttf");
        titleToolbar.setTypeface(myTypeface);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        drawerNameHeader = (TextView) headerView.findViewById(R.id.drawer_name_header);
        drawerEmailHeader = (TextView) headerView.findViewById(R.id.drawer_email_header);
        drawerImageHeader = (CircleImageView) headerView.findViewById(R.id.drawer_image_header);


        Intent intent = getIntent();
        long photoId = intent.getLongExtra("PHOTO", -1);
        String photoUrl = intent.getStringExtra("photoURL");


        getSupportActionBar().hide();
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        evenentsFragment = new EvenentsFragment();
        evenentsFragment.setArgs(MainInConnectionActivity.this, eventsList, repPhotoEvent);


        if (photoUrl != null) { //si on a utilisé google ou facebook on doit enregistrer la photo de profil en chargement fragment


            //................Chargement de données (fragment)
            chargementFragment = new ChargementFragment();
            chargementFragment.setArgs(getSupportActionBar(), titleToolbar, navigationView, evenentsFragment, getSupportFragmentManager(), photoUrl, repPhotoProfil.getAbsolutePath() + "/" + photoId + ".jpg", photoId, drawerImageHeader, drawer);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, chargementFragment);
            transaction.commit();


        } else {


            //................Chargement de données (fragment)
            chargementFragment = new ChargementFragment();
            chargementFragment.setArgs(getSupportActionBar(), titleToolbar, navigationView, evenentsFragment, getSupportFragmentManager(), repPhotoProfil.getAbsolutePath() + "/" + photoId + ".jpg", drawerImageHeader, drawer);

            if (photoId == -1) // si on connecte avec le fichier de connexion on met la photo de profil directement sans chargement
                chargementFragment.setChangerphotoProfil(false);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, chargementFragment);
            transaction.commit();

        }


        new Thread(new ConnectionVerif()).start();







               /*--------------Récupérer les données du compte-----------------------------------------------*/


        //Avoir l'email du compte connécter

        email = recupererEmailFromFichierConecter();


        //Avoir les données du compte connecter depuis SQLite database
        MemberDAO memberDAO = new MemberDAO(getApplicationContext());
        SQLiteDatabase db = memberDAO.open();
        Cursor cursor = db.rawQuery("SELECT * FROM " + MemberDAO.TABLE_NAME + " WHERE " + MemberDAO.EMAIL + "=?", new String[]{email});


        cursor.moveToNext();


        long id = cursor.getLong(0);
        String tel = cursor.getString(1);
        String email = cursor.getString(3);
        String nom = cursor.getString(4);
        String prenom = cursor.getString(5);
        String password = cursor.getString(6);
        boolean insatien = Boolean.parseBoolean(cursor.getString(7));
        String filliere = cursor.getString(8);
        String niveau = cursor.getString(9);
        boolean connecter = Boolean.parseBoolean(cursor.getString(10));
        String poste = cursor.getString(11);
        long photo = cursor.getLong(12);
        int blocage = cursor.getInt(13);

        boolean bloc = (blocage == 1) ? true : false;


        member = new Membre(email, password, nom, prenom, tel, insatien, filliere, niveau, connecter, poste, photo, id, bloc);








        drawerEmailHeader.setText(email);
        String name = prenom + " " + nom;
        if (name.length() < 20)
            drawerNameHeader.setText(name);
        else
            drawerNameHeader.setText(name.substring(0, 20) + "...");







        //services notifications-----------------------------------------------------------------------------------
        serviceInt = new Intent(MainInConnectionActivity.this, NotificationService.class);

        ifHuaweiAlert();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(serviceInt);
        else
        startService(serviceInt);






        //------------------------------------------------------------------------------------------------------------



//verifier si il ya une connexion internet (donc telechargé events depuis firebase) sinon affiché les events depuis SQLite database

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {


            //clear memory cache photo
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Glide.get(MainInConnectionActivity.this).clearDiskCache();
                }
            }).start();


            setMembreStateConnectionInFirebase();
            telechargerPhotoProfilEtCouverture();


            refrech = true;


            chargementFragment.setDebutChargement(new Date());
            DatabaseReference tableEventsFireBase = FirebaseDatabase.getInstance().getReference("events");
            tableEventsFireBase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    eventsList.clear();

                    ArrayList<File> photosFile = new ArrayList<File>();
                    ArrayList<StorageReference> pathImageFirebase = new ArrayList<StorageReference>();

                    //vider les events depuis SQLite
                    SQLiteDatabase database = new EventDAO(MainInConnectionActivity.this).open();
                    database.execSQL("DELETE FROM " + EventDAO.TABLE_NAME);
                    database.close();


                    for (DataSnapshot val : dataSnapshot.getChildren()) {


                        //récupérer l'evenement dans un objet Events
                        Events events = (Events) val.getValue(Events.class);
                        eventsList.add(events);


                        //poser l'evenement dans la base de données SQLite
                        database = new EventDAO(MainInConnectionActivity.this).open();
                        ContentValues values = new ContentValues();
                        values.put(EventDAO.ID, events.getId());
                        values.put(EventDAO.TITRE, events.getTitre());
                        values.put(EventDAO.DESCRIPTION, events.getDescription());
                        values.put(EventDAO.DATE, events.getDate());

                        database.insert(EventDAO.TABLE_NAME, null, values);

                        database.close();


                        //récupérer l'image de l'evenement depuis firebase et la mettre dans le dossier "photo_event"
                        StorageReference firebaseRef = FirebaseStorage.getInstance().getReference();
                        StorageReference pathImage = firebaseRef.child("photo_events/" + events.getId() + ".jpg");
                        pathImageFirebase.add(pathImage);
                        File imageEvents = new File(repPhotoEvent + "/" + events.getId() + ".jpg");
                        photosFile.add(imageEvents);


                    }


                    chargementFragment.setNbrTotalEventTelecharger(photosFile.size());
                    chargementFragment.setTotalEventDejaTelecharger(true);


                    //téléchargement des images récupérer
                    for (int i = 0; i < photosFile.size(); i++) {

                        StorageReference pathImage = pathImageFirebase.get(i);
                        File photoFile = photosFile.get(i);
                        pathImage.getFile(photoFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                chargementFragment.setNbrPhotoEventCompleted(++nbrChargementPhotoEventComplete);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("hhh", "pas de telechargement de la photo event" + nbrChargementPhotoEventComplete + " " + e.getMessage());
                            }
                        });


                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        } else {   // sinon affiché les events depuis SQLite


            refrech = true;
            afficheEventsFromSQlite();

            File file = new File(repPhotoProfil.getAbsolutePath() + "/" + member.getPhoto() + ".jpg");
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            drawerImageHeader.setImageBitmap(bitmap);


        }


    }


    //-----------------------------------------------methode utiles

    private void membreBloquer() {

        Toast.makeText(MainInConnectionActivity.this, "Désolé votre compte est bloqué", Toast.LENGTH_LONG).show();


        viderFichierConnexion();


        member.setMembreBloquerSQLite(MainInConnectionActivity.this);
        Intent intent = new Intent(MainInConnectionActivity.this, MainActivity.class);
        startActivity(intent);
        MainInConnectionActivity.this.finish();


    }


    private void afficheEventsFromSQlite() {

        getSupportActionBar().show();
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);


        eventsList = getEventsFromSQLiteDatabase();

        navigationView.getMenu().getItem(0).setChecked(true);
        titleToolbar.setText("Actualité");


        Collections.reverse(eventsList);
        EvenentsFragment evenentsFragment = new EvenentsFragment();
        evenentsFragment.setArgs(MainInConnectionActivity.this, eventsList, repPhotoEvent);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, evenentsFragment);
        transaction.commit();


    }


    private void afficheEventsFromFirebase() {


        DatabaseReference tableEventsFireBase = FirebaseDatabase.getInstance().getReference("events");
        tableEventsFireBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                eventsList.clear();

                ArrayList<File> photosFile = new ArrayList<File>();
                ArrayList<StorageReference> pathImageFirebase = new ArrayList<StorageReference>();

                //vider les events depuis SQLite
                SQLiteDatabase database = new EventDAO(MainInConnectionActivity.this).open();
                database.execSQL("DELETE FROM " + EventDAO.TABLE_NAME);
                database.close();


                for (DataSnapshot val : dataSnapshot.getChildren()) {


                    //récupérer l'evenement dans un objet Events
                    Events events = (Events) val.getValue(Events.class);
                    eventsList.add(events);


                    //poser l'evenement dans la base de données SQLite
                    database = new EventDAO(MainInConnectionActivity.this).open();
                    ContentValues values = new ContentValues();
                    values.put(EventDAO.ID, events.getId());
                    values.put(EventDAO.TITRE, events.getTitre());
                    values.put(EventDAO.DESCRIPTION, events.getDescription());
                    values.put(EventDAO.DATE, events.getDate());

                    database.insert(EventDAO.TABLE_NAME, null, values);

                    database.close();


                    //récupérer l'image de l'evenement depuis firebase et la mettre dans le dossier "photo_event"
                    StorageReference firebaseRef = FirebaseStorage.getInstance().getReference();
                    StorageReference pathImage = firebaseRef.child("photo_events/" + events.getId() + ".jpg");
                    pathImageFirebase.add(pathImage);
                    File imageEvents = new File(repPhotoEvent + "/" + events.getId() + ".jpg");
                    photosFile.add(imageEvents);


                }


                //téléchargement des images récupérer
                for (int i = 0; i < photosFile.size(); i++) {

                    StorageReference pathImage = pathImageFirebase.get(i);
                    File photoFile = photosFile.get(i);

                    pathImage.getFile(photoFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("hhh", "pas de telechargement de la photo event" + nbrChargementPhotoEventComplete + " " + e.getMessage());
                        }
                    });


                }


                if (!isFinishing()) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    Collections.reverse(eventsList);
                    evenentsFragment = new EvenentsFragment();
                    evenentsFragment.setArgs(MainInConnectionActivity.this, eventsList, repPhotoEvent);
                    transaction.replace(R.id.fragment_container, evenentsFragment);
                    transaction.commitAllowingStateLoss();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    private void telechargerPhotoProfilEtCouverture() {


        //télécharger la photo de couverture
        new Thread(new Runnable() {
            @Override
            public void run() {


                File file = new File(repPhotoCouverture.getAbsolutePath() + "/" + member.getPhoto() + ".jpg");

                StorageReference reference = FirebaseStorage.getInstance().getReference("photo_couverture/" + member.getPhoto() + ".jpg");
                reference.getFile(file).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("hhh", "failure :ooo");
                    }
                });


            }
        }).start();


        //télécharger la photo de profil
        new Thread(new Runnable() {
            @Override
            public void run() {

                File file = new File(repPhotoProfil.getAbsolutePath() + "/" + member.getPhoto() + ".jpg");


                StorageReference reference = FirebaseStorage.getInstance().getReference("photo_profil/" + member.getPhoto() + ".jpg");

                reference.getFile(file).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("hhh", "failure :ooo");
                    }
                });


                changeImgHeader.sendEmptyMessage(0);

            }
        }).start();


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


    private void viderFichierConnexion() {

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(compteConnecter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        writer.print("");
        writer.close();

    }


    private ArrayList<Events> getEventsFromSQLiteDatabase() {

        ArrayList<Events> lista = new ArrayList<Events>();
        EventDAO tableEvent = new EventDAO(MainInConnectionActivity.this);
        SQLiteDatabase database = tableEvent.open();
        Cursor cursor = database.rawQuery("SELECT * FROM " + EventDAO.TABLE_NAME, new String[]{});

        while (cursor.moveToNext()) {

            int id = cursor.getInt(0);
            String titre = cursor.getString(1);
            String description = cursor.getString(2);
            String date = cursor.getString(3);

            Events event = new Events(id, titre, description, date);


            lista.add(event);

        }

        return lista;
    }


    private void setMembreStateConnectionInFirebase() {


        DatabaseReference tableMembre = FirebaseDatabase.getInstance().getReference("membre");
        tableMembre.orderByChild("id").equalTo(member.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                Membre m = null;

                for (DataSnapshot val : dataSnapshot.getChildren()) {
                    m = (Membre) val.getValue(Membre.class);
                }


                if (m == null) {

                    Toast.makeText(MainInConnectionActivity.this, "Compte supprimé", Toast.LENGTH_LONG).show();
                    viderFichierConnexion();
                    Intent i1 = new Intent(MainInConnectionActivity.this, MainActivity.class);
                    startActivity(i1);
                    MainInConnectionActivity.this.finish();

                } else if (m.isBlocage()) {

                    membreBloquer();

                } else {


                    if (entrer) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("membre");
                        reference.child(m.getId() + "").child("connecter").setValue(true);
                        entrer = false;
                    }


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void memberDeconnecter() {

        //deconnecter depuis firebaseAuth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();


        //vider le fichier de connexion
        viderFichierConnexion();


        member.setConnecter(false);
        final DatabaseReference tableMembreFireBase = FirebaseDatabase.getInstance().getReference("membre");
        tableMembreFireBase.child(member.getId() + "").child("connecter").setValue(false);


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragmentPourAfficher = null;


        if (id == R.id.nav_actualite) {
            drawerPos = id;
            titleToolbar.setText("Actualité");
            evenentsFragment = new EvenentsFragment();
            evenentsFragment.setArgs(this, eventsList, repPhotoEvent);
            fragmentPourAfficher = evenentsFragment;


        } else if (id == R.id.nav_liste_staff) {

            if (cnxExiste()) {
                drawerPos = id;
                titleToolbar.setText("Staff");
                listeStaffFragment = new ListeStaffFragment();
                listeStaffFragment.setArgs(this, this);
                fragmentPourAfficher = listeStaffFragment;
                besoinCnx = false;
            } else {
                besoinCnx = true;

            }


        } else if (id == R.id.nav_profil) {
            drawerPos = id;
            String name = member.getPrenom() + " " + member.getNom();
            if (name.length() < 17)
                titleToolbar.setText(name);
            else
                titleToolbar.setText(name.substring(0, 17) + "...");

            profilFragment = new ProfilFragment();
            profilFragment.setArgs(this, drawerImageHeader, member, repPhotoProfil.getAbsolutePath() + "/" + member.getId() + ".jpg");
            fragmentPourAfficher = profilFragment;


        } else if (id == R.id.nav_message) {

            drawerPos = id;
            titleToolbar.setText("Messages");
            messagesFragment = new MessagesFragment();
            messagesFragment.setArgs(MainInConnectionActivity.this, member.getId(), repPhotoMessages);
            fragmentPourAfficher = messagesFragment;


        } else if (id == R.id.nav_contacter_membre) {

            if (cnxExiste()) {
                drawerPos = id;
                titleToolbar.setText("Membres");
                ListMemberFragment listMemberFragment = new ListMemberFragment();
                listMemberFragment.setArgs(MainInConnectionActivity.this, MainInConnectionActivity.this);
                fragmentPourAfficher = listMemberFragment;
                besoinCnx = false;
            } else {
                besoinCnx = true;
            }


        } else if (id == R.id.nav_deconnexion) {


            if (cnxExiste()) {
                memberDeconnecter();
                Intent intent = new Intent(MainInConnectionActivity.this, MainActivity.class);
                startActivity(intent);
                MainInConnectionActivity.this.finish();
                deconnecter = true;
                return true;
            } else {
                besoinCnx = true;
            }

        } else if (id == R.id.nav_parametre) {
            drawerPos = id;
            titleToolbar.setText("Paramétres");
            parametreFragment = new ParametreFragment();
            parametreFragment.setArgs(MainInConnectionActivity.this, member);
            fragmentPourAfficher = parametreFragment;


        }


        if (!deconnecter && !besoinCnx) {
            transaction.replace(R.id.fragment_container, fragmentPourAfficher);
            transaction.commit();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (!deconnecter) {
            drawerMenuBesoinCnx();
        }


        return true;


    }


    private void drawerMenuBesoinCnx() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(MainInConnectionActivity.this);
        dialog.setTitle("  Pas de connexion internet");
        dialog.setIcon(R.drawable.invalide_icon);
        dialog.setMessage("  Vérifier votre connexion internet Svp");
        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                navigationView.setCheckedItem(drawerPos);
            }
        });


        dialog.show();


        besoinCnx = false;
    }


    private boolean cnxExiste() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED)
            return true;
        else
            return false;
    }


//back button


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    //verif cnx
    class ConnectionVerif implements Runnable {


        @Override
        public void run() {


            while (true) {

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();


                if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {

                    if (refrech && getSupportActionBar().isShowing()) {


                        afficheEventsFromFirebase();
                      //  affichePub();

                        refrech = false;

                    }

                } else {

                    refrech = true;

                }


                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }


        }
    }





  /*  //pub
    private void affichePub() {

        MobileAds.initialize(MainInConnectionActivity.this, "ca-app-pub-2299738567543197~9299360783");

        mInterstitialAd = new InterstitialAd(MainInConnectionActivity.this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2299738567543197/1935876089");


        request = new AdRequest.Builder().build();

        loadPubHandler.sendEmptyMessage(0);


        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {

                loadPubHandler.sendEmptyMessage(0);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(90000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        afficherPubHandler.sendEmptyMessage(0);
                    }
                }).start();

            }

            @Override
            public void onAdFailedToLoad(int i) {
                Log.e("hhh", "failed :'(((");
            }
        });


    }
*/



    @Override
    public void onRequestPermissionsResult(int requestCode,

                                       String permissions[], int[] grantResults) {
        if(!callString.equals("")){
            switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + callString));

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        Toast.makeText(MainInConnectionActivity.this,"Autoriser les appels dans les paramétres de l'application",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startActivity(callIntent);
                    callString = "";
                }
                else
                {

                }
                return;
            }
        }}
    }


    //Permissions
    private void getPermissions(){
        // The request code used in ActivityCompat.requestPermissions()
// and returned in the Activity's onRequestPermissionsResult()
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        if(!hasPermissions(this, PERMISSIONS)){
            Toast.makeText(MainInConnectionActivity.this,"Pour que l'application puisse fonctionner correctement veuillez donner ses permissions",Toast.LENGTH_LONG).show();
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









    private void ifHuaweiAlert() {
        final SharedPreferences settings = getSharedPreferences("ProtectedApps", MODE_PRIVATE);
        final String saveIfSkip = "skipProtectedAppsMessage";
        boolean skipMessage = settings.getBoolean(saveIfSkip, false);
        if (!skipMessage) {
            final SharedPreferences.Editor editor = settings.edit();
            Intent intent = new Intent();
            intent.setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity");
            if (isCallable(intent)) {
                final AppCompatCheckBox dontShowAgain = new AppCompatCheckBox(this);
                dontShowAgain.setText("Do not show again");
                dontShowAgain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        editor.putBoolean(saveIfSkip, isChecked);
                        editor.apply();
                    }
                });

                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Huawei Protected Apps")
                        .setMessage(String.format("L'application %s doit étre 'Protected Apps' pour fonctionner corréctement sur votre appareil.%n", getString(R.string.app_name)))
                        .setView(dontShowAgain)
                        .setPositiveButton("Protected Apps", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                huaweiProtectedApps();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            } else {
                editor.putBoolean(saveIfSkip, true);
                editor.apply();
            }
        }
    }

    private boolean isCallable(Intent intent) {
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void huaweiProtectedApps() {
        try {
            String cmd = "am start -n com.huawei.systemmanager/.optimize.process.ProtectActivity";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                cmd += " --user " + getUserSerial();
            }
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ignored) {
        }
    }

    private String getUserSerial() {
        //noinspection ResourceType
        Object userManager = getSystemService(USER_SERVICE);
        if (null == userManager) return "";

        try {
            Method myUserHandleMethod = android.os.Process.class.getMethod("myUserHandle", (Class<?>[]) null);
            Object myUserHandle = myUserHandleMethod.invoke(android.os.Process.class, (Object[]) null);
            Method getSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", myUserHandle.getClass());
            Long userSerial = (Long) getSerialNumberForUser.invoke(userManager, myUserHandle);
            if (userSerial != null) {
                return String.valueOf(userSerial);
            } else {
                return "";
            }
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException ignored) {
        }
        return "";
    }









}
