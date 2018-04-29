package com.insat.ghazi.iac.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.insat.ghazi.iac.Events;
import com.insat.ghazi.iac.R;
import com.insat.ghazi.iac.Adapters.RecycleEventsAdapter;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;



/**
 * A simple {@link Fragment} subclass.
 */
public class EvenentsFragment extends Fragment {


    private Context context = null;
    private ArrayList<Events> eventsList = null;
    private File repImageEvent = null;


    private RecyclerView recyclerView = null;
    private RecycleEventsAdapter recycleEventsAdapter = null;
    private RecyclerView.LayoutManager layoutManager = null;





    public EvenentsFragment(){}


    public void setArgs (Context context, ArrayList<Events> eventsList,File repImageEvent)
    {

        this.context = context;
        this.eventsList = eventsList;
        this.repImageEvent = repImageEvent;


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_evenents, container, false);


            recyclerView = (RecyclerView) view.findViewById(R.id.recycle_events);




            recycleEventsAdapter = new RecycleEventsAdapter(eventsList,repImageEvent,context);
            layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(recycleEventsAdapter);





        // Inflate the layout for this fragment
        return view;
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
