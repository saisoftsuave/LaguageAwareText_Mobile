package com.softsuave.languagetext

import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

object TranslatorManager {
    private var translator: Translator? = null

    fun initialize(targetLanguage: String) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(targetLanguage)
            .build()
        translator = Translation.getClient(options)

        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        translator?.downloadModelIfNeeded(conditions)
            ?.addOnSuccessListener {
                Log.d("TranslatorManager", "Model downloaded successfully.")
            }
            ?.addOnFailureListener { exception ->
                Log.e("TranslatorManager", "Error downloading model: ${exception.message}")
            }
    }

    fun getTranslator(): Translator? {
        return translator
    }
}
