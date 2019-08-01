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

package com.gmail.tarekmabdallah91.news.views.articlesFragment.paging;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.tarekmabdallah91.news.R;
import com.gmail.tarekmabdallah91.news.models.articles.Article;
import com.gmail.tarekmabdallah91.news.models.articles.Fields;
import com.gmail.tarekmabdallah91.news.utils.NetworkState;
import com.gmail.tarekmabdallah91.news.views.articlesFragment.paging.ListItemClickListener;
import com.gmail.tarekmabdallah91.news.views.articlesFragment.paging.OnArticleClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.gmail.tarekmabdallah91.news.models.articles.Article.DIFF_CALLBACK;
import static com.gmail.tarekmabdallah91.news.utils.Constants.ZERO;
import static com.gmail.tarekmabdallah91.news.utils.ViewsUtils.getCurrentTime;
import static com.gmail.tarekmabdallah91.news.utils.ViewsUtils.getTextFromEditText;
import static com.gmail.tarekmabdallah91.news.utils.ViewsUtils.handelErrorMsg;
import static com.gmail.tarekmabdallah91.news.utils.ViewsUtils.loadImage;
import static com.gmail.tarekmabdallah91.news.utils.ViewsUtils.showNormalProgressBar;

public class ItemAdapter extends PagedListAdapter<Article, RecyclerView.ViewHolder> {

    protected Context context;
    private List<Article> articles;
    OnArticleClickListener listener;
    private NetworkState networkState;
    ListItemClickListener itemClickListener;

    public ItemAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view;
        if (viewType == R.layout.item_article) {
            view = layoutInflater.inflate(R.layout.item_article, parent, false);
            return new ArticleViewHolder(view);
        } else {
            view = layoutInflater.inflate(R.layout.progress_bar_no_data_tv_layout, parent, false);
            return new NetworkStateItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case R.layout.item_article:
                ((ArticleViewHolder) holder).bindTo(getItem(position));
                break;
            case R.layout.progress_bar_no_data_tv_layout:
                ((NetworkStateItemViewHolder) holder).bindView(networkState);
                break;
        }
    }

    @Nullable
    @Override
    protected Article getItem(int position) {
        try {
            return super.getItem(position);
        } catch (IndexOutOfBoundsException e) {
            return articles.get(position);
        }
    }

    String getDate(String inputDate) {
        String t = "T";
        String date = inputDate.split(t)[ZERO];
        String today = getCurrentTime();
        if (today.equals(date)) date = context.getString(R.string.today_label);
        return date;
    }

    public boolean hasExtraRow() {
        return networkState != null && networkState != NetworkState.LOADED;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return R.layout.progress_bar_no_data_tv_layout;
        } else {
            return R.layout.item_article;

        }
    }

    public void setNetworkState(NetworkState newNetworkState) {
        this.networkState = newNetworkState;
    }

    /**
     * when articles list is not null means that it shows articles from Db, so getMyApplication it's size
     * when articles is null means that it shows pagedList loaded from tha API, so getMyApplication super.getItemCount()
     */
    @Override
    public int getItemCount() {
        return null == articles ? super.getItemCount() : articles.size();
    }

    public void swapList(List<Article> articles) {
        this.articles = articles;
        notifyDataSetChanged();
    }

    public void setOnArticleClickListener(OnArticleClickListener listener) {
        this.listener = listener;
    }

    public void setItemClickListener(ListItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    class ArticleViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.article_image)
        ImageView articleImage;
        @BindView(R.id.article_title)
        TextView articleTitle;
        @BindView(R.id.section)
        TextView articleSection;
        @BindView(R.id.author)
        TextView articleAuthor;
        @BindView(R.id.date)
        TextView articleDate;

        ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            View.OnClickListener onArticleClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    listener.onClickArticle(getItem(position));
                }
            };
            itemView.setOnClickListener(onArticleClickListener);
        }

        @OnClick(R.id.section)
        void onClickSectionTV() {
            int position = getAdapterPosition();
            listener.onClickArticleSection(getItem(position));
        }

        void bindTo(Article currentArticle) {
            articleTitle.setText(currentArticle.getWebTitle());
            articleDate.setText(getDate(currentArticle.getWebPublicationDate()));
            Fields fields = currentArticle.getFields();
            if (null != fields) {
                articleAuthor.setText(fields.getAuthorName());
                loadImage(fields.getThumbnail(), articleImage);
            }
            articleSection.setText(currentArticle.getSectionName());
        }
    }

    public class NetworkStateItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.msg_iv)
        protected ImageView errorIV;
        @BindView(R.id.progress_bar)
        protected View progressBar;
        @BindView(R.id.msg_tv)
        protected TextView errorTV;
        @BindView(R.id.msg_layout)
        protected View errorLayout;

        NetworkStateItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindView(NetworkState networkState) {
            boolean isStateRunning = networkState != null && networkState.getStatus() == NetworkState.Status.RUNNING;
            showNormalProgressBar(progressBar, isStateRunning);
            handelErrorMsg(networkState, errorLayout, progressBar, errorTV, errorIV);
        }

        /**
         * to reload data if the user click on the error image and it was because failure in internet connection
         */
        @OnClick(R.id.msg_iv)
        void onClickMsgIV() {
            String errorMsg = getTextFromEditText(errorTV);
            if (context.getString(R.string.no_connection).equals(errorMsg))
                itemClickListener.onRetryClick();
        }

        @OnClick(R.id.msg_tv)
        void onClickMsgTV() {
            onClickMsgIV();
        }
    }

}