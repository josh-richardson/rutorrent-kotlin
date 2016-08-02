package com.eagles13.rutorrent

import org.w3c.dom.Document
import org.w3c.dom.Node
import org.xml.sax.InputSource
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * Created by Joshua on 01/08/2016.
 */


class Request (val url: String, val username: String, val password: String, vararg requestTypes : TorrentRequestProperty){
    val result : String
    val requestTypesInternal : Array<out TorrentRequestProperty>

    init {
        requestTypesInternal = requestTypes
        result = authorizedRequest(generateTorrentRequestXML())
    }

    fun getTorrents() : ArrayList<Torrent> {
        val toReturn = ArrayList<Torrent>()
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(InputSource(StringReader(result)))
        val nodes = doc.getElementsByTagName("data")
        for (n in 0..nodes.length - 1) {
            if (nodes.item(n).childNodes.length == requestTypesInternal.size) {
                val children = nodes.item(n).childNodes
                val tor =  Torrent(children.item(requestTypesInternal.indexOf(TorrentRequestProperty.HASH)).textContent,
                        children.item(requestTypesInternal.indexOf(TorrentRequestProperty.NAME)).textContent,
                        children.item(requestTypesInternal.indexOf(TorrentRequestProperty.PATH)).textContent,
                        children.item(requestTypesInternal.indexOf(TorrentRequestProperty.LEFT_BYTES)).textContent == "0")
                toReturn.add(tor)
            }
        }
        return toReturn
    }


    fun authorizedRequest(data: String): String {
        val obj = URL(this.url)
        val connection = if (url.startsWith("https")) obj.openConnection() as HttpsURLConnection else obj.openConnection() as HttpURLConnection

        connection.requestMethod = "POST"
        connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).toByteArray()))
        connection.doOutput = true
        val wr = DataOutputStream(connection.outputStream)
        wr.writeBytes(data)
        wr.flush()
        wr.close()
        val response = BufferedReader(InputStreamReader(connection.inputStream))
        val stringResponse = response.readLines().joinToString("")
        response.close()
        return stringResponse
    }

    fun generateTorrentRequestXML(): String {
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        val baseElement = doc.createElement("methodCall")
        doc.appendChild(baseElement)

        val method = baseElement.appendChild(doc.createElement("methodName"))
        method.appendChild(doc.createTextNode("d.multicall"))

        val params = baseElement.appendChild(doc.createElement("params"))
        params.appendChild(createParam("main", doc))

        requestTypesInternal.forEach { params.appendChild(createParam(it.data, doc)) }

        val writer = StringWriter()
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.transform(DOMSource(doc), StreamResult(writer))
        return writer.buffer.toString()
    }

    fun createParam(value: String, doc: Document): Node {
        val param = doc.createElement("param")
        val _value =param.appendChild(doc.createElement("value"))
        val string = _value.appendChild(doc.createElement("string"))
        string.appendChild(doc.createTextNode(value))
        return param
    }
}