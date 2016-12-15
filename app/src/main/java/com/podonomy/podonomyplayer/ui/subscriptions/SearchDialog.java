package com.podonomy.podonomyplayer.ui.subscriptions;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import static org.apache.commons.lang3.StringUtils.isBlank;

import com.podonomy.podonomyplayer.PlayerApplication;
import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.dao.SearchQueryDAO;
import com.podonomy.podonomyplayer.event.EventLogger;
import com.podonomy.podonomyplayer.dao.SearchQuery;

import io.realm.Realm;

/**
 * This is the search dialog allowing the user to search for podacast.  The search is not peformed by this
 * class.  This class is solely to acquire teh search data from the user and validates that it is
 * specified properly.
 */
public class SearchDialog extends Dialog {
  private View.OnClickListener cancelClickListener = null;
  private View.OnClickListener searchClickListener = null;
  private EditText keywords_textbox = null;
  private EditText numberWeeksTextbox = null;
  private CheckBox audioCheckbox = null;
  private CheckBox videoCheckbox = null;
  private CheckBox localCheckbox = null;
  private Spinner  languageDropDown = null;
  private EventLogger logger = null;
  private Button cancelButton = null;
  private Button searchButton = null;
  private boolean dataIsValid = false;
  private boolean isFirstCharType = true;

  public SearchDialog(Context context, EventLogger logger) {
    super(context);
    if (logger == null || context == null)
      throw new IllegalArgumentException("Both context and logger must not be null");

    if (context instanceof Activity)
      super.setOwnerActivity((Activity) context);
    this.logger = logger;
    this.isFirstCharType = true;
  }

  public void setCancelClickListener(View.OnClickListener l){
    cancelClickListener = l;
  }

  public void setSearchClickListener(View.OnClickListener l){
    searchClickListener = l;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setTitle(R.string.search_dialog_title);
    setContentView(R.layout.search_dialog);

    //// set the handle on the UI components
    keywords_textbox   = (EditText) findViewById(R.id.search_textBox);
    numberWeeksTextbox = (EditText) findViewById(R.id.search_numberWeeksTextBox);
    audioCheckbox      = (CheckBox) findViewById(R.id.search_audio_checkBox);
    videoCheckbox      = (CheckBox) findViewById(R.id.search_video_checkBox);
    localCheckbox      = (CheckBox) findViewById(R.id.search_local_checkbox);
    languageDropDown   = (Spinner) findViewById(R.id.search_languagesDropdown);
    cancelButton       = (Button) findViewById(R.id.search_cancel_button);
    searchButton       = (Button) findViewById(R.id.search_search_button);

    cancelButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        cancelButtonClicked(v);
      }
    });
    searchButton.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v) {
        searchButtonClicked(v);
      }
    });
    keywords_textbox.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (isFirstCharType){
          isFirstCharType = false;
          keywords_textbox.setText("");
        }
        return false;
      }
    });
  }

  /**
   * Returns the search query details as specified by the user.
   */
  public SearchQuery getSearchQuery(){
    SearchQuery query = SearchQueryDAO.nnew();

    if (!dataIsValid)
      return query;

    query.setKeywords(keywords_textbox.getText().toString());
    try{
      query.setNumberWeeks(Integer.parseInt(numberWeeksTextbox.getText().toString().trim()));
    }
    catch (NumberFormatException e){
      query.setNumberWeeks(null);
    }

    query.setAudio(audioCheckbox != null && audioCheckbox.isChecked());
    query.setVideo(videoCheckbox != null && videoCheckbox.isChecked());
    query.setLocal(localCheckbox != null && localCheckbox.isChecked());

    if (languageDropDown != null && languageDropDown.getSelectedItem() != null) {
      String lang = (String) languageDropDown.getSelectedItem();
      //// TODO: 3/11/2016 Set the query language properly. Probably need to be a code like EN or FR...
    }

    return query;
  }

  private void cancelButtonClicked(View v){
    SearchDialog.this.dismiss();
    dataIsValid = false;
    if (cancelClickListener != null)
      cancelClickListener.onClick(v);
  }

  /**
   * Invoked when the user pressed the Search button
   */
  private void searchButtonClicked(View v){
    dataIsValid = false;
    ////////////
    //Validate the data.
    if (validateSearchKeywords() && validateNumberWeeks() && searchClickListener != null){
       dataIsValid = true;
       dismiss();
       searchClickListener.onClick(v);
    }
  }

  /**
   * Validates that the search keywords are specified.
   */
  private boolean validateSearchKeywords(){
    if (isBlank(keywords_textbox.getText().toString())){
      getToastOverView(getContext(), keywords_textbox, R.string.search_err_keyword_needed, Toast.LENGTH_LONG).show();
      logger.error(PlayerApplication.getInstance().getDeviceID(), "No search keywords were provided.");
      return false;
    }
    return true;
  }

  /**
   * Ensure that the if the number of weeks is specified, it is a number...
   */
  private boolean validateNumberWeeks(){
    try{
      if (isBlank(numberWeeksTextbox.getText().toString()))
        return true;

      int value = Integer.parseInt(numberWeeksTextbox.getText().toString());
      return true;
    }
    catch(NumberFormatException e) {
      getToastOverView(getContext(), numberWeeksTextbox, R.string.search_err_invalid_number_of_weeks, Toast.LENGTH_LONG).show();
      logger.error(PlayerApplication.getInstance().getDeviceID(), numberWeeksTextbox.getText().toString(), " is not a valid number of weeks.");
      return false;
    }
  }


  /**
   * Creates a Toast over the given view (at the top left of that view).
   */
  private static Toast getToastOverView(Context c, View v, int stringResID, int duration){
    Toast toast = Toast.makeText(c, stringResID, duration);
    int[] pos = new int[2];
    v.getLocationOnScreen(pos);
    toast.setGravity(Gravity.TOP | Gravity.START, pos[0], pos[1]);
    return toast;
  }
}
