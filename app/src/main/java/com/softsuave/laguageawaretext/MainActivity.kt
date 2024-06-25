package com.softsuave.laguageawaretext

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softsuave.laguageawaretext.ui.theme.LaguageAwareTextTheme
import com.softsuave.languagetext.LanguageAwareText
import com.softsuave.languagetext.TranslatorManager
import kotlinx.coroutines.DelicateCoroutinesApi

class MainActivity : ComponentActivity() {
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        TranslatorManager.initialize("hi")
        setContent {
            LaguageAwareTextTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        Greeting()
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LaguageAwareTextTheme {
        Greeting()
    }
}

@Composable
fun Greeting() {
    val stringValue =
        "These steps will help you to integrate Hilt for DI in your Android project using Kotlin DSL and ensure your translation functionality is properly managed."
    val state = rememberLazyListState()
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(state = state) {
            items(key = {
                it
            }, count = 100) {
                LanguageAwareText(
                    originalText = stringValue,
                    targetLanguage = "te",
                    modifier = Modifier.padding(top = 100.dp)
                )
            }
        }
    }
}
