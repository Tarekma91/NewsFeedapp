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

package com.gmail.tarekmabdallah91.news.paging;

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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.gmail.tarekmabdallah91.news.models.articles.Article.DIFF_CALLBACK;
import static com.gmail.tarekmabdallah91.news.utils.Constants.ZERO;
import static com.gmail.tarekmabdallah91.news.utils.ViewsUtils.getCurrentTime;
import static com.gmail.tarekmabdallah91.news.utils.ViewsUtils.loadImage;
import static com.gmail.tarekmabdallah91.news.utils.ViewsUtils.showShortToastMsg;

public class ItemAdapter extends PagedListAdapter<Article, ItemAdapter.ArticleViewHolder>{

    private Context context;
    private List<Article> articles;
    private OnArticleClickListener listener;

    public ItemAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article currentArticle = getItem(position);
        if (currentArticle != null) {
            holder.articleTitle.setText(currentArticle.getWebTitle());
            holder.articleDate.setText(getDate(currentArticle.getWebPublicationDate()));
            Fields fields = currentArticle.getFields();
            if (null != fields) {
                holder.articleAuthor.setText(fields.getAuthorName());
                loadImage(fields.getThumbnail(), holder.articleImage);
            }
            holder.articleSection.setText(currentArticle.getSectionName());
        }else{
            showShortToastMsg(context, "Item is null");
        }
    }

    @Nullable
    @Override
    protected Article getItem(int position) {
        try {
            return super.getItem(position);
        }catch (IndexOutOfBoundsException e){
            return articles.get(position);
        }
    }

    private String getDate(String inputDate){
        String t = "T";
        String date = inputDate.split(t)[ZERO];
        String today = getCurrentTime();
        if (today.equals(date)) date = context.getString(R.string.today_label);
        return date;
    }

    /**
     * when articles list is not null means that it shows articles from Db, so get it's size
     * when articles is null means that it shows pagedList loaded from tha API, so get super.getItemCount()
     */
    @Override
    public int getItemCount() {
        return null == articles ? super.getItemCount() : articles.size();
    }

    public void swapList (List<Article> articles) {
        this.articles = articles;
        notifyDataSetChanged();
    }

    public void clear(){
        articles.clear();
        notifyDataSetChanged();
    }

    public void setOnArticleClickListener(OnArticleClickListener listener) {
        this.listener = listener;
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
        void onClickSectionTV (){
            int position = getAdapterPosition();
            listener.onClickArticleSection(getItem(position));
        }
    }

}