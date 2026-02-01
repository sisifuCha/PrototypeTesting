package com.example.prototypetesting.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class TimelineData(
    @SerializedName("totalDurationSeconds")
    val totalDurationSeconds: Int,
    @SerializedName("events")
    val events: List<TimelineEvent>
) {
    val formattedDuration: String
        get() {
            val minutes = totalDurationSeconds / 60
            val seconds = totalDurationSeconds % 60
            return String.format("%d:%02d", minutes, seconds)
        }
}

data class TimelineEvent(
    @SerializedName("timeSeconds")
    val timeSeconds: Int,
    @SerializedName("userAction")
    val userAction: UserAction?,
    @SerializedName("userState")
    val userState: UserState?
) {
    val formattedTime: String
        get() {
            val minutes = timeSeconds / 60
            val seconds = timeSeconds % 60
            return String.format("%d:%02d", minutes, seconds)
        }
}

data class UserAction(
    @SerializedName("type")
    val type: String,
    @SerializedName("target")
    val target: String,
    @SerializedName("description")
    val description: String
)

data class UserState(
    @SerializedName("emotion")
    val emotion: String,
    @SerializedName("description")
    val description: String
)

object TimelineLoader {
    fun loadFromAssets(context: Context, fileName: String = "timeline_data.json"): TimelineData? {
        return try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            Gson().fromJson(jsonString, TimelineData::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
