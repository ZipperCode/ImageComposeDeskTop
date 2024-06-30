package ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ext.FileSelectorType
import ext.isImage
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PlatformDirectory
import kotlinx.coroutines.launch
import platform.LocalPlatform
import java.awt.FileDialog
import java.io.File
import java.util.UUID


@Composable
fun FileSelector(snackbarHostState: SnackbarHostState) {
    val scope = rememberCoroutineScope()
    var selectFilePathList by remember { mutableStateOf(listOf<String>()) }
    var selectPath by remember { mutableStateOf("") }
    Column {
        Box {
            FileDrag(Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clickable {
                    scope.launch {
                        showFileSelector(FileSelectorType.Image) {
                            if (it.isEmpty()) {
                                return@showFileSelector
                            }
                            scope.launch {
                                if (it.isImage) {
                                    snackbarHostState.showSnackbar("Image: ${it.first()}")
                                } else {
                                    snackbarHostState.showSnackbar("Not Image: ${it.first()}")
                                }
                            }
                        }
                    }
                }) {
                selectFilePathList = it
            }
            FilePicker2(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomEnd)
            ) {
            }
        }
        FilePathSelectorInput(selectPath, "路径") {
            selectPath = it
            selectFilePathList = listOf(it)
        }
        Column {
            for (path in selectFilePathList) {
                Text(path)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun FileDrag(
    modifier: Modifier = Modifier,
    onDrawFiles: (List<String>) -> Unit
) {
    var isDragging by remember { mutableStateOf(false) }
    Row(
        modifier
            .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
            .background(
                if (isDragging) {
                    androidx.compose.material3.MaterialTheme.colorScheme.inverseOnSurface
                } else {
                    MaterialTheme.colors.background
                }, shape = RoundedCornerShape(10.dp)
            )
            .padding(16.dp)
            .onExternalDrag(
                onDragStart = {
                    println("onStart")
                    isDragging = true
                },
                onDragExit = {
                    isDragging = false
                },
                onDrop = {
                    println("onDrop")
                    if (it.dragData is DragData.FilesList) {
                        val fileList = it.dragData as DragData.FilesList
                        onDrawFiles.invoke(fileList.readFiles())
                    }
                    isDragging = false
                }
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource("drawable/file_uploader.svg"),
            contentDescription = "", Modifier.size(20.dp, 20.dp)
        )
        Spacer(Modifier.width(5.dp))
        Text("拖拽文件")
    }
}


@Composable
internal fun FilePicker(modifier: Modifier = Modifier, onPickFile: (String) -> Unit) {
    val directory: PlatformDirectory? by remember { mutableStateOf(null) }
    val filePicker = rememberFilePickerLauncher(
        title = "仅选择文件",
        initialDirectory = directory?.path,
        onResult = {
            println("选择文件完成 result = ${it?.file}")
            it.takeIf { it != null && it.file.exists() }?.apply {
                onPickFile.invoke(file.absolutePath)
            }
        }
    )
    FloatingActionButton(onClick = {
        filePicker.launch()
    }, modifier = modifier.size(40.dp, 40.dp)) {
        Icon(
            painter = painterResource("drawable/file_uploader.svg"),
            contentDescription = "", Modifier.size(10.dp, 10.dp)
        )
    }
}

@Composable
internal fun FilePicker2(modifier: Modifier = Modifier, onPickFile: (String) -> Unit) {
    FloatingActionButton(onClick = {
        showFileSelector(FileSelectorType.Image) {
            onPickFile.invoke(it)
        }
    }, modifier = modifier.size(40.dp, 40.dp)) {
        Icon(
            painter = painterResource("drawable/file_uploader.svg"),
            contentDescription = "", Modifier.size(10.dp, 10.dp)
        )
    }
}

/**
 * 文件夹输入框
 * @param value 输入框的值
 * @param label 输入框的标签
 * @param isError 是否错误
 * @param onValueChange 输入值改变回调
 */
@Composable
fun FolderInput(value: String, label: String, isError: Boolean, onValueChange: (String) -> Unit) {
    var showDirPicker by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        CurrentTextField(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 3.dp).weight(1f),
            value = value,
            label = label,
            isError = isError,
            onValueChange = onValueChange
        )
        SmallFloatingActionButton(onClick = {
            if (LocalPlatform.isMac) {
                showFolderSelector { path ->
                    onValueChange(path)
                }
            } else {
                showDirPicker = true
            }
        }) {
//            Icon(Icons.Rounded.FolderOpen, "选择文件夹")
        }
    }
    if (!LocalPlatform.isMac) {
        LaunchedEffect(showDirPicker) {
            showFolderSelector { path ->
                if (path.isNotEmpty()) {
                    onValueChange(path)
                }
                showDirPicker = false
            }
        }
    }
}

@Composable
fun FilePathSelectorInput(
    value: String,
    label: String,
    onValueChange: (String) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.OutlinedTextField(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 3.dp).weight(1f),
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(label, style = androidx.compose.material3.MaterialTheme.typography.labelLarge)
            },
            singleLine = true,
            isError = false,
        )

        SmallFloatingActionButton(onClick = {
            showPicker = true
        }) {
//            Icon(Icons.Rounded.FolderOpen, "选择文件夹")
        }
    }

    FilePicker(showPicker) { newPath ->
        onValueChange(newPath)
        showPicker = false
    }
}

@Composable
internal fun FilePicker(
    showSelector: Boolean,
    type: FileSelectorType = FileSelectorType.Image,
    onSelected: (String) -> Unit
) {
    if (showSelector) {
        LaunchedEffect(UUID.randomUUID().toString()) {
            when (type) {
                FileSelectorType.Dir -> showFolderSelector { path ->
                    onSelected(path)
                }

                FileSelectorType.Image,
                FileSelectorType.File -> showFileSelector { path ->
                    onSelected(path)
                }
            }
        }
    }
}

/**
 * 显示文件选择器
 * @param fileSelectorType 文件选择类型
 * @param onFileSelected 选择回调
 */
private fun showFileSelector(
    vararg fileSelectorType: FileSelectorType, onFileSelected: (String) -> Unit
) {
    val fileDialog = FileDialog(ComposeWindow())
    fileDialog.isMultipleMode = true
    fileDialog.setFilenameFilter { file, name ->
        val sourceFile = File(file, name)
        if (!sourceFile.isFile) {
            return@setFilenameFilter false
        }
        for (type in fileSelectorType) {
            val isConform = when (type) {
                FileSelectorType.Image -> name.isImage
                FileSelectorType.Dir -> sourceFile.isDirectory
                else -> true
            }
            if (isConform) return@setFilenameFilter true
        }
        return@setFilenameFilter false
    }
    fileDialog.isVisible = true
    val directory = fileDialog.directory
    val file = fileDialog.file
    var path = ""
    if (directory != null && file != null) {
        path = "$directory$file"
    }
    onFileSelected(path)
}

fun showFolderSelector(
    onFolderSelected: (String) -> Unit
) {
    System.setProperty("apple.awt.fileDialogForDirectories", "true")
    val fileDialog = FileDialog(ComposeWindow())
    fileDialog.isMultipleMode = false
    fileDialog.isVisible = true
    val directory = fileDialog.directory
    val file = fileDialog.file
    var path = ""
    if (directory != null && file != null) {
        path = "$directory$file"
    }
    onFolderSelected(path)
    System.setProperty("apple.awt.fileDialogForDirectories", "false")
}

