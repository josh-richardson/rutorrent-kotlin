package com.eagles13.rutorrent

/**
 * Created by Joshua on 02/08/2016.
 */
data class Torrent(val hash: String, val name: String, val location: String, var isDone: Boolean) {
//    fun getFiles() : ArrayList<String> {
////        val toReturn = ArrayList<String>()
////        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(InputSource(StringReader(result)))
////        val nodes = doc.getElementsByTagName("data")
////        for (n in 0..nodes.length - 1) {
////            if (nodes.item(n).childNodes.length == requestTypesInternal.size) {
////                val children = nodes.item(n).childNodes
////                val tor =  Torrent(children.item(requestTypesInternal.indexOf(TorrentRequestProperty.HASH)).textContent,
////                        children.item(requestTypesInternal.indexOf(TorrentRequestProperty.NAME)).textContent,
////                        children.item(requestTypesInternal.indexOf(TorrentRequestProperty.PATH)).textContent,
////                        children.item(requestTypesInternal.indexOf(TorrentRequestProperty.LEFT_BYTES)).textContent == "0")
////                toReturn.add(tor)
////            }
////        }
////        return toReturn
//
//    }
}
