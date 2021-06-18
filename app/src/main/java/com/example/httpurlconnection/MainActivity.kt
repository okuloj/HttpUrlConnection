package com.example.httpurlconnection

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val url = "https://reqres.in/api/users?page=2"
        val urlPost = "https://reqres.in/api/users"
        GlobalScope.launch(Dispatchers.Main) {
            //HttpGet(url)
            HttpPost(urlPost)
        }
    }
}

private suspend fun HttpGet(url: String) {
    var urlConn: HttpURLConnection? = null
    GlobalScope.launch(Dispatchers.IO) {
        try {
            urlConn = URL(url).openConnection() as HttpURLConnection
            if (urlConn != null) {
                val code = urlConn!!.responseCode
                if (code != 200) {
                    throw IOException("Invalid response from server: $code")
                }
                val rd = BufferedReader(InputStreamReader(urlConn!!.inputStream))

                lateinit var line: String
                while (true) {
                    line = rd.readLine()
                    if (line == null) {
                        break
                    }
                    else {
                        Log.v("Data", line)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            urlConn?.disconnect()
        }
    }
}

private suspend fun HttpPost(url: String) {
    var urlConn: HttpURLConnection? = null
    var params: MutableList<User> = mutableListOf()
    params.add(User("aaa", "111"))
    params.add(User("abc def", "12345we"))
    GlobalScope.launch(Dispatchers.IO) {
        try {
            urlConn = URL(url).openConnection() as HttpURLConnection
            if (urlConn != null) {
                urlConn!!.requestMethod = "POST"
                urlConn!!.readTimeout = 15000
                urlConn!!.connectTimeout = 10000
                urlConn!!.doInput = true
                urlConn!!.doOutput = true
                urlConn!!.setChunkedStreamingMode(0)
                val os: OutputStream = BufferedOutputStream(urlConn!!.outputStream)
                var bufferedWriter = BufferedWriter(OutputStreamWriter(os, "utf-8"))

                bufferedWriter.write(getQuery(params as ArrayList<User>))
                bufferedWriter.flush()
                bufferedWriter.close()
                os.close()

                val code = urlConn!!.responseCode
                if (code != 201) {
                    throw Exception("Invalid response from server: $code")
                }

                val bufferedReader = BufferedReader(InputStreamReader(urlConn!!.inputStream))

                lateinit var line: String
                while (true) {
                    line = bufferedReader.readLine()
                    if (line == null) {
                        break
                    }
                    else {
                        Log.v("DataPost", line)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            urlConn?.disconnect()
        }
    }
}

private fun getQuery(array: ArrayList<User>): String? {
    var stringBuilder = StringBuilder()
    var first: Boolean = true
    for (pair in array) {
        if (first) {
            first = false
        } else {
            stringBuilder.append("&")
        }
        stringBuilder.append(URLEncoder.encode(pair.userName, StandardCharsets.UTF_8.toString()))
        stringBuilder.append("=")
        stringBuilder.append(URLEncoder.encode(pair.pass, StandardCharsets.UTF_8.toString()))
    }

    return stringBuilder.toString()
}