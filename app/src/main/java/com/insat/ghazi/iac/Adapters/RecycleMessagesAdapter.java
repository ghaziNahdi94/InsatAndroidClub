package com.insat.ghazi.iac.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.insat.ghazi.iac.Activitys.MessageActivity;
import com.insat.ghazi.iac.Membre;
import com.insat.ghazi.iac.Message;
import com.insat.ghazi.iac.MessageInMessageFrgment;
import com.insat.ghazi.iac.R;
import com.rockerhieu.emojicon.EmojiconTextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by ozil_ on 24/02/2017.
 */

public class RecycleMessagesAdapter extends RecyclerView.Adapter<RecycleMessagesAdapter.MessageViewHolder>{

    private Context context = null;
    private ArrayList<MessageInMessageFrgment>  messages = null;
    private File repPhotoMessages = null;
    private LayoutInflater layoutInflater = null;
    private DatabaseReference tableMembre = null;
    private ValueEventListener listener = null;





    public RecycleMessagesAdapter(Context context, ArrayList<MessageInMessageFrgment> messages, File repPhotoMessages) {
        this.context = context;
        this.messages = messages;
        this.repPhotoMessages = repPhotoMessages;
        layoutInflater = LayoutInflater.from(context);


    }





    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = layoutInflater.inflate(R.layout.item_recycle_messages,parent,false);


        MessageViewHolder messageViewHolder = new MessageViewHolder(view);

        return messageViewHolder;
    }



    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {


        final MessageInMessageFrgment message = messages.get(position);




        if(message.getNomPrenom().length() <= 15)
            holder.nomPrenom.setText(message.getNomPrenom());
        else
            holder.nomPrenom.setText(message.getNomPrenom().substring(0,15)+"...");

        holder.time.setText(adaptTime(message.getTime()));
        holder.date.setText(adaptDate(message.getDate()));


        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();





        final File msgFile = new File(repPhotoMessages.getAbsolutePath()+"/"+message.getImage()+".jpg");

        if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {



            final StorageReference reference = FirebaseStorage.getInstance().getReference("/photo_profil/" + message.getImage() + ".jpg");

            Glide.clear(holder.image);
            Glide.with(context).using(new FirebaseImageLoader()).load(reference).error(R.drawable.anonyme)
                    .into(holder.image);


            new Thread(new Runnable() {
                @Override
                public void run() {




                    if(msgFile.exists())
                        msgFile.delete();


                  reference.getFile(msgFile).addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {
                          Log.e("hhh","failure");
                      }
                  });



                }
            }).start();







        }else{

            Bitmap bitmap = BitmapFactory.decodeFile(msgFile.getAbsolutePath());
            holder.image.setImageBitmap(bitmap);

        }





        if(message.getTexte().toString().length() <= 20)
            holder.texte.setText(message.getTexte());
        else
            holder.texte.setText(message.getTexte().substring(0,20)+"...");



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(message.getInfos() == null) {
                    //on récupére les informations du membre de la conversation
                    tableMembre = FirebaseDatabase.getInstance().getReference("membre");
                    listener = tableMembre.orderByChild("id").equalTo(message.getImage()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Membre membre = null;
                            for (DataSnapshot val : dataSnapshot.getChildren()) {
                                membre = (Membre) val.getValue(Membre.class);
                            }


                            Intent intent = new Intent(context, MessageActivity.class);
                            intent.putExtra("idMembreConversation", message.getImage());
                            intent.putExtra("prenomNomMembreConversation", membre.getPrenom() + " " + membre.getNom());
                            intent.putExtra("emailMembreConversation", membre.getEmail());
                            intent.putExtra("telMembreConversation",membre.getTel());
                            intent.putExtra("appelMembreConversation",membre.getParametres().getTel());
                            intent.putExtra("smsMembreConversation",membre.getParametres().getSms());


                            context.startActivity(intent);


                            tableMembre.removeEventListener(listener);



                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                }else{


                    String[] infos = message.getInfos();
                    int[] parametrs = message.getParametres();
                    Intent intent = new Intent(context, MessageActivity.class);
                    intent.putExtra("idMembreConversation", message.getImage());
                    intent.putExtra("prenomNomMembreConversation", infos[0] + " " + infos[1]);
                    intent.putExtra("emailMembreConversation", infos[2]);
                    intent.putExtra("telMembreConversation",infos[3]);
                    intent.putExtra("appelMembreConversation",parametrs[0]);
                    intent.putExtra("smsMembreConversation",parametrs[1]);
                    context.startActivity(intent);


                }




            }
        });


    }


    @Override
    public int getItemCount() {
        return messages.size();
    }






    private String adaptTime(String time){

        int hour = Integer.parseInt(time.split(":")[0].trim());

        String h = "";
        if(hour < 10)
            h = "0"+hour;
        else
            h = hour+"";


        int minute =Integer.parseInt(time.split(":")[1].trim());

        String m ="";

        if(minute < 10)
            m = "0"+minute;
        else
            m = minute+"";


        return h+":"+m;


    }

    private String adaptDate(String date){

        int year = Integer.parseInt(date.split("/")[2].trim());

        String y = "";

        if(year < 10)
            y = "0"+year;
        else
            y = year+"";


        int month = Integer.parseInt(date.split("/")[1].trim());

        String m = "";

        if(month < 10)
            m = "0"+month;
        else
            m = month+"";


        int day = Integer.parseInt(date.split("/")[0].trim());


        String d = "";

        if(day < 10)
            d = "0"+day;
        else
            d = day+"";



        return d+"/"+m+"/"+y;


    }




   public class MessageViewHolder extends RecyclerView.ViewHolder{

        private de.hdodenhof.circleimageview.CircleImageView image = null;
        private TextView date = null;
        private TextView time = null;
        private EmojiconTextView texte = null;
        private TextView nomPrenom = null;


        public MessageViewHolder(View view) {
            super(view);

            image = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.photoProfil_messages);
            date = (TextView) view.findViewById(R.id.date_messages);
            time = (TextView) view.findViewById(R.id.time_messages);
            texte = (EmojiconTextView) view.findViewById(R.id.texte_messages);
            nomPrenom = (TextView) view.findViewById(R.id.nomPrenom_messages);

        }
    }



}
