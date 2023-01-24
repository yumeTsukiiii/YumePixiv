package fan.yumetsuki.yumepixiv.ui.screen.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun IllustDetail() {

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {

        items(arrayOf(Color.Red, Color.Green, Color.Gray, Color.Yellow)) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(
                it
            ))
        }

    }

}

@Preview
@Composable
fun IllustDetailPreview() {

    IllustDetail()

}