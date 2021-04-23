package com.udacity.Model

/**
 * Created by Bhoomi on 4/22/2021.
 */
//Create the object for download file data
data class FileDownload(
    val name : String = "",
    val url :String = "",
    var status : String = "" ,
    var fileName : String = ""
) {
}