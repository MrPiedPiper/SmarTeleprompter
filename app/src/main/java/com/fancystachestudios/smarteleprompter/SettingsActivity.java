package com.fancystachestudios.smarteleprompter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.content.SharedPreferences.Editor;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Thanks to
 * https://androidresearch.wordpress.com/2012/03/31/writing-and-reading-from-sharedpreferences/
 * for the overview of getting/writing sharedpreferences
 */

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.settings_theme_spinner)
    Spinner themeSpinner;
    @BindView(R.id.settings_layout)
    LinearLayout settingsLayout;
    @BindView(R.id.settings_scroll_speed_edittext)
    EditText scrollSpeedDefaultEditText;
    @BindView(R.id.settings_font_size_edittext)
    EditText fontSizeDefaultEditText;

    SharedPreferences sharedPreferences;

    String currTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyTheme();
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_settings_key), MODE_PRIVATE);
        String selectedTheme = sharedPreferences.getString(getString(R.string.shared_pref_settings_theme_key), "");
        String lightThemeValue = getString(R.string.settings_theme_light);
        String darkThemeValue = getString(R.string.settings_theme_dark);
        if(selectedTheme.equals(lightThemeValue)){
            setTheme(R.style.AppThemeLight);
        }else if(selectedTheme.equals(darkThemeValue)){
            setTheme(R.style.AppThemeDark);
        }

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.settings_spinner_theme,
                android.R.layout.simple_spinner_dropdown_item
        );
        themeSpinner.setAdapter(spinnerAdapter);
        themeSpinner.setSelection(getSpinnerIndex(themeSpinner, selectedTheme), false);
        themeSpinner.setOnItemSelectedListener(this);

        int scrollSpeed = sharedPreferences.getInt(getString(R.string.shared_pref_settings_scroll_speed_key), 0);
        int fontSize = sharedPreferences.getInt(getString(R.string.shared_pref_settings_font_size_key), 0);
        if(scrollSpeed != 0){
            scrollSpeedDefaultEditText.setText(String.valueOf(scrollSpeed));
        }
        if(fontSize != 0){
            fontSizeDefaultEditText.setText(String.valueOf(fontSize));
        }
    }

    @Override
    public void finish() {
        Editor editor = sharedPreferences.edit();
        if(scrollSpeedDefaultEditText.getText().toString().isEmpty()){
            editor.putInt(getString(R.string.shared_pref_settings_scroll_speed_key), Integer.parseInt(getString(R.string.default_scroll_speed)));
        }else{
            editor.putInt(getString(R.string.shared_pref_settings_scroll_speed_key), Integer.parseInt(scrollSpeedDefaultEditText.getText().toString()));
        }
        if(fontSizeDefaultEditText.getText().toString().isEmpty()){
            editor.putInt(getString(R.string.shared_pref_settings_font_size_key), Integer.parseInt(getString(R.string.default_font_size)));
        }else{
            editor.putInt(getString(R.string.shared_pref_settings_font_size_key), Integer.parseInt(fontSizeDefaultEditText.getText().toString()));
        }
        editor.apply();
        super.finish();
    }

    //Function created referencing https://stackoverflow.com/questions/8769368/how-to-set-position-in-spinner
    private int getSpinnerIndex(Spinner spinner, String item){
        for(int i=0; i<spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).equals(item)) {
                return i;
            }
        }
        return 0;
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

        currTheme = selectedTheme;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String selection = parent.getItemAtPosition(pos).toString();
        if(selection.equals(getString(R.string.settings_theme_light))){
            Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.shared_pref_settings_theme_key), selection);
            editor.apply();
        }else if(selection.equals(getString(R.string.settings_theme_dark))){
            Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.shared_pref_settings_theme_key), selection);
            editor.apply();
        }
        applyTheme();
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        //Restart copied from "N J"'s answer at https://stackoverflow.com/questions/34065087/restart-app-programmatically
        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
