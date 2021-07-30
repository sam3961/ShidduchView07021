package shiddush.view.com.mmvsd.socket

import org.json.JSONObject
import shiddush.view.com.mmvsd.model.chat.ReceivedMessageResponse
import shiddush.view.com.mmvsd.model.onlineOffline.OnlineOfflineResponse
import shiddush.view.com.mmvsd.model.videocall.SocketCallResponse

/**
 * Created by Sumit Kumar.
 * this interface is use to communicate with socket and activity
 */
interface SocketCallBackListeners {
    fun onMatchedResponse(data: SocketCallResponse)
    fun onUserInfoResponse(data: SocketCallResponse)
    fun onAcceptRejectResponse(data: SocketCallResponse)
    fun onSocketConnected()
    fun onSocketDisconnected()
    fun onRefreshBestMatch(status: String)
    fun callAccepted(status: Boolean, tokboxToken: String, time: Long, matchFound: Boolean, count: Int, fromsocketid: String, toSocketId: String, toUserSessionId: String, fromId: String, toId: String, fromUserSessionId: String, tokboxApi: String,rejoin:Boolean)
    fun callReconnectIsUserOnline(status:Boolean)
    fun callDropWantToConnect(userId: String, friendId: String, firstName: String, friendFirstName: String, type: String)
    fun connectedToCall(status: Boolean, tokboxToken: String, time: Long, matchFound: Boolean, count: Int, fromsocketid: String, toSocketId: String, toUserSessionId: String, fromId: String, toId: String, fromUserSessionId: String, tokboxApi: String,rejoin:Boolean)
    fun friendOnline(arrayListUsers: MutableList<OnlineOfflineResponse>)
    fun friendOffline(arrayListUsers: MutableList<OnlineOfflineResponse>)
    fun bestMatches(arrayListUsers: MutableList<OnlineOfflineResponse>)
    fun onNotifyEndCall(data: JSONObject)
    fun onChatMessageReceive(data: ReceivedMessageResponse)
}
