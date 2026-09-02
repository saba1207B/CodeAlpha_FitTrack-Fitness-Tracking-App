package com.example.fittrack.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrack.model.DayStat
import java.text.NumberFormat
import java.util.Locale

@Composable
fun WeeklyBarChart(
    dayStats: List<DayStat>,
    chartType: ChartType,
    modifier: Modifier = Modifier,
    testTag: String = "weekly_bar_chart"
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
    val maxValue = dayStats.maxOfOrNull { chartType.getValue(it) }?.coerceAtLeast(1) ?: 1

    var selectedDayIndex by remember { mutableStateOf<Int?>(null) }
    val selectedDay = selectedDayIndex?.let { dayStats.getOrNull(it) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header: Title and selected day inspector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = chartType.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Last 7 days activity",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (selectedDay != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(chartType.accentColor.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${selectedDay.formattedDate}: ${numberFormat.format(chartType.getValue(selectedDay))} ${chartType.unit}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = chartType.accentColor
                        )
                    }
                } else {
                    val total = dayStats.sumOf { chartType.getValue(it) }
                    Text(
                        text = "Total: ${numberFormat.format(total)} ${chartType.unit}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = chartType.accentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Chart Bars container
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                dayStats.forEachIndexed { index, day ->
                    val value = chartType.getValue(day)
                    val fraction = if (maxValue > 0) (value.toFloat() / maxValue.toFloat()).coerceIn(0.04f, 1f) else 0.04f
                    val isSelected = selectedDayIndex == index
                    val isToday = index == dayStats.lastIndex

                    val animatedHeightFraction by animateFloatAsState(
                        targetValue = fraction,
                        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
                        label = "bar_height_$index"
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable {
                                selectedDayIndex = if (selectedDayIndex == index) null else index
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        // Value tag on top of bar
                        if (value > 0) {
                            Text(
                                text = if (value >= 1000) "${value / 1000}k" else value.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected || isToday) chartType.accentColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                maxLines = 1
                            )
                        } else {
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Animated Bar
                        val barColor = when {
                            isSelected -> chartType.accentColor
                            isToday -> chartType.accentColor.copy(alpha = 0.9f)
                            value > 0 -> chartType.accentColor.copy(alpha = 0.5f)
                            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .fillMaxHeight(0.78f * animatedHeightFraction)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                                .background(barColor)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Day label (e.g. Mon)
                        Text(
                            text = day.dayLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            fontWeight = if (isToday) FontWeight.ExtraBold else if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isToday) chartType.accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

sealed class ChartType(
    val title: String,
    val unit: String,
    val accentColor: Color
) {
    abstract fun getValue(day: DayStat): Int

    data object Calories : ChartType(
        title = "Weekly Calories Burned",
        unit = "kcal",
        accentColor = Color(0xFFF97316)
    ) {
        override fun getValue(day: DayStat): Int = day.calories
    }

    data object Duration : ChartType(
        title = "Weekly Workout Duration",
        unit = "min",
        accentColor = Color(0xFF06B6D4)
    ) {
        override fun getValue(day: DayStat): Int = day.durationMinutes
    }

    data object Steps : ChartType(
        title = "Weekly Steps Progress",
        unit = "steps",
        accentColor = Color(0xFF10B981)
    ) {
        override fun getValue(day: DayStat): Int = day.steps
    }
}
