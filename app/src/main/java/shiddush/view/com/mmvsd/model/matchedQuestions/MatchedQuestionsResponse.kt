package shiddush.view.com.mmvsd.model.matchedQuestions

import com.google.gson.annotations.SerializedName

data class MatchedQuestionsResponse(

	@field:SerializedName("questions")
	val questions: List<QuestionsItem>? = null,

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class QuestionsItem(

	@field:SerializedName("question")
	val question: String? = null,

	@field:SerializedName("answer")
	val answer: String? = null
)
