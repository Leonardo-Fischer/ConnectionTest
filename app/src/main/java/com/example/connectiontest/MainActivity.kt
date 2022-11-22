package com.example.connectiontest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection
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
                    connection = url.openConnection() as HttpsURLConnection?
                    connection?.doOutput = true
                    connection?.connectTimeout = 10000
                    connection?.readTimeout = 60000
                    connection?.connect()

                    val outputStream: OutputStream? = connection?.outputStream
                    outputStream?.write(10)
                    outputStream?.flush()
                    outputStream?.close()


                    Log.d("URL_connect", "Successfully connected")
                } catch (e: Exception) {
                    Log.e("URL_connect", "threw $e")
                }
            }
        }

        btnSend.setOnClickListener {
            thread {
                try {

                    Log.d("URL_send", "Successfully sent\n Response body: ")
                } catch (e: Exception) {
                    Log.e("URL_send ", "threw $e")
                }
            }
        }


//        btnSend.setOnClickListener {
//            thread {
//                try {
//                    val response = url.openStream()
//                    val scanner = Scanner(response)
//                    val responseBody = scanner.useDelimiter("\\A").next()
//                    Log.d("URL_send", "Successfully sent\n Response body: $responseBody")
//                } catch (e: Exception) {
//                    Log.e("URL_send ", "threw $e")
//                }
//            }
//        }
    }

    companion object {
        val url = URL("https://www.google.com")
        var connection: HttpsURLConnection? = null
    }

}