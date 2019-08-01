package com.gmail.tarekmabdallah91.news.views.section;

import android.content.Context;
import android.content.Intent;

import com.gmail.tarekmabdallah91.news.R;
import com.gmail.tarekmabdallah91.news.data.sp.SharedPreferencesHelper;
import com.gmail.tarekmabdallah91.news.views.articlesFragment.ArticlesFragment;
import com.gmail.tarekmabdallah91.news.views.bases.BaseActivity;

import static com.gmail.tarekmabdallah91.news.utils.Constants.IS_COUNTRY_SECTION;
import static com.gmail.tarekmabdallah91.news.utils.Constants.SECTION_ID_KEYWORD;
import static com.gmail.tarekmabdallah91.news.utils.Constants.TITLE_KEYWORD;
import static com.gmail.tarekmabdallah91.news.utils.ViewsUtils.restartActivity;

public class SectionActivity extends BaseActivity {

    protected ArticlesFragment fragment;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_section;
    }

    @Override
    protected void initiateValues() {
        fragment = ArticlesFragment.getInstance();
    }

    @Override
    protected void setActivityWhenSaveInstanceStateNull() {
        setFragmentToCommit(fragment, R.id.fragment_articles_container);
    }

    @Override
    protected void setUI() {
        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
        boolean isSPUpdated = sharedPreferencesHelper.getIsSPUpdated();
        if (isSPUpdated) {
            sharedPreferencesHelper.saveIsSPUpdated(false);
            restartActivity(this);
        }
    }

    @Override
    protected String getActivityTitle(){
        Intent comingIntent = getIntent();
        return comingIntent.getStringExtra(TITLE_KEYWORD);
    }

    public static void openSectionActivity(Context context, String sectionId, String sectionTitle, boolean isCountrySection){
        Intent openSectionActivity = new Intent(context, SectionActivity.class);
        openSectionActivity.putExtra(SECTION_ID_KEYWORD, sectionId);
        openSectionActivity.putExtra(TITLE_KEYWORD, sectionTitle);
        openSectionActivity.putExtra(IS_COUNTRY_SECTION, isCountrySection);
        context.startActivity(openSectionActivity);
    }
}
