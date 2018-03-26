package com.mxt.anitrend.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.mxt.anitrend.model.entity.anilist.edge.CharacterEdge;
import com.mxt.anitrend.model.entity.anilist.edge.MediaEdge;
import com.mxt.anitrend.model.entity.anilist.edge.StaffEdge;
import com.mxt.anitrend.model.entity.base.MediaBase;
import com.mxt.anitrend.model.entity.base.StaffBase;
import com.mxt.anitrend.model.entity.container.body.EdgeContainer;
import com.mxt.anitrend.model.entity.group.EntityGroup;
import com.mxt.anitrend.model.entity.group.EntityHeader;
import com.mxt.anitrend.view.fragment.group.CharacterActorsFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by max on 2018/02/18.
 * Utils for group various types of media
 */

public class GroupingUtil {

    /**
     * Groups media by the media format, assuming that the media has be sorted by format
     * @see KeyUtils.MediaFormat
     * <br/>
     *
     * Only to be used when the sort type is @{@link KeyUtils.MediaSort#FORMAT}
     * which is the default sort type for the request @{@link KeyUtils#STAFF_MEDIA_REQ}
     * <br/>
     *
     * @param edges The potential external model response which needs to be grouped
     * @param model The current model item/s containing all data minus current mediaItems
     */
    public static List<EntityGroup> groupMediaByFormat(@NonNull List<MediaBase> edges, @Nullable List<EntityGroup> model) {
        List<EntityGroup> entityMap = new ArrayList<>();

        Map<String, List<MediaBase>> map = Stream.of(edges)
                .filter(value -> !TextUtils.isEmpty(value.getFormat()))
                .collect(Collectors.groupingBy(MediaBase::getFormat));
        for (Map.Entry<String, List<MediaBase>> entry: CompatUtil.getKeyFilteredMap(map)) {
            EntityHeader entityHeader = new EntityHeader(entry.getKey(), entry.getValue().size());
            if(model == null || !model.contains(entityHeader))
                entityMap.add(entityHeader);
            entityMap.addAll(entry.getValue());
        }

        return entityMap;
    }

    /**
     * Groups media by the media format, assuming that the media has be sorted by language
     * @see KeyUtils.StaffSort
     * <br/>
     *
     * Only to be used when the sort type is @{@link KeyUtils.StaffSort#LANGUAGE}
     * which is the default sort type for the request @{@link KeyUtils#STAFF_MEDIA_REQ}
     * <br/>
     *
     * @param edges The potential external model response which needs to be grouped
     * @param model The current model item/s containing all data minus current mediaItems
     */
    public static List<EntityGroup> groupStaffByLanguage(@NonNull List<StaffBase> edges, @Nullable List<EntityGroup> model) {
        List<EntityGroup> entityMap = new ArrayList<>();

        Map<String, List<StaffBase>> map = Stream.of(edges)
                .filter(value -> !TextUtils.isEmpty(value.getLanguage()))
                .collect(Collectors.groupingBy(StaffBase::getLanguage));
        for (Map.Entry<String, List<StaffBase>> entry: CompatUtil.getKeyFilteredMap(map)) {
            EntityHeader entityHeader = new EntityHeader(entry.getKey(), entry.getValue().size());
            if(model == null || !model.contains(entityHeader))
                entityMap.add(entityHeader);
            entityMap.addAll(entry.getValue());
        }

        return entityMap;
    }

    /**
     * Groups edge container items their media/node and the character role
     * N.B. In this use case the main model is not used to check for existence
     * of a given role because the voiceActors and characterRoles are grouped by media
     * <br/>
     *
     * @see CharacterActorsFragment restricted and should only be used by this
     * @see EdgeContainer
     * <br/>
     *
     * @param edges The potential external model response which needs to be grouped
     */
    public static List<EntityGroup> groupActorMediaEdge(List<MediaEdge> edges) {
        List<EntityGroup> entityMap = new ArrayList<>();
        for (MediaEdge edge : edges) {
            if(edge.getNode() != null) {
                if(!TextUtils.isEmpty(edge.getCharacterRole()))
                    edge.getNode().setSubGroupTitle(edge.getCharacterRole());
                edge.getNode().setContentType(KeyUtils.RECYCLER_TYPE_HEADER);
                entityMap.add(edge.getNode());
            }
            if(!CompatUtil.isEmpty(edge.getVoiceActors()))
                entityMap.addAll(edge.getVoiceActors());
        }
        return entityMap;
    }

    public static List<EntityGroup> groupMediaByRelationType(List<MediaEdge> edges) {
        List<EntityGroup> entityMap = new ArrayList<>();
        for (MediaEdge edge: edges) {
            EntityHeader entityHeader = new EntityHeader(edge.getRelationType());
            if(!entityMap.contains(entityHeader))
                entityMap.add(entityHeader);
            else {
                entityHeader.setSize(entityHeader.getSize() + 1);
                entityMap.add(edge.getNode());
            }
        }
        return entityMap;
    }

    public static List<EntityGroup> groupCharactersByRole(List<CharacterEdge> edges, List<EntityGroup> model) {
        List<EntityGroup> entityMap = new ArrayList<>();
        for (CharacterEdge edge: edges) {
            EntityHeader entityHeader = new EntityHeader(edge.getRole());
            if(model == null || !model.contains(entityHeader))
                entityMap.add(entityHeader);
            else
                entityMap.add(edge.getNode());
        }
        return entityMap;
    }

    public static List<EntityGroup> groupStaffByRole(List<StaffEdge> edges, List<EntityGroup> model) {
        List<EntityGroup> entityMap = new ArrayList<>();
        for (StaffEdge edge: edges) {
            EntityHeader entityHeader = new EntityHeader(edge.getRole());
            if(model == null || !model.contains(entityHeader))
                entityMap.add(entityHeader);
            else
                entityMap.add(edge.getNode());
        }
        return entityMap;
    }

    public static List<EntityGroup> groupMediaByStaffRole(List<MediaEdge> edges, List<EntityGroup> model) {
        List<EntityGroup> entityMap = new ArrayList<>();
        for (MediaEdge edge: edges) {
            EntityHeader entityHeader = new EntityHeader(edge.getStaffRole());
            if(model == null || !model.contains(entityHeader))
                entityMap.add(entityHeader);
            else
                entityMap.add(edge.getNode());
        }
        return entityMap;
    }

    public static <T extends EntityGroup> List<EntityGroup> wrapInGroup(List<T> data) {
        return new ArrayList<>(data);
    }
}