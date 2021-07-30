package shiddush.view.com.mmvsd.model.subscriptionStatus

data class SubscriptionStatus(
	val code: Int? = null,
	val data: Data? = null,
	val message: String? = null
)

data class Data(
	val subscriptionStatus: Boolean? = null,
	val userid: String? = null
)

