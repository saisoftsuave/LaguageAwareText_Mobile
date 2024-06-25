package com.softsuave.languagetext

import android.util.Log
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.mlkit.nl.translate.Translator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Composable
fun LanguageAwareText(
    originalText : String,
    targetLanguage: String,
    modifier: Modifier = Modifier
) {
    var translatedText by remember { mutableStateOf( originalText) }

    LaunchedEffect(targetLanguage) {
        withContext(Dispatchers.Default) {
            val result = translateText(originalText)
            if(result?.isEmpty() != true){
                translatedText = result.toString()
            }
        }
    }
        BasicText(text = translatedText,modifier)
}

suspend fun translateText(text: String): String? {
    val translator = TranslatorManager.getTranslator()

    if (translator == null) {
        Log.e("translateText", "Translator is not initialized")
        return text
    }

    return try {
        withContext(Dispatchers.IO) {
            translator.translateSuspend(text)
        }
    } catch (exception: Exception) {
        Log.e("translateText", "Error translating text: ${exception.message}")
        text
    }
}

private suspend fun Translator.translateSuspend(text: String): String =
    suspendCancellableCoroutine { continuation ->
        translate(text)
            .addOnSuccessListener { result ->
                continuation.resume(result)
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }
