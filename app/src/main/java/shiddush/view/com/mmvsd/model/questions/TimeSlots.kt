package shiddush.view.com.mmvsd.model.questions

import com.google.gson.annotations.SerializedName

data class TimeSlots(
	@field:SerializedName("timeForCountDown")
	val timeForCountDown: Int? = null,

	@field:SerializedName("dayOfWeek")
	val dayOfWeek: String? = null,

	@field:SerializedName("timeToText")
	val timeToText: String? = null
)
