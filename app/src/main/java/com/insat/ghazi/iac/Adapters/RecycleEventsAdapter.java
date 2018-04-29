package com.insat.ghazi.iac.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.insat.ghazi.iac.Activitys.EventActivity;
import com.insat.ghazi.iac.Activitys.MainInConnectionActivity;
import com.insat.ghazi.iac.Events;
import com.insat.ghazi.iac.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ozil_ on 26/01/2017.
 */

public class RecycleEventsAdapter extends RecyclerView.Adapter<RecycleEventsAdapter.RecyclerViewHolder> {

    private Context context = null;
    private ArrayList<Events> events = null;
    private File repPhotoEvents = null;




    public RecycleEventsAdapter(ArrayList<Events> events,File repPhotoEvents,Context context) {
        this.events = events;
        this.repPhotoEvents = repPhotoEvents;
        this.context = context;
    }


    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyle_events,null);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);



        return recyclerViewHolder ;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {

        final Events event = events.get(position);


        String titre = event.getTitre();


        if(titre.length() > 20)
            titre = titre.substring(0,20)+"...";

        String description = event.getDescription();

        if(description.length() > 80)
            description = description.substring(0,80)+"...";




        Bitmap bitmap = BitmapFactory.decodeFile(repPhotoEvents.getAbsolutePath()+"/"+event.getId()+".jpg");
        holder.imageView.setImageBitmap(bitmap);
        holder.title.setText(titre);
        holder.description.setText(description);
        holder.date.setText(event.getDate());


        //si on clique sur un evenement on l'affiche dans "EventActivity"
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, EventActivity.class);

                intent.putExtra("event_id",event.getId());
                intent.putExtra("event_titre",event.getTitre());
                intent.putExtra("event_description",event.getDescription());
                intent.putExtra("event_date",event.getDate());

                context.startActivity(intent);


            }
        });

    }

    @Override
    public int getItemCount() {
        return events.size();
    }






    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView title,description,date;

        public RecyclerViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.image_recycle_events);
            title = (TextView) view.findViewById(R.id.title_recycle_events);
            description = (TextView) view.findViewById(R.id.description_recycle_events);
            date = (TextView) view.findViewById(R.id.date_recycle_events);

        }
    }


}
