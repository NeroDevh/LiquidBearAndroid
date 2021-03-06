package com.pillowapps.liqear.components;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.*;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.Utils;
import com.pillowapps.liqear.models.*;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapter;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapterWrapper;

public class ModeClickListener implements android.widget.AdapterView.OnItemClickListener {
    private MainActivity activity;

    public ModeClickListener(MainActivity context) {
        this.activity = context;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Mode mode;
        if (adapterView.getAdapter() instanceof StickyGridHeadersBaseAdapterWrapper) {
            StickyGridHeadersBaseAdapterWrapper adapter =
                    ((StickyGridHeadersBaseAdapterWrapper) adapterView.getAdapter());
            StickyGridHeadersBaseAdapter wrappedAdapter = adapter.getWrappedAdapter();
            mode = (Mode) wrappedAdapter.getItem(position);
        } else {
            Object itemAtPosition = adapterView.getItemAtPosition(position);
            if (itemAtPosition instanceof ListItem) {
                mode = ((ListItem) itemAtPosition).getMode();
            } else {
                return;
            }
        }
        if (mode.isNeedLastfm() && !AuthorizationInfoManager.isAuthorizedOnLastfm()) {
            Toast.makeText(activity, R.string.last_fm_not_authorized, Toast.LENGTH_SHORT).show();
            return;
        } else if ((mode.getCategory() == Category.VK || mode.getModeEnum() == ModeEnum.RADIOMIX
                || mode.getModeEnum() == ModeEnum.LIBRARY)
                && Utils.isOnline() && !AuthorizationInfoManager.isAuthorizedOnVk()) {
            Toast.makeText(activity, R.string.vk_not_authorized, Toast.LENGTH_SHORT).show();
            return;
        } else if (mode.getCategory() != Category.LOCAL && !Utils.isOnline()) {
            Toast.makeText(activity, R.string.no_internet, Toast.LENGTH_SHORT).show();
            return;
        }
        switch (mode.getModeEnum()) {
            case LOVED: {
                Intent intent = new Intent(activity, UserViewerLastfmActivity.class);
                intent.putExtra(UserViewerLastfmActivity.USER,
                        new User(AuthorizationInfoManager.getLastfmName()));
                intent.putExtra(UserViewerLastfmActivity.TAB_INDEX,
                        UserViewerLastfmActivity.LOVED_INDEX);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] User Loved");
            }
            break;
            case TOP_TRACKS: {
                Intent intent = new Intent(activity, UserViewerLastfmActivity.class);
                intent.putExtra(UserViewerLastfmActivity.USER,
                        new User(AuthorizationInfoManager.getLastfmName()));
                intent.putExtra(UserViewerLastfmActivity.TAB_INDEX,
                        UserViewerLastfmActivity.TOP_TRACKS_INDEX);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Top Tracks");
            }
            break;
            case TOP_ARTISTS: {
                Intent intent = new Intent(activity, UserViewerLastfmActivity.class);
                intent.putExtra(UserViewerLastfmActivity.USER,
                        new User(AuthorizationInfoManager.getLastfmName()));
                intent.putExtra(UserViewerLastfmActivity.TAB_INDEX,
                        UserViewerLastfmActivity.TOP_ARTISTS_INDEX);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Top Artists");
            }
            break;
            case CHARTS: {
                Intent intent = new Intent(activity, ChartsActivity.class);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Charts");
            }
            break;
            case LIBRARY: {
                activity.openLibrary();
                sendAnalyticsModeClickEvent("[LAST.FM] Library");
            }
            break;
            case ARTIST_RADIO: {
                Intent intent = new Intent(activity, SearchSherlockListActivity.class);
                intent.putExtra(SearchSherlockListActivity.SEARCH_MODE,
                        SearchSherlockListActivity.SearchMode.ARTIST);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Artist search");
            }
            break;
            case TAG_RADIO: {
                Intent intent = new Intent(activity, SearchSherlockListActivity.class);
                intent.putExtra(SearchSherlockListActivity.SEARCH_MODE,
                        SearchSherlockListActivity.SearchMode.TAG);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Tag search");
            }
            break;
            case ALBUM_RADIO: {
                Intent intent = new Intent(activity, SearchSherlockListActivity.class);
                intent.putExtra(SearchSherlockListActivity.SEARCH_MODE,
                        SearchSherlockListActivity.SearchMode.ALBUM);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Album search");
            }
            break;
            case RECOMMENDATIONS: {
                Intent intent = new Intent(activity, RecommendationsSherlockActivity.class);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Recommended");
            }
            break;
            case RADIOMIX: {
                activity.openRadiomix();
                sendAnalyticsModeClickEvent("[LAST.FM] Radiomix");
            }
            break;
            case NEIGHBOURS: {
                Intent intent = new Intent(activity, SearchSherlockListActivity.class);
                intent.putExtra(SearchSherlockListActivity.SEARCH_MODE,
                        SearchSherlockListActivity.SearchMode.NEIGHBOURS);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Neighbours");
            }
            break;
            case FRIENDS_LAST: {
                Intent intent = new Intent(activity, SearchSherlockListActivity.class);
                intent.putExtra(SearchSherlockListActivity.SEARCH_MODE,
                        SearchSherlockListActivity.SearchMode.LASTFM_FRIENDS);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Friends");
            }
            break;
            case RECENT_LAST: {
                Intent intent = new Intent(activity, UserViewerLastfmActivity.class);
                intent.putExtra(UserViewerLastfmActivity.USER,
                        new User(AuthorizationInfoManager.getLastfmName()));
                intent.putExtra(UserViewerLastfmActivity.TAB_INDEX,
                        UserViewerLastfmActivity.RECENT_INDEX);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LAST.FM] Recent");
            }
            break;
            case USER_AUDIO_VK: {
                Intent intent = new Intent(activity, UserViewerVkActivity.class);
                intent.putExtra(UserViewerVkActivity.USER,
                        new User(AuthorizationInfoManager.getVkName(),
                                AuthorizationInfoManager.getVkUserId()));
                intent.putExtra(UserViewerVkActivity.TAB_INDEX,
                        UserViewerVkActivity.USER_AUDIO_INDEX);
                intent.putExtra(UserViewerVkActivity.YOU_MODE, true);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[VK] User Audio");
            }
            break;
            case GROUP_VK: {
                Intent intent = new Intent(activity, SearchSherlockListActivity.class);
                intent.putExtra(SearchSherlockListActivity.SEARCH_MODE,
                        SearchSherlockListActivity.SearchMode.GROUP);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[VK] Group");
            }
            break;
            case FRIENDS_VK: {
                Intent intent = new Intent(activity, SearchSherlockListActivity.class);
                intent.putExtra(SearchSherlockListActivity.SEARCH_MODE,
                        SearchSherlockListActivity.SearchMode.VK_FRIENDS);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[VK] Friends");
            }
            break;
            case SEARCH_VK: {
                Intent intent = new Intent(activity, SearchSherlockListActivity.class);
                intent.putExtra(SearchSherlockListActivity.SEARCH_MODE,
                        SearchSherlockListActivity.SearchMode.VK_SIMPLE_SEARCH);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[VK] Search");
            }
            break;
            case ALBUMS_VK: {
                Intent intent = new Intent(activity, UserViewerVkActivity.class);
                intent.putExtra(UserViewerVkActivity.USER,
                        new User(AuthorizationInfoManager.getVkName(),
                                AuthorizationInfoManager.getVkUserId()));
                intent.putExtra(UserViewerVkActivity.TAB_INDEX, UserViewerVkActivity.ALBUM_INDEX);
                intent.putExtra(UserViewerVkActivity.YOU_MODE, true);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[VK] Albums");
            }
            break;
            case RECOMMENDATIONS_VK: {
                Intent intent = new Intent(activity, SearchSherlockListActivity.class);
                intent.putExtra(SearchSherlockListActivity.SEARCH_MODE,
                        SearchSherlockListActivity.SearchMode.VK_RECOMMENDATIONS);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[VK] Recommendations");
            }
            break;
            case WALL_VK: {
                Intent intent = new Intent(activity, UserViewerVkActivity.class);
                intent.putExtra(UserViewerVkActivity.USER,
                        new User(AuthorizationInfoManager.getVkName(),
                                AuthorizationInfoManager.getVkUserId()));
                intent.putExtra(UserViewerVkActivity.TAB_INDEX, UserViewerVkActivity.WALL_INDEX);
                intent.putExtra(UserViewerVkActivity.YOU_MODE, true);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[VK] Wall");
            }
            break;
            case FAVORITES_VK: {
                Intent intent = new Intent(activity, UserViewerVkActivity.class);
                intent.putExtra(UserViewerVkActivity.USER,
                        new User(AuthorizationInfoManager.getVkName(),
                                AuthorizationInfoManager.getVkUserId()));
                intent.putExtra(UserViewerVkActivity.TAB_INDEX, UserViewerVkActivity.FAVORITES);
                intent.putExtra(UserViewerVkActivity.YOU_MODE, true);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[VK] Favorites");
            }
            break;
            case FEED_VK: {
                Intent intent = new Intent(activity, UserViewerVkActivity.class);
                intent.putExtra(UserViewerVkActivity.USER,
                        new User(AuthorizationInfoManager.getVkName(),
                                AuthorizationInfoManager.getVkUserId()));
                intent.putExtra(UserViewerVkActivity.TAB_INDEX, UserViewerVkActivity.NEWS_FEED);
                intent.putExtra(UserViewerVkActivity.YOU_MODE, true);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[VK] Feed");
            }
            break;
            case FUNKY: {
                Intent intent = new Intent(activity, NewcomersSherlockListActivity.class);
                intent.putExtra(NewcomersSherlockListActivity.MODE,
                        NewcomersSherlockListActivity.Mode.FUNKYSOULS);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[OTHER] FunkySouls");
            }
            break;
            case ALTERPORTAL: {
                Intent intent = new Intent(activity, NewcomersSherlockListActivity.class);
                intent.putExtra(NewcomersSherlockListActivity.MODE,
                        NewcomersSherlockListActivity.Mode.ALTERPORTAL);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[OTHER] Alterportal");
            }
            break;
            case POSTHARDCORE: {
                Intent intent = new Intent(activity, NewcomersSherlockListActivity.class);
                intent.putExtra(NewcomersSherlockListActivity.MODE,
                        NewcomersSherlockListActivity.Mode.POST_HARDCORE_RU);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[OTHER] Post-hardcore.ru");
            }
            break;
            case SETLIST: {
                Intent intent = new Intent(activity, SetlistSearchSherlockActivity.class);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[OTHER] Setlists");
            }
            break;
            case LOCAL_ARTISTS: {
                Intent intent = new Intent(activity, SearchSherlockListActivity.class);
                intent.putExtra(SearchSherlockListActivity.SEARCH_MODE,
                        SearchSherlockListActivity.SearchMode.LOCAL_ARTISTS);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LOCAL] Artists");
            }
            break;
            case LOCAL_TRACKS: {
                Intent intent = new Intent(activity, SearchSherlockListActivity.class);
                intent.putExtra(SearchSherlockListActivity.SEARCH_MODE,
                        SearchSherlockListActivity.SearchMode.LOCAL_TRACKS);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LOCAL] Tracks");
            }
            break;
            case LOCAL_ALBUMS: {
                Intent intent = new Intent(activity, SearchSherlockListActivity.class);
                intent.putExtra(SearchSherlockListActivity.SEARCH_MODE,
                        SearchSherlockListActivity.SearchMode.LOCAL_ALBUMS);
                activity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                sendAnalyticsModeClickEvent("[LOCAL] Albums");
            }
            break;
            default:
                break;
        }
    }

    private void sendAnalyticsModeClickEvent(String mode) {
        EasyTracker easyTracker = EasyTracker.getInstance(activity);
        easyTracker.send(MapBuilder
                .createEvent("ui_action",     // Event category (required)
                        "mode_click",  // Event action (required)
                        mode,   // Event label
                        null)            // Event value
                .build()
        );
    }
}