package com.insat.ghazi.iac.Fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.insat.ghazi.iac.Activitys.MainInConnectionActivity;
import com.insat.ghazi.iac.Adapters.RecycleListeMembreAdapter;
import com.insat.ghazi.iac.Membre;
import com.insat.ghazi.iac.R;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListMemberFragment extends Fragment {


    private MainInConnectionActivity activity = null;
    private Context context = null;

    private android.support.v7.widget.SearchView membreSearch = null;
    private RecyclerView membreRecycler = null;


    private ArrayList<Membre> membres = null;
    private RecycleListeMembreAdapter adapter = null;


    private ProgressDialog progressDialog = null;


    public ListMemberFragment() {
        // Required empty public constructor
    }


    public void setArgs(MainInConnectionActivity activity,Context context){
        this.context = context;
        this.activity = activity;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_list_member, container, false);




        membreSearch = (android.support.v7.widget.SearchView) view.findViewById(R.id.search_membre);
        membreRecycler = (RecyclerView) view.findViewById(R.id.recycle_liste_membre);

        membreSearch.setIconifiedByDefault(true);
        membreSearch.setFocusable(true);
        membreSearch.setIconified(false);
        membreSearch.requestFocusFromTouch();
        membreSearch.setQueryHint("Cherchez un membre");
        membreSearch.clearFocus();





        progressDialog = ProgressDialog.show(context,"","Chargement");


        DatabaseReference tableMembre = FirebaseDatabase.getInstance().getReference("membre");




        tableMembre.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                membres = new ArrayList<Membre>();


               for(DataSnapshot val : dataSnapshot.getChildren()){

                   Membre membre = (Membre) val.getValue(Membre.class);
                   if(membre.getPoste().equals("rien"))
                   membres.add(membre);

               }




                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                membreRecycler.setLayoutManager(linearLayoutManager);

                adapter = new RecycleListeMembreAdapter(activity,context,membres);


                membreRecycler.setAdapter(adapter);


               progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






        //Search event

        membreSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                String searching = membreSearch.getQuery().toString();


                if(!searching.isEmpty())
                {

                    ArrayList<Membre> list = new ArrayList<Membre>();

                    for(Membre m : membres){

                        String pn = m.getPrenom()+" "+m.getNom();
                        String np = m.getNom()+" "+m.getPrenom();
                        np = np.toUpperCase(); pn = pn.toUpperCase();
                        searching = searching.toUpperCase();

                        if(np.startsWith(searching) || pn.startsWith(searching))
                            list.add(m);

                    }


                    adapter = new RecycleListeMembreAdapter(activity,context,list);
                    membreRecycler.setAdapter(adapter);


                }else
                {

                    adapter = new RecycleListeMembreAdapter(activity,context,membres);
                    membreRecycler.setAdapter(adapter);

                }



                return true;
            }
        });










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
