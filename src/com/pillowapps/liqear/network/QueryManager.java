package com.pillowapps.liqear.network;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Html;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.pillowapps.liqear.LiqearApplication;
import com.pillowapps.liqear.audio.AudioTimeline;
import com.pillowapps.liqear.components.CancellableThread;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.PlaylistManager;
import com.pillowapps.liqear.helpers.StringUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Entry point into the API.
 */
@SuppressWarnings({"unchecked"})
public class QueryManager {
    public static final String PHOTOS_GET_WALL_UPLOAD_SERVER = "photos.getWallUploadServer";
    public static final String SETLISTS = "setlists";
    private static final String WALL_POST = "wall.post";
    private static final String PHOTOS_SAVE_WALL_PHOTO = "photos.saveWallPhoto";
    private final String secret;
    private final String apiKey;
    private final String sk;
    private DefaultHttpClient mHttpClient;
    private GetTask task;
    private CancellableThread scrobbleThread;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public QueryManager() {
        this.apiKey = AuthorizationInfoManager.getLastfmApiKey();
        this.sk = AuthorizationInfoManager.getLastfmKey();
        this.secret = AuthorizationInfoManager.getLastfmSecret();
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        mHttpClient = new DefaultHttpClient(params);
    }

    public static QueryManager getInstance() {
        return new QueryManager();
    }

    private static String createSignature(String method, Map<String,
            String> params, String secret) {
        params = new TreeMap<>(params);
        params.put("method", method);
        StringBuilder b = new StringBuilder(100);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            b.append(entry.getKey());
            b.append(entry.getValue());
        }
        b.append(secret);
        return StringUtils.md5(b.toString());
    }

    private void doQuery(final GetResponseCallback callback, Params methodParams) {
        if (task != null) task.cancel(true);
        RestTaskCallback restTaskCallback = new RestTaskCallback() {
            @Override
            public void onTaskComplete(ReadyResult response) {
                if (response != null)
                    callback.onDataReceived(response);
            }
        };
        task = new GetTask(restTaskCallback);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, methodParams);
        } else {
            task.execute(methodParams);
        }
    }

    public void getArtistImages(String artist, int page, final GetResponseCallback callback) {
        final Params params = new Params(null, ApiMethod.ARTIST_IMAGES);
        params.setApiSource(Params.ApiSource.STRAIGHT);
        params.setUrl(String.format("http://www.lastfm.ru/music/%s/+images?page=%d",
                StringUtils.encode(artist), page));
        doQuery(callback, params);
    }

    public void scrobbleOffline() {
        if (scrobbleThread != null) {
            scrobbleThread.setCancelled(true);
        }
        scrobbleThread = new CancellableThread(new Runnable() {
            @Override
            public void run() {
                final PlaylistManager playlistManager = PlaylistManager.getInstance();
                while (true) {
                    Track track = playlistManager.loadHeadTrackToScrobble();
                    if (track == null) break;
                    Map<String, String> params = StringUtils.map("api_key", apiKey,
                            "artist", Html.fromHtml(track.getArtist()).toString(), "track", Html
                                    .fromHtml(track.getTitle()).toString(), "timestamp",
                            String.valueOf(track.getScrobbleTime())
                    );
                    params.put("sk", sk);
                    String apiSig = createSignature("track.scrobble", params, secret);
                    params.put("api_sig", apiSig);
                    Params methodParams = new Params("track.scrobble",
                            ApiMethod.TRACK_SCROBBLE, params);
                    methodParams.setApiSource(Params.ApiSource.LASTFM);
                    if (scrobbleThread.isCancelled()) return;
                    new PostTask(new RestTaskCallback() {
                        @Override
                        public void onTaskComplete(ReadyResult result) {
                        }
                    }).doHttpQuery(methodParams);
                    playlistManager.removeHeadTrackToScrobble();
                }
            }
        });
        scrobbleThread.start();
    }

    public void postVkUserWall(String message, String bitmap, final Track track,
                               final GetResponseCallback callback) {
        final Params params = new Params(WALL_POST, ApiMethod.WALL_POST);
        params.putParameter("message", message);
        final StringBuilder attachments = new StringBuilder();
        if (bitmap != null) {
            uploadBitmap(bitmap, new GetResponseCallback() {
                @Override
                public void onDataReceived(ReadyResult result) {
                    String photoId = (String) result.getObject();
                    attachments.append(photoId);
                    if (track.getOwnerId() != 0) {
                        if (photoId != null) {
                            attachments.append(",");
                        }
                        attachments.append("audio")
                                .append(track.getOwnerId()).append("_").append(track.getAid());
                    }
                    params.putParameter("attachments", attachments.toString());
                    params.setApiSource(Params.ApiSource.VK);
                    doQuery(callback, params);
                }
            });
        } else {
            if (track.getOwnerId() != 0) {
                attachments.append("audio").append(track.getOwnerId())
                        .append("_").append(track.getAid());
            }
            params.putParameter("attachments", attachments.toString());
            params.setApiSource(Params.ApiSource.VK);
            doQuery(callback, params);
        }
    }

    public void uploadBitmap(final String imageUrl, final GetResponseCallback callback) {
        final File cachedImage = ImageLoader.getInstance().getDiscCache().get(imageUrl);
        getPhotosWallUploadServer(new GetResponseCallback() {
            @Override
            public void onDataReceived(final ReadyResult result) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        uploadUserPhoto(cachedImage, (String) result.getObject(), callback);
                    }
                }).start();
            }
        });
    }

    private void saveWallPhoto(String server, String photo, String hash,
                               GetResponseCallback callback) {
        final Params params = new Params(PHOTOS_SAVE_WALL_PHOTO,
                ApiMethod.PHOTOS_SAVE_WALL_PHOTO);
        params.putParameter("server", server);
        params.putParameter("photo", photo);
        params.putParameter("hash", hash);
        params.setApiSource(Params.ApiSource.VK);
        doQuery(callback, params);
    }

    public void getPhotosWallUploadServer(GetResponseCallback callback) {
        final Params params = new Params(PHOTOS_GET_WALL_UPLOAD_SERVER,
                ApiMethod.PHOTOS_GET_WALL_UPLOAD_SERVER);
        params.setApiSource(Params.ApiSource.VK);
        doQuery(callback, params);
    }

    public void uploadUserPhoto(File image, String server, GetResponseCallback callback) {
        try {
            HttpPost httppost = new HttpPost(server);
            MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
            multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            ContentBody cbFile = new FileBody(image,
                    ContentType.create("image/jpeg"), "photo.jpg");
            multipartEntity.addPart("photo", cbFile);
            httppost.setEntity(multipartEntity.build());
            mHttpClient.execute(httppost, new PhotoUploadResponseHandler(callback));

        } catch (Exception ignored) {
        }
    }

    public void getAlbumImage(final Album album, final CompletionListener listener) {
        if (album == null) return;
        imageLoader.loadImage(LiqearApplication.getAppContext(),
                album.getImageUrl(), new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted() {

                    }

                    @Override
                    public void onLoadingFailed(FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(Bitmap bitmap) {
                        AudioTimeline.setCurrentAlbumBitmap(bitmap);
                        listener.onCompleted();
                    }

                    @Override
                    public void onLoadingCancelled() {

                    }
                });
    }

    private class PhotoUploadResponseHandler implements ResponseHandler<Object> {

        private final GetResponseCallback callback;

        public PhotoUploadResponseHandler(GetResponseCallback callback) {
            this.callback = callback;
        }

        @Override
        public Object handleResponse(HttpResponse response)
                throws IOException {

            HttpEntity r_entity = response.getEntity();
            String responseString = EntityUtils.toString(r_entity);
            Result uploadResult = new Result(responseString, null, null, null);
            ReadyResult readyResult = Parser.getInstance(uploadResult,
                    ApiMethod.UPLOAD_PHOTO).parse();
            List<String> list = (List<String>) readyResult.getObject();
            saveWallPhoto(list.get(0), list.get(1), list.get(2), new GetResponseCallback() {
                @Override
                public void onDataReceived(ReadyResult result) {
                    callback.onDataReceived(result);
                }
            });

            return null;
        }
    }
}

