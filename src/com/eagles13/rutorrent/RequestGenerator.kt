package com.eagles13.rutorrent

import com.eagles13.rutorrent.Credentials
import com.eagles13.rutorrent.TorrentRequestProperty
import org.w3c.dom.Document
import org.w3c.dom.Node
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.io.StringWriter
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
 * Created by Joshua on 02/08/2016.
 */

class RequestGenerator() {
    fun generateTorrentRequestXML(requestTypes: ArrayList<TorrentRequestProperty>): String {
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        val baseElement = doc.createElement("methodCall")
        doc.appendChild(baseElement)

        val method = baseElement.appendChild(doc.createElement("methodName"))
        method.appendChild(doc.createTextNode("d.multicall"))

        val params = baseElement.appendChild(doc.createElement("params"))
        params.appendChild(createParam("main", doc))

        requestTypes.forEach { params.appendChild(createParam(it.data, doc)) }

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


    fun authorizedRequest(credentials: Credentials, url: String, data: String): String {
        val obj = URL(url)
        val connection = if (url.startsWith("https")) obj.openConnection() as HttpsURLConnection else obj.openConnection() as HttpURLConnection

        connection.requestMethod = "POST"
        connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((credentials.username + ":" + credentials.password).toByteArray()))
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

}
