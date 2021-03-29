package com.mobiquity.challenge.ui.help

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.mobiquity.challenge.R


class HelpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_help, container, false)
        val webview: WebView = root.findViewById(R.id.view)

        webview.webViewClient = WebViewClient()
        webview.settings.javaScriptEnabled = true
        webview.settings.javaScriptCanOpenWindowsAutomatically = true
        webview.settings.pluginState = WebSettings.PluginState.ON
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webview.settings.mediaPlaybackRequiresUserGesture = false
        }
        webview.webChromeClient = WebChromeClient()
        webview.loadUrl("https://youtu.be/6bHlIdnl2GQ")

        return root
    }
}