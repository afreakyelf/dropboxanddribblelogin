package com.example.logindad

import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.DbxRequestConfig


object DropboxClient {

    fun getClient(ACCESS_TOKEN: String): DbxClientV2 {
        val config = DbxRequestConfig("dropbox/sample-app", "en_US")
        return DbxClientV2(config, ACCESS_TOKEN)
    }
}