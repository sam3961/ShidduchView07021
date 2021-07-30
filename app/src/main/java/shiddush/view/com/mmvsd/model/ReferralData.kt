package shiddush.view.com.mmvsd.model

import com.google.gson.annotations.SerializedName

data class ReferralData(

	@field:SerializedName("~creation_source")
	val creationSource: Int? = null,

	@field:SerializedName("referringUserId")
	val referringUserId: String? = null,

	@field:SerializedName("+click_timestamp")
	val clickTimestamp: Int? = null,

	@field:SerializedName("$"+"identity_id")
	val identityId: Long? = null,

	@field:SerializedName("~stage")
	val stage: String? = null,

	@field:SerializedName("~feature")
	val feature: String? = null,

	@field:SerializedName("+match_guaranteed")
	val matchGuaranteed: Boolean? = null,

	@field:SerializedName("+clicked_branch_link")
	val clickedBranchLink: Boolean? = null,

	@field:SerializedName("~id")
	val id: Long? = null,

	@field:SerializedName("$"+"one_time_use")
	val oneTimeUse: Boolean? = null,

	@field:SerializedName("$"+"canonical_url")
	val canonicalUrl: String? = null,

	@field:SerializedName("+is_first_session")
	val isFirstSession: Boolean? = null,

	@field:SerializedName("~referring_link")
	val referringLink: String? = null,

	@field:SerializedName("~channel")
	val channel: String? = null,

	@field:SerializedName("referringUsername")
	val referringUsername: String? = null,

	@field:SerializedName("$"+"always_deeplink")
	val alwaysDeeplink: String? = null
)
