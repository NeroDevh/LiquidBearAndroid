package com.pillowapps.liqear.helpers;

import com.pillowapps.liqear.models.Album;
import com.pillowapps.liqear.models.Artist;
import com.pillowapps.liqear.models.Tag;
import com.pillowapps.liqear.models.Track;
import com.pillowapps.liqear.models.User;
import com.pillowapps.liqear.models.lastfm.LastfmAlbum;
import com.pillowapps.liqear.models.lastfm.LastfmArtist;
import com.pillowapps.liqear.models.lastfm.LastfmImage;
import com.pillowapps.liqear.models.lastfm.LastfmTag;
import com.pillowapps.liqear.models.lastfm.LastfmTrack;
import com.pillowapps.liqear.models.lastfm.LastfmUser;

import java.util.ArrayList;
import java.util.List;

public class Converter {

    public static Track convertTrack(LastfmTrack lastfmTrack) {
        String artist = lastfmTrack.getArtist().getName();
        String title = lastfmTrack.getName();
        return new Track(artist, title);
    }

    public static Artist convertArtist(LastfmArtist lastfmArtist) {
        String artistName = lastfmArtist.getName();
        List<LastfmImage> images = lastfmArtist.getImages();
        Artist artist = new Artist(artistName);
        if (images != null) {
            String previewUrl = images.get(images.size() - 1).getUrl();
            artist.setPreviewUrl(previewUrl);
        }
        return artist;
    }

    private static User convertUser(LastfmUser lastfmUser) {
        String name = lastfmUser.getName();
        User user = new User(name);
        List<LastfmImage> images = lastfmUser.getImages();
        if (images != null) {
            user.setImageUrl(images.get(images.size() - 1).getUrl());
        }
        return user;
    }

    private static Tag convertTag(LastfmTag lastfmTag) {
        String name = lastfmTag.getName();
        return new Tag(name);
    }

    private static Album convertAlbum(LastfmAlbum lastfmAlbum) {
        String artist = lastfmAlbum.getArtist();
        String name = lastfmAlbum.getName();

        return new Album(artist, name);
    }

    public static List<Track> convertTrackList(List<LastfmTrack> lastfmTracks) {
        List<Track> tracks = new ArrayList<>();
        for (LastfmTrack lastfmTrack : lastfmTracks) {
            Track track = convertTrack(lastfmTrack);
            tracks.add(track);
        }
        return tracks;
    }

    public static List<Artist> convertArtistList(List<LastfmArtist> lastfmArtists) {
        List<Artist> artists = new ArrayList<>();
        for (LastfmArtist lastfmArtist : lastfmArtists) {
            Artist artist = convertArtist(lastfmArtist);
            artists.add(artist);
        }
        return artists;
    }

    public static List<Tag> convertTags(List<LastfmTag> lastfmTags) {
        List<Tag> tags = new ArrayList<>();
        for (LastfmTag lastfmTag : lastfmTags) {
            Tag tag = convertTag(lastfmTag);
            tags.add(tag);
        }
        return tags;
    }

    public static List<Album> convertAlbums(List<LastfmAlbum> lastfmAlbums) {
        List<Album> albums = new ArrayList<>();
        for (LastfmAlbum lastfmAlbum : lastfmAlbums) {
            Album album = convertAlbum(lastfmAlbum);
            albums.add(album);
        }
        return albums;
    }

    public static List<User> convertUsers(List<LastfmUser> lastfmUsers) {
        List<User> users = new ArrayList<>();
        for (LastfmUser lastfmUser : lastfmUsers) {
            User user = convertUser(lastfmUser);
            users.add(user);
        }
        return users;
    }
}