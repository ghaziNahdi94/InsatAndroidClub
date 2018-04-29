package com.insat.ghazi.iac.Adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.insat.ghazi.iac.Activitys.MainInConnectionActivity;
import com.insat.ghazi.iac.Activitys.MessageActivity;
import com.insat.ghazi.iac.Activitys.ProfilActivity;
import com.insat.ghazi.iac.Membre;
import com.insat.ghazi.iac.R;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by ozil_ on 26/01/2017.
 */

public class RecycleListeStaffAdapter extends RecyclerView.Adapter<RecycleListeStaffAdapter.RecycleViewHolder> {

    private ArrayList<Membre> staffsList = null;
    private Context context = null;
    private MainInConnectionActivity activity = null;

    public RecycleListeStaffAdapter(Context context, MainInConnectionActivity activity, ArrayList<Membre> staffsList) {
        this.staffsList = staffsList;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_liste_staff,null);
        RecycleViewHolder recyclerViewHolder = new RecycleViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecycleViewHolder holder, int position) {

        final Membre staff = staffsList.get(position);


        String nom = staff.getPrenom() + " " + staff.getNom();



        if (nom.length() < 20)
            holder.name.setText(nom);
        else
            holder.name.setText(nom.substring(0, 20) + "...");


        holder.poste.setText(staff.getPoste());





        StorageReference reference = FirebaseStorage.getInstance().getReference("/photo_profil/" + staff.getPhoto() + ".jpg");

        Glide.clear(holder.circleImageView);
        Glide.with(context).using(new FirebaseImageLoader()).load(reference).error(R.drawable.anonyme)
                .into(holder.circleImageView);







        holder.chat.setOnClickListener(new ChatClickListener(staff));



        holder.name.setOnClickListener(new VisiteProfilListener(staff));
        holder.circleImageView.setOnClickListener(new VisiteProfilListener(staff));



        if (staff.getParametres().getTel() == 0) {
            holder.tel.setVisibility(View.GONE);
        } else {
            holder.tel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + staff.getTel()));

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context,"Il faut autoriser les appels dans les paramétres de l'application",Toast.LENGTH_SHORT).show();
                        activity.setCallString(staff.getTel());
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE},1);
                        return;
                    }

                    context.startActivity(intent);
                }
            });
        }




        if(staff.getParametres().getSms() == 0){
            holder.sms.setVisibility(View.GONE);
        }else{
            holder.sms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.SEND_SMS},1);

                    final AlertDialog.Builder dialog = new AlertDialog.Builder(context);

                    String nom = staff.getPrenom() + " " + staff.getNom();



                    if (nom.length() < 20)
                        dialog.setTitle("SMS à "+nom);
                    else
                        dialog.setTitle("SMS à "+nom.substring(0, 20) + "...");


                    final EditText editText = new EditText(context);
                    dialog.setView(editText);

                    dialog.setPositiveButton("Envoyer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(staff.getTel(),null,editText.getText().toString(),null,null);
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
                }
            });
        }





    }

    @Override
    public int getItemCount() {
        return staffsList.size();
    }




    public static class RecycleViewHolder extends RecyclerView.ViewHolder{

       private de.hdodenhof.circleimageview.CircleImageView circleImageView;
        private TextView name,poste;
        private ImageButton tel,sms,chat;

         public RecycleViewHolder(View itemView) {
            super(itemView);

            circleImageView =(de.hdodenhof.circleimageview.CircleImageView) itemView.findViewById(R.id.image_recycle_liste_staffs);
            name = (TextView) itemView.findViewById(R.id.name_recycle_liste_staffs);
            poste = (TextView) itemView.findViewById(R.id.poste_recycle_liste_staffs);
             tel = (ImageButton) itemView.findViewById(R.id.tel_recycle_liste_staffs);
             sms = (ImageButton) itemView.findViewById(R.id.sms_recycle_liste_staffs);
             chat = (ImageButton) itemView.findViewById(R.id.chat_recycle_liste_staffs);
        }
    }







    /*-------------les evenements des boutons de communications-----------------------*/


    class ChatClickListener implements View.OnClickListener{

        private Membre membre = null;

        public ChatClickListener(Membre membre){

            this.membre = membre;
        }

        @Override
        public void onClick(View view) {

            Intent intent = new Intent(context, MessageActivity.class);

            intent.putExtra("idMembreConversation",membre.getId());
            intent.putExtra("prenomNomMembreConversation",membre.getPrenom()+" "+membre.getNom());
            intent.putExtra("emailMembreConversation",membre.getEmail());
            intent.putExtra("telMembreConversation",membre.getTel());
            intent.putExtra("appelMembreConversation",membre.getParametres().getTel());
            intent.putExtra("smsMembreConversation",membre.getParametres().getSms());
            context.startActivity(intent);

        }
    }



    class VisiteProfilListener implements View.OnClickListener{

        private Membre membre = null;


        public VisiteProfilListener(Membre membre){ this.membre = membre;}

        @Override
        public void onClick(View view) {

            Intent intent = new Intent(context, ProfilActivity.class);
            intent.putExtra("id",membre.getId());
            context.startActivity(intent);


        }
    }










}
