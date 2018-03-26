package com.mxt.anitrend.view.fragment.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.mxt.anitrend.R;
import com.mxt.anitrend.adapter.recycler.index.UserAdapter;
import com.mxt.anitrend.base.custom.fragment.FragmentBaseList;
import com.mxt.anitrend.model.entity.base.UserBase;
import com.mxt.anitrend.model.entity.container.body.PageContainer;
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder;
import com.mxt.anitrend.presenter.base.BasePresenter;
import com.mxt.anitrend.util.CompatUtil;
import com.mxt.anitrend.util.GraphUtil;
import com.mxt.anitrend.util.KeyUtils;
import com.mxt.anitrend.view.activity.detail.ProfileActivity;

import java.util.Collections;

/**
 * Created by max on 2017/12/20.
 */

public class UserSearchFragment  extends FragmentBaseList<UserBase, PageContainer<UserBase>, BasePresenter> {

    private String searchQuery;

    public static UserSearchFragment newInstance(Bundle args) {
        UserSearchFragment fragment = new UserSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Override and set presenter, mColumnSize, and fetch argument/s
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null)
            searchQuery = getArguments().getString(KeyUtils.arg_search);
        setPresenter(new BasePresenter(getContext()));
        mColumnSize = R.integer.single_list_x1; isPager = true;
        setViewModel(true);
    }

    /**
     * Is automatically called in the @onStart Method if overridden in list implementation
     */
    @Override
    protected void updateUI() {
        if(mAdapter == null)
           mAdapter = new UserAdapter(model, getContext());
        injectAdapter();
    }

    /**
     * All new or updated network requests should be handled in this method
     */
    @Override
    public void makeRequest() {
        QueryContainerBuilder queryContainer = GraphUtil.getDefaultQuery(isPager)
                .putVariable(KeyUtils.arg_search, searchQuery)
                .putVariable(KeyUtils.arg_page, getPresenter().getCurrentPage())
                .putVariable(KeyUtils.arg_sort, KeyUtils.SEARCH_MATCH);
        getViewModel().getParams().putParcelable(KeyUtils.arg_graph_params, queryContainer);
        getViewModel().requestData(KeyUtils.USER_SEARCH_REQ, getContext());
    }

    /**
     * When the target view from {@link View.OnClickListener}
     * is clicked from a view holder this method will be called
     *
     * @param target view that has been clicked
     * @param data   the model that at the click index
     */
    @Override
    public void onItemClick(View target, UserBase data) {
        switch (target.getId()) {
            case R.id.container:
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(KeyUtils.arg_id, data.getId());
                CompatUtil.startRevealAnim(getActivity(), target, intent);
                break;
        }
    }

    /**
     * When the target view from {@link View.OnLongClickListener}
     * is clicked from a view holder this method will be called
     *
     * @param target view that has been long clicked
     * @param data   the model that at the long click index
     */
    @Override
    public void onItemLongClick(View target, UserBase data) {

    }

    /**
     * Called when the model state is changed.
     *
     * @param content The new data
     */
    @Override
    public void onChanged(@Nullable PageContainer<UserBase> content) {
        if(content != null) {
            if(content.hasPageInfo())
                pageInfo = content.getPageInfo();
            if(!content.isEmpty())
                onPostProcessed(content.getPageData());
            else
                onPostProcessed(Collections.emptyList());
        }
        if(model == null)
            onPostProcessed(null);
    }
}