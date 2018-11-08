package com.fancystachestudios.smarteleprompter.scriptRecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.fancystachestudios.smarteleprompter.R;
import com.fancystachestudios.smarteleprompter.ScriptSettingsActivity;
import com.fancystachestudios.smarteleprompter.customClasses.Script;
import com.fancystachestudios.smarteleprompter.room.ScriptRoomDatabase;
import com.fancystachestudios.smarteleprompter.room.ScriptSingleton;
import com.fancystachestudios.smarteleprompter.utility.ScriptSearchLoader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

public class ScriptRecyclerViewAdapter extends RecyclerView.Adapter<ScriptRecyclerViewAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Script> data;
    private ArrayList<Script> showing;
    ScriptSearchLoader scriptSearchLoader;
    LoaderManager loaderManager;

    String selectedTheme;
    String lightThemeValue;
    String darkThemeValue;

    Script clickedMenuScript;

    ScriptRoomDatabase scriptRoomDatabase;

    public ScriptRecyclerViewAdapter(Context context, LoaderManager loaderManager, List<Script> data){
        this.context = context;
        this.loaderManager = loaderManager;
        this.data = (ArrayList<Script>) data;
        showing = this.data;

        SharedPreferences themeSharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_pref_settings_key), MODE_PRIVATE);
        selectedTheme = themeSharedPreferences.getString(context.getString(R.string.shared_pref_settings_theme_key), "");
        lightThemeValue = context.getString(R.string.settings_theme_light);
        darkThemeValue = context.getString(R.string.settings_theme_dark);

        scriptRoomDatabase = ScriptSingleton.getInstance(context);
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Script currScript = showing.get(position);
        holder.titleText.setText(currScript.getTitle());
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a");
        holder.dateTimeText.setText(dateFormat.format(currScript.getDate()));

        holder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(holder.menuButton, position, currScript);
            }
        });
        if(selectedTheme.equals(lightThemeValue)){
            holder.menuButton.setImageResource(R.drawable.baseline_more_vert_black_24);
        }else if(selectedTheme.equals(darkThemeValue)){
            holder.menuButton.setImageResource(R.drawable.baseline_more_vert_white_24);
        }
    }


    @Override
    public int getItemCount() {
        return showing.size();
    }

    public void updateData(List<Script> newData){
        data = (ArrayList<Script>) newData;
        showing = data;
        notifyDataSetChanged();
    }

    public void updateShowing(List<Script> newData){
        Log.d("naptest", newData.size()+"");
        showing = (ArrayList<Script>) newData;
        notifyDataSetChanged();
    }

    public void clearList(){
        data.clear();
        showing.clear();
        notifyDataSetChanged();
    }

    public void sortByDate(){
        Collections.sort(showing, new Comparator<Script>() {
            @Override
            public int compare(Script script1, Script script2) {
                return script2.getDate().compareTo(script1.getDate());
            }
        });
        notifyDataSetChanged();
    }

    public void sortByTitle(){
        Collections.sort(showing, new Comparator<Script>() {
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
        }
        scriptSearchLoader.searchForTitle(data, searchString);
    }

    //Popup added referencing the answer by:
    //Shylendra Madda
    //From:
    //https://stackoverflow.com/questions/21329132/android-custom-dropdown-popup-menu
    private void showPopup(View pressedView, final int index, final Script currScript){
        PopupMenu popupMenu = new PopupMenu(context, pressedView);
        popupMenu.getMenuInflater()
                .inflate(R.menu.main_script_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int clickedId = menuItem.getItemId();
                if(clickedId == R.id.menu_main_script_scriptSettings){
                    Intent intent = new Intent(context, ScriptSettingsActivity.class);
                    intent.putExtra(context.getString(R.string.menu_main_script_settings_script_key), showing.get(index));
                    context.startActivity(intent);
                }else if(clickedId == R.id.menu_main_script_delete){
                    //Created referencing answer by "Maaalte" edited by "Nicholas Betsworth" at https://stackoverflow.com/questions/5127407/how-to-implement-a-confirmation-yes-no-dialogpreference
                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.script_settings_delete_dialog_title))
                            .setMessage(String.format(context.getString(R.string.script_settings_delete_dialog_message), currScript.getTitle()))
                            .setPositiveButton(context.getString(R.string.script_settings_delete_dialog_yes), new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    AsyncTask.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            scriptRoomDatabase.scriptDao().delete(currScript.getId());
                                        }
                                    });
                                }
                            })
                            .setNegativeButton(context.getString(R.string.script_settings_delete_dialog_no), null)
                            .show();
                }
                return true;
            }
        });
        popupMenu.show();
    }
}
