package com.gmail.tarekmabdallah91.news.views.bases;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.tarekmabdallah91.news.R;
import com.gmail.tarekmabdallah91.news.views.articlesFragment.paging.ListItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.gmail.tarekmabdallah91.news.utils.ViewsUtils.getTextFromEditText;
import static com.gmail.tarekmabdallah91.news.utils.ViewsUtils.restartActivity;
import static com.gmail.tarekmabdallah91.news.utils.ViewsUtils.showShortToastMsg;

public abstract class BaseFragment extends Fragment implements ListItemClickListener {

    @Nullable @BindView(R.id.msg_iv)
    protected ImageView errorIV;
    @Nullable @BindView(R.id.progress_bar)
    protected View progressBar;
    @Nullable @BindView(R.id.msg_tv)
    protected TextView errorTV;
    @Nullable @BindView(R.id.msg_layout)
    protected View errorLayout;

    protected Activity activity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResId(), container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initiateValues();
        if (null == savedInstanceState) {
            setActivityWhenSaveInstanceStateNull();
        } else {
            reSetActivityWithSaveInstanceState(savedInstanceState);
        }
        initiateValuesAfterCheckSaveInstanceState();
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickMsgIV();
            }
        };
        if (null != errorIV) errorIV.setOnClickListener(onClickListener);
        if (null != errorTV) errorTV.setOnClickListener(onClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUI();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    /**
     * to reload data if the user click on the error image and it was because failure in internet connection
     */
    void onClickMsgIV(){
        String errorMsg = getTextFromEditText(errorTV);
        if (activity.getString(R.string.no_connection).equals(errorMsg)) onRetryClick();
    }

    @Override
    public void onRetryClick() {
        restartActivity(activity);
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
     * to init some values after check if SaveInstanceState is null or not
     */
    protected void initiateValuesAfterCheckSaveInstanceState(){

    }
    /**
     * override it to set the  UI
     * it is called in onResume() to recalled each time the activity resumed
     */
    protected void setUI() {}

    /**
     * simple method to show Toast Msg and control all Toast's style in the app
     *
     * @param msg which will be shown
     */
    protected void showToastMsg(String msg) {
        showShortToastMsg(activity, msg);
    }

    /**
     * @return the layout resource id for each activity
     */
    protected abstract int getLayoutResId();
}
