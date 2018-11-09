package com.fancystachestudios.smarteleprompter;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fancystachestudios.smarteleprompter.customClasses.Script;
import com.fancystachestudios.smarteleprompter.room.ScriptRoomDatabase;
import com.fancystachestudios.smarteleprompter.room.ScriptSingleton;
import com.fancystachestudios.smarteleprompter.room.ScriptViewModel;
import com.google.android.gms.common.util.NumberUtils;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TeleprompterActivity extends AppCompatActivity {

    @BindView(R.id.teleprompter_scrollview)
    ScrollView scrollView;

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
    boolean autoScrolling = false;

    ScriptRoomDatabase scriptRoomDatabase;

    SharedPreferences sharedPreferences;

    Timer scrollTimer;
    ValueAnimator smoothScroller;

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
            scriptSetup();
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
                    if(!autoScrolling){
                        startScroll();
                    }else{
                        stopScroll();
                    }
                }
            }
        });
    }

    private void scriptSetup(){
        ScriptViewModel scriptViewModel = ViewModelProviders.of(this).get(ScriptViewModel.class);
        scriptViewModel.getScript(currScript.getId()).observe(this, new Observer<Script>() {
            @Override
            public void onChanged(@Nullable Script script) {
                if(script != null) {
                    currScript = script;
                    loadCurrScript();
                }else if(script == null && currMode == null || script == null && !makingNewScript){
                    finish();
                }
                Long fontSize;
                if(currScript != null && currScript.getFontSize() != null){
                    fontSize = currScript.getFontSize();
                }else {
                    fontSize = (long) sharedPreferences.getInt(getString(R.string.shared_pref_settings_font_size_key), Integer.parseInt(getString(R.string.default_font_size)));
                }
                titleTextView.setTextSize(fontSize * 2);
                bodyTextView.setTextSize(fontSize);
                titleEditText.setTextSize(fontSize * 2);
                bodyEditText.setTextSize(fontSize);
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
                fab.setContentDescription(getString(R.string.telepromter_fab_content_description_play));
                updateTextViews();
            }
        });
    }

    private void editScriptMode(){
        currMode = getString(R.string.teleprompter_pass_mode_edit);
        editTextsLinearLayout.setVisibility(View.VISIBLE);
        textViewsLinearLayout.setVisibility(View.GONE);
        fab.setImageResource(R.drawable.baseline_save_white_36);
        fab.setContentDescription(getString(R.string.telepromter_fab_content_description_save));
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
                scriptSetup();
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
        autoScrolling = true;
        scrollTimer = new Timer();
        fab.setImageResource(R.drawable.baseline_pause_white_36);
        fab.setContentDescription(getString(R.string.telepromter_fab_content_description_pause));
        //Smooth scrolling created referencing answer by "Bartek Lipinski" at https://stackoverflow.com/questions/33870408/android-how-to-use-valueanimator
        if(currScript.getEnableSmartScroll() == null || !currScript.getEnableSmartScroll()){
            scrollTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Runnable scrollRunnable = new Runnable() {
                        @Override
                        public void run() {
                            int scrollSpeed;
                            int fontSize;
                            int scrollAmount;

                            if(currScript.getScrollSpeed() != null){
                                scrollSpeed = Integer.parseInt(String.valueOf(currScript.getScrollSpeed()));
                            }else{
                                scrollSpeed = sharedPreferences.getInt(getString(R.string.shared_pref_settings_scroll_speed_key), Integer.valueOf(getString(R.string.default_scroll_speed)));
                            }
                            scrollSpeed *= 10;
                            if(currScript.getFontSize() != null){
                                fontSize = Integer.parseInt(String.valueOf(currScript.getFontSize()));
                            }else{
                                fontSize = sharedPreferences.getInt(getString(R.string.shared_pref_settings_font_size_key), Integer.valueOf(getString(R.string.default_font_size)));
                            }

                            scrollAmount = (scrollSpeed / fontSize);

                            smoothScroller = ValueAnimator.ofInt(scrollView.getScrollY(), scrollAmount + scrollView.getScrollY());
                            smoothScroller.setDuration(1000);
                            smoothScroller.setInterpolator(null);
                            smoothScroller.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    int scrollTo = (Integer)valueAnimator.getAnimatedValue();
                                    scrollView.scrollTo(0, scrollTo);
                                    if(!scrollView.canScrollVertically(1)){
                                        stopScroll();
                                    }
                                }
                            });
                            smoothScroller.start();
                        }
                    };
                    runOnUiThread(scrollRunnable);
                }
            }, 0, 1000);
        }else{

        }
    }

    private void stopScroll(){
        if(scrollTimer != null && smoothScroller != null){
            autoScrolling = false;
            fab.setImageResource(R.drawable.baseline_play_arrow_white_36);
            fab.setContentDescription(getString(R.string.telepromter_fab_content_description_play));
            scrollTimer.cancel();
            scrollTimer = null;
            smoothScroller.cancel();
            smoothScroller = null;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_script_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int clickedId = item.getItemId();
        if(clickedId == R.id.menu_main_script_scriptSettings){
            Intent intent = new Intent(this, ScriptSettingsActivity.class);
            intent.putExtra(getString(R.string.menu_main_script_settings_script_key), currScript);
            startActivity(intent);
        }else if(clickedId == R.id.menu_main_script_delete){
            //Created referencing answer by "Maaalte" edited by "Nicholas Betsworth" at https://stackoverflow.com/questions/5127407/how-to-implement-a-confirmation-yes-no-dialogpreference
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.script_settings_delete_dialog_title))
                    .setMessage(String.format(getString(R.string.script_settings_delete_dialog_message), currScript.getTitle()))
                    .setPositiveButton(getString(R.string.script_settings_delete_dialog_yes), new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    scriptRoomDatabase.scriptDao().delete(currScript.getId());
                                    finish();
                                }
                            });
                        }
                    })
                    .setNegativeButton(getString(R.string.script_settings_delete_dialog_no), null)
                    .show();
        }else if(clickedId == R.id.menu_main_script_edit){
            editScriptMode();
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Referenced answer by "nir" at https://stackoverflow.com/questions/5440601/android-how-to-enable-disable-option-menu-item-on-button-click
        boolean isEnabled = true;
        if(currMode.equals(getString(R.string.teleprompter_pass_mode_edit)) ||
                currMode.equals(getString(R.string.teleprompter_pass_mode_new))){
            isEnabled = false;
        }
        menu.getItem(0).setEnabled(isEnabled);
        menu.getItem(1).setEnabled(isEnabled);
        menu.getItem(2).setEnabled(isEnabled);
        return true;
    }
}
