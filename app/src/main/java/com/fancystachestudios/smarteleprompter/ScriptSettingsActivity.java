package com.fancystachestudios.smarteleprompter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fancystachestudios.smarteleprompter.customClasses.Script;
import com.fancystachestudios.smarteleprompter.room.ScriptRoomDatabase;
import com.fancystachestudios.smarteleprompter.room.ScriptSingleton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScriptSettingsActivity extends AppCompatActivity {

    Context context;

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

    @BindView(R.id.script_settings_delete_button)
    Button deleteButton;

    ScriptRoomDatabase scriptRoomDatabase;
    SharedPreferences sharedPreferences;

    Script passedScript;

    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyTheme();
        setContentView(R.layout.activity_script_settings);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;

        scriptRoomDatabase = ScriptSingleton.getInstance(this);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_settings_key), MODE_PRIVATE);

        fontSizeEditText.setHint(String.valueOf(sharedPreferences.getInt(getString(R.string.shared_pref_settings_font_size_key), Integer.parseInt(getString(R.string.default_font_size)))));
        scrollSpeedEditText.setHint(String.valueOf(sharedPreferences.getInt(getString(R.string.shared_pref_settings_scroll_speed_key), Integer.parseInt(getString(R.string.default_scroll_speed)))));

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

            final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            originalDateTextView.setText(String.format(getString(R.string.script_settings_original_date_dynamic), dateFormat.format(new Date(passedScript.getOriginalDate()))));
            currentDateTextView.setText(String.format(getString(R.string.script_settings_current_date_dynamic), dateFormat.format(new Date(passedScript.getDate()))));
            final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                    Calendar todayCalendar = Calendar.getInstance();
                    todayCalendar.set(
                            datePicker.getYear(),
                            datePicker.getMonth(),
                            datePicker.getDayOfMonth()
                    );
                    passedScript.setDate(todayCalendar.getTime().getTime());
                    calendar.setTime(new Date(passedScript.getDate()));

                    originalDateTextView.setText(String.format(getString(R.string.script_settings_original_date_dynamic), dateFormat.format(new Date(passedScript.getOriginalDate()))));
                    currentDateTextView.setText(String.format(getString(R.string.script_settings_current_date_dynamic), dateFormat.format(new Date(passedScript.getDate()))));

                    updateScript();
                }
            };
            //Date Picker dialog created referencing answer by "Android_coder" edited by "talz" at https://stackoverflow.com/questions/14933330/datepicker-how-to-popup-datepicker-when-click-on-edittext
            calendar = Calendar.getInstance();
            calendar.setTime(new Date(passedScript.getDate()));
            dateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DatePickerDialog(context, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            final SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
            originalTimeTextView.setText(String.format(getString(R.string.script_settings_original_time_dynamic), timeFormat.format(new Date(passedScript.getOriginalDate()))));
            currentTimeTextView.setText(String.format(getString(R.string.script_settings_current_time_dynamic), timeFormat.format(new Date(passedScript.getDate()))));
            final TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                    int hour = timePicker.getHour();
                    int minute = timePicker.getMinute();
                    calendar.setTime(new Date(passedScript.getDate()));
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);

                    passedScript.setDate(calendar.getTime().getTime());

                    originalTimeTextView.setText(String.format(getString(R.string.script_settings_original_time_dynamic), timeFormat.format(new Date(passedScript.getOriginalDate()))));
                    currentTimeTextView.setText(String.format(getString(R.string.script_settings_current_time_dynamic), timeFormat.format(new Date(passedScript.getDate()))));

                    updateScript();
                }
            };
            //Date Picker dialog created referencing answer by "Android_coder" edited by "talz" at https://stackoverflow.com/questions/14933330/datepicker-how-to-popup-datepicker-when-click-on-edittext
            calendar = Calendar.getInstance();
            calendar.setTime(new Date(passedScript.getDate()));
            timeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new TimePickerDialog(context, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
                }
            });

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

                    if(b){
                        Toast.makeText(context, R.string.script_settings_wait_tags_toast, Toast.LENGTH_LONG).show();
                    }
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Created referencing answer by "Maaalte" edited by "Nicholas Betsworth" at https://stackoverflow.com/questions/5127407/how-to-implement-a-confirmation-yes-no-dialogpreference
                    new AlertDialog.Builder(context)
                            .setTitle(getString(R.string.script_settings_delete_dialog_title))
                            .setMessage(String.format(getString(R.string.script_settings_delete_dialog_message), passedScript.getTitle()))
                            .setPositiveButton(getString(R.string.script_settings_delete_dialog_yes), new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    AsyncTask.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            scriptRoomDatabase.scriptDao().delete(passedScript.getId());
                                            finish();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton(getString(R.string.script_settings_delete_dialog_no), null)
                            .show();
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
