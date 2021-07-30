package shiddush.view.com.mmvsd.model.signup

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Sumit Kumar.
 */
class SignUpData {

    @SerializedName("_id")
    @Expose
    private var id: String? = ""
    @SerializedName("isBlocked")
    @Expose
    private var isBlocked: Boolean? = false
    @SerializedName("device_token")
    @Expose
    private var deviceToken: String? = ""
    @SerializedName("device_type")
    @Expose
    private var deviceType: String? = ""
    @SerializedName("loginType")
    @Expose
    private var loginType: List<Int>? = null
    @SerializedName("isQuizComplete")
    @Expose
    private var isQuizComplete: Boolean? = false
    @SerializedName("isDeleted")
    @Expose
    private var isDeleted: Boolean? = false
    @SerializedName("isNormalQuizComplete")
    @Expose
    private var isNormalQuizComplete: Boolean? = false
    @SerializedName("isBibleQuizComplete")
    @Expose
    private var isBibleQuizComplete: Boolean? = false
    @SerializedName("isActive")
    @Expose
    private var isActive: Boolean? = false
    @SerializedName("isOnline")
    @Expose
    private var isOnline: Boolean? = false
    @SerializedName("isTandC")
    @Expose
    private var isTandC: Boolean? = false
    @SerializedName("isDivorsed")
    @Expose
    private var isDivorsed: Boolean? = false
    @SerializedName("modifiedDate")
    @Expose
    private var modifiedDate: String? = ""
    @SerializedName("createdDate")
    @Expose
    private var createdDate: String? = ""
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
    @SerializedName("images")
    @Expose
    private var images: List<Any>? = null
    @SerializedName("createdAt")
    @Expose
    private var createdAt: String? = null
    @SerializedName("updatedAt")
    @Expose
    private var updatedAt: String? = null
    @SerializedName("__v")
    @Expose
    private var v: Int? = 0
    @SerializedName("token")
    @Expose
    private var token: String? = ""
    @SerializedName("queryStatus")
    @Expose
    private var queryStatus: Boolean? = false
    @SerializedName("facebookSocialId")
    @Expose
    private var facebookSocialId: String? = ""
    @SerializedName("googleSocialId")
    @Expose
    private var googleSocialId: String? = ""
    @SerializedName("isSignUpPerformed")
    @Expose
    private var isSignUpPerformed: Boolean? = false
    @SerializedName("isSubscriptionDone")
    @Expose
    private var isSubscriptionDone: Boolean? = false
    @SerializedName("isSubscriptionFree")
    @Expose
    private var isSubscriptionFree: Boolean? = false
    @SerializedName("isLocationAlarmEnabled")
    @Expose
    private var isLocationAlarmEnabled: Boolean = false
    @SerializedName("isGenderAlarmEnabled")
    @Expose
    private var isGenderAlarmEnabled: Boolean = false
    @SerializedName("phone_no")
    @Expose
    private var phone_no: String = ""
    @SerializedName("subscriptionStatus")
    @Expose
    private var subscriptionStatus: Boolean? = false

    fun getPhone(): String? {
        return phone_no
    }

    fun setPhone(phoneNo: String) {
        this.phone_no = phoneNo
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String) {
        this.id = id
    }

    fun getIsBlocked(): Boolean? {
        return isBlocked
    }

    fun setIsBlocked(isBlocked: Boolean?) {
        this.isBlocked = isBlocked
    }

    fun getDeviceToken(): String? {
        return deviceToken
    }

    fun setDeviceToken(deviceToken: String) {
        this.deviceToken = deviceToken
    }

    fun getDeviceType(): String? {
        return deviceType
    }

    fun setDeviceType(deviceType: String) {
        this.deviceType = deviceType
    }

    fun getLoginType(): List<Int>? {
        return loginType
    }

    fun setLoginType(loginType: List<Int>) {
        this.loginType = loginType
    }

    fun getIsQuizComplete(): Boolean? {
        return isQuizComplete
    }

    fun setIsQuizComplete(isQuizComplete: Boolean?) {
        this.isQuizComplete = isQuizComplete
    }

    fun getIsDeleted(): Boolean? {
        return isDeleted
    }

    fun setIsDeleted(isDeleted: Boolean?) {
        this.isDeleted = isDeleted
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

    fun getIsOnline(): Boolean? {
        return isOnline
    }

    fun setIsOnline(isOnline: Boolean?) {
        this.isOnline = isOnline
    }

    fun getIsTandC(): Boolean? {
        return isTandC
    }

    fun setIsTandC(isTandC: Boolean?) {
        this.isTandC = isTandC
    }

    fun getIsDivorsed(): Boolean? {
        return isDivorsed
    }

    fun setIsDivorsed(isDivorsed: Boolean?) {
        this.isDivorsed = isDivorsed
    }

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

    fun getImages(): List<Any>? {
        return images
    }

    fun setImages(images: List<Any>) {
        this.images = images
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

    fun getToken(): String? {
        return token
    }

    fun setToken(token: String) {
        this.token = token
    }

    fun getQueryStatus(): Boolean? {
        return queryStatus
    }

    fun setQueryStatus(queryStatus: Boolean?) {
        this.queryStatus = queryStatus
    }

    fun getFacebookSocialId(): String? {
        return facebookSocialId
    }

    fun setFacebookSocialId(token: String) {
        this.facebookSocialId = token
    }

    fun getGoogleSocialId(): String? {
        return googleSocialId
    }

    fun setGoogleSocialId(token: String) {
        this.googleSocialId = token
    }

    fun getIsSignUpPerformed(): Boolean? {
        return isSignUpPerformed
    }

    fun setIsSignUpPerformed(isSignUpPerformed: Boolean?) {
        this.isSignUpPerformed = isSignUpPerformed
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

    fun getLocationAlarmEnabled(): Boolean {
        return isLocationAlarmEnabled
    }

    fun setLocationAlarmEnabled(isLocationAlarmEnabled: Boolean) {
        this.isLocationAlarmEnabled = isLocationAlarmEnabled
    }

    fun getGenderAlarmEnabled(): Boolean {
        return isGenderAlarmEnabled
    }

    fun setGenderAlarmEnabled(isGenderAlarmEnabled: Boolean) {
        this.isGenderAlarmEnabled = isGenderAlarmEnabled
    }
    fun getSubscriptionStatus(): Boolean? {
        return subscriptionStatus
    }


}
