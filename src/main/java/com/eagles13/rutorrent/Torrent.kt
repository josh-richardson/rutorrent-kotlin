package com.eagles13.rutorrent

/**
 * Created by Joshua on 02/08/2016.
 */
data class Torrent(val hash: String, val name: String, val location: String, val size: Long, var isDone: Boolean)