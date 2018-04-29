package com.insat.ghazi.iac.Fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.insat.ghazi.iac.Activitys.MainInConnectionActivity;
import com.insat.ghazi.iac.Adapters.RecycleListeMembreAdapter;
import com.insat.ghazi.iac.Membre;
import com.insat.ghazi.iac.R;
import com.insat.ghazi.iac.Adapters.RecycleListeStaffAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;


public class ListeStaffFragment extends Fragment {


    private Context context = null;
    private MainInConnectionActivity activity = null;

    private android.support.v7.widget.SearchView searchStaff = null;
    private RecyclerView recyclerView = null;
    private RecycleListeStaffAdapter recycleListeStaffAdapter = null;
    private ArrayList<Membre> staffsList = new ArrayList<Membre>();

    private ProgressDialog progressDialog = null;


    public ListeStaffFragment() {
        // Required empty public constructor
    }



    public void setArgs(Context context,MainInConnectionActivity activity){
        this.context = context;
        this.activity = activity;
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_liste_staff, container, false);




        searchStaff = (android.support.v7.widget.SearchView) view.findViewById(R.id.search_staff);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_liste_staff);

        searchStaff.setIconifiedByDefault(true);
        searchStaff.setFocusable(true);
        searchStaff.setIconified(false);
        searchStaff.requestFocusFromTouch();
        searchStaff.setQueryHint("Cherchez un staff");
        searchStaff.clearFocus();





        progressDialog = ProgressDialog.show(context,"","Chargement");


        DatabaseReference tableMembre = FirebaseDatabase.getInstance().getReference("membre");




        tableMembre.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                staffsList = new ArrayList<Membre>();


                for(DataSnapshot val : dataSnapshot.getChildren()){

                    Membre membre = (Membre) val.getValue(Membre.class);
                    if(!membre.getPoste().equals("rien"))
                        staffsList.add(membre);

                }




                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(linearLayoutManager);

                recycleListeStaffAdapter = new RecycleListeStaffAdapter(getContext(),activity,staffsList);


                recyclerView.setAdapter(recycleListeStaffAdapter);


                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






        //Search event

        searchStaff.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                String searching = searchStaff.getQuery().toString();


                if(!searching.isEmpty())
                {

                    ArrayList<Membre> list = new ArrayList<Membre>();

                    for(Membre m : staffsList){

                        String pn = m.getPrenom()+" "+m.getNom();
                        String np = m.getNom()+" "+m.getPrenom();

                        pn = pn.toUpperCase(); np = np.toUpperCase();
                        searching = searching.toUpperCase();

                        if(np.startsWith(searching) || pn.startsWith(searching))
                            list.add(m);

                    }


                    recycleListeStaffAdapter = new RecycleListeStaffAdapter(getContext(),activity,list);
                    recyclerView.setAdapter(recycleListeStaffAdapter);


                }else
                {

                    recycleListeStaffAdapter = new RecycleListeStaffAdapter(getContext(),activity,staffsList);
                    recyclerView.setAdapter(recycleListeStaffAdapter);

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
