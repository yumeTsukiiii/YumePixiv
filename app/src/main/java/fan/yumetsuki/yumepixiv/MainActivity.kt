package fan.yumetsuki.yumepixiv

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import fan.yumetsuki.yumepixiv.ui.YumePixivApp
import fan.yumetsuki.yumepixiv.ui.screen.LoginScreen
import fan.yumetsuki.yumepixiv.ui.theme.YumePixivTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println("OpenUrl: MainActivity onCreate")
        Toast.makeText(this, "OpenUrl: MainActivity onCreate", Toast.LENGTH_SHORT).show()

        setContent {
            YumePixivTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    YumePixivApp()
                    LoginScreen()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    YumePixivTheme {
        Greeting("Android")
    }
}