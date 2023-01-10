package fan.yumetsuki.yumepixiv.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import fan.yumetsuki.yumepixiv.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {

        TextButton(onClick = {
            viewModel.jumpToLogin(context)
        }) {
            Text(text = "登录")
        }

    }

}