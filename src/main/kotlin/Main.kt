import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ui.widget.FileSelector

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        MainContentScreen()
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainContentScreen() {
    val scope = rememberCoroutineScope()
    val stroke = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
    val snackbarHostState = remember { SnackbarHostState() }
    val windowInsert = remember { WindowInsets(16.dp, 16.dp, 16.dp, 16.dp) }
    Scaffold(windowInsert, snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding)) {
            Column {
                Text("title")
            }
            FileSelector(snackbarHostState)
        }


    }
}