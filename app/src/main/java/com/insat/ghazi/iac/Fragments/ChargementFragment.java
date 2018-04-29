package com.insat.ghazi.iac.Fragments;



import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.insat.ghazi.iac.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChargementFragment extends Fragment {



    private TextView chargement = null;

    private ImageView chargement1 = null;
    private ImageView chargement2 = null;
    private ImageView chargement3 = null;
    private ImageView chargement4 = null;
    private ImageView chargement5 = null;
    private ImageView chargement6 = null;
    private ImageView chargement7 = null;
    private ImageView chargement8 = null;
    private ImageView chargement9 = null;










    //Handlers pour afficher et masquer les points vert de chargement
    private Handler chargement1VisibleHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            chargement1.setVisibility(View.VISIBLE);
        }
    };
    private Handler chargement2VisibleHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            chargement2.setVisibility(View.VISIBLE);
        }
    };
    private Handler chargement3VisibleHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            chargement3.setVisibility(View.VISIBLE);
        }
    };
    private Handler chargement4VisibleHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            chargement4.setVisibility(View.VISIBLE);
        }
    };
    private Handler chargement5VisibleHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            chargement5.setVisibility(View.VISIBLE);
        }
    };
    private Handler chargement6VisibleHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            chargement6.setVisibility(View.VISIBLE);
        }
    };
    private Handler chargement7VisibleHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            chargement7.setVisibility(View.VISIBLE);
        }
    };
    private Handler chargement8VisibleHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            chargement8.setVisibility(View.VISIBLE);
        }
    };


    private Handler chargement9VisibleHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            chargement9.setVisibility(View.VISIBLE);
        }
    };


    private Handler chargement1InvisibleHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            chargement1.setVisibility(View.INVISIBLE);
        }
    };
    private Handler chargement2InvisibleHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            chargement2.setVisibility(View.INVISIBLE);
        }
    };
    private Handler chargement3InvisibleHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            chargement3.setVisibility(View.INVISIBLE);
        }
    };
    private Handler chargement4InvisibleHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            chargement4.setVisibility(View.INVISIBLE);
        }
    };
    private Handler chargement5InvisibleHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            chargement5.setVisibility(View.INVISIBLE);
        }
    };
    private Handler chargement6InvisibleHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            chargement6.setVisibility(View.INVISIBLE);
        }
    };
    private Handler chargement7InvisibleHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            chargement7.setVisibility(View.INVISIBLE);
        }
    };
    private Handler chargement8InvisibleHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            chargement8.setVisibility(View.INVISIBLE);
        }
    };


    private Handler chargement9InvisibleHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            chargement9.setVisibility(View.INVISIBLE);
        }
    };




    //Handlers pour mettre le fragment  event aprés le telechargement des données
    private Handler actionBarShow = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            actionBar.show();
        }
    };
    private Handler toolBarTitleChange = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            titleToolbar.setText("Actualité");
        }
    };
    private Handler navigationMenuShow = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            navigationView.getMenu().getItem(0).setChecked(true);
        }
    };
    private Handler changeChargementFragmentToEventFragment = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Activity activity = getActivity();
            if(activity != null && !activity.isFinishing()) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, evenentsFragment);
                transaction.commitAllowingStateLoss();//executé aprés saving instance state
            }
        }
    };


    private Handler changeImageToolbar = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(changerphotoProfil) {
                Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
                drawerImage.setImageBitmap(bitmap);
            }
        }
    };

    private Handler lockDrawerView = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    };













    private int nbrPhotoEventCompleted = 0;
    private int nbrTotalEventTelecharger = 0;

    //Thread pour afficher le chargement
    private Thread chargementThread = null;
    private ActionBar actionBar = null;
    private TextView titleToolbar = null;
    private NavigationView navigationView = null;
    private DrawerLayout drawerLayout = null;
    private EvenentsFragment evenentsFragment = null;
    private FragmentManager fragmentManager = null;

    private boolean totalEventDejaTelecharger = false;

    private Date debutChargement = null;
    private Date finChargement = null;


    private boolean changerphotoProfil = true;
    private String photoUrl = null;
    private String photoPath = null;
    private boolean photoEnregistrer = false;
    private de.hdodenhof.circleimageview.CircleImageView drawerImage = null;


    private long userID = -1;




    public ChargementFragment() {

    }




    public void setArgs (ActionBar actionBar, TextView titleToolbar, NavigationView navigationView,EvenentsFragment evenentsFragment,FragmentManager fragmentManager,String photoPath,de.hdodenhof.circleimageview.CircleImageView drawerImage,DrawerLayout drawerLayout)
    {


        this.actionBar = actionBar;
        this.titleToolbar = titleToolbar;
        this.navigationView = navigationView;
        this.evenentsFragment = evenentsFragment;
        this.fragmentManager = fragmentManager;
        this.photoPath = photoPath;
        this.drawerImage = drawerImage;
        this.drawerLayout = drawerLayout;
        photoEnregistrer = true;


    }



    public void setArgs (ActionBar actionBar, TextView titleToolbar, NavigationView navigationView,EvenentsFragment evenentsFragment,FragmentManager fragmentManager,String photoUrl,String photoPath,long userID,de.hdodenhof.circleimageview.CircleImageView drawerImage,DrawerLayout drawerLayout)
    {

        this.actionBar = actionBar;
        this.titleToolbar = titleToolbar;
        this.navigationView = navigationView;
        this.evenentsFragment = evenentsFragment;
        this.fragmentManager = fragmentManager;
        this.photoUrl = photoUrl;
        this.photoPath = photoPath;
        this.userID = userID;
        this.drawerImage = drawerImage;
        this.drawerLayout = drawerLayout;


    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_chargement, container, false);


        chargement = (TextView) view.findViewById(R.id.chargement);
        chargement1 = (ImageView) view.findViewById(R.id.chargement1);
        chargement2 = (ImageView) view.findViewById(R.id.chargement2);
        chargement3 = (ImageView) view.findViewById(R.id.chargement3);
        chargement4 = (ImageView) view.findViewById(R.id.chargement4);
        chargement5 = (ImageView) view.findViewById(R.id.chargement5);
        chargement6 = (ImageView) view.findViewById(R.id.chargement6);
        chargement7 = (ImageView) view.findViewById(R.id.chargement7);
        chargement8 = (ImageView) view.findViewById(R.id.chargement8);
        chargement9 = (ImageView) view.findViewById(R.id.chargement9);


        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),"fonts/georgia.ttf");
        chargement.setTypeface(typeface);


        chargement1.setVisibility(View.INVISIBLE);
        chargement2.setVisibility(View.INVISIBLE);
        chargement3.setVisibility(View.INVISIBLE);
        chargement4.setVisibility(View.INVISIBLE);
        chargement5.setVisibility(View.INVISIBLE);
        chargement6.setVisibility(View.INVISIBLE);
        chargement7.setVisibility(View.INVISIBLE);
        chargement8.setVisibility(View.INVISIBLE);
        chargement9.setVisibility(View.INVISIBLE);


        if(!photoEnregistrer) {
            EnregistrementPhotoThread enregistrementPhotoThread = new EnregistrementPhotoThread();
            Thread thread = new Thread(enregistrementPhotoThread);
            thread.start();
        }


        chargementThread = new Thread(new Runnable() {
            @Override
            public void run() {



                    while ((nbrPhotoEventCompleted < nbrTotalEventTelecharger) ||  !totalEventDejaTelecharger || !photoEnregistrer){

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        chargement1VisibleHandler.sendEmptyMessage(0);



                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        chargement2VisibleHandler.sendEmptyMessage(0);


                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        chargement3VisibleHandler.sendEmptyMessage(0);



                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        chargement4VisibleHandler.sendEmptyMessage(0);




                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        chargement5VisibleHandler.sendEmptyMessage(0);



                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        chargement6VisibleHandler.sendEmptyMessage(0);



                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        chargement7VisibleHandler.sendEmptyMessage(0);



                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        chargement8VisibleHandler.sendEmptyMessage(0);



                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        chargement9VisibleHandler.sendEmptyMessage(0);






                        //**************




                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }




                        chargement1InvisibleHandler.sendEmptyMessage(0);
                        chargement2InvisibleHandler.sendEmptyMessage(0);
                        chargement3InvisibleHandler.sendEmptyMessage(0);
                        chargement4InvisibleHandler.sendEmptyMessage(0);
                        chargement5InvisibleHandler.sendEmptyMessage(0);
                        chargement6InvisibleHandler.sendEmptyMessage(0);
                        chargement7InvisibleHandler.sendEmptyMessage(0);
                        chargement8InvisibleHandler.sendEmptyMessage(0);
                        chargement9InvisibleHandler.sendEmptyMessage(0);







                    }







                //Chargement Términer
                finChargement = new Date();

                long dureeChargement = finChargement.getTime() - debutChargement.getTime();
                long seconds = TimeUnit.MILLISECONDS.toSeconds(dureeChargement);

                if(seconds < 9){

                    try {
                        Thread.sleep(1000*(9-seconds));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }





                //modifier la photo du NavigationDrawer si l'inscription n'est pas manuelle
                changeImageToolbar.sendEmptyMessage(0);







                //Initialiser le premier fragment (EventFragments)
                changeChargementFragmentToEventFragment.sendEmptyMessage(0);


                //afficher l'actionBar
                actionBarShow.sendEmptyMessage(0);
                toolBarTitleChange.sendEmptyMessage(0);
                navigationMenuShow.sendEmptyMessage(0);
                lockDrawerView.sendEmptyMessage(0);




            }
        });



        chargementThread.start();



        // Inflate the layout for this fragment
        return view;
    }


    public void setDebutChargement(Date debutChargement) {
        this.debutChargement = debutChargement;
    }

    public void setFinChargement(Date finChargement) {
        this.finChargement = finChargement;
    }

    public void setTotalEventDejaTelecharger(boolean totalEventDejaTelecharger) {
        this.totalEventDejaTelecharger = totalEventDejaTelecharger;
    }

    public void setNbrPhotoEventCompleted(int nbrPhotoEventCompleted) {
        this.nbrPhotoEventCompleted = nbrPhotoEventCompleted;
    }

    public void setNbrTotalEventTelecharger(int nbrTotalEventTelecharger) {
        this.nbrTotalEventTelecharger = nbrTotalEventTelecharger;
    }











    //-----------------------------------------------Classes---------------------------------------------------------------------///
    class EnregistrementPhotoThread implements Runnable{

        @Override
        public void run() {

            try {
                FileOutputStream fichierImage = new FileOutputStream(photoPath);
                URL url = new URL(photoUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                Bitmap photoProfilBitmap = BitmapFactory.decodeStream(inputStream);
                photoProfilBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fichierImage);
                fichierImage.close();




            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }




            StorageReference rootRef = FirebaseStorage.getInstance().getReference();
            StorageReference photoProfilReference = rootRef.child("photo_profil/"+userID+".jpg");



            try {

                File file = new File(photoPath);
                Log.e("tag",file.toString());

                while(!file.exists())
                    Thread.sleep(500);


                InputStream inputStream = new FileInputStream(file);
                UploadTask uploadTask = photoProfilReference.putStream(inputStream);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.e("NoNEnvoyer",e.getMessage());

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        photoEnregistrer = true;
                    }
                });


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }

    public void setChangerphotoProfil(boolean changerphotoProfil) {
        this.changerphotoProfil = changerphotoProfil;
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

}
