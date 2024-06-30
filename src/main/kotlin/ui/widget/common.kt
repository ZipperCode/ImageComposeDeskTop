package ui.widget

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 通用输入框
 */
@Composable
fun CurrentTextField(
    modifier: Modifier,
    value: String,
    label: String,
    isError: Boolean,
    realOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.labelLarge) },
        singleLine = true,
        isError = isError,
        readOnly = realOnly,
        trailingIcon = trailingIcon
    )
}