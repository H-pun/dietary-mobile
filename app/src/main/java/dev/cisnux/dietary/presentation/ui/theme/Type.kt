package dev.cisnux.dietary.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.cisnux.dietary.R

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = DietaryTypeScaleTokens.BodyLargeFont,
        fontWeight = DietaryTypeScaleTokens.BodyLargeWeight,
        fontSize = DietaryTypeScaleTokens.BodyLargeSize,
        lineHeight = DietaryTypeScaleTokens.BodyLargeLineHeight,
        letterSpacing = DietaryTypeScaleTokens.BodyLargeTracking,
    ),
    bodyMedium = TextStyle(
        fontFamily = DietaryTypeScaleTokens.BodyMediumFont,
        fontWeight = DietaryTypeScaleTokens.BodyMediumWeight,
        fontSize = DietaryTypeScaleTokens.BodyMediumSize,
        lineHeight = DietaryTypeScaleTokens.BodyMediumLineHeight,
        letterSpacing = DietaryTypeScaleTokens.BodyMediumTracking,
    ),
    bodySmall = TextStyle(
        fontFamily = DietaryTypeScaleTokens.BodySmallFont,
        fontWeight = DietaryTypeScaleTokens.BodySmallWeight,
        fontSize = DietaryTypeScaleTokens.BodySmallSize,
        lineHeight = DietaryTypeScaleTokens.BodySmallLineHeight,
        letterSpacing = DietaryTypeScaleTokens.BodySmallTracking,
    ),
    displayLarge = TextStyle(
        fontFamily = DietaryTypeScaleTokens.DisplayLargeFont,
        fontWeight = DietaryTypeScaleTokens.DisplayLargeWeight,
        fontSize = DietaryTypeScaleTokens.DisplayLargeSize,
        lineHeight = DietaryTypeScaleTokens.DisplayLargeLineHeight,
        letterSpacing = DietaryTypeScaleTokens.DisplayLargeTracking,
    ),
    displayMedium = TextStyle(
        fontFamily = DietaryTypeScaleTokens.DisplayMediumFont,
        fontWeight = DietaryTypeScaleTokens.DisplayMediumWeight,
        fontSize = DietaryTypeScaleTokens.DisplayMediumSize,
        lineHeight = DietaryTypeScaleTokens.DisplayMediumLineHeight,
        letterSpacing = DietaryTypeScaleTokens.DisplayMediumTracking,
    ),
    displaySmall = TextStyle(
        fontFamily = DietaryTypeScaleTokens.DisplaySmallFont,
        fontWeight = DietaryTypeScaleTokens.DisplaySmallWeight,
        fontSize = DietaryTypeScaleTokens.DisplaySmallSize,
        lineHeight = DietaryTypeScaleTokens.DisplaySmallLineHeight,
        letterSpacing = DietaryTypeScaleTokens.DisplaySmallTracking,
    ),
    headlineLarge = TextStyle(
        fontFamily = DietaryTypeScaleTokens.HeadlineLargeFont,
        fontWeight = DietaryTypeScaleTokens.HeadlineLargeWeight,
        fontSize = DietaryTypeScaleTokens.HeadlineLargeSize,
        lineHeight = DietaryTypeScaleTokens.HeadlineLargeLineHeight,
        letterSpacing = DietaryTypeScaleTokens.HeadlineLargeTracking,
    ),
    headlineMedium = TextStyle(
        fontFamily = DietaryTypeScaleTokens.HeadlineMediumFont,
        fontWeight = DietaryTypeScaleTokens.HeadlineMediumWeight,
        fontSize = DietaryTypeScaleTokens.HeadlineMediumSize,
        lineHeight = DietaryTypeScaleTokens.HeadlineMediumLineHeight,
        letterSpacing = DietaryTypeScaleTokens.HeadlineMediumTracking,
    ),
    headlineSmall = TextStyle(
        fontFamily = DietaryTypeScaleTokens.HeadlineSmallFont,
        fontWeight = DietaryTypeScaleTokens.HeadlineSmallWeight,
        fontSize = DietaryTypeScaleTokens.HeadlineSmallSize,
        lineHeight = DietaryTypeScaleTokens.HeadlineSmallLineHeight,
        letterSpacing = DietaryTypeScaleTokens.HeadlineSmallTracking,
    ),
    labelLarge = TextStyle(
        fontFamily = DietaryTypeScaleTokens.LabelLargeFont,
        fontWeight = DietaryTypeScaleTokens.LabelLargeWeight,
        fontSize = DietaryTypeScaleTokens.LabelLargeSize,
        lineHeight = DietaryTypeScaleTokens.LabelLargeLineHeight,
        letterSpacing = DietaryTypeScaleTokens.LabelLargeTracking,
    ),
    labelMedium = TextStyle(
        fontFamily = DietaryTypeScaleTokens.LabelMediumFont,
        fontWeight = DietaryTypeScaleTokens.LabelMediumWeight,
        fontSize = DietaryTypeScaleTokens.LabelMediumSize,
        lineHeight = DietaryTypeScaleTokens.LabelMediumLineHeight,
        letterSpacing = DietaryTypeScaleTokens.LabelMediumTracking,
    ),
    labelSmall = TextStyle(
        fontFamily = DietaryTypeScaleTokens.LabelSmallFont,
        fontWeight = DietaryTypeScaleTokens.LabelSmallWeight,
        fontSize = DietaryTypeScaleTokens.LabelSmallSize,
        lineHeight = DietaryTypeScaleTokens.LabelSmallLineHeight,
        letterSpacing = DietaryTypeScaleTokens.LabelSmallTracking,
    ),
    titleLarge = TextStyle(
        fontFamily = DietaryTypeScaleTokens.TitleLargeFont,
        fontWeight = DietaryTypeScaleTokens.TitleLargeWeight,
        fontSize = DietaryTypeScaleTokens.TitleLargeSize,
        lineHeight = DietaryTypeScaleTokens.TitleLargeLineHeight,
        letterSpacing = DietaryTypeScaleTokens.TitleLargeTracking,
    ),
    titleMedium = TextStyle(
        fontFamily = DietaryTypeScaleTokens.TitleMediumFont,
        fontWeight = DietaryTypeScaleTokens.TitleMediumWeight,
        fontSize = DietaryTypeScaleTokens.TitleMediumSize,
        lineHeight = DietaryTypeScaleTokens.TitleMediumLineHeight,
        letterSpacing = DietaryTypeScaleTokens.TitleMediumTracking,
    ),
    titleSmall = TextStyle(
        fontFamily = DietaryTypeScaleTokens.TitleSmallFont,
        fontWeight = DietaryTypeScaleTokens.TitleSmallWeight,
        fontSize = DietaryTypeScaleTokens.TitleSmallSize,
        lineHeight = DietaryTypeScaleTokens.TitleSmallLineHeight,
        letterSpacing = DietaryTypeScaleTokens.TitleSmallTracking,
    ),
)

@Immutable
private object DietaryTypeScaleTokens {
    val BodyLargeFont = DietaryTypefaceTokens.Plain
    val BodyLargeLineHeight = 24.0.sp
    val BodyLargeSize = 16.sp
    val BodyLargeTracking = 0.5.sp
    val BodyLargeWeight = DietaryTypefaceTokens.WeightRegular
    val BodyMediumFont = DietaryTypefaceTokens.Plain
    val BodyMediumLineHeight = 20.0.sp
    val BodyMediumSize = 14.sp
    val BodyMediumTracking = 0.2.sp
    val BodyMediumWeight = DietaryTypefaceTokens.WeightRegular
    val BodySmallFont = DietaryTypefaceTokens.Plain
    val BodySmallLineHeight = 16.0.sp
    val BodySmallSize = 12.sp
    val BodySmallTracking = 0.4.sp
    val BodySmallWeight = DietaryTypefaceTokens.WeightRegular
    val DisplayLargeFont = DietaryTypefaceTokens.Brand
    val DisplayLargeLineHeight = 64.0.sp
    val DisplayLargeSize = 57.sp
    val DisplayLargeTracking = (-0.2).sp
    val DisplayLargeWeight = DietaryTypefaceTokens.WeightRegular
    val DisplayMediumFont = DietaryTypefaceTokens.Brand
    val DisplayMediumLineHeight = 52.0.sp
    val DisplayMediumSize = 45.sp
    val DisplayMediumTracking = 0.0.sp
    val DisplayMediumWeight = DietaryTypefaceTokens.WeightRegular
    val DisplaySmallFont = DietaryTypefaceTokens.Brand
    val DisplaySmallLineHeight = 44.0.sp
    val DisplaySmallSize = 36.sp
    val DisplaySmallTracking = 0.0.sp
    val DisplaySmallWeight = DietaryTypefaceTokens.WeightRegular
    val HeadlineLargeFont = DietaryTypefaceTokens.Brand
    val HeadlineLargeLineHeight = 40.0.sp
    val HeadlineLargeSize = 32.sp
    val HeadlineLargeTracking = 0.0.sp
    val HeadlineLargeWeight = DietaryTypefaceTokens.WeightRegular
    val HeadlineMediumFont = DietaryTypefaceTokens.Brand
    val HeadlineMediumLineHeight = 36.0.sp
    val HeadlineMediumSize = 28.sp
    val HeadlineMediumTracking = 0.0.sp
    val HeadlineMediumWeight = DietaryTypefaceTokens.WeightRegular
    val HeadlineSmallFont = DietaryTypefaceTokens.Brand
    val HeadlineSmallLineHeight = 32.0.sp
    val HeadlineSmallSize = 24.sp
    val HeadlineSmallTracking = 0.0.sp
    val HeadlineSmallWeight = DietaryTypefaceTokens.WeightRegular
    val LabelLargeFont = DietaryTypefaceTokens.Plain
    val LabelLargeLineHeight = 20.0.sp
    val LabelLargeSize = 14.sp
    val LabelLargeTracking = 0.1.sp
    val LabelLargeWeight = DietaryTypefaceTokens.WeightMedium
    val LabelMediumFont = DietaryTypefaceTokens.Plain
    val LabelMediumLineHeight = 16.0.sp
    val LabelMediumSize = 12.sp
    val LabelMediumTracking = 0.5.sp
    val LabelMediumWeight = DietaryTypefaceTokens.WeightMedium
    val LabelSmallFont = DietaryTypefaceTokens.Plain
    val LabelSmallLineHeight = 16.0.sp
    val LabelSmallSize = 11.sp
    val LabelSmallTracking = 0.5.sp
    val LabelSmallWeight = DietaryTypefaceTokens.WeightMedium
    val TitleLargeFont = DietaryTypefaceTokens.Brand
    val TitleLargeLineHeight = 28.0.sp
    val TitleLargeSize = 22.sp
    val TitleLargeTracking = 0.0.sp
    val TitleLargeWeight = DietaryTypefaceTokens.WeightRegular
    val TitleMediumFont = DietaryTypefaceTokens.Plain
    val TitleMediumLineHeight = 24.0.sp
    val TitleMediumSize = 16.sp
    val TitleMediumTracking = 0.2.sp
    val TitleMediumWeight = DietaryTypefaceTokens.WeightMedium
    val TitleSmallFont = DietaryTypefaceTokens.Plain
    val TitleSmallLineHeight = 20.0.sp
    val TitleSmallSize = 14.sp
    val TitleSmallTracking = 0.1.sp
    val TitleSmallWeight = DietaryTypefaceTokens.WeightMedium
}

@Immutable
private object DietaryTypefaceTokens {
    val Brand = FontFamily(Font(R.font.noto_sans))
    val Plain = FontFamily(Font(R.font.noto_sans))
    val WeightMedium = FontWeight.Medium
    val WeightRegular = FontWeight.Normal
}