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

package com.example.tarek.news.views.search;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.tarek.news.R;
import com.example.tarek.news.apis.APIClient;
import com.example.tarek.news.apis.APIServices;
import com.example.tarek.news.data.sp.SharedPreferencesHelper;
import com.example.tarek.news.models.Search.Article;
import com.example.tarek.news.models.Search.ResponseSearchForKeyWord;
import com.example.tarek.news.views.bases.ArticleArrayAdapter;
import com.example.tarek.news.views.bases.BaseActivityNoMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import retrofit2.Call;

import static com.example.tarek.news.apis.APIClient.getResponse;
import static com.example.tarek.news.utils.Constants.FIVE;
import static com.example.tarek.news.utils.Constants.QUERY_Q_KEYWORD;
import static com.example.tarek.news.utils.Constants.ZERO;
import static com.example.tarek.news.utils.ViewsUtils.getQueriesMap;
import static com.example.tarek.news.utils.ViewsUtils.isValidString;
import static com.example.tarek.news.utils.ViewsUtils.makeViewGone;
import static com.example.tarek.news.utils.ViewsUtils.makeViewVisible;

public class SearchActivity extends BaseActivityNoMenu {

    private ArticleArrayAdapter adapter;

    @BindView(R.id.list_view)
    ListView articlesListView;
    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.search_history_list_view)
    ListView searchHistoryListView;

    private SharedPreferencesHelper sharedPreferencesHelper;
    private Map<String, String> queries;
    private List<String> searchHistoryList;
    private SearchHistoryAdapter searchHistoryAdapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initiateValues() {
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
        queries = getQueriesMap();
        setListView();
        setSearchHistoryListView();
        setSearchView();
    }

    @Override
    protected void setUI() {
        String searchKeyword = getSearchKeyword();
        if (isValidString(searchKeyword)) super.setUI();
        else makeViewVisible(searchHistoryListView);
    }

    private void setListView() {
        adapter = new ArticleArrayAdapter(this, new ArrayList<Article>());
        articlesListView.setAdapter(adapter);
        articlesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article article = adapter.getItem(position);
                try {
                    Intent openWebPage;
                    if (article != null) {
                        openWebPage = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getWebUrl()));
                        startActivity(openWebPage);
                    }
                } catch (ActivityNotFoundException e) {
                    showToastMsg(getString(R.string.no_browser_msg));
                }
            }
        });
    }

    public void setSearchHistoryListView() {
        searchHistoryAdapter = new SearchHistoryAdapter(this);
        updateSearchAdapter();
        onClickItemListener onClickItemListener = new onClickItemListener() {
                @Override
                public void onClickItem(String item) {
                    searchForKeyWord(item);
                    makeViewGone(searchHistoryListView);
                }

                @Override
                public void removeItem(String item) {
                    updateSearchHistory(item, false);
                }
            };
        searchHistoryAdapter.setItemListener(onClickItemListener);
        searchHistoryListView.setAdapter(searchHistoryAdapter);
    }

    private void updateSearchHistory(String keyword, boolean isToAdd){
        if (isToAdd){
            if (!searchHistoryList.contains(keyword))searchHistoryList.add(keyword);
            if (searchHistoryList.size() > FIVE) searchHistoryList.remove(ZERO);
        }else {
            searchHistoryList.remove(keyword);
        }
        sharedPreferencesHelper.saveSearchHistory(searchHistoryList);
        updateSearchAdapter();
    }

    private void updateSearchAdapter (){
        searchHistoryList = sharedPreferencesHelper.getSearchHistory(); // to get arranged list
        searchHistoryAdapter.clear();
        searchHistoryAdapter.addAll(searchHistoryList);
    }

    /**
     * to save the keyword in search after 2 seconds
     * https://stackoverflow.com/questions/35224459/how-to-detect-if-users-stop-typing-in-edittext-android
     */
    private final int DELAY = 2000; // 2 seconds after user stoped typing
    private long lastTextEdit = ZERO;
    Handler handler = new Handler();
    private Runnable inputFinishChecker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (lastTextEdit + DELAY - 500)) {
                String searchKeyword = getSearchKeyword();
                if (isValidString(searchKeyword)) updateSearchHistory(searchKeyword, true);
                else makeViewVisible(searchHistoryListView);
            }
        }
    };

    public void setSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // called when the user clicked on the search Icon in the keyboard
                updateSearchHistory(query, true);
                searchForKeyWord(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchForKeyWord(newText);
                lastTextEdit = System.currentTimeMillis();
                handler.postDelayed(inputFinishChecker, DELAY);
                return false;
            }
        });
        View.OnClickListener onClickSearchViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeViewVisible(searchHistoryListView);
                makeViewGone(articlesListView);
            }
        };
        searchView.setOnClickListener(onClickSearchViewListener);
    }

    /**
     * set queries params with the @param searchKeyword
     */
    private void searchForKeyWord(String searchKeyword) {
        adapter.clear();
        if (isValidString(searchKeyword)) {
            queries.put(QUERY_Q_KEYWORD, searchKeyword);
            callAPi();
        } else makeViewVisible(searchHistoryListView);
    }

    private String getSearchKeyword() {
        return searchView.getQuery().toString();
    }

    protected void callAPi() {
        APIServices apiServices = APIClient.getInstance(this).create(APIServices.class);
        Call<ResponseSearchForKeyWord> searchForKeyWord = apiServices.searchForKeyword(queries);
        getResponse(searchForKeyWord, this);
    }

    @Override
    protected void whenDataFetchedGetResponse(Object response) {
        adapter.clear();
        if (response instanceof ResponseSearchForKeyWord) {
            ResponseSearchForKeyWord responseSearchForKeyWord = (ResponseSearchForKeyWord) response;
            List<Article> articleList = responseSearchForKeyWord.getResponse().getItems();
            if (articleList != null && !articleList.isEmpty()) {
                adapter.addAll(articleList);
                makeViewVisible(articlesListView);
                makeViewGone(searchHistoryListView);
            } else handleNoDataFromResponse();
        }
    }

    public static Intent openSearchActivity(Context context) {
        return new Intent(context, SearchActivity.class);
    }

}