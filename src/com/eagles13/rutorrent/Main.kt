package com.eagles13.rutorrent

import org.apache.commons.io.IOUtils
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection


/**
 * Created by Joshua on 12/06/2016.
 */
data class Torrent(val name: String, val location: String);


class ruTorrent(val url: String, val username: String, val password: String) {


    fun getTorrents(): String {
        val res = IOUtils.toString(javaClass.getResource("requests/getTorrents.xml"), "UTF-8")
        return authorizedRequest(url + "plugins/rpc/rpc.php", res)
    }



    fun authorizedRequest(reqUrl:String, data: String): String {
        val obj = URL(reqUrl)
        val connection = if (reqUrl.startsWith("https")) obj.openConnection() as HttpsURLConnection else obj.openConnection() as HttpURLConnection

        connection.requestMethod = "POST"
        connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).toByteArray()))
        connection.doOutput = true
        val wr = DataOutputStream(connection.outputStream)
        wr.writeBytes(data)
        wr.flush()
        wr.close()

        val response = BufferedReader(InputStreamReader(connection.inputStream))
        val stringResponse = response.readLines().joinToString { "" }
        response.close()
        return stringResponse
    }


}


fun main(args: Array<String>) {
    val instance = ruTorrent("", "", "")
    println(instance.getTorrents())

}