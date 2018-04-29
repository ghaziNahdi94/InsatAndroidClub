package com.insat.ghazi.iac.Activitys;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.insat.ghazi.iac.Membre;
import com.insat.ghazi.iac.R;

import java.util.UUID;

public class ProfilActivity extends AppCompatActivity {

    private ImageView photoCouverture = null;
    private de.hdodenhof.circleimageview.CircleImageView photoProfil = null;
    private TextView nameProfil = null;
    private TextView poste = null;
    private TextView email = null;
    private ImageButton tel = null;
    private ImageButton sms = null;
    private ImageButton chat = null;


    private Membre membre = null;
    private long idMembre = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);


        photoCouverture = (ImageView) findViewById(R.id.couvertureProfilActivity);
        photoProfil = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.photo_profil_activity);
        nameProfil = (TextView) findViewById(R.id.name_profil_activity);
        poste = (TextView) findViewById(R.id.poste_profil_activity);
        email = (TextView) findViewById(R.id.email_profil_activity);
        tel = (ImageButton) findViewById(R.id.tel_profil_activity);
        sms = (ImageButton) findViewById(R.id.sms_profil_activity);
        chat = (ImageButton) findViewById(R.id.chat_profil_activity);


             Intent intent = getIntent();
            idMembre = intent.getLongExtra("id",-1);






            DatabaseReference tableMembre = FirebaseDatabase.getInstance().getReference("membre");
            tableMembre.orderByChild("id").equalTo(idMembre).addValueEventListener(new MembreListner(this));





            tel.setOnClickListener(new TelClickListener());

            sms.setOnClickListener(new SmsClickListener());

            chat.setOnClickListener(new ChatClickListener());





    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()){

            case android.R.id.home :
                ProfilActivity.this.finish();
                return true;


            default:return super.onOptionsItemSelected(item);
        }
    }




    //Class Listeners
    class MembreListner implements ValueEventListener{

        private Context context;

        public  MembreListner(Context context){
            this.context = context;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {


            for (DataSnapshot val : dataSnapshot.getChildren())
                membre = (Membre) val.getValue(Membre.class);



            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


            StorageReference photoProfilFirebase = FirebaseStorage.getInstance().getReference("photo_profil/"+membre.getPhoto()+".jpg");

            Glide.clear(photoProfil);
            Glide.with(context.getApplicationContext()).using(new FirebaseImageLoader()).load(photoProfilFirebase).error(R.drawable.anonyme)
                    .signature(new StringSignature(UUID.randomUUID().toString())).skipMemoryCache(true)
                    .into(photoProfil);






            StorageReference photoCouvertureFirebase = FirebaseStorage.getInstance().getReference("photo_couverture/"+membre.getPhoto()+".jpg");
            Glide.clear(photoCouverture);
            Glide.with(context.getApplicationContext()).using(new FirebaseImageLoader()).load(photoCouvertureFirebase).error(R.drawable.bache2015)
                    .signature(new StringSignature(UUID.randomUUID().toString())).skipMemoryCache(true)
                    .into(photoCouverture);






            String name = membre.getPrenom()+" "+membre.getNom();
            if (name.length() < 17) {
                getSupportActionBar().setTitle(name);
                nameProfil.setText(name);
            }else {
                getSupportActionBar().setTitle(name.substring(0, 17) + "...");
                nameProfil.setText(name.substring(0,17)+"...");
            }


            email.setText(membre.getEmail());





            if(!membre.getPoste().equalsIgnoreCase("rien"))
                poste.setText(membre.getPoste());
            else
                poste.setText("");





            if(membre.getParametres().getTel() == 0)
                tel.setVisibility(View.GONE);
            else
                tel.setVisibility(View.VISIBLE);



            if (membre.getParametres().getSms() == 0)
                sms.setVisibility(View.GONE);
            else
                sms.setVisibility(View.VISIBLE);



        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    }




    class TelClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + membre.getTel()));

            if (ActivityCompat.checkSelfPermission(ProfilActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            startActivity(intent);
        }
    }




    class SmsClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            ActivityCompat.requestPermissions(ProfilActivity.this,new String[]{Manifest.permission.SEND_SMS},1);

            final AlertDialog.Builder dialog = new AlertDialog.Builder(ProfilActivity.this);

            String nom = membre.getPrenom() + " " + membre.getNom();



            if (nom.length() < 20)
                dialog.setTitle("SMS à "+nom);
            else
                dialog.setTitle("SMS à "+nom.substring(0, 20) + "...");


            final EditText editText = new EditText(ProfilActivity.this);
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
    }





    class ChatClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ProfilActivity.this, MessageActivity.class);

            intent.putExtra("idMembreConversation",membre.getId());
            intent.putExtra("prenomNomMembreConversation",membre.getPrenom()+" "+membre.getNom());
            intent.putExtra("emailMembreConversation",membre.getEmail());
            intent.putExtra("telMembreConversation",membre.getTel());
            intent.putExtra("appelMembreConversation",membre.getParametres().getTel());
            intent.putExtra("smsMembreConversation",membre.getParametres().getSms());
            startActivity(intent);
        }
    }



}
