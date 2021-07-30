package shiddush.view.com.mmvsd.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Sumit Kumar
 * @Base User class :  This class contain the all the fields having required for registration of the user..
 *
 *
 */

class User {

    @SerializedName("_id")
    @Expose
    private var id: String = ""
    @SerializedName("firstName")
    @Expose
    private var firstName: String = ""
    @SerializedName("lastName")
    @Expose
    private var lastName: String = ""
    @SerializedName("email")
    @Expose
    private var email:String = ""
    @SerializedName("phone_no")
    @Expose
    private var phone_no:String = ""
    @SerializedName("password")
    @Expose
    private var password: String = ""
    @SerializedName("gender")
    @Expose
    private var gender: Int = 0
    @SerializedName("DOB")
    @Expose
    private var dOB: String = ""
    @SerializedName("token")
    @Expose
    private var token: String = ""
    @SerializedName("loginType")
    @Expose
    private var loginType: Int = 0
    @SerializedName("facebookSocialId")
    @Expose
    private var facebookSocialId: String = ""
    @SerializedName("googleSocialId")
    @Expose
    private var googleSocialId: String = ""
    @SerializedName("isNormalQuizComplete")
    @Expose
    private var isNormalQuizComplete: Boolean = false
    @SerializedName("isBibleQuizComplete")
    @Expose
    private var isBibleQuizComplete: Boolean = false
    @SerializedName("isActive")
    @Expose
    private var isActive: Boolean = false
    @SerializedName("isSignUpPerformed")
    @Expose
    private var isSignUpPerformed: Boolean = false
    @SerializedName("isBlocked")
    @Expose
    private var isBlocked: Boolean = false
    @SerializedName("isSubscriptionDone")
    @Expose
    private var isSubscriptionDone: Boolean = false
    @SerializedName("isSubscriptionFree")
    @Expose
    private var isSubscriptionFree: Boolean = false

    @SerializedName("lat")
    @Expose
    private var lat: String = ""
    @SerializedName("lng")
    @Expose
    private var lng: String = ""
    @SerializedName("city")
    @Expose
    private var city: String = ""
    @SerializedName("country")
    @Expose
    private var country: String = ""
    @SerializedName("countryCode")
    @Expose
    private var countryCode: String = ""

    fun getIsNormalQuizComplete(): Boolean? {
        return isNormalQuizComplete
    }

    fun setIsNormalQuizComplete(isNormalQuizComplete: Boolean) {
        this.isNormalQuizComplete = isNormalQuizComplete
    }

    fun getIsBibleQuizComplete(): Boolean? {
        return isBibleQuizComplete
    }

    fun setIsBibleQuizComplete(isBibleQuizComplete: Boolean) {
        this.isBibleQuizComplete = isBibleQuizComplete
    }

        fun getIsActive(): Boolean? {
            return isActive
        }

        fun setIsActive(isActive: Boolean) {
            this.isActive = isActive
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
    fun setPhoneNo(phoneNo: String) {
        this.phone_no = phoneNo
    }

    fun getEmail(): String? {
        return email
    }

    fun getPhone(): String? {
        return phone_no
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun getPassword(): String? {
        return password
    }

    fun setPassword(password: String) {
        this.password = password
    }

    fun getGender(): Int? {
        return gender
    }

    fun setGender(gender: Int) {
        this.gender = gender
    }

    fun getDOB(): String? {
        return dOB
    }

    fun setDOB(dOB: String) {
        this.dOB = dOB
    }

    fun getToken(): String? {
        return token
    }

    fun setToken(token: String) {
        this.token = token
    }

    fun getLoginType(): Int? {
        return loginType
    }

    fun setLoginType(loginType: Int) {
        this.loginType = loginType
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

    fun setIsSignUpPerformed(isSignUpPerformed: Boolean) {
        this.isSignUpPerformed = isSignUpPerformed
    }

    fun getIsBlocked(): Boolean? {
        return isBlocked
    }

    fun setIsBlocked(isBlocked: Boolean) {
        this.isBlocked = isBlocked
    }

    fun getSubscriptionDone(): Boolean? {
        return isSubscriptionDone
    }

    fun setIsSubscriptionDone(isSubscriptionDone: Boolean) {
        this.isSubscriptionDone = isSubscriptionDone
    }

    fun getSubscriptionFree(): Boolean? {
        return isSubscriptionFree
    }

    fun setIsSubscriptionFree(isSubscriptionFree: Boolean) {
        this.isSubscriptionFree = isSubscriptionFree
    }

    fun getLat(): String? {
        return lat
    }

    fun setLat(lat: String) {
        this.lat = lat
    }

    fun getLng(): String? {
        return lng
    }

    fun setLng(lng: String) {
        this.lng = lng
    }

    fun getCity(): String? {
        return city
    }

    fun setCity(city: String) {
        this.city = city
    }

    fun getCountry(): String? {
        return country
    }

    fun setCountry(country: String) {
        this.country = country
    }

    fun getCountryCode(): String? {
        return countryCode
    }

    fun setCountryCode(countryCode: String) {
        this.countryCode = countryCode
    }
}