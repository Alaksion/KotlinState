package io.github.alaksion.sample

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.alaksion.UiState
import io.github.alaksion.UiStateType
import io.github.alaksion.sample.xml.XmlActivity

class ComposeActivity : ComponentActivity() {

    private val viewModel by viewModels<StateViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by viewModel.state.collectAsState()
            Content(
                state = state,
                updateText = viewModel::updateText,
                submitName = viewModel::submitName
            )
        }
    }

}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
private fun Content(
    state: UiState<SampleState>,
    updateText: (String) -> Unit,
    submitName: () -> Unit
) {

    MaterialTheme {
        when (state.stateType) {
            UiStateType.Content -> {
                ContentView(
                    state = state.stateData,
                    updateText = updateText,
                    submitName = submitName
                )
            }

            UiStateType.Loading -> LoadingView()
            is UiStateType.Error -> Unit
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
private fun ContentView(
    state: SampleState,
    updateText: (String) -> Unit,
    submitName: () -> Unit
) {
    val context = LocalContext.current

    Scaffold() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = state.name,
                    onValueChange = updateText,
                    label = { Text("Sample Text Field") },
                    maxLines = 1,
                    shape = RoundedCornerShape(64.dp)
                )
                IconButton(onClick = submitName) {
                    Icon(imageVector = Icons.Default.Add, null)
                }
            }
            Spacer(Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.names) {
                    Card(
                        Modifier.fillMaxWidth()
                    ) {
                        Text(it, Modifier.padding(16.dp))
                    }
                }
            }
            Button(
                onClick = {
                    context.startActivity(Intent(context, XmlActivity::class.java))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Go To Xml Activity")
            }
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
private fun LoadingView() {
    Scaffold() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }
}