package dev.lackluster.hyperx.compose.base

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.utils.G2RoundedCornerShape

data class ImageIcon(
    val iconVector: ImageVector? = null,
    val iconRes: Int? = null,
    val iconBitmap: ImageBitmap? = null,
    val iconSize: IconSize = IconSize.Small,
    val iconSizeDp: Dp = Dp.Unspecified,
    val cornerRadius: Dp = Dp.Unspecified,
) {
    constructor(
        iconVector: ImageVector? = null,
        iconRes: Int? = null,
        iconBitmap: ImageBitmap? = null,
        iconSize: IconSize,
        cornerRadius: Dp = Dp.Unspecified,
    ) : this(iconVector, iconRes, iconBitmap, iconSize, Dp.Unspecified, cornerRadius)

    constructor(
        iconVector: ImageVector? = null,
        iconRes: Int? = null,
        iconBitmap: ImageBitmap? = null,
        iconSize: Dp,
        cornerRadius: Dp = Dp.Unspecified,
    ) : this(iconVector, iconRes, iconBitmap, IconSize.Unspecified, iconSize, cornerRadius)

    fun getSize(): Dp {
        return when (iconSize) {
            IconSize.Small -> 28.dp
            IconSize.Medium -> 38.dp
            IconSize.Large -> 44.dp
            IconSize.App -> 40.dp
            IconSize.SeekBar -> 26.dp
            IconSize.Unspecified -> iconSizeDp
        }
    }

    fun getHorizontalPadding(): Dp {
        return when (iconSize) {
            IconSize.Small -> 16.dp
            IconSize.Medium -> 11.dp
            IconSize.Large -> 8.dp
            IconSize.App -> 16.dp
            IconSize.SeekBar -> 12.dp
            IconSize.Unspecified -> 0.dp
        }
    }
}

enum class IconSize {
    Small,
    Medium,
    Large,
    App,
    SeekBar,
    Unspecified
}

@Composable
fun DrawableResIcon(
    imageIcon: ImageIcon
) {
    val iconSizeDp = imageIcon.getSize()
    val iconCornerRadius = imageIcon.cornerRadius
    val iconPaddingDp = imageIcon.getHorizontalPadding()
    val modifier = Modifier
        .padding(end = iconPaddingDp)
        .size(iconSizeDp)
        .then(
            if (iconCornerRadius != Dp.Unspecified)
                Modifier.clip(
                    if (iconCornerRadius >= iconSizeDp / 2) CircleShape
                    else G2RoundedCornerShape(iconCornerRadius)
                )
            else
                Modifier
        )
    imageIcon.iconRes?.let {
        Image(
            modifier = modifier,
            painter = painterResource(it),
            contentDescription = null
        )
    } ?: imageIcon.iconBitmap?.let {
        Image(
            modifier = modifier,
            bitmap = it,
            contentDescription = null
        )
    } ?: imageIcon.iconVector?.let {
        Image(
            modifier = modifier,
            imageVector = it,
            contentDescription = null
        )
    }
}