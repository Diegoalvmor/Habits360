package com.example.habits360.utils


import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.example.habits360.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt


@SuppressLint("ViewConstructor")
class CustomMarkerViewSolo (context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {

    private val tvDate: TextView = findViewById(R.id.tvDate)
    private val tvValue: TextView = findViewById(R.id.tvValue)
    private val currentMonth = YearMonth.now()

    @SuppressLint("SetTextI18n")
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        e?.let {
            // Obtener fecha del mes actual
            val day = e.x.toInt().coerceIn(1, currentMonth.lengthOfMonth())
            val date = currentMonth.atDay(day)
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
            tvDate.text = date.format(formatter)

            // Redondear Y a decena más cercana
            val roundedY = (e.y / 10).roundToInt() * 10
            tvValue.text = "Puntos: $roundedY"
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2f), -height.toFloat())
    }
}
