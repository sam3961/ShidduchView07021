package shiddush.view.com.mmvsd.model.chat

import com.google.gson.annotations.SerializedName

data class ReceivedMessageResponse(

	@field:SerializedName("friend")
	val friend: String? = null,

	@field:SerializedName("message_type")
	val messageType: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("userid")
	val userid: String? = null
)
