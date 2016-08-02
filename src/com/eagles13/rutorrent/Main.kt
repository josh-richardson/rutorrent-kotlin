package com.eagles13.rutorrent

import com.eagles13.rutorrent.Requests.TorrentRequest
import java.util.*


/**
 * Created by Joshua on 12/06/2016.
 */

class ruTorrent(val url: String, val username: String, val password: String) {
    fun getTorrents(): ArrayList<Torrent> {
//        url + , username, password
        return TorrentRequest(Credentials(url, username, password), "plugins/rpc/rpc.php").getTorrents()
    }
}

fun main(args: Array<String>) {
    val downloaded = ArrayList<Torrent>()
    val downloading = ArrayList<Torrent>()

    val instance = ruTorrent("http://helios.feralhosting.com/jackeagles1/rutorrent/", "jackeagles1", "7ML4IIg7hbV6zr5Z")
    instance.getTorrents().forEach { println(it) }

}