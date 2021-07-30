package shiddush.view.com.mmvsd.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Sumit Kumar
 * @Base User class :  This class contain the all the fields having required for registration of the user..
 *
 *
 */

class CallData {

    @SerializedName("callduration")
    @Expose
    private var callduration: Long = 0
   @SerializedName("tokenBoxApi")
    @Expose
    private var tokenBoxApi: String = ""
    @SerializedName("fromId")
    @Expose
    private var fromId: String = ""
    @SerializedName("toId")
    @Expose
    private var toId: String = ""
    @SerializedName("fromUserSessionId")
    @Expose
    private var fromUserSessionId: String = ""
    @SerializedName("toUserSessionId")
    @Expose
    private var toUserSessionId: String = ""
    @SerializedName("matchFound")
    @Expose
    private var matchFound: String = ""
    @SerializedName("tokBoxToken")
    @Expose
    private var tokBoxToken: String = ""
    @SerializedName("time")
    @Expose
    private var time: String = ""
    @SerializedName("count")
    @Expose
    private var count: Int = 0
    @SerializedName("fromSocketId")
    @Expose
    private var fromSocketId: String = ""
    @SerializedName("toSocketId")
    @Expose
    private var toSocketId: String = ""
    @SerializedName("matchedUserName")
    @Expose
    private var matchedUserName: String = ""
    @SerializedName("date")
    @Expose
    private var date: String = ""
    @SerializedName("isDropCall")
    @Expose
    private var isDropCall: Boolean = false

    fun isDropCall(): Boolean? {
        return isDropCall
    }

    fun setDropCall(isActive: Boolean) {
        this.isDropCall = isActive
    }

    fun getDate(): String? {
        return date
    }

    fun setDate(id: String) {
        this.date = id
    }

    fun getMatchedUserName(): String? {
        return matchedUserName
    }

    fun setMatchedUserName(id: String) {
        this.matchedUserName = id
    }

    fun getToSocketId(): String? {
        return toSocketId
    }

    fun setToSocketId(id: String) {
        this.toSocketId = id
    }

    fun getFromSocketId(): String? {
        return fromSocketId
    }

    fun setFromSocketId(id: String) {
        this.fromSocketId = id
    }

    fun getCount(): Int? {
        return count
    }

    fun setCount(id: Int) {
        this.count = id
    }

    fun getTime(): String? {
        return time
    }

    fun setTime(id: String) {
        this.time = id
    }

    fun getTokBoxToken(): String? {
        return tokBoxToken
    }

    fun setTokBoxToken(id: String) {
        this.tokBoxToken = id
    }

    fun getMatchFound(): String? {
        return matchFound
    }

    fun setMatchFound(id: String) {
        this.matchFound = id
    }

    fun getToUserSessionId(): String? {
        return toUserSessionId
    }

    fun setToUserSessionId(id: String) {
        this.toUserSessionId = id
    }

    fun getFromUserSessionId(): String? {
        return fromUserSessionId
    }

    fun setFromUserSessionId(id: String) {
        this.fromUserSessionId = id
    }

    fun getToId(): String? {
        return toId
    }

    fun setToId(id: String) {
        this.toId = id
    }

    fun getFromId(): String? {
        return fromId
    }

    fun setFromId(id: String) {
        this.fromId = id
    }

    fun getTokenBoxApi(): String? {
        return tokenBoxApi
    }

    fun setTokenBoxApi(id: String) {
        this.tokenBoxApi = id
    }

    fun setCallDuration(callduration: Long) {
        this.callduration = callduration

    }

    fun getCallDuration(): Long? {
        return callduration
    }

}