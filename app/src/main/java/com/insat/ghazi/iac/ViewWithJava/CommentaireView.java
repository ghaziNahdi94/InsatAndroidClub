package com.insat.ghazi.iac.ViewWithJava;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.insat.ghazi.iac.Activitys.CommentairesActivity;
import com.insat.ghazi.iac.Commentaire;
import com.insat.ghazi.iac.R;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconTextView;


/**
 * Created by ozil_ on 15/02/2017.
 */

public class CommentaireView extends LinearLayout {


    private Context context = null;

    private de.hdodenhof.circleimageview.CircleImageView image = null;
    private TextView nomPrenom = null;
    private TextView dateCommentaire = null;
    private EmojiconTextView texteCommentaire = null;
    private TextView modifier = null;
    private TextView effacer = null;



    private long commentaireId = -1;
    private int eventId = -1;
    private String emailMembre = null;




    private final int ESPACE_ENTRE_LES_VUES = 5;



    public long getCommentaireId(){return this.commentaireId;}
    public int getEventId(){return this.eventId;}


    public CommentaireView(Context context,long commentaireId,int eventId,String emailMembre, Bitmap bitmap,String nomAndPrenom,String texte,String date,boolean proprietaire) {
        super(context);


        this.context = context;
        this.commentaireId = commentaireId;
        this.eventId = eventId;
        this.emailMembre = emailMembre;



        if (!(nomAndPrenom.length() < 20))
        nomAndPrenom = nomAndPrenom.substring(0,20)+"...";

        image = new de.hdodenhof.circleimageview.CircleImageView(context);  image.setImageBitmap(bitmap);
        nomPrenom = new TextView(context);nomPrenom.setText(nomAndPrenom); nomPrenom.setTypeface(null, Typeface.BOLD);
        dateCommentaire = new TextView(context); dateCommentaire.setText(date);
        texteCommentaire = new EmojiconTextView(context); texteCommentaire.setText(texte);texteCommentaire.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        final float scaleE = getContext().getResources().getDisplayMetrics().density;
        int tailleE = 17;
        int pixelsE = (int) (tailleE * scaleE + 0.5f);
        texteCommentaire.setEmojiconSize(pixelsE);
        modifier = new TextView(context); modifier.setText("Modifier");
        effacer = new TextView(context); effacer.setText("Supprimer");





        //cadre image
        image.setBorderWidth(1); image.setBorderColor(Color.BLACK);

        //size of textes
        nomPrenom.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        dateCommentaire.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        modifier.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);effacer.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);



        //couleur des textes
        nomPrenom.setTextColor(Color.parseColor("#FF429948")); // nom prénom en blanc
        dateCommentaire.setTextColor(Color.parseColor("#2F4F4F")); // date commentaire en rouge
        texteCommentaire.setTextColor(Color.BLACK); // texte en noire
        modifier.setTextColor(Color.parseColor("#005571"));effacer.setTextColor(Color.parseColor("#005571")); //couleur (modifier + effacer)



        //modifier et effacer commentaire listeners
        modifier.setOnClickListener(new ModifierCommentaireListener());
        effacer.setOnClickListener(new EffacerCommentaireListener());


        //paramétre de CommentaireView
        LinearLayout.LayoutParams commentaireLayoutParams = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(commentaireLayoutParams);
        this.setOrientation(HORIZONTAL);
        this.setPadding(10,10,10,10);
        this.setBackgroundColor(Color.parseColor("#f4fdf4"));



        //image à gauche
        final int taille = 50;
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (taille * scale + 0.5f);
        this.addView(image,pixels,pixels);



        //Layout for à droite (nomPrénom , texteCommentaire , dateCommentaire )
        LinearLayout texteLayout = new LinearLayout(context);
        texteLayout.setOrientation(VERTICAL);
        texteLayout.setPadding(6,4,4,4);
        texteLayout.addView(nomPrenom,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        texteLayout.addView(new View(context),LayoutParams.MATCH_PARENT,ESPACE_ENTRE_LES_VUES); // espace entre nomPrenom et le texte du commentaire
        texteLayout.addView(texteCommentaire,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        texteLayout.addView(new View(context),LayoutParams.MATCH_PARENT,ESPACE_ENTRE_LES_VUES); // espace entre texte du commentaire et la date

        //layout pour date+modifier+effacer commentaire
        LinearLayout layoutDateModifierEffacerCommentaire = new LinearLayout(context);
        layoutDateModifierEffacerCommentaire.setOrientation(HORIZONTAL);
        layoutDateModifierEffacerCommentaire.addView(dateCommentaire);
        //on affiche modifier et supprimer si le membre actuel est le propriétaire du commentaire
        if(proprietaire) {
            layoutDateModifierEffacerCommentaire.addView(new View(context), 50, LayoutParams.MATCH_PARENT);
            layoutDateModifierEffacerCommentaire.addView(modifier, LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            layoutDateModifierEffacerCommentaire.addView(new View(context), 25, LayoutParams.MATCH_PARENT);
            layoutDateModifierEffacerCommentaire.addView(effacer,LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        }
        texteLayout.addView(layoutDateModifierEffacerCommentaire,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);


        //parametre texteLayout
        LinearLayout.LayoutParams texteLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        texteLayoutParam.setMargins(10,0,0,0);
        texteLayout.setLayoutParams(texteLayoutParam);


        this.addView(texteLayout);



    }








    //methode utiles
    private void erreurCnx(){

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
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









    //Modifier+effacer  commentaire listeneres

    class ModifierCommentaireListener implements OnClickListener{

        private EmojiconEditText editCommentaire = null;


        @Override
        public void onClick(View v) {


            //il faut vérifier la cnx internet

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();





            if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED){


            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Modification du commentaire");



            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.dialog_modifier_commentaire,null);


            builder.setView(view);


                editCommentaire = (EmojiconEditText) view.findViewById(R.id.modificateur_commentaire);



            editCommentaire.setText(texteCommentaire.getText().toString());







            builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                }
            });



            builder.setPositiveButton("Modifier", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {



                    Commentaire commentaire = new Commentaire(commentaireId,dateCommentaire.getText().toString(),editCommentaire.getText().toString(),emailMembre,eventId);

                    DatabaseReference tableCommentaire = FirebaseDatabase.getInstance().getReference("commentaire");


                    tableCommentaire.child(commentaireId+"").setValue(commentaire);


                    dialog.dismiss();

                }
            });




            builder.show();




            }else{   //pas de cnx internet


                erreurCnx();



            }



        }



















    }









    class EffacerCommentaireListener implements OnClickListener{
        @Override
        public void onClick(View v) {

            //il faut vérifier la cnx internet

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();





            if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Suppression du commentaire");

                builder.setMessage("Voulez vous vraiment supprimer cet commentaire ?");


                builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                });


                builder.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DatabaseReference tableCommentaire = FirebaseDatabase.getInstance().getReference("commentaire");

                        tableCommentaire.child(commentaireId + "").removeValue();

                        dialog.dismiss();

                    }
                });


                builder.show();

            }else{  //pas de cnx

                erreurCnx();

            }


        }
    }

    public EmojiconTextView getTexteCommentaire() {
        return texteCommentaire;
    }


}
