package shiddush.view.com.mmvsd.model.chat

import com.google.gson.annotations.SerializedName

data class FileUploadResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Data(

	@field:SerializedName("filename")
	val filename: String? = null
)
