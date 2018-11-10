package com.fancystachestudios.smarteleprompter;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.crashlytics.android.Crashlytics;
import com.fancystachestudios.smarteleprompter.customClasses.Script;
import com.fancystachestudios.smarteleprompter.room.ScriptDao;
import com.fancystachestudios.smarteleprompter.room.ScriptRoomDatabase;
import com.fancystachestudios.smarteleprompter.room.ScriptSingleton;
import com.fancystachestudios.smarteleprompter.room.ScriptViewModel;
import com.fancystachestudios.smarteleprompter.scriptRecyclerView.ScriptRecyclerViewAdapter;
import com.fancystachestudios.smarteleprompter.teleprompterWidget.TeleprompterWidgetIntentService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

/**
 * Room access and LiveDate done referencing https://codelabs.developers.google.com/codelabs/android-room-with-a-view/
 */
public class MainActivity extends AppCompatActivity
        implements
            AdapterView.OnItemSelectedListener,
            ScriptRecyclerViewAdapter.scriptSearchInterface{

    @BindView(R.id.main_fab)
    FloatingActionButton FAB;

    @BindView(R.id.main_edittext_search)
    EditText searchEditText;

    @BindView(R.id.main_spinner_sort)
    Spinner sortSpinner;

    @BindView(R.id.main_recyclerview_scripts)
    RecyclerView recyclerView;

    @BindView(R.id.main_layout)
    ConstraintLayout mainLayout;

    ScriptRecyclerViewAdapter adapter;

    String selectedSort;

    private String sortTitleKey;
    private String sortDateKey;

    ScriptRoomDatabase scriptRoomDatabase;
    ScriptDao dao;

    Context context;

    List<Script> startScriptArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        applyTheme();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        context = this;

        sortTitleKey = getString(R.string.main_sort_name);
        sortDateKey = getString(R.string.main_sort_date);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.main_spinner_items,
                android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);
        sortSpinner.setOnItemSelectedListener(this);

        scriptRoomDatabase = ScriptSingleton.getInstance(this);
        dao = scriptRoomDatabase.scriptDao();
        ScriptViewModel scriptViewModel = ViewModelProviders.of(this).get(ScriptViewModel.class);

        scriptViewModel.getAllScripts().observe(this, new Observer<List<Script>>() {
            @Override
            public void onChanged(@Nullable List<Script> scripts) {
                startScriptArrayList = scripts;
                TeleprompterWidgetIntentService.startActionUpdateTeleprompterWidgets(getApplicationContext());
                adapter.updateData(startScriptArrayList);
            }
        });

        adapter = new ScriptRecyclerViewAdapter(this, getSupportLoaderManager(), startScriptArrayList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TeleprompterActivity.class);
                intent.putExtra(getString(R.string.teleprompter_pass_mode), getString(R.string.teleprompter_pass_mode_new));
                startActivity(intent);
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.searchForTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        restoreViews(savedInstanceState);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        selectedSort = parent.getItemAtPosition(pos).toString();
        if(selectedSort.equals(sortDateKey)){
            adapter.sortByDate();
        }else if(selectedSort.equals(sortTitleKey)){
            adapter.sortByTitle();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void searchComplete(final ArrayList<Script> searchResults) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.updateShowing(searchResults);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.main_menu_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(getString(R.string.main_edittext_search_savestate), searchEditText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    private void restoreViews(Bundle savedInstanceState){
        if(savedInstanceState != null){
            searchEditText.setText(savedInstanceState.getString(getString(R.string.main_edittext_search_savestate)));
        }
    }

    private void applyTheme(){
        SharedPreferences themeSharedPreferences = getSharedPreferences(getString(R.string.shared_pref_settings_key), MODE_PRIVATE);
        String selectedTheme = themeSharedPreferences.getString(getString(R.string.shared_pref_settings_theme_key), "");
        String lightThemeValue = getString(R.string.settings_theme_light);
        String darkThemeValue = getString(R.string.settings_theme_dark);
        if(selectedTheme.equals(lightThemeValue)){
            setTheme(R.style.AppThemeLight);
        }else if(selectedTheme.equals(darkThemeValue)){
            setTheme(R.style.AppThemeDark);
        }
    }
}
