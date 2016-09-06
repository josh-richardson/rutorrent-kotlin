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

fun main(args: Array<String>) {
    val downloadDirectory = "D:\\Download"
    val gson = Gson()
    val details : ArrayList<String>? = gson.fromJson(Scanner(File("src/com/eagles13/rut_downloader/credentials.txt")).useDelimiter("\\Z").next(), object : TypeToken<ArrayList<String>>(){}.type)

    val t = Thread(Runnable {
        if (details != null) {
            val downloaded = ArrayList<Torrent>()
            val downloading = ArrayList<Torrent>()

            val instance= ruTorrent(details[0], details[1], details[2])

            while (true) {
                instance.getTorrents().filter { x -> x.isDone }.forEach {
//                  tests serialization
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
                        getFilesRecursive(it.location, ftp).forEach { fi, isDirectory ->
                            if (!isDirectory) {
                                val dlFile = File(downloadDirectory + fi.substring(File(it.location).parent.length).replace("/", System.getProperty("file.separator")))
                                val parent = File(dlFile.parent)
                                if (!parent.exists()) {
                                    parent.mkdirs()
                                }
                                val stream = BufferedOutputStream(FileOutputStream(dlFile))
                                println("Downloading: " + fi)
                                println(if (ftp.retrieveFile(fi, stream)) "Success" else "Failure")
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

fun getFilesRecursive(initialDir: String, ftp: FTPClient): HashMap<String, Boolean> {
    val directories = HashMap<String, Boolean>()
    ftp.listFiles(initialDir).forEach {
        if (it.isDirectory) {
            directories.put(initialDir + "/" + it.name, true)
            directories.putAll(getFilesRecursive(initialDir + "/" + it.name, ftp))
        } else {
            directories.put(initialDir + "/" + it.name, false);
        }
    }
    return directories
}