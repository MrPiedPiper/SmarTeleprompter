package com.fancystachestudios.smarteleprompter.scriptRecyclerView;

import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fancystachestudios.smarteleprompter.R;
import com.fancystachestudios.smarteleprompter.customClasses.Script;
import com.fancystachestudios.smarteleprompter.utility.ScriptSearchLoader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScriptRecyclerViewAdapter extends RecyclerView.Adapter<ScriptRecyclerViewAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Script> data;
    ScriptSearchLoader scriptSearchLoader;
    LoaderManager loaderManager;

    public ScriptRecyclerViewAdapter(Context context, LoaderManager loaderManager, ArrayList<Script> data){
        this.context = context;
        this.loaderManager = loaderManager;
        this.data = data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.script_list_item_title) TextView titleText;
        @BindView(R.id.script_list_item_date_time) TextView dateTimeText;
        @BindView(R.id.script_list_item_menu) ImageButton menuButton;

        ViewHolder(View v){
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.script_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Script currScript = data.get(position);
        holder.titleText.setText(currScript.getTitle());
        DateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy hh:mm:ss");
        holder.dateTimeText.setText(dateFormat.format(currScript.getDate()));
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateData(ArrayList<Script> newData){
        data = newData;
        notifyDataSetChanged();
    }

    public void clearList(){
        data.clear();
        notifyDataSetChanged();
    }

    public void sortByDate(){
        Collections.sort(data, new Comparator<Script>() {
            @Override
            public int compare(Script script1, Script script2) {
                return script1.getDate().compareTo(script2.getDate());
            }
        });
        notifyDataSetChanged();
    }

    public void sortByTitle(){
        Collections.sort(data, new Comparator<Script>() {
            @Override
            public int compare(Script script1, Script script2) {
                return script1.getTitle().compareTo(script2.getTitle());
            }
        });
        notifyDataSetChanged();
    }

    public void searchForTitle(String searchString){
        if(scriptSearchLoader == null){
            scriptSearchLoader = new ScriptSearchLoader(context, loaderManager);
            scriptSearchLoader.searchForTitle(data, searchString);
        }
    }
}
