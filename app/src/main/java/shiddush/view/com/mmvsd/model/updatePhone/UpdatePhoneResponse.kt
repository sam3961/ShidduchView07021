package shiddush.view.com.mmvsd.model.updatePhone

import com.google.gson.annotations.SerializedName

data class UpdatePhoneResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null
)