package fan.yumetsuki.yumepixiv.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
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
fun BottomSheetDragHandle(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    shape: Shape = MaterialTheme.shapes.small
) {
    Box(
        modifier = modifier.width(32.dp)
            .height(4.dp)
            .clip(shape)
            .alpha(0.4f)
            .background(color = color)
    )
}

@Composable
fun BottomSheet(
    modifier: Modifier = Modifier,
    showDragHandle: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
    ) {
        if (showDragHandle) {
            BottomSheetDragHandle(
                modifier = Modifier.align(Alignment.CenterHorizontally)
                    .padding(vertical = 22.dp)
            )
        }
        content()
    }
}

@Composable
fun BottomSheetScaffold(
    // TODO foldHeight 默认值等抽象 Defaults
    foldHeight: Dp,
    modifier: Modifier = Modifier,
    expandHeight: Dp = Dp.Infinity,
    bottomSheetState: BottomSheetState = rememberBottomSheetState(),
    density: Density = LocalDensity.current,
    bottomSheet: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit
) {

    var bottomSheetHeight by remember(expandHeight) {
        mutableStateOf(
            if (expandHeight == Dp.Infinity) {
                0
            } else {
                with(density) {
                    expandHeight.roundToPx()
                }
            }
        )
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
        Animatable(
            max(
                0f,
                bottomSheetHeight - with(density) {
                    foldHeight.toPx()
                }
            )
        )
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

    BoxWithConstraints(
        modifier = modifier
    ) {
        content()
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset {
                    IntOffset(
                        x = 0,
                        y = bottomSheetOffset.value.roundToInt()
                    )
                }.draggable(
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
                            bottomSheetState.isExpand =
                                bottomSheetOffset.value < bottomSheetHeight / 2
                        }
                        isDragging = false
                    },
                    orientation = Orientation.Vertical
                )
                .run {
                    if (expandHeight != Dp.Infinity) {
                        height(expandHeight)
                    } else {
                        onSizeChanged {
                            bottomSheetHeight = it.height
                        }
                    }
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