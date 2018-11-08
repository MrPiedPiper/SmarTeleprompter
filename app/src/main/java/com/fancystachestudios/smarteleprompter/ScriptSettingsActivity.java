package com.fancystachestudios.smarteleprompter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.fancystachestudios.smarteleprompter.customClasses.Script;
import com.fancystachestudios.smarteleprompter.room.ScriptRoomDatabase;
import com.fancystachestudios.smarteleprompter.room.ScriptSingleton;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScriptSettingsActivity extends AppCompatActivity {

    @BindView(R.id.script_settings_title_edittext)
    EditText titleEditText;

    @BindView(R.id.script_settings_date_original_textview)
    TextView originalDateTextView;
    @BindView(R.id.script_settings_date_current_textview)
    TextView currentDateTextView;
    @BindView(R.id.script_settings_date_button)
    Button dateButton;

    @BindView(R.id.script_settings_time_original_textview)
    TextView originalTimeTextView;
    @BindView(R.id.script_settings_time_current_textview)
    TextView currentTimeTextView;
    @BindView(R.id.script_settings_time_button)
    Button timeButton;

    @BindView(R.id.script_settings_scroll_speed_edittext)
    EditText scrollSpeedEditText;

    @BindView(R.id.script_settings_font_size_edittext)
    EditText fontSizeEditText;

    @BindView(R.id.script_settings_wait_tags_switch)
    Switch waitTagsSwitch;

    @BindView(R.id.script_settings_smart_scroll_switch)
    Switch smartScrollSwitch;

    @BindView(R.id.script_settings_record_button)
    Button recordSmartScrollButton;

    @BindView(R.id.script_settings_delete_button)
    Button deleteButton;

    ScriptRoomDatabase scriptRoomDatabase;

    Script passedScript;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyTheme();
        setContentView(R.layout.activity_script_settings);
        ButterKnife.bind(this);

        scriptRoomDatabase = ScriptSingleton.getInstance(this);

        Intent extras = getIntent();

        if(extras != null){
            passedScript = (Script) extras.getParcelableExtra(getString(R.string.menu_main_script_settings_script_key));

            titleEditText.setText(passedScript.getTitle());
            titleEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    passedScript.setTitle(titleEditText.getText().toString());
                    updateScript();
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            originalDateTextView.setText(String.format(getString(R.string.script_settings_original_date_dynamic), dateFormat.format(new Date(passedScript.getOriginalDate()))));
            currentDateTextView.setText(String.format(getString(R.string.script_settings_current_date_dynamic), dateFormat.format(new Date(passedScript.getDate()))));

            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
            originalTimeTextView.setText(String.format(getString(R.string.script_settings_original_time_dynamic), timeFormat.format(new Date(passedScript.getOriginalDate()))));
            currentTimeTextView.setText(String.format(getString(R.string.script_settings_current_time_dynamic), timeFormat.format(new Date(passedScript.getDate()))));

            if(passedScript.getScrollSpeed() != null && passedScript.getScrollSpeed() != 0){
                scrollSpeedEditText.setText(String.valueOf(passedScript.getScrollSpeed()));
            }
            scrollSpeedEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String currText = scrollSpeedEditText.getText().toString();
                    if(currText.isEmpty() || currText.equals("0")){
                        passedScript.setScrollSpeed(null);
                        updateScript();
                        return;
                    }
                    passedScript.setScrollSpeed(Long.parseLong(currText));
                    updateScript();
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            if(passedScript.getFontSize() != null && passedScript.getFontSize() != 0){
                fontSizeEditText.setText(String.valueOf(passedScript.getFontSize()));
            }
            fontSizeEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String currText = fontSizeEditText.getText().toString();
                    if(currText.isEmpty() || currText.equals("0")){
                        passedScript.setFontSize(null);
                        updateScript();
                        return;
                    }
                    passedScript.setFontSize(Long.parseLong(currText));
                    updateScript();
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            if(passedScript.getEnableWaitTags() != null){
                waitTagsSwitch.setChecked(passedScript.getEnableWaitTags());
            }
            waitTagsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    passedScript.setEnableWaitTags(b);
                    updateScript();
                }
            });

            if(passedScript.getEnableSmartScroll() != null){
                smartScrollSwitch.setChecked(passedScript.getEnableSmartScroll());
            }
            recordSmartScrollButton.setEnabled(smartScrollSwitch.isChecked());
            smartScrollSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    recordSmartScrollButton.setEnabled(smartScrollSwitch.isChecked());
                    passedScript.setEnableSmartScroll(b);
                    updateScript();
                }
            });

        }
    }
    
    private void updateScript(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                scriptRoomDatabase.scriptDao().update(passedScript);
            }
        });
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
