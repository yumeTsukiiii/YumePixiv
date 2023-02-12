package fan.yumetsuki.yumepixiv.ui.screen.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import fan.yumetsuki.yumepixiv.ui.components.YumePixivTip

@Composable
fun NoDataTip(
    isError: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isError) {
            YumePixivTip(text = "网络寄啦！！！下拉刷新重试下？！")
        } else {
            YumePixivTip(text = "没有数据欸？！！！！下拉刷新重试下？！")
        }
    }
}