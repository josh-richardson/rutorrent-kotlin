package com.eagles13.rutorrent.Requests

import com.eagles13.rutorrent.Credentials
import com.eagles13.rutorrent.RequestGenerator
import com.eagles13.rutorrent.Torrent
import com.eagles13.rutorrent.TorrentRequestProperty
import org.xml.sax.InputSource
import java.io.StringReader
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Created by Joshua on 02/08/2016.
 */
class TorrentRequest(credentials: Credentials, urlAppend: String) : Request(credentials, urlAppend) {

    val torrentRequestData = arrayListOf(TorrentRequestProperty.HASH, TorrentRequestProperty.NAME, TorrentRequestProperty.PATH, TorrentRequestProperty.LEFT_BYTES);

    init {
        result = RequestGenerator().authorizedRequest(credentials, credentials.url + urlAppend, RequestGenerator().generateTorrentRequestXML(torrentRequestData))
    }

    fun getTorrents(): ArrayList<Torrent> {
        val toReturn = ArrayList<Torrent>()
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(InputSource(StringReader(result)))
        val nodes = doc.getElementsByTagName("data")
        for (n in 0..nodes.length - 1) {
            if (nodes.item(n).childNodes.length == torrentRequestData.size) {
                val children = nodes.item(n).childNodes
                val tor = Torrent(children.item(0).textContent,
                        children.item(1).textContent,
                        children.item(2).textContent,
                        children.item(3).textContent == "0")
                toReturn.add(tor)
            }
        }
        return toReturn
    }
}