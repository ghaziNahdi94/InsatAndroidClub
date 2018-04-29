package com.insat.ghazi.iac.Adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
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
import com.insat.ghazi.iac.Message;
import com.insat.ghazi.iac.R;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by ozil_ on 08/03/2017.
 */

public class RecycleListeMembreAdapter extends RecyclerView.Adapter<RecycleListeMembreAdapter.MemberViewHolder> {


    private MainInConnectionActivity activity = null;
    private Context context = null;
    private ArrayList<Membre> membres = null;


    public RecycleListeMembreAdapter(MainInConnectionActivity activity, Context context, ArrayList<Membre> membres) {

        this.context = context;
        this.membres = membres;
        this.activity = activity;

    }


    @Override
    public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_liste_membre, null);
        MemberViewHolder memberViewHolder = new MemberViewHolder(view);


        return memberViewHolder;
    }


    @Override
    public void onBindViewHolder(MemberViewHolder holder, int position) {

        final Membre membre = membres.get(position);


        String nom = membre.getPrenom() + " " + membre.getNom();


        if (nom.length() < 20)
            holder.name.setText(nom);
        else
            holder.name.setText(nom.substring(0, 20) + "...");


        StorageReference reference = FirebaseStorage.getInstance().getReference("/photo_profil/" + membre.getPhoto() + ".jpg");
        Glide.clear(holder.circleImageView);
        Glide.with(context).using(new FirebaseImageLoader()).load(reference).error(R.drawable.anonyme)
                .into(holder.circleImageView);


        holder.chat.setOnClickListener(new ChatClickListener(membre));


        holder.name.setOnClickListener(new VisiteProfilListener(membre));
        holder.circleImageView.setOnClickListener(new VisiteProfilListener(membre));


        if (membre.getParametres().getTel() == 0) {
            holder.tel.setVisibility(View.GONE);
        } else {
            holder.tel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent callIntent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:" + membre.getTel()));

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        Toast.makeText(context,"Il faut autoriser les appels dans les paramétres de l'application",Toast.LENGTH_SHORT).show();
                        activity.setCallString(membre.getTel());
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE},1);

                    }else {
                        context.startActivity(callIntent);
                    }




                }
            });
        }





        if(membre.getParametres().getSms() == 0){
            holder.sms.setVisibility(View.GONE);
        }else{
            holder.sms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.SEND_SMS},1);

                    final AlertDialog.Builder dialog = new AlertDialog.Builder(context);

                    String nom = membre.getPrenom() + " " + membre.getNom();



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
                            smsManager.sendTextMessage(membre.getTel(),null,editText.getText().toString(),null,null);
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
        return membres.size();
    }



    class MemberViewHolder extends RecyclerView.ViewHolder{

        private de.hdodenhof.circleimageview.CircleImageView circleImageView;
        private TextView name;
        private ImageButton tel,sms,chat;

        public MemberViewHolder(View itemView) {
            super(itemView);

            circleImageView = (de.hdodenhof.circleimageview.CircleImageView) itemView.findViewById(R.id.image_recycle_liste_membres);
            name = (TextView) itemView.findViewById(R.id.name_recycle_liste_membres);
            tel = (ImageButton) itemView.findViewById(R.id.tel_recycle_liste_membres);
            sms = (ImageButton) itemView.findViewById(R.id.sms_recycle_liste_membres);
            chat = (ImageButton) itemView.findViewById(R.id.chat_recycle_liste_membres);

        }



    }






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
