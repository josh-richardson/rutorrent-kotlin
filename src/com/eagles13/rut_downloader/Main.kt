package com.eagles13.rut_downloader

import com.eagles13.rutorrent.Credentials
import com.eagles13.rutorrent.Requests.TorrentRequest
import com.eagles13.rutorrent.Torrent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.concurrent.Executors


/**
 * Created by Joshua on 12/06/2016.
 */
inline fun <reified T> genericType() = object: TypeToken<T>() {}.type!!


class ruTorrent(val url: String, val username: String, val password: String) {
    fun getTorrents(): ArrayList<Torrent> {
        return TorrentRequest(Credentials(url, username, password), "plugins/rpc/rpc.php").getTorrents()
    }
}


fun getFiles(dir: String) {

}

fun main(args: Array<String>) {
    val downloadDirectory = "D:\\Download"
    val gson = Gson()
    val details : ArrayList<String>? = gson.fromJson(Scanner(File("src/com/eagles13/rut_downloader/credentials.txt")).useDelimiter("\\Z").next(), object : TypeToken<ArrayList<String>>(){}.type)

    val t = Thread(Runnable {
        if (details != null) {
            val downloaded = ArrayList<Torrent>()
            val downloading = ArrayList<Torrent>()

            val instance= ruTorrent(details[0], details[1], details[2])
            //endregion

            while (true) {
                instance.getTorrents().forEach {
                    val rit : Torrent = gson.fromJson(gson.toJson(it), genericType<Torrent>())
                    println(rit)

                    if (!downloaded.contains(it) && !downloading.contains(it)) {
                        downloading.add(it)

                        val ftp = FTPClient()
                        ftp.connect(details[3])
                        ftp.login(details[4], details[5])
                        ftp.enterLocalPassiveMode()
                        ftp.setFileType(FTP.BINARY_FILE_TYPE)

                        println(ftp.replyCode)
                        ftp.listFiles(it.location).forEach {fi ->
                            if (fi.isFile) {
                                val dlFile = File(downloadDirectory + "\\" + fi.name)
                                val stream = BufferedOutputStream(FileOutputStream(dlFile))
                                println(ftp.retrieveFile(it.location + "/"+ fi.name, stream))
                                stream.close()
                            }
                        }


                    }
                }
                Thread.sleep(5000)
            }
        }

    })


    val executor = Executors.newFixedThreadPool(1)
    executor.submit(t)
    readLine()
    executor.shutdownNow()





}



fun serializeTorrent(torrent: Torrent) {

}