package fan.yumetsuki.yumepixiv.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun rememberBottomSheetState(): BottomSheetState {
    val isExpandState = remember {
        mutableStateOf(false)
    }
    return remember {
        DefaultBottomSheetState(isExpandState)
    }
}

@Composable
fun BottomSheetScaffold(
    // TODO foldHeight 默认值等抽象 Defaults
    foldHeight: Dp,
    expandHeight: Dp = Dp.Infinity,
    bottomSheetState: BottomSheetState = rememberBottomSheetState(),
    density: Density = LocalDensity.current,
    bottomSheet: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit
) {

    var bottomSheetHeight by remember {
        mutableStateOf(0)
    }

    val hidedHeight by remember(bottomSheetHeight, foldHeight) {
        derivedStateOf {
            max(
                0f,
                bottomSheetHeight - with(density) {
                    foldHeight.toPx()
                }
            )
        }
    }

    val bottomSheetOffset = remember {
        Animatable(0f)
    }

    var isDragging by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(hidedHeight) {
        bottomSheetOffset.snapTo(hidedHeight)
    }

    LaunchedEffect(isDragging, bottomSheetState.isExpand, hidedHeight) {
        if (bottomSheetState.isExpand) {
            bottomSheetOffset.animateTo(0f)
        } else {
            bottomSheetOffset.animateTo(hidedHeight)
        }
    }

    Box {
        content()
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .draggable(
                    state = rememberDraggableState { delta ->
                        coroutineScope.launch {
                            bottomSheetOffset.snapTo(
                                (bottomSheetOffset.value + delta).coerceIn(
                                    0f, hidedHeight
                                )
                            )
                        }
                    },
                    onDragStarted = {
                        isDragging = true
                    },
                    onDragStopped = { velocity ->
                        if (velocity > 300f) {
                            bottomSheetState.isExpand = false
                        } else if (velocity < -300f) {
                            bottomSheetState.isExpand = true
                        } else {
                            bottomSheetState.isExpand = bottomSheetOffset.value < bottomSheetHeight / 2
                        }
                        isDragging = false
                    },
                    orientation = Orientation.Vertical
                )
                .offset {
                    IntOffset(x = 0, y = bottomSheetOffset.value.roundToInt())
                }
                .run {
                    if (expandHeight != Dp.Infinity) {
                        height(expandHeight)
                    } else {
                        this
                    }
                }
                .onSizeChanged {
                    bottomSheetHeight = it.height
                }
        ) {
            bottomSheet()
        }
    }

}

interface BottomSheetState {

    var isExpand: Boolean

}

class DefaultBottomSheetState(
    isExpandState: MutableState<Boolean>
) : BottomSheetState {

    override var isExpand by isExpandState

}

@Preview
@Composable
fun BottomSheetPreview() {

    BottomSheetScaffold(foldHeight = 96.dp, bottomSheet = {
        Column(modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.Red)) {
            Box(modifier = Modifier
                .height(96.dp)
                .background(Color.Green))
        }
    }) {

        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow))

    }

}