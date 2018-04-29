package com.insat.ghazi.iac.Fragments;


import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.insat.ghazi.iac.Membre;
import com.insat.ghazi.iac.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.UUID;


import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfilFragment extends Fragment {

    private Context context = null;
    private Membre membre = null;
    private String pathPhotoProfil =  null;


    private de.hdodenhof.circleimageview.CircleImageView photoProfil = null;
    private de.hdodenhof.circleimageview.CircleImageView photoProfilDrawer = null;
    private TextView nom = null;
    private TextView poste = null;
    private TextView email = null;
    private ImageView couverture = null;


    private ImageButton modifier = null;

    private LinearLayout menuProfil = null;
    private boolean open = false;
    private ImageView fermer = null;

    private File repPhotoCouverture = null;
    private File repPhotoProfil = null;


    private TextView photoProfilModif = null;
    private TextView photoCouvertureModif = null;
    private TextView passwordModif = null;
    private View viewModif3 = null;


    private  boolean passwordChanger = false;


    public ProfilFragment() {
        // Required empty public constructor
    }

    public void setArgs(Context context,de.hdodenhof.circleimageview.CircleImageView photoProfilDrawer, Membre membre,String pathPhotoProfil)
    {
        this.context = context;
        this.membre = membre;
        this.pathPhotoProfil = pathPhotoProfil;
        this.photoProfilDrawer = photoProfilDrawer;

    }





    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {


        View view  = inflater.inflate(R.layout.fragment_profil, container, false);


        /*------------------------------------------File---------------------------------------------------------*/
        String rootPath = context.getFilesDir().getAbsolutePath();
        repPhotoCouverture = new File(rootPath+"/photo_couverture");
        repPhotoProfil = new File(rootPath+"/photo_profil");
        /*------------------------------------------------------------------------------------------------------------------*/




        photoProfil = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.photo_profil_fragment);
        nom = (TextView) view.findViewById(R.id.name_profil_fragment);
        email = (TextView) view.findViewById(R.id.email_profil_fragment);
        poste = (TextView) view.findViewById(R.id.poste_profil_fragment);
        modifier = (ImageButton) view.findViewById(R.id.modifier_profil_fragment);
        menuProfil = (LinearLayout) view.findViewById(R.id.menu_profil);
        fermer = (ImageView) view.findViewById(R.id.fermer_menuProfil);
        couverture = (ImageView) view.findViewById(R.id.couvertureProfil);
        photoProfilModif = (TextView) view.findViewById(R.id.modif1);
        photoCouvertureModif = (TextView) view.findViewById(R.id.modif2);
        photoProfil = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.photo_profil_fragment);
        passwordModif = (TextView) view.findViewById(R.id.modif3);
        viewModif3 =  view.findViewById(R.id.view_modif3);




        ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        final File fileProfil = new File(repPhotoProfil.getAbsolutePath() + "/" + membre.getPhoto() + ".jpg");
        final File fileCovert = new File(repPhotoCouverture.getAbsolutePath()+"/"+membre.getId()+".jpg");

        if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED){


            final StorageReference referenceProfil = FirebaseStorage.getInstance().getReference("/photo_profil/" + membre.getPhoto() + ".jpg");
            final StorageReference referenceCoverture = FirebaseStorage.getInstance().getReference("photo_couverture/" + membre.getPhoto() + ".jpg");




            Glide.clear(couverture);
                    Glide.with(context).using(new FirebaseImageLoader()).load(referenceCoverture).error(R.drawable.bache2015)
                            .signature(new StringSignature(UUID.randomUUID().toString())).skipMemoryCache(true)
                            .into(couverture);


            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(fileProfil.exists())
                        fileProfil.delete();


                    referenceProfil.getFile(fileProfil).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("hhh","failed :oo");
                        }
                    });

                }
            }).start();










                    Glide.clear(photoProfil);
                    Glide.with(context).using(new FirebaseImageLoader()).load(referenceProfil).error(R.drawable.anonyme)
                            .signature(new StringSignature(UUID.randomUUID().toString())).skipMemoryCache(true)
                            .into(photoProfil);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(fileCovert.exists())
                        fileCovert.delete();


                    referenceCoverture.getFile(fileCovert).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                       Log.e("hhh","failure :ooo");
                        }
                    });

                }
            }).start();











        }else {


            if(fileProfil.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(fileProfil.getAbsolutePath());
                photoProfil.setImageBitmap(bitmap);
            }else{
                Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(),R.drawable.anonyme);
                photoProfil.setImageBitmap(bitmap1);
            }


            if(fileCovert.exists()) {
                Bitmap bitmap1 = BitmapFactory.decodeFile(fileCovert.getAbsolutePath());
                couverture.setImageBitmap(bitmap1);
            }else{
                Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(),R.drawable.bache2015);
                couverture.setImageBitmap(bitmap1);
            }
        }










        if(membre != null) {
            String name = membre.getPrenom()+" "+membre.getNom();
            if (name.length() < 17)
                nom.setText(name);
            else
                nom.setText(name.substring(0,17)+"...");

            email.setText(membre.getEmail());





            if (!membre.getPoste().equalsIgnoreCase("rien"))
                poste.setText(membre.getPoste());
            else
                poste.setText("");
        }




        ObjectAnimator mover = ObjectAnimator.ofFloat(menuProfil, "translationY",0,menuProfil.getHeight());

        mover.setDuration(2000);
        mover.start();


        menuProfil.setVisibility(View.INVISIBLE);



        modifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();



                if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED){


                    menuProfil.setVisibility(View.VISIBLE);

                    if(!open) {
                        ObjectAnimator mover = ObjectAnimator.ofFloat(menuProfil, "translationY", menuProfil.getHeight(), 0);

                        mover.setDuration(2000);
                        mover.start();

                        open = true;

                    }else{

                        ObjectAnimator mover = ObjectAnimator.ofFloat(menuProfil, "translationY",0,menuProfil.getHeight());

                        mover.setDuration(2000);
                        mover.start();



                      open = false;
                    }


                }else{


                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle("  Pas de connexion internet");
                    dialog.setIcon(R.drawable.invalide_icon);
                    dialog.setMessage("Pour modifier le profil il faut connecter à internet");
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






        if(membre.getPassword().equalsIgnoreCase("facebook") || membre.getPassword().equalsIgnoreCase("google") || membre.getPassword().equals("")){
            passwordModif.setVisibility(View.GONE);
            viewModif3.setVisibility(View.GONE);
        }


        fermer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(open) {
                    ObjectAnimator mover = ObjectAnimator.ofFloat(menuProfil, "translationY", 0, menuProfil.getHeight());

                    mover.setDuration(2000);
                    mover.start();


                    open = false;
                }


            }
        });




        photoProfilModif.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    photoProfilModif.setBackgroundColor(Color.GRAY);

                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();



                    if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {


                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(context,"Il faut autoriser la lecture des fichiers dans les paramétres de l'application",Toast.LENGTH_LONG).show();
                        }else {

                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_PICK);
                            startActivityForResult(Intent.createChooser(intent, "Choisir une photo de profil"), 1);

                        }

                    }else{

                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                        dialog.setTitle("  Pas de connexion internet");
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
                    photoProfilModif.setBackgroundColor(Color.WHITE);
                }


                return true;
            }
        });



        photoCouvertureModif.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    photoCouvertureModif.setBackgroundColor(Color.GRAY);

                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();



                    if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {

                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(context,"Il faut autoriser la lecture des fichiers dans les paramétres de l'application",Toast.LENGTH_LONG).show();
                        }else {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_PICK);
                            startActivityForResult(Intent.createChooser(intent, "Choisir une photo de couverture"), 2);
                        }

                    }else{

                            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                            dialog.setTitle("  Pas de connexion internet");
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
                    photoCouvertureModif.setBackgroundColor(Color.WHITE);
                }

                return true;
            }
        });



        passwordModif.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    passwordModif.setBackgroundColor(Color.GRAY);

                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();



                    if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {


                        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_change_password,null);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                        dialog.setTitle("Changer le mot de passe");
                        dialog.setView(dialogView);
                        final EditText editText = (EditText) dialogView.findViewById(R.id.password_dialog);
                        final EditText ancienEditText = (EditText) dialogView.findViewById(R.id.passwordAncien_dialog);

                        dialog.setNegativeButton("Annler", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        dialog.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                                passwordChanger = false;

                                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                                if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {


                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("membre");
                                    reference.orderByChild("id").equalTo(membre.getId()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if(!passwordChanger){

                                            Membre m = null;
                                            for (DataSnapshot val : dataSnapshot.getChildren()) {

                                                m = (Membre) val.getValue(Membre.class);

                                            }


                                            String passwordAncien = ancienEditText.getText().toString();
                                            String passwordCrypte = Base64.encodeToString(passwordAncien.getBytes(), Base64.DEFAULT);


                                            if (m.getPassword().equals(passwordCrypte)) {

                                                changerPassword(editText.getText().toString());

                                            } else {


                                                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                                dialog.setTitle("  Mot de passe invalide");
                                                dialog.setIcon(R.drawable.invalide_icon);
                                                dialog.setMessage("Ancien mot de passe invalide");
                                                dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });


                                                dialog.show();


                                            }


                                            passwordChanger = true;
                                        }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                } else {

                                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                    dialog.setTitle("  Pas de connexion internet");
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
                        });
                        dialog.show();




                    }else{

                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                        dialog.setTitle("  Pas de connexion internet");
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
                    passwordModif.setBackgroundColor(Color.WHITE);
                }

                return true;
            }
        });






        // Inflate the layout for this fragment
        return view;
    }






    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {

            if (requestCode == 1 && resultCode == RESULT_OK) {





                   final Uri imageUri = data.getData();
                    InputStream imageStream = null;

                try {
                   imageStream  = getContext().getContentResolver().openInputStream(imageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


                BitmapFactory.Options bmpFactory = new BitmapFactory.Options();
                    bmpFactory.inSampleSize = 3;

                    final Bitmap bitmap = BitmapFactory.decodeStream(imageStream,null,bmpFactory);

                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();


                    if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {


                        final ProgressDialog pd = ProgressDialog.show(getContext(),"","En cours...");

                        StorageReference storageReference = FirebaseStorage.getInstance().getReference("photo_profil/" + membre.getPhoto() + ".jpg");



                        UploadTask uploadTask = storageReference.putBytes(bitmapCompressed(bitmap));
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                //Failure
                                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                dialog.setMessage("Un probléme a été survenue\nveuillez résseiller");
                                dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();

                            }
                        });


                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {


                                String fileStr = repPhotoProfil.getAbsolutePath() + "/" + membre.getPhoto() + ".jpg";


                                try {
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(fileStr));
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }


                                pd.dismiss();
                                photoProfil.setImageBitmap(bitmap);
                                photoProfilDrawer.setImageBitmap(bitmap);



                                //Succes
                                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                dialog.setMessage("La photo de profil à été modifié");
                                dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            }
                        });




                    } else {

                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                        dialog.setTitle("  Pas de connexion internet");
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








            } else if (resultCode == RESULT_OK && requestCode == 2) {




                    Uri imageUri = data.getData();

                    InputStream imageStream = null;

                try {
                    imageStream = getContext().getContentResolver().openInputStream(imageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


                BitmapFactory.Options bmpFactory = new BitmapFactory.Options();
                    bmpFactory.inSampleSize = 3;


                    final Bitmap bitmap = BitmapFactory.decodeStream(imageStream,null,bmpFactory);
                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();


                    if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {

                        final ProgressDialog pd = ProgressDialog.show(getContext(),"","En cours...");
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference("photo_couverture/" + membre.getPhoto() + ".jpg");


                        UploadTask uploadTask = storageReference.putBytes(bitmapCompressed(bitmap));
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                //Failure
                                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                dialog.setMessage("Un probléme a été survenue\nveuillez résseiller");
                                dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            }
                        });


                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {


                                String fileStr = repPhotoCouverture.getAbsolutePath()+"/"+membre.getId()+".jpg";

                                try {
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(fileStr));
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }




                                pd.dismiss();


                                Bitmap bitmap1 = BitmapFactory.decodeFile(fileStr);
                                couverture.setImageBitmap(bitmap1);


                                //Succes
                                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                dialog.setMessage("La photo de couverture à été modifié");
                                dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            }
                        });





                    }else{


                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                        dialog.setTitle("  Pas de connexion internet");
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

    }




    public Membre getMembre() {
        return membre;
    }

    public void setMembre(Membre membre) {
        this.membre = membre;
    }







    //Verification password
    private boolean passwordValide(String password){
        boolean ok = false;

        if(password.length() >= 8 && stringContientUnNombre(password)){
            ok = true;
        }


        return ok;
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







    private void changerPassword(String password){




        if (passwordValide(password)) {

            String passwordCyrpte = Base64.encodeToString(password.getBytes(), Base64.DEFAULT);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("membre");

            membre.setPassword(passwordCyrpte);

            reference.child(membre.getId() + "").setValue(membre);


            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("  Opération réussie");
            dialog.setMessage("Votre mot de passe a été changé avec succès");
            dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });


            dialog.show();

        }else{


            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("  Erreur");
            dialog.setIcon(R.drawable.invalide_icon);
            dialog.setMessage("Password doit contenir au moins 8 caractéres et un nombre");
            dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });


            dialog.show();


        }

    }




    private byte[] bitmapCompressed(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOS);
        byte[] byteArray = byteArrayOS.toByteArray();
        return byteArray;
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
