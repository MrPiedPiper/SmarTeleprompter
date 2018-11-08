package com.fancystachestudios.smarteleprompter;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.crashlytics.android.Crashlytics;
import com.fancystachestudios.smarteleprompter.customClasses.Script;
import com.fancystachestudios.smarteleprompter.scriptRecyclerView.ScriptRecyclerViewAdapter;
import com.fancystachestudios.smarteleprompter.utility.ScriptSearchLoader;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
        implements
            AdapterView.OnItemSelectedListener,
            ScriptSearchLoader.scriptSearchLoaderInterface{

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

    ScriptSearchLoader scriptSearchLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        applyTheme();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        sortTitleKey = getString(R.string.main_sort_name);
        sortDateKey = getString(R.string.main_sort_date);

        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("naputest", "FAB clicked");
            }
        });

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.main_spinner_items,
                android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);
        sortSpinner.setOnItemSelectedListener(this);

        final Date date = new Date();
        final ArrayList<Script> testData = new ArrayList<>();
        testData.add(new Script(date.getTime(), "test1", date.getTime(), date.getTime()));
        testData.add(new Script(date.getTime()+1000, "btest2", date.getTime()+1000, date.getTime()+1000));
        testData.add(new Script(date.getTime()+2000, "ftest3", date.getTime()+2000, date.getTime()+2000));
        testData.add(new Script(date.getTime()+3000, "atest4", date.getTime()+3000, date.getTime()+3000));
        testData.add(new Script(date.getTime()+4000, "gtest5", date.getTime()+4000, date.getTime()+4000));
        testData.add(new Script(date.getTime()+5000, "dtest6", date.getTime()+5000, date.getTime()+5000));
        testData.add(new Script(date.getTime()+6000, "ctest7", date.getTime()+6000, date.getTime()+6000));
        testData.add(new Script(date.getTime()+7000, "etest8", date.getTime()+7000, date.getTime()+7000));

        adapter = new ScriptRecyclerViewAdapter(this, getSupportLoaderManager(), testData);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        scriptSearchLoader = new ScriptSearchLoader(this, getSupportLoaderManager());
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
    public void searchComplete(ArrayList<Script> searchResults) {
        adapter.updateData(searchResults);
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
