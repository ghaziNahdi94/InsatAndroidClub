package com.insat.ghazi.iac.Fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.insat.ghazi.iac.Membre;
import com.insat.ghazi.iac.Parametres;
import com.insat.ghazi.iac.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;


public class ParametreFragment extends Fragment {


    private Context context = null;
    private File parametres = null;


    private LinearLayout layout1 = null;
    private CheckBox checkBox1 = null;
    private LinearLayout layout2 = null;
    private CheckBox checkBox2 = null;

    private Membre membre = null;

    public ParametreFragment() {
        // Required empty public constructor
    }


    public void setArgs(Context context,Membre membre){
        this.context = context;
        this.membre = membre;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_parametre, container, false);



  /*--------------------------- Les fichiers de l'application------------------------------------------*/
        String rootPath = context.getFilesDir().getAbsolutePath();
        parametres = new File(rootPath+"/param.txt");






        layout1 = (LinearLayout) view.findViewById(R.id.layout_check1);
        layout2 = (LinearLayout) view.findViewById(R.id.layout_check2);
        checkBox1 = (CheckBox) view.findViewById(R.id.check1);
        checkBox2 = (CheckBox) view.findViewById(R.id.check2);



        try {
            FileReader fr = new FileReader(parametres);
            BufferedReader br = new BufferedReader(fr);


            String l1 = br.readLine().trim();
            String l2 = br.readLine().trim();

            br.close(); fr.close();


            Integer integer1 = Integer.parseInt(l1);
            Integer integer2 = Integer.parseInt(l2);


            if(integer1 == 1)
                checkBox1.setChecked(true);
            else
                checkBox1.setChecked(false);


            if(integer2 == 1)
                checkBox2.setChecked(true);
            else
                checkBox2.setChecked(false);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Inflate the layout for this fragment
        return view;
    }











    @Override
    public void onStart() {
        super.onStart();


       layout1.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View view, MotionEvent motionEvent) {

               if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){

                   layout1.setBackgroundColor(Color.parseColor("#c7c7c7"));

                   ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                   NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();


                   if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED){


                       checkBox1.setChecked(!checkBox1.isChecked());

                       try {
                           FileReader fr = new FileReader(parametres);
                           BufferedReader br = new BufferedReader(fr);

                           br.readLine();
                           String l2 = br.readLine().trim();

                           br.close();fr.close();

                           FileWriter fw = new FileWriter(parametres,false);
                           BufferedWriter bw = new BufferedWriter(fw);


                           if(checkBox1.isChecked()) {
                               bw.write("1");
                               bw.newLine();
                               bw.write(l2);

                               Parametres parametres = new Parametres(1,Integer.parseInt(l2));
                               membre.setParametres(parametres);
                               DatabaseReference reference = FirebaseDatabase.getInstance().getReference("membre");
                               reference.child(membre.getId()+"").setValue(membre);

                           }else{
                               bw.write("0");
                               bw.newLine();
                               bw.write(l2);

                               Parametres parametres = new Parametres(0,Integer.parseInt(l2));
                               membre.setParametres(parametres);
                               DatabaseReference reference = FirebaseDatabase.getInstance().getReference("membre");
                               reference.child(membre.getId()+"").setValue(membre);


                           }


                           bw.close();fw.close();

                       } catch (FileNotFoundException e) {
                           e.printStackTrace();
                       } catch (IOException e) {
                           e.printStackTrace();
                       }


                   }else{

                       AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                       dialog.setTitle("  Pas de connexion internet");
                       dialog.setIcon(R.drawable.invalide_icon);
                       dialog.setMessage("Pour modifier les paramétres il faut connecter à internet");
                       dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               dialog.dismiss();
                           }
                       });


                       dialog.show();

                   }



               }else{
                    layout1.setBackgroundColor(Color.WHITE);


               }



               return true;
           }
       });








        layout2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){

                    layout2.setBackgroundColor(Color.parseColor("#c7c7c7"));

                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();


                    if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED){

                        checkBox2.setChecked(!checkBox2.isChecked());


                        try {
                            FileReader fr = new FileReader(parametres);
                            BufferedReader br = new BufferedReader(fr);

                            String l1 = br.readLine().trim();


                            br.close();fr.close();

                            FileWriter fw = new FileWriter(parametres,false);
                            BufferedWriter bw = new BufferedWriter(fw);


                            if(checkBox2.isChecked()) {
                                bw.write(l1);
                                bw.newLine();
                                bw.write("1");

                                Parametres parametres = new Parametres(Integer.parseInt(l1),1);
                                membre.setParametres(parametres);
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("membre");
                                reference.child(membre.getId()+"").setValue(membre);

                            }else{
                                bw.write(l1);
                                bw.newLine();
                                bw.write("0");

                                Parametres parametres = new Parametres(Integer.parseInt(l1),0);
                                membre.setParametres(parametres);
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("membre");
                                reference.child(membre.getId()+"").setValue(membre);


                            }


                            bw.close();fw.close();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }else{

                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                        dialog.setTitle("  Pas de connexion internet");
                        dialog.setIcon(R.drawable.invalide_icon);
                        dialog.setMessage("Pour modifier les paramétres il faut connecter à internet");
                        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });


                        dialog.show();

                    }

                }else{
                    layout2.setBackgroundColor(Color.WHITE);


                }

                return true;
            }
        });




        checkBox1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){

                    layout1.setBackgroundColor(Color.parseColor("#c7c7c7"));

                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();


                    if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED){



                        try {
                            FileReader fr = new FileReader(parametres);
                            BufferedReader br = new BufferedReader(fr);

                            br.readLine();
                            String l2 = br.readLine().trim();

                            br.close();fr.close();

                            FileWriter fw = new FileWriter(parametres,false);
                            BufferedWriter bw = new BufferedWriter(fw);


                            if(checkBox1.isChecked()) {
                                bw.write("1");
                                bw.newLine();
                                bw.write(l2);

                                Parametres parametres = new Parametres(1,Integer.parseInt(l2));
                                membre.setParametres(parametres);
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("membre");
                                reference.child(membre.getId()+"").setValue(membre);

                            }else{
                                bw.write("0");
                                bw.newLine();
                                bw.write(l2);

                                Parametres parametres = new Parametres(0,Integer.parseInt(l2));
                                membre.setParametres(parametres);
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("membre");
                                reference.child(membre.getId()+"").setValue(membre);


                            }


                            bw.close();fw.close();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }else{

                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                        dialog.setTitle("  Pas de connexion internet");
                        dialog.setIcon(R.drawable.invalide_icon);
                        dialog.setMessage("Pour modifier les paramétres il faut connecter à internet");
                        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });


                        dialog.show();

                    }



                }else{
                    layout1.setBackgroundColor(Color.WHITE);


                }


                return true;
            }
        });







        checkBox2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {



                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){

                    layout2.setBackgroundColor(Color.parseColor("#c7c7c7"));

                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();





                    if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED){




                        try {
                            FileReader fr = new FileReader(parametres);
                            BufferedReader br = new BufferedReader(fr);

                            String l1 = br.readLine().trim();


                            br.close();fr.close();

                            FileWriter fw = new FileWriter(parametres,false);
                            BufferedWriter bw = new BufferedWriter(fw);


                            if(checkBox2.isChecked()) {
                                bw.write(l1);
                                bw.newLine();
                                bw.write("1");

                                Parametres parametres = new Parametres(Integer.parseInt(l1),1);
                                membre.setParametres(parametres);
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("membre");
                                reference.child(membre.getId()+"").setValue(membre);

                            }else{
                                bw.write(l1);
                                bw.newLine();
                                bw.write("0");

                                Parametres parametres = new Parametres(Integer.parseInt(l1),0);
                                membre.setParametres(parametres);
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("membre");
                                reference.child(membre.getId()+"").setValue(membre);


                            }


                            bw.close();fw.close();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }else{

                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                        dialog.setTitle("  Pas de connexion internet");
                        dialog.setIcon(R.drawable.invalide_icon);
                        dialog.setMessage("Pour modifier les paramétres il faut connecter à internet");
                        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });


                        dialog.show();

                    }

                }else{
                    layout2.setBackgroundColor(Color.WHITE);


                }
                return false;
            }
        });

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
