/*
 *
 * Copyright 2019 tarekmabdallah91@gmail.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.gmail.tarekmabdallah91.smooth.ui.viewmodel;

import android.app.Activity;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import com.gmail.tarekmabdallah91.news.models.articles.Article;
import com.gmail.tarekmabdallah91.news.utils.NetworkState;
import com.gmail.tarekmabdallah91.smooth.service.repository.ArticleRepository;



public class ArticlesListViewModel extends AndroidViewModel {

    private ArticleRepository repository;

    /**
     * @param activity just passed it as we can pass some values via it without needing to pas a Bundle or something else
     */
    public ArticlesListViewModel(@NonNull Activity activity) {
        super(activity.getApplication());
        repository = ArticleRepository.getInstance(activity);
    }

    public LiveData<PagedList<Article>> getArticles() {
        return repository.getArticles();
    }

    public LiveData<NetworkState> getNetworkState() {
        return repository.getNetworkState();
    }
}
