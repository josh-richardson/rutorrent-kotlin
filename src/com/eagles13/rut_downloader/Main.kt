package com.eagles13.rut_downloader

import com.eagles13.rutorrent.Torrent
import com.eagles13.rutorrent.ruTorrent
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.Executors


/**
 * Created by Joshua on 12/06/2016.
 */
inline fun <reified T> genericType() = object : TypeToken<T>() {}.type!!

var cliDownload = false
val downloadDirectory = "/home/joshua/Downloads"
val gson = Gson()
val downloaded = ArrayList<Torrent>()
val downloading = ArrayList<Torrent>()


fun main(args: Array<String>) {
    try {
        JsonParser().parse(String(Files.readAllBytes(Paths.get("rut-downloader.json")))).asJsonArray.forEach { downloaded.add(gson.fromJson(it, genericType<Torrent>())) }
    } catch (e: Exception) {}

    val t = Thread(Runnable {
        val instance = ruTorrent(args[0], args[1], args[2])
        val ftpDetails = FTPCredentials(args[3], args[4], args[5], args[6])
        while (true) {
            instance.getTorrents().filter { x -> x.isDone }.forEach {
                if (!downloaded.contains(it) && !downloading.contains(it)) {
                    downloading.add(it)
                    if (cliDownload) {
                        cliDownloadTorrent(it, ftpDetails)
                    } else {
                        val ftp = FTPClient()
                        ftp.connect(args[3])
                        ftp.login(args[4], args[5])
                        ftp.enterLocalPassiveMode()
                        ftp.setFileType(FTP.BINARY_FILE_TYPE)
                        ftp.setReceiveBufferSize(1048576)
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
                    downloaded.add(it)
                    downloading.remove(it)
                } else {
                    println("Already downloaded: " + it)
                }
            }
            Thread.sleep(5000)
        }

    })


    val executor = Executors.newFixedThreadPool(1)
    executor.submit(t)
    readLine()
    executor.shutdownNow()

    val writer = PrintWriter("rut-downloader.json", "UTF-8")
    writer.write(gson.toJson(downloaded))
    writer.close()

}

fun cliDownloadTorrent(t: Torrent, c: FTPCredentials) {
    val tempScript = createDownloadScript(t, c)
    try {
        val pb = ProcessBuilder("bash", tempScript.toString())
        pb.inheritIO()
        val process = pb.start()
        process.waitFor()
    } finally {
        tempScript.delete()
    }
}

fun createDownloadScript(t: Torrent, c: FTPCredentials): File {
    val tempScript = File.createTempFile("script", null)
    val streamWriter = OutputStreamWriter(FileOutputStream(tempScript))
    val printWriter = PrintWriter(streamWriter)
    printWriter.println("cd " + downloadDirectory)
    val downloadCommmand = String.format("lftp -c \"mirror --use-pget-n=16 ftp://%s:%s@%s/%s\"", c.userName, c.password, c.url, t.location.replace(c.pathTruncate, ""))
    println("Downloading: " + downloadCommmand)
    printWriter.println(downloadCommmand)
    printWriter.println("exit")
    printWriter.println("exit")
    printWriter.close()
    return tempScript
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