/*
 * Copyright 2019 tarekmabdallah91@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gmail.tarekmabdallah91.news.views.bases;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.gmail.tarekmabdallah91.news.R;

import butterknife.ButterKnife;

import static aboutMe.AboutMeActivity.openAboutMeActivity;
import static com.gmail.tarekmabdallah91.news.utils.Constants.ZERO;
import static com.gmail.tarekmabdallah91.news.utils.Constants.makeTypeFaceTitleStyle;
import static com.gmail.tarekmabdallah91.news.utils.ViewsUtils.showShortToastMsg;
import static com.gmail.tarekmabdallah91.news.views.search.SearchActivity.openSearchActivity;
import static com.gmail.tarekmabdallah91.news.views.sections.SectionsActivity.openSectionsActivity;
import static com.gmail.tarekmabdallah91.news.views.settings.SettingsActivity.openSettingsActivity;

public abstract class BaseActivity extends AppCompatActivity {

    // TODO: 07-Apr-19 to use Fb sdk for logging
    // TODO: 07-Apr-19 to use firebase for crash analytics and messing
    // TODO: 07-Apr-19 to use dagger 2
    // TODO: 07-Apr-19 to update the UI (Responsive UI)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        ButterKnife.bind(this);
        initiateValues();
        setActionBar();
        if (null == savedInstanceState) {
            setActivityWhenSaveInstanceStateNull();
        } else {
            reSetActivityWithSaveInstanceState(savedInstanceState);
        }
    }

    protected void setActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            customTitleTVStyle(actionBar);
        }
    }
    
    protected void customTitleTVStyle(ActionBar actionBar){
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.action_bar_title_layout);
        TextView titleTV = findViewById(R.id.action_bar_title);
        makeTypeFaceTitleStyle(titleTV);
        titleTV.setText(getActivityTitle());
    }

    protected abstract String getActivityTitle();

    @Override
    protected void onResume() {
        super.onResume();
        setUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item_settings) openSettingsActivity(this);
        else if (id == R.id.item_search) openSearchActivity(this);
        else if (id == R.id.item_sections) openSectionsActivity(this);
        else if (id == R.id.item_about_me) openAboutMeActivity(this,
                "Tarek AbdAllah",
                "Android Developer",
                "+201096071130",
                "tarekmabdallah91@gmail.com",
                "http://bit.ly/2kfdLeB",
                "http://bit.ly/2Pi2h84");
        else if (item.getItemId() == android.R.id.home) finish();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        hideMenuItemsByIds(menu, getMenuItemIdsToHide());
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * to hide all items @param ids array from @param menu
     */
    private void hideMenuItemsByIds(Menu menu, int...ids){
        if (null != ids && ZERO < ids.length)
            for (int id:ids) menu.findItem(id).setVisible(false);
    }

    /**
     * override to put and @return  an int[] of the items wanted to be invisible in the action bar menu
     */
    protected int[] getMenuItemIdsToHide(){
        return null;
    }

    /**
     * called when the activity created for the first time (WhenSaveInstanceStateNull)
     */
    protected void setActivityWhenSaveInstanceStateNull() {
    }

    /**
     * called when the device rotated  (WhenSaveInstanceState IS NOT Null)
     *
     * @param savedInstanceState -
     */
    protected void reSetActivityWithSaveInstanceState(Bundle savedInstanceState) {
    }

    /**
     * to init some values once and will be called every time the device rotated
     */
    protected void initiateValues() {
    }

    /**
     * override it to set the  UI
     * it is called in onResume() to recalled each time the activity resumed
     */
    protected void setUI() {
    }

    protected void setFragmentToCommit (BaseFragment fragment, int containerId){
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(containerId, fragment).commit();
    }

    /**
     * simple method to show Toast Msg and control all Toast's style in the app
     *
     * @param msg which will be shown
     */
    protected void showToastMsg(String msg) {
        showShortToastMsg(this, msg);
    }

    /**
     * @return the layout resource id for each activity
     */
    protected abstract int getLayoutResId();
}
