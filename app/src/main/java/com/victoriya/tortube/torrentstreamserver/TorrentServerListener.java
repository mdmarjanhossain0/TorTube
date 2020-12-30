package com.victoriya.tortube.torrentstreamserver;

import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;

public interface TorrentServerListener extends TorrentListener {

    void onServerReady(String url);

}
