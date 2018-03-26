package com.mxt.anitrend.view.fragment.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.mxt.anitrend.R;
import com.mxt.anitrend.adapter.recycler.group.GroupCharacterAdapter;
import com.mxt.anitrend.base.custom.fragment.FragmentBaseList;
import com.mxt.anitrend.model.entity.anilist.edge.CharacterEdge;
import com.mxt.anitrend.model.entity.base.CharacterBase;
import com.mxt.anitrend.model.entity.container.body.ConnectionContainer;
import com.mxt.anitrend.model.entity.container.body.EdgeContainer;
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder;
import com.mxt.anitrend.model.entity.group.EntityGroup;
import com.mxt.anitrend.presenter.fragment.MediaPresenter;
import com.mxt.anitrend.util.CompatUtil;
import com.mxt.anitrend.util.GraphUtil;
import com.mxt.anitrend.util.GroupingUtil;
import com.mxt.anitrend.util.KeyUtils;
import com.mxt.anitrend.view.activity.detail.CharacterActivity;

import java.util.Collections;

/**
 * Created by max on 2018/01/18.
 */

public class MediaCharacterFragment extends FragmentBaseList<EntityGroup, ConnectionContainer<EdgeContainer<CharacterEdge>>, MediaPresenter> {

    private @KeyUtils.MediaType String mediaType;
    private long mediaId;

    public static MediaCharacterFragment newInstance(Bundle args) {
        MediaCharacterFragment fragment = new MediaCharacterFragment();
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
        if(getArguments() != null) {
            mediaId = getArguments().getLong(KeyUtils.arg_id);
            mediaType = getArguments().getString(KeyUtils.arg_mediaType);
        } mColumnSize = R.integer.grid_giphy_x3; isPager = true;
        setPresenter(new MediaPresenter(getContext()));
        setViewModel(true);
    }

    /**
     * Is automatically called in the @onStart Method if overridden in list implementation
     */
    @Override
    protected void updateUI() {
        if(mAdapter == null)
            mAdapter = new GroupCharacterAdapter(model, getContext());
        setSwipeRefreshLayoutEnabled(false);
        injectAdapter();
    }

    /**
     * All new or updated network requests should be handled in this method
     */
    @Override
    public void makeRequest() {
        QueryContainerBuilder queryContainer = GraphUtil.getDefaultQuery(isPager)
                .putVariable(KeyUtils.arg_id, mediaId)
                .putVariable(KeyUtils.arg_type, mediaType)
                .putVariable(KeyUtils.arg_page, getPresenter().getCurrentPage());

        getViewModel().getParams().putParcelable(KeyUtils.arg_graph_params, queryContainer);
        getViewModel().requestData(KeyUtils.MEDIA_CHARACTERS_REQ, getContext());
    }

    /**
     * When the target view from {@link View.OnClickListener}
     * is clicked from a view holder this method will be called
     *
     * @param target view that has been clicked
     * @param data   the model that at the click index
     */
    @Override
    public void onItemClick(View target, EntityGroup data) {
        switch (target.getId()) {
            case R.id.container:
                Intent intent = new Intent(getActivity(), CharacterActivity.class);
                intent.putExtra(KeyUtils.arg_id, ((CharacterBase)data).getId());
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
    public void onItemLongClick(View target, EntityGroup data) {

    }

    @Override
    public void onChanged(@Nullable ConnectionContainer<EdgeContainer<CharacterEdge>> content) {
        EdgeContainer<CharacterEdge> edgeContainer;
        if (content != null && (edgeContainer = content.getConnection()) != null) {
            if(!edgeContainer.isEmpty()) {
                if (edgeContainer.hasPageInfo())
                    pageInfo = edgeContainer.getPageInfo();
                if (!edgeContainer.isEmpty())
                    onPostProcessed(GroupingUtil.groupCharactersByRole(edgeContainer.getEdges(), model));
                else
                    onPostProcessed(Collections.emptyList());
            }
        }
        if(model == null)
            onPostProcessed(null);
    }
}