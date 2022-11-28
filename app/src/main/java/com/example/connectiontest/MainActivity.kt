package com.example.connectiontest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import java.io.InputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnConnect = findViewById<Button>(R.id.btnConnect)
        val btnSend = findViewById<Button>(R.id.btnSend)

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
    }

    companion object {
        val url = URL("https://www.amazon.com.br/")
        var connection: HttpsURLConnection? = null
    }

}