package com.insat.ghazi.iac.AsynckTasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;
import com.insat.ghazi.iac.Activitys.CommentairesActivity;
import com.insat.ghazi.iac.ViewWithJava.CommentaireView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by ozil_ on 17/02/2017.
 */

public class AjouterCommentaireTask extends AsyncTask<Object,Integer,Bitmap> {



    private Context context = null;
    private StorageReference pathPhotoProfil = null;
    private LinearLayout commentaireLayout = null;
    private String nomPrenom = null;
    private String texteCommentaire = null;
    private String dateCommentaire = null;
    private boolean proprietaire = false;


    private long commentaireId = -1;
    private int eventId = -1;
    private String emailMembre = null;


    private String state;



    public AjouterCommentaireTask(Context context,long commentaireId,int eventId,String emailMembre ,StorageReference pathPhotoProfil, LinearLayout commentaireLayout, String nomPrenom, String texteCommentaire, String dateCommentaire,boolean proprietaire,String state) {
        this.context = context;
        this.pathPhotoProfil = pathPhotoProfil;
        this.commentaireLayout = commentaireLayout;
        this.nomPrenom = nomPrenom;
        this.texteCommentaire = texteCommentaire;
        this.dateCommentaire = dateCommentaire;
        this.proprietaire = proprietaire;
        this.commentaireId = commentaireId;
        this.eventId = eventId;
        this.emailMembre = emailMembre;
        this.state = state;
    }




    @Override
    protected Bitmap doInBackground(Object... params) {


        Bitmap bitmap = null;
        try {
          bitmap = Glide.with(context).using(new FirebaseImageLoader()).load(pathPhotoProfil).asBitmap().into(70,70).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        return bitmap;

    }




    @Override
    protected void onPostExecute(final Bitmap bitmap) {
        super.onPostExecute(bitmap);


        new Handler().post(new Runnable() {
            @Override
            public void run() {
                CommentaireView commentaireView = new CommentaireView(context,commentaireId,eventId,emailMembre,bitmap,nomPrenom,texteCommentaire,dateCommentaire,proprietaire);


                if(state.equalsIgnoreCase("add")) {

                    Log.e("hhhhhhhhhhhh",commentaireView.getTexteCommentaire().getText().toString());
                    commentaireLayout.addView(commentaireView, 0);
                }else if(state.equalsIgnoreCase("change")){



                    ArrayList<Object> list = getCommentaireViewById(commentaireView.getCommentaireId(),commentaireView.getEventId());


                    int pos = (int) list.get(0);
                    CommentaireView cv = (CommentaireView) list.get(1);




                    commentaireLayout.removeView(cv);

                    commentaireLayout.addView(commentaireView,pos);



                }else if(state.equalsIgnoreCase("remove")) {


                    ArrayList<Object> list = getCommentaireViewById(commentaireView.getCommentaireId(),commentaireView.getEventId());



                    CommentaireView cv = (CommentaireView) list.get(1);




                    commentaireLayout.removeView(cv);



                }

            }
        });



    }




    private ArrayList<Object> getCommentaireViewById(long id,int evt){

        ArrayList<Object> list = new ArrayList<>();

        final int childCount = commentaireLayout.getChildCount();

        for(int i=0;i<childCount;i++){

            View v = commentaireLayout.getChildAt(i);

            if(v instanceof CommentaireView){

                CommentaireView c = (CommentaireView) v;


                if(id == c.getCommentaireId() && evt == c.getEventId()){

                    list.add(i);
                    list.add(c);
                    break;
                }

            }

        }

        return list;
    }



}
