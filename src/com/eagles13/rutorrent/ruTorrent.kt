package com.eagles13.rutorrent

import com.eagles13.rutorrent.Requests.TorrentRequest
import java.util.*

/**
 * Created by Joshua on 06/09/2016.
 */
class ruTorrent(val url: String, val username: String, val password: String) {
    fun getTorrents(): ArrayList<Torrent> {
        return TorrentRequest(Credentials(url, username, password), "plugins/rpc/rpc.php").getTorrents()
    }
}