package com.softsuave.languagetext

import android.content.Context
import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class TranslationService(
    private val context: Context
) {

    private val downloadConditions = DownloadConditions.Builder().requireWifi().build()

    suspend fun translateAndGenerateStringsFile(targetLanguage: String) {

        val defaultStrings = parseStringsXml(context.assets.open("${context.filesDir}/res/values/strings.xml",3))
        Log.d("TranslationService", "defaultStrings: $defaultStrings")
        val translatedStrings = defaultStrings.map { (key, value) ->
            key to translateText(value, targetLanguage)
        }.toMap()

        generateStringsXml(targetLanguage, translatedStrings)
    }

    private suspend fun translateText(text: String, targetLanguage: String): String {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(targetLanguage)
            .build()
        val translator = Translation.getClient(options)

        return suspendCancellableCoroutine { continuation ->
            translator.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener {
                    translator.translate(text)
                        .addOnSuccessListener { translatedText ->
                            if (continuation.isActive) {
                                continuation.resume(translatedText)
                            }
                        }
                        .addOnFailureListener { exception ->
                            if (continuation.isActive) {
                                continuation.resumeWithException(exception)
                            }
                        }
                }
                .addOnFailureListener { exception ->
                    if (continuation.isActive) {
                        continuation.resumeWithException(exception)
                    }
                }

            continuation.invokeOnCancellation {
                translator.close()
            }
        }
    }

    private fun parseStringsXml(inputStream: InputStream): Map<String, String> {
        val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val document: Document = documentBuilder.parse(inputStream)
        val stringsMap = mutableMapOf<String, String>()

        val nodeList = document.getElementsByTagName("string")
        for (i in 0 until nodeList.length) {
            val node = nodeList.item(i) as Element
            val name = node.getAttribute("name")
            val value = node.textContent
            stringsMap[name] = value
        }

        return stringsMap
    }

    private fun generateStringsXml(languageCode: String, stringsMap: Map<String, String>) {
        val folder = File(context.filesDir, "res/values-$languageCode")
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val file = File(folder, "strings.xml")
        val fos = FileOutputStream(file)

        fos.use {
            it.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n".toByteArray())
            stringsMap.forEach { (key, value) ->
                it.write("\t<string name=\"$key\">$value</string>\n".toByteArray())
            }
            it.write("</resources>".toByteArray())
        }
    }
}
