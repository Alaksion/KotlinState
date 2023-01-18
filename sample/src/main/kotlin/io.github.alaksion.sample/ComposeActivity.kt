package io.github.alaksion.sample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class ComposeActivity : ComponentActivity() {

    init {
        Log.d("ActivityDebug", "initialized")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Content()
        }
    }

}

@Composable
private fun Content() {
    MaterialTheme() {
        Surface(Modifier.fillMaxSize()) {
            Text("Hello World")
        }
    }
}