package com.insat.ghazi.iac.Activitys;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.insat.ghazi.iac.Events;
import com.insat.ghazi.iac.R;

import java.io.File;

public class EventActivity extends AppCompatActivity {

    private Events event = null;
    private File repImageEvent = null;

    private ImageView image = null;
    private TextView titre = null;
    private TextView description = null;
    private ImageButton commentaireButton = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



          /*--------------------------- Les fichiers de l'application------------------------------------------*/

        String rootPath = getFilesDir().getAbsolutePath();

        repImageEvent = new File(rootPath+"/photo_events");


          /*---------------------------------------------------------------------------------------------------*/



        image = (ImageView) findViewById(R.id.event_image);
        titre = (TextView)  findViewById(R.id.event_titre);
        description = (TextView) findViewById(R.id.event_description);
        commentaireButton = (ImageButton) findViewById(R.id.commentaire_button);



        Intent intent = getIntent();

        int id = intent.getIntExtra("event_id",0);
        String title = intent.getStringExtra("event_titre");
        String desc = intent.getStringExtra("event_description");
        String datee = intent.getStringExtra("event_date");

        event = new Events(id,title,desc,datee);

        getSupportActionBar().setTitle(datee);

        Bitmap bitmap = BitmapFactory.decodeFile(repImageEvent.getAbsolutePath()+"/"+event.getId()+".jpg");
        image.setImageBitmap(bitmap);

        titre.setText(event.getTitre());

        description.setText(event.getDescription());


    }





    @Override
    protected void onStart() {
        super.onStart();


        //Si on clique sur le bouton du commentaire

        commentaireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //les commentaires ne s'ouvre si il ya une connexion internet
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {

                    Intent intent = new Intent(EventActivity.this, CommentairesActivity.class);
                    intent.putExtra("pathImageEvent", repImageEvent.getAbsolutePath() + "/" + event.getId() + ".jpg");
                    intent.putExtra("eventId",event.getId());
                    intent.putExtra("eventTitle",event.getTitre());
                    startActivity(intent);


                }else{

                    AlertDialog.Builder builder = new AlertDialog.Builder(EventActivity.this);
                    builder.setTitle("Impossible d'afficher les commentaires");
                    builder.setMessage("Vérifiez votre connexion internet Svp");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    });

                    builder.show();

                }

            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()){

            case android.R.id.home :
                EventActivity.this.finish();
                return true;


            default:return super.onOptionsItemSelected(item);
        }

    }
}
