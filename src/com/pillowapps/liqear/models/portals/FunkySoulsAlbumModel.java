package com.pillowapps.liqear.models.portals;

import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.network.callbacks.NewcomersSimpleCallback;
import com.pillowapps.liqear.network.funkysouls.FunkySoulsReader;

import java.util.List;

import inaka.com.tinytask.DoThis;
import inaka.com.tinytask.Something;
import inaka.com.tinytask.TinyTask;

public class FunkySoulsAlbumModel {

    public void getNewcomers(final List<Integer> pages, final NewcomersSimpleCallback<List<Album>> callback) {
        TinyTask.perform(new Something<List<Album>>() {
            @Override
            public List<Album> whichDoes() throws Exception {
                return new FunkySoulsReader().selectAlbumsFromPages(pages);
            }
        }).whenDone(new DoThis<List<Album>>() {
            @Override
            public void ifOK(List<Album> albums) {
                callback.success(albums);
            }

            @Override
            public void ifNotOK(Exception e) {
                callback.failure(e.getMessage());
            }
        }).go();
    }

}
