package shiddush.view.com.mmvsd.model.timer

import com.google.gson.annotations.SerializedName

data class CountDownTimerResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null
)