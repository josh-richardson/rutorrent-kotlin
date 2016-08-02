package com.eagles13.rutorrent

import java.util.*


/**
 * Created by Joshua on 12/06/2016.
 */
data class Torrent(val hash: String, val name: String, val location: String, var isDone: Boolean)


class ruTorrent(val url: String, val username: String, val password: String) {
    fun getTorrents(): ArrayList<Torrent> {
        return Request(url + "plugins/rpc/rpc.php", username, password, TorrentRequestProperty.HASH, TorrentRequestProperty.NAME, TorrentRequestProperty.PATH, TorrentRequestProperty.LEFT_BYTES).getTorrents()
    }
}

fun main(args: Array<String>) {
    val downloaded = ArrayList<Torrent>()
    val downloading = ArrayList<Torrent>()

    val instance = ruTorrent("http://helios.feralhosting.com/jackeagles1/rutorrent/", "jackeagles1", "7ML4IIg7hbV6zr5Z")
    instance.getTorrents().forEach { println(it) }

}