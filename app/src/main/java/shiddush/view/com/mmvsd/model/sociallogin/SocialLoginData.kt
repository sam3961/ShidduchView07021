package shiddush.view.com.mmvsd.model.sociallogin

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Sumit Kumar.
 */
class SocialLoginData {

    @SerializedName("isBlocked")
    @Expose
    private var isBlocked: Boolean? = false
    @SerializedName("isNormalQuizComplete")
    @Expose
    private var isNormalQuizComplete: Boolean? = false
    @SerializedName("isBibleQuizComplete")
    @Expose
    private var isBibleQuizComplete: Boolean? = false
    @SerializedName("isActive")
    @Expose
    private var isActive: Boolean? = false
    @SerializedName("isSignUpPerformed")
    @Expose
    private var isSignUpPerformed: Boolean? = false
    @SerializedName("_id")
    @Expose
    private var id: String? = ""
    @SerializedName("firstName")
    @Expose
    private var firstName: String? = ""
    @SerializedName("lastName")
    @Expose
    private var lastName: String? = ""
    @SerializedName("email")
    @Expose
    private var email: String? = ""
    @SerializedName("gender")
    @Expose
    private var gender: Int? = 0
    @SerializedName("DOB")
    @Expose
    private var dOB: String? = ""
    @SerializedName("age")
    @Expose
    private var age: Int? = 0
    @SerializedName("token")
    @Expose
    private var token: String? = ""
    @SerializedName("googleSocialId")
    @Expose
    private var googleSocialId: String? = ""
    @SerializedName("facebookSocialId")
    @Expose
    private var facebookSocialId: String? = ""
    @SerializedName("isReviewDone")
    @Expose
    private var isReviewDone: Boolean? = false
    @SerializedName("review_for")
    @Expose
    private var reviewFor: String? = ""
    @SerializedName("isSubscriptionDone")
    @Expose
    private var isSubscriptionDone: Boolean? = false
    @SerializedName("isSubscriptionFree")
    @Expose
    private var isSubscriptionFree: Boolean? = false
    @SerializedName("phone_no")
    @Expose
    private var phone_no: String? = ""
    @SerializedName("subscriptionStatus")
    @Expose
    private var subscriptionStatus: Boolean? = false

    fun getIsBlocked(): Boolean? {
        return isBlocked
    }

    fun setIsBlocked(isBlocked: Boolean?) {
        this.isBlocked = isBlocked
    }

    fun getIsNormalQuizComplete(): Boolean? {
        return isNormalQuizComplete
    }

    fun setIsNormalQuizComplete(isNormalQuizComplete: Boolean?) {
        this.isNormalQuizComplete = isNormalQuizComplete
    }

    fun getIsBibleQuizComplete(): Boolean? {
        return isBibleQuizComplete
    }

    fun setIsBibleQuizComplete(isBibleQuizComplete: Boolean?) {
        this.isBibleQuizComplete = isBibleQuizComplete
    }

    fun getIsActive(): Boolean? {
        return isActive
    }

    fun setIsActive(isActive: Boolean?) {
        this.isActive = isActive
    }

    fun getIsSignUpPerformed(): Boolean? {
        return isSignUpPerformed
    }

    fun setIsSignUpPerformed(isSignUpPerformed: Boolean?) {
        this.isSignUpPerformed = isSignUpPerformed
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String) {
        this.id = id
    }

    fun getFirstName(): String? {
        return firstName
    }

    fun setFirstName(firstName: String) {
        this.firstName = firstName
    }

    fun getLastName(): String? {
        return lastName
    }

    fun setLastName(lastName: String) {
        this.lastName = lastName
    }

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun getGender(): Int? {
        return gender
    }

    fun setGender(gender: Int?) {
        this.gender = gender
    }

    fun getDOB(): String? {
        return dOB
    }

    fun setDOB(dOB: String) {
        this.dOB = dOB
    }

    fun getAge(): Int? {
        return age
    }

    fun setAge(age: Int?) {
        this.age = age
    }

    fun getToken(): String? {
        return token
    }

    fun setToken(token: String) {
        this.token = token
    }

    fun getGoogleSocialId(): String? {
        return googleSocialId
    }

    fun setGoogleSocialId(googleSocialId: String) {
        this.googleSocialId = googleSocialId
    }

    fun getFacebookSocialId(): String? {
        return facebookSocialId
    }

    fun setFacebookSocialId(facebookSocialId: String) {
        this.facebookSocialId = facebookSocialId
    }

    fun getIsReviewDone(): Boolean? {
        return isReviewDone
    }

    fun setIsReviewDone(isReviewDone: Boolean?) {
        this.isReviewDone = isReviewDone
    }

    fun getReviewFor(): String? {
        return reviewFor
    }

    fun setReviewFor(reviewFor: String) {
        this.reviewFor = reviewFor
    }

    fun getSubscriptionDone(): Boolean? {
        return isSubscriptionDone
    }

    fun setIsSubscriptionDone(isSubscriptionDone: Boolean?) {
        this.isSubscriptionDone = isSubscriptionDone
    }

    fun getSubscriptionFree(): Boolean? {
        return isSubscriptionFree
    }

    fun setIsSubscriptionFree(isSubscriptionFree: Boolean?) {
        this.isSubscriptionFree = isSubscriptionFree
    }

    fun getPhoneNo(): String? {
        return phone_no
    }

    fun setPhoneNo(phoneNo: String?) {
        this.phone_no = phoneNo
    }
    fun getSubscriptionStatus(): Boolean? {
        return subscriptionStatus
    }

}
