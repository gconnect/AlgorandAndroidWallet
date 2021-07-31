package com.africinnovate.algorandandroidkotlin.utils

import android.app.Activity
import android.app.Application
import android.content.ClipData
import android.content.Context
import android.os.Build
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.net.ConnectivityManager
import android.widget.EditText
import androidx.core.content.ContextCompat


object Viewutil {

    fun copy(text: EditText, context: Context) {
        val startSelection: Int = text.selectionStart
        val endSelection: Int = text.selectionEnd
        if (text.text != null && endSelection > startSelection) {
            val selectedText: String =
                text.text.toString().substring(startSelection, endSelection)
            val sdk = Build.VERSION.SDK_INT
            if (sdk < Build.VERSION_CODES.HONEYCOMB) {
                val clipboard =
                    context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
                clipboard!!.text = selectedText
            } else {
                val clipboard =
                    context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
                val clip = ClipData.newPlainText("WordKeeper", selectedText)
                clipboard!!.setPrimaryClip(clip)
            }
        }
    }

    fun paste(text: EditText, context: Context) {
        val sdk = Build.VERSION.SDK_INT
        if (sdk < Build.VERSION_CODES.HONEYCOMB) {
            val clipboard =
                context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
            if (clipboard!!.text != null) {
                text.text.insert(text.selectionStart, clipboard.text)
            }
        } else {
            val clipboard =
                context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val item = clipboard!!.primaryClip!!.getItemAt(0)
            if (item.text != null) {
                text.text.insert(text.selectionStart, item.text)
            }
        }
    }

     fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

}