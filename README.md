# rutorrent-kotlin
A Kotlin library for interacting with the ruTorrent web client. The library lets you pull torrent details (hash, name, file size, location on disk, whether the torrent has finished downloading).
Plans for extensions to upload torrents, and to delete torrents are in the works.

## ruTorrent Downloader
This program can be used to download torrents from ruTorrent (via FTP) when they have completed. Command line arguments required are as followed:
ruTorrent public facing interface location, ruTorrent username, ruTorrent password, FTP server name, FTP username, FTP password.
