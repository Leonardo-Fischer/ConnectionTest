package com.example.connectiontest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import java.io.InputStream
import java.io.OutputStreamWriter
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnConnect = findViewById<Button>(R.id.btnConnect)
        val btnSend = findViewById<Button>(R.id.btnSend)
        val makeRequest = findViewById<Button>(R.id.makeRequest)

        btnConnect.setOnClickListener {
            thread {
                try {
                    if (connection == null) {
                        connection = url.openConnection() as HttpsURLConnection?
                        connection?.doInput = true
                        connection?.connectTimeout = 10000
                        connection?.readTimeout = 60000
                        connection?.useCaches = false

                        SSLContext.getInstance("TLSv1.2").also {
                            it.init(null, null, null)
                            connection?.sslSocketFactory = it.socketFactory
                        }
                    }

                    connection?.connect()

                    Log.d("URL_connect", "Successfully connected")
                } catch (e: Exception) {
                    Log.e("URL_connect", "threw $e")
                }
            }
        }

        btnSend.setOnClickListener {
            thread {
                try {

                    Log.d("URL_send","Connection is $connection")

                    val inputStream: InputStream? = connection?.inputStream
                    val readStream = inputStream?.read()
                    inputStream?.close()

                    connection?.disconnect()
                    connection = null

                    Log.d("URL_send", "Successfully sent\n Response body: $readStream")
                } catch (e: Exception) {
                    Log.e("URL_send ", "threw $e")
                }
            }
        }

        makeRequest.setOnClickListener {
            thread {
                try {
                    val url = "https://test.com.br/posts"
                    val headers = mapOf("HeaderCustom1" to "HeaderValue1", "HeaderCustom2" to "HeaderValue2")
                    val parameters = mapOf("title" to "foo", "body" to "bar", "userId" to "1")

                    val response = sendRequest(
                        urlString = url,
                        method = "POST",
                        headers = headers,
                        parameters = parameters
                    )

                    Log.d("MakeRequest", "Response: $response")
                } catch (e: Exception) {
                    Log.e("MakeRequest", "threw $e")
                }
            }
        }
    }

    private fun sendRequest(
        urlString: String,
        method: String = "GET",
        headers: Map<String, String> = emptyMap(),
        parameters: Map<String, String> = emptyMap()
    ): String {
        val url = if (method.equals("GET", ignoreCase = true) && parameters.isNotEmpty()) {
            URL(urlString + "?" + parameters.map {
                "${URLEncoder.encode(it.key, "UTF-8")}=${URLEncoder.encode(it.value, "UTF-8")}"
            }.joinToString("&"))
        } else {
            URL(urlString)
        }

        val connection = url.openConnection() as HttpsURLConnection
        connection.requestMethod = method
        headers.forEach { (key, value) -> connection.setRequestProperty(key, value) }

        if (method.equals("POST", ignoreCase = true) || method.equals("PUT", ignoreCase = true)) {
            connection.doOutput = true
            val postData = parameters.map {
                "${URLEncoder.encode(it.key, "UTF-8")}=${URLEncoder.encode(it.value, "UTF-8")}"
            }.joinToString("&")
            connection.outputStream.use { output ->
                OutputStreamWriter(output).use { writer ->
                    writer.write(postData)
                    writer.flush()
                }
            }
        }

        return connection.inputStream.bufferedReader().use { it.readText() }
    }

    companion object {
        val url = URL("https://www.amazon.com.br/")
        var connection: HttpsURLConnection? = null
    }

}