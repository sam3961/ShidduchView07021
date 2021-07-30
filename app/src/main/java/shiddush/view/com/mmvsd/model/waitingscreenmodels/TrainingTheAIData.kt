package shiddush.view.com.mmvsd.model.waitingscreenmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Sumit Kumar.
 */
class TrainingTheAIData {

    @SerializedName("modifiedDate")
    @Expose
    private var modifiedDate: String? = ""
    @SerializedName("createdDate")
    @Expose
    private var createdDate: String? = ""
    @SerializedName("_id")
    @Expose
    private var id: String? = null
    @SerializedName("questions")
    @Expose
    private var questions: String? = ""
    @SerializedName("option1")
    @Expose
    private var option1: String? = ""
    @SerializedName("option2")
    @Expose
    private var option2: String? = ""
    @SerializedName("qType")
    @Expose
    private var qType: Int? = 0
    @SerializedName("createdAt")
    @Expose
    private var createdAt: String? = ""
    @SerializedName("updatedAt")
    @Expose
    private var updatedAt: String? = ""
    @SerializedName("__v")
    @Expose
    private var v: Int? = 0

    fun getModifiedDate(): String? {
        return modifiedDate
    }

    fun setModifiedDate(modifiedDate: String) {
        this.modifiedDate = modifiedDate
    }

    fun getCreatedDate(): String? {
        return createdDate
    }

    fun setCreatedDate(createdDate: String) {
        this.createdDate = createdDate
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String) {
        this.id = id
    }

    fun getQuestions(): String? {
        return questions
    }

    fun setQuestions(questions: String) {
        this.questions = questions
    }

    fun getOption1(): String? {
        return option1
    }

    fun setOption1(option1: String) {
        this.option1 = option1
    }

    fun getOption2(): String? {
        return option2
    }

    fun setOption2(option2: String) {
        this.option2 = option2
    }

    fun getQType(): Int? {
        return qType
    }

    fun setQType(qType: Int?) {
        this.qType = qType
    }

    fun getCreatedAt(): String? {
        return createdAt
    }

    fun setCreatedAt(createdAt: String) {
        this.createdAt = createdAt
    }

    fun getUpdatedAt(): String? {
        return updatedAt
    }

    fun setUpdatedAt(updatedAt: String) {
        this.updatedAt = updatedAt
    }

    fun getV(): Int? {
        return v
    }

    fun setV(v: Int?) {
        this.v = v
    }


}
