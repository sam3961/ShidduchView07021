package shiddush.view.com.mmvsd.model

import com.google.gson.annotations.SerializedName

data class SingleChatResponse(

	@field:SerializedName("msg")
	val msg: String? = null,

	@field:SerializedName("response")
	val response: Boolean? = null,

	@field:SerializedName("messages")
	val messages: MutableList<SingleChatItem>? = null
)

data class SingleChatItem(

	@field:SerializedName("touser")
	val touser: String? = null,

	@field:SerializedName("profile_photo")
	val profilePhoto: String? = null,

	@field:SerializedName("touser_type")
	val touserType: String? = null,

	@field:SerializedName("agent_profile_pic")
	val agentProfilePic: String? = null,

	@field:SerializedName("photo")
	val photo: String? = null,

	@field:SerializedName("message_id")
	val messageId: String? = null,

	@field:SerializedName("fromuser")
	val fromuser: String? = null,

	@field:SerializedName("ip_address")
	val ipAddress: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("file_data")
	val fileData: String? = null,

	@field:SerializedName("agent_full_name")
	val agentFullName: String? = null,

	@field:SerializedName("datetime")
	val datetime: String? = null,

	@field:SerializedName("user_type")
	val userType: String? = null,

	@field:SerializedName("full_name")
	val fullName: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("isread")
	val isread: String? = null,

	@field:SerializedName("admin_id")
	val adminId: String? = null,

	@field:SerializedName("drive_data")
	val driveData: Any? = null,

	@field:SerializedName("drive")
	val drive: Any? = null,

	@field:SerializedName("fromuser_type")
	val fromuserType: String? = null
)
