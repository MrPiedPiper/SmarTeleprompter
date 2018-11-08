package com.fancystachestudios.smarteleprompter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fancystachestudios.smarteleprompter.customClasses.Script;
import com.fancystachestudios.smarteleprompter.room.ScriptRoomDatabase;
import com.fancystachestudios.smarteleprompter.room.ScriptSingleton;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TeleprompterActivity extends AppCompatActivity {

    @BindView(R.id.teleprompter_textviews)
    LinearLayout textViewsLinearLayout;
    @BindView(R.id.teleprompter_title_textview)
    TextView titleTextView;
    @BindView(R.id.teleprompter_body_textview)
    TextView bodyTextView;

    @BindView(R.id.teleprompter_edittexts)
    LinearLayout editTextsLinearLayout;
    @BindView(R.id.teleprompter_title_edittext)
    TextView titleEditText;
    @BindView(R.id.teleprompter_body_edittext)
    TextView bodyEditText;

    @BindView(R.id.teleprompter_fab)
    FloatingActionButton fab;

    String currMode;

    Script currScript;

    boolean makingNewScript = false;

    ScriptRoomDatabase scriptRoomDatabase;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyTheme();
        setContentView(R.layout.activity_teleprompter);
        ButterKnife.bind(this);

        scriptRoomDatabase = ScriptSingleton.getInstance(this);

        sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_settings_key), MODE_PRIVATE);

        Intent launchIntent = getIntent();
        currMode = launchIntent.getStringExtra(getString(R.string.teleprompter_pass_mode));
        currScript = launchIntent.getParcelableExtra(getString(R.string.teleprompter_pass_script));
        if(currScript != null){
            loadCurrScript();
            Long fontSize = currScript.getFontSize();
            if(fontSize == null){
                fontSize = (long)sharedPreferences.getInt(getString(R.string.shared_pref_settings_font_size_key), Integer.parseInt(getString(R.string.default_font_size)));
            }
            titleTextView.setTextSize(fontSize*2);
            bodyTextView.setTextSize(fontSize);
            titleEditText.setTextSize(fontSize*2);
            bodyEditText.setTextSize(fontSize);
        }

        if(currMode != null && !currMode.isEmpty()){
            if(currMode.equals(getString(R.string.teleprompter_pass_mode_edit))){
                editScriptMode();
            }else if(currMode.equals(getString(R.string.teleprompter_pass_mode_new))){
                newScriptMode();
            }else if(currMode.equals(getString(R.string.teleprompter_pass_mode_smart_scroll))){
                recordSmartScrollMode();
            }else if(currMode.equals(getString(R.string.teleprompter_pass_mode_normal))){
                normalScriptMode();
            }
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currMode.equals(getString(R.string.teleprompter_pass_mode_edit))){
                    saveEdit();
                }else if(currMode.equals(getString(R.string.teleprompter_pass_mode_new))){
                    saveEdit();
                }else if(currMode.equals(getString(R.string.teleprompter_pass_mode_smart_scroll))){
                    saveSmartScroll();
                }else if(currMode.equals(getString(R.string.teleprompter_pass_mode_normal))){
                    startScroll();
                }
            }
        });
    }

    private void normalScriptMode(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currMode = getString(R.string.teleprompter_pass_mode_normal);
                editTextsLinearLayout.setVisibility(View.GONE);
                textViewsLinearLayout.setVisibility(View.VISIBLE);
                fab.setImageResource(R.drawable.baseline_play_arrow_white_36);
                updateTextViews();
            }
        });
    }

    private void editScriptMode(){
        currMode = getString(R.string.teleprompter_pass_mode_edit);
        editTextsLinearLayout.setVisibility(View.VISIBLE);
        textViewsLinearLayout.setVisibility(View.GONE);
        fab.setImageResource(R.drawable.baseline_save_white_36);
        updateEditTexts();
    }

    private void saveEdit(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                currScript.setTitle(titleEditText.getText().toString());
                currScript.setBody(bodyEditText.getText().toString());
                if(makingNewScript){
                    scriptRoomDatabase.scriptDao().insert(currScript);
                    makingNewScript = false;
                }else{
                    scriptRoomDatabase.scriptDao().update(currScript);
                }
                normalScriptMode();
            }
        });
    }

    private void newScriptMode(){
        currMode = getString(R.string.teleprompter_pass_mode_new);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Date currDate = new Date();
                currScript = new Script(currDate.getTime(), "New Script", currDate.getTime(), currDate.getTime());
                makingNewScript = true;
                loadCurrScript();
                editScriptMode();
            }
        });
    }

    private void recordSmartScrollMode(){
        editTextsLinearLayout.setVisibility(View.VISIBLE);
        textViewsLinearLayout.setVisibility(View.GONE);
    }

    private void updateTextViews(){
        titleTextView.setText(titleEditText.getText().toString());
        bodyTextView.setText(bodyEditText.getText().toString());
    }

    private void updateEditTexts(){
        titleEditText.setText(titleTextView.getText().toString());
        bodyEditText.setText(bodyTextView.getText().toString());
    }

    private void loadCurrScript(){
        titleTextView.setText(currScript.getTitle());
        bodyTextView.setText(currScript.getBody());
        titleEditText.setText(currScript.getTitle());
        bodyEditText.setText(currScript.getBody());
    }

    private void saveSmartScroll(){
        //TODO implement saveSmartScroll()
    }

    private void startScroll(){
        //TODO implement startScroll()
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
    public void onBackPressed() {
        if(currMode.equals(getString(R.string.teleprompter_pass_mode_edit))){
            showConfirmLeaveDialog();
        }else{
            super.onBackPressed();
        }
    }

    public void showConfirmLeaveDialog(){
        //Created referencing answer by "Maaalte" edited by "Nicholas Betsworth" at https://stackoverflow.com/questions/5127407/how-to-implement-a-confirmation-yes-no-dialogpreference
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.teleprompter_dialog_dont_save_title))
                .setMessage(getString(R.string.teleprompter_dialog_dont_save_body))
                .setPositiveButton(getString(R.string.teleprompter_dialog_dont_save_yes), new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        quitWithoutSaving();
                    }
                })
                .setNegativeButton(getString(R.string.teleprompter_dialog_dont_save_no), null)
                .show();
    }

    private void quitWithoutSaving(){
        if(makingNewScript){
            finish();
        }else{
            loadCurrScript();
            normalScriptMode();
        }
    }
}
