package com.eagles13.rutorrent

/**
 * Created by Joshua on 01/08/2016.
 */
enum class TorrentRequestProperty(val data: String) {
    HASH("d.get_hash="),
    NAME("d.get_name="),
    PATH("d.get_base_path="),
    LEFT_BYTES("d.get_left_bytes="),
    SIZE_BYTES("d.get_size_bytes=")

//    More to be added
}
