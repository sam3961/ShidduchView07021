package shiddush.view.com.mmvsd.socket

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Ack
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import shiddush.view.com.mmvsd.BuildConfig
import shiddush.view.com.mmvsd.model.chat.ChatListResponse
import shiddush.view.com.mmvsd.model.chat.ReceivedMessageResponse
import shiddush.view.com.mmvsd.model.chat.SingleChatResponse
import shiddush.view.com.mmvsd.model.matchedQuestions.MatchedQuestionsResponse
import shiddush.view.com.mmvsd.model.onlineOffline.OnlineOfflineResponse
import shiddush.view.com.mmvsd.model.videocall.SocketCallResponse
import shiddush.view.com.mmvsd.ui.chat.ChatViewModel
import shiddush.view.com.mmvsd.utill.AppInstance
import shiddush.view.com.mmvsd.utill.WAITING_SCREEN
import shiddush.view.com.mmvsd.utill.getUserObject

/**
 * Created by Sumit Kumar.
 * this class is use to communicate with socket
 */
@SuppressLint("StaticFieldLeak")
object SocketCommunication {

    private var activity: Activity? = null
    private var mSocket: Socket? = null
    private val USER_ID: String = "user_id"
    private val CURRENT_SCREEN: String = "current_screen"
    private val USERID: String = "userid"
    private val FRIEND: String = "friend"
    private val RESPONSE: String = "response"
    private val TYPE: String = "type"
    private val MESSAGE: String = "message"
    private val MESSAGE_TYPE: String = "message_type"
    private val TIMESLOT: String = "timeslot"
    private val FRIEND_NAME: String = "friendFirstName"
    private val FIRST_NAME: String = "firstName"
    private val FROM_ID: String = "fromId"
    private val TO_ID: String = "toId"
    private val FROM_SOCKET_ID: String = "fromSocketID"
    private val TO_SOCKET_ID: String = "toSocketID"
    private val COUNT: String = "count"
    private val STATUS: String = "status"
    private val REASON: String = "reason"
    private val USER_RESPONSE: String = "emitdatatoparticluaruser"
    private val CALL_MATCHING_REQUEST: String = "callmatching"
    private val VIDEO_DATE_END_REQUEST: String = "notify_end_call"
    private val BEST_MATCH_REFRESH_REQUEST: String = "reload_matches"
    private val REJOIN_REQUEST: String = "rejoin_request"
    private val SEND_MESSAGE_REQUEST: String = "send_msg"
    private val GET_MESSAGE_REQUEST: String = "get_msg"
    private val READ_MESSAGE_REQUEST: String = "read_msg"
    private val GET_MATCHING_QUESTIONS_REQUEST: String = "matching_questions"
    private val CALL_ONLINE_ACTIVITY_REQUEST: String = "onlineactivity"
    private val IN_SCREEN_REQUEST: String = "in_screen"
    private val BEST_MATCH_REQUEST: String = "best_matches"
    private val RECALL_REQUEST: String = "recall_request"
    private val ALERT_EMIT_RESPONSE: String = "alert_response"
    private val RECALL_RESPONSE: String = "recall_response"
    private val MOBILE_LOG = "mobile_log"
    private val CALL_RESPONSE: String = "call_response"
    private val CHAT_MESSAGE_RESPONSE: String = "incoming_msg"
    private val FRIEND_ONLINE_RESPONSE: String = "friend_online"
    private val FRIEND_OFFLINE_RESPONSE: String = "friend_offline"
    private val REJOIN_REQUEST_RESPONSE: String = "emit_rejoin_request"
    private val EMIT_REJOIN_RESPONSE: String = "emit_rejoin_response"
    private val REJOIN_REMOVE_RESPONSE: String = "emit_rejoin_remove"
    private val NOTIFY_END_CALL_RESPONSE: String = "emit_end_call"
    private val REJOIN_RESPONSE: String = "rejoin_response"
    private val CALL_REQUEST_RESPONSE: String = "call_request"
    private val USER_ACCEPT_REJECT_REQUEST: String = "acceptreject"
    private val REMOVE_USER_REQUEST: String = "remove_online_list"
    private val BEST_MATCH_REMOVE_REQUEST: String = "remove_best_match"
    private val USER_ACCEPT_REJECT_RESPONSE: String = "acceptrejectstatus"
    private val GENERATE_TOKEN_REQUEST: String = "generateToken"
    private val SEND_SESSION_TOKEN_RESPONSE: String = "sendsessionidandtoken"
    private var arrayListUsers: MutableList<OnlineOfflineResponse> = arrayListOf()
    private var arrayListBestMatch: MutableList<OnlineOfflineResponse> = arrayListOf()
    private var callBack: SocketCallBackListeners? = null

    //++Socket connection
    /**
     * to connect socket
     */
    fun connectSocket(activity: Activity, mSocketCallBackListeners: SocketCallBackListeners) {
        this.callBack = mSocketCallBackListeners
        this.activity = activity
        try {
            if (mSocket == null) {
                try {
                    val userId = "$USER_ID=" + getUserObject(activity).getId()!!
                    Log.e("SWAPLOGSOCKET", "socket userId=>>> $userId")
                    val options = IO.Options()
                    options.forceNew = true
                    options.query = userId
                    mSocket = IO.socket(BuildConfig.SOCKET_URL, options)
                    mSocket!!.on(Socket.EVENT_CONNECT, onConnect)
                    mSocket!!.on(Socket.EVENT_DISCONNECT, onDisconnect)
                    mSocket!!.on(USER_RESPONSE, onResponse)
                    mSocket!!.on(SEND_SESSION_TOKEN_RESPONSE, onUserInfoResponse)
                    mSocket!!.on(USER_ACCEPT_REJECT_RESPONSE, onAcceptRejectResponse)
                    mSocket!!.on(CALL_MATCHING_REQUEST, callMatching)
                    mSocket!!.on(FRIEND_ONLINE_RESPONSE, friendOnlineResponse)
                    mSocket!!.on(FRIEND_OFFLINE_RESPONSE, friendOfflineResponse)
                    mSocket!!.on(REJOIN_REQUEST_RESPONSE, rejoinRequestResponse)
                    mSocket!!.on(EMIT_REJOIN_RESPONSE, rejoinResponse)
                    mSocket!!.on(REJOIN_REMOVE_RESPONSE, onOpponentUserRemove)
                    mSocket!!.on(NOTIFY_END_CALL_RESPONSE, onNotifyEndCall)
                    mSocket!!.on(CHAT_MESSAGE_RESPONSE, onReceiveChatMessage)
                    mSocket!!.connect()
                    Log.e("SWAPLOGSOCKET", "socket call and connect")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * to disconnect socket
     */
    fun disconnectSocket(isLogout: Boolean) {
        try {
            if (mSocket != null) {  // && mSocket?.connected()!!
                activity!!.runOnUiThread {
                    try {
                        if (mSocket != null) { // && !mSocket?.connected()!!
                            try {
                                Log.e("SWAPLOGSOCKET", "socket user disconnect")
                                if (mSocket?.connected()!!) {
                                    mSocket!!.disconnect()
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }

                            try {
                                if (isLogout) {
                                    mSocket = null

                                    //clear users rejoin list
                                    arrayListUsers.clear()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * check socket connected, if not then connect again
     */
    fun isSocketConnected(): Boolean {
        var isConnected = false
        try {
            isConnected = if (mSocket != null && mSocket?.connected()!!) {
                true
            } else {
                mSocket!!.connect()
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.e("isSocketConnected", "==== isSocketConnected " + isConnected)
        return isConnected
    }

    /**
     * on connect socket call
     */
    private val onConnect = Emitter.Listener {
        activity!!.runOnUiThread {
            Log.e("ACK_SOCKET", "onConnect : Connected")
            callBack!!.onSocketConnected()
        }
    }

    /**
     * on disconnect socket call
     */
    private val onDisconnect = Emitter.Listener {
        activity!!.runOnUiThread {
            Log.e("ACK_SOCKET", "onDisConnect : Disconnected")
            callBack!!.onSocketDisconnected()

        }
    }

    /**
     * response from socket for matched user with all details
     */
    private val onResponse = Emitter.Listener {
        activity!!.runOnUiThread {
            try {
                val data = it[0] as JSONObject
                Log.e("ACK_SOCKET", "onResponse : $data")
                val gson = GsonBuilder().create()
                val response =
                    gson.fromJson(data.toString(), SocketCallResponse::class.java)
                callBack!!.onMatchedResponse(response)
                // callBack!!.onUserInfoResponse(response)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     *  notify if opposite user  end call
     */
    private val onNotifyEndCall = Emitter.Listener {
        activity!!.runOnUiThread {
            try {
                val data = it[0] as JSONObject
                Log.e("ACK_SOCKET", "onResponse : $data")
                val gson = GsonBuilder().create()
                callBack!!.onNotifyEndCall(data)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Remove user because he also removes from you list
     */
    private val onOpponentUserRemove = Emitter.Listener {
        activity!!.runOnUiThread {
            try {
                val data = it[0] as JSONObject
                val id = data.getString("userid");

                val templist: ArrayList<OnlineOfflineResponse> = ArrayList<OnlineOfflineResponse>()
                templist.addAll(arrayListUsers)

                if (arrayListUsers.size > 0) {
                    for (i in 0..arrayListUsers.size - 1) {
                        if (id.equals(templist.get(i).userid)) {
                            templist.removeAt(i)
                            Log.d("removedUser", "ackData" + " " + arrayListUsers.size)
                            break
                        }
                    }
                }
                arrayListUsers = templist
                Log.d("friendOffline", "ackData" + " " + arrayListUsers.size)
                callBack?.friendOffline(removeDuplicacy())

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * emit endVideoDate
     */

    fun emitVideoCallEnd(friendId: String) {
        try {
            if (mSocket != null) {  // && mSocket?.connected()!!
                activity!!.runOnUiThread {
                    try {
                        if (mSocket != null) { // && !mSocket?.connected()!!
                            val connectObject = JSONObject()
                            try {
                                connectObject.put(USERID, getUserObject(activity!!).getId()!!)
                                connectObject.put(FRIEND, friendId)
                                mSocket?.emit(VIDEO_DATE_END_REQUEST, connectObject)
                                Log.e(
                                    "SWAPLOGSOCKET.....",
                                    "connectObject " + connectObject + " call"
                                )
                                Log.e(
                                    "SWAPLOGSOCKET.....",
                                    "emit socket " + mSocket?.emit(
                                        VIDEO_DATE_END_REQUEST,
                                        connectObject
                                    )
                                )
                            } catch (e: JSONException) {
                                Log.e("VideoDateEnd", "VideoDateEnd exception")
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("VideoDateEnd", "VideoDateEnd exception")
                    }
                }
                //clear users rejoin list
                arrayListUsers.clear()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("CallMatching", "CallMatching exception")
        }
    }


    /**
     * emit callmatching
     */
    fun emitCallMatching() {
        try {
            if (mSocket != null) {  // && mSocket?.connected()!!
                activity!!.runOnUiThread {
                    try {
                        if (mSocket != null) { // && !mSocket?.connected()!!
                            val connectObject = JSONObject()
                            try {
                                connectObject.put(USER_ID, getUserObject(activity!!).getId()!!)
                                mSocket?.emit(CALL_MATCHING_REQUEST, connectObject)
                                Log.e(
                                    "SWAPLOGSOCKET.....",
                                    "connectObject " + connectObject + " call"
                                )
                                Log.e(
                                    "SWAPLOGSOCKET.....",
                                    "emit socket " + mSocket?.emit(
                                        CALL_MATCHING_REQUEST,
                                        connectObject
                                    )
                                )
                            } catch (e: JSONException) {
                                Log.e("CallMatching", "CallMatching exception")
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("CallMatching", "CallMatching exception")
                    }
                }
                //clear users rejoin list
                arrayListUsers.clear()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("CallMatching", "CallMatching exception")
        }
    }


    fun emitRejoinRequest(id: String, name: String, type: String) {
        try {
            if (mSocket != null) {  // && mSocket?.connected()!!
                activity!!.runOnUiThread {
                    try {
                        if (mSocket != null) { // && !mSocket?.connected()!!
                            val connectObject = JSONObject()
                            try {
                                connectObject.put(USERID, getUserObject(activity!!).getId()!!)
                                connectObject.put(FRIEND, id)
                                connectObject.put(FRIEND_NAME, name)
                                connectObject.put(TYPE, type)
                                connectObject.put(
                                    FIRST_NAME,
                                    getUserObject(activity!!).getFirstName()!!
                                )
                                mSocket?.emit(REJOIN_REQUEST, connectObject, Ack {
                                    val ackData = it[0] as JSONObject
                                    Log.d(
                                        "emitCallRequestActivity",
                                        "emitCallRequestActivity :" + ackData.toString()
                                    )
                                    val status = ackData.getBoolean("is_online")
                                    if (status) {
                                        callBack?.callReconnectIsUserOnline(true)
                                    } else {
                                        callBack?.callReconnectIsUserOnline(false)
                                    }
                                    Log.e(
                                        "SWAPLOGSOCKET.....",
                                        "emit socket " + REJOIN_REQUEST + "  " + connectObject
                                    )
                                })
                            } catch (e: JSONException) {
                                Log.e("rejoinRequest", "rejoinRequest exception")
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("rejoinRequest", "rejoinRequest exception")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("rejoinRequest", "rejoinRequest exception")
        }
    }

    fun emitSendChatMessage(
        friendId: String,
        message: String,
        type: String,
        viewModel: ChatViewModel
    ) {
        try {
            if (mSocket != null) {  // && mSocket?.connected()!!
                activity!!.runOnUiThread {
                    try {
                        if (mSocket != null) { // && !mSocket?.connected()!!
                            val connectObject = JSONObject()
                            try {
                                connectObject.put(USERID, getUserObject(activity!!).getId()!!)
                                connectObject.put(FRIEND, friendId)
                                connectObject.put(MESSAGE, message)
                                connectObject.put(MESSAGE_TYPE, type)
                                Log.d("emitSendChatMessage", connectObject.toString())
                                mSocket?.emit(SEND_MESSAGE_REQUEST, connectObject, Ack {
                                    val ackData = it[0] as JSONObject
                                    Log.d("emitSendChatMessage", ackData.toString())
                                    val chatResponse = Gson().fromJson(
                                        ackData.toString(),
                                        SingleChatResponse::class.java
                                    )
                                    viewModel.sendMessageCallBack(chatResponse)
                                });
                            } catch (e: JSONException) {
                                Log.e("emitSendChatMessage", "rejoinRequest exception")
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("emitSendChatMessage", "rejoinRequest exception")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("emitSendChatMessage", "rejoinRequest exception")
        }
    }

    fun emitReceiveChatMessage(friendId: String, viewModel: ChatViewModel) {
        try {
            if (mSocket != null) {  // && mSocket?.connected()!!
                activity!!.runOnUiThread {
                    try {
                        if (mSocket != null) { // && !mSocket?.connected()!!
                            val connectObject = JSONObject()
                            try {
                                connectObject.put(USERID, getUserObject(activity!!).getId()!!)
                                connectObject.put(FRIEND, friendId)
                                Log.d("emitReceiveChatMessage", connectObject.toString())
                                mSocket?.emit(GET_MESSAGE_REQUEST, connectObject, Ack {
                                    val ackData = it[0] as JSONObject
                                    Log.d("emitReceiveChatMessage", ackData.toString())
                                    val chatResponse = Gson().fromJson(
                                        ackData.toString(),
                                        ChatListResponse::class.java
                                    )
                                    viewModel.chatListCallBack(chatResponse)
                                })
                            } catch (e: JSONException) {
                                Log.e("emitSendChatMessage", "rejoinRequest exception")
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("emitSendChatMessage", "rejoinRequest exception")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("emitSendChatMessage", "rejoinRequest exception")
        }
    }

    fun emitReadChatMessage(friendId: String, viewModel: ChatViewModel) {
        try {
            if (mSocket != null) {  // && mSocket?.connected()!!
                activity!!.runOnUiThread {
                    try {
                        if (mSocket != null) { // && !mSocket?.connected()!!
                            val connectObject = JSONObject()
                            try {
                                connectObject.put(USERID, getUserObject(activity!!).getId()!!)
                                connectObject.put(FRIEND, friendId)
                                Log.d("emitReadChatMessage", connectObject.toString())
                                mSocket?.emit(GET_MESSAGE_REQUEST, connectObject, Ack {
                                    val ackData = it[0] as JSONObject
                                    Log.d("emitReceiveChatMessage", ackData.toString())
                                    val chatResponse = Gson().fromJson(
                                        ackData.toString(),
                                        ChatListResponse::class.java
                                    )
                                    viewModel.chatListCallBack(chatResponse)
                                })
                            } catch (e: JSONException) {
                                Log.e("emitReadChatMessage", "emitReadChatMessage exception")
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("emitReadChatMessage", "emitReadChatMessage exception")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("emitSendChatMessage", "rejoinRequest exception")
        }
    }

    // emit matching question
    fun emitMatchingQuestions(friendId: String, viewModel: ChatViewModel) {
        try {
            if (mSocket != null) {  // && mSocket?.connected()!!
                activity!!.runOnUiThread {
                    try {
                        if (mSocket != null) { // && !mSocket?.connected()!!
                            val connectObject = JSONObject()
                            try {
                                connectObject.put(USER_ID, getUserObject(activity!!).getId()!!)
                                connectObject.put(FRIEND, friendId)
                                Log.d("emitMatchingQuestions", connectObject.toString())
                                mSocket?.emit(GET_MATCHING_QUESTIONS_REQUEST, connectObject, Ack {
                                    val ackData = it[0] as JSONObject
                                    Log.d("emitMatchingQuestions", ackData.toString())
                                    val chatResponse = Gson().fromJson(
                                        ackData.toString(),
                                        MatchedQuestionsResponse::class.java
                                    )
                                    viewModel.matchedQuestionCallback(chatResponse)
                                })
                            } catch (e: JSONException) {
                                Log.e("emitMatchingQuestions", "rejoinRequest exception")
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("emitMatchingQuestions", "rejoinRequest exception")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("emitMatchingQuestions", "rejoinRequest exception")
        }
    }

    /**
     * emit accept, reject or timeout video call
     */
    fun acceptRejectCall(fromId: String, toId: String, status: String, reasonRejectCall: String) {
        try {
            Log.e("SWAPLOGSOCKET", "acceptRejectCall : fromId=$fromId, toId=$toId, status=$status")
            if (mSocket != null) {  // && mSocket?.connected()!!
                activity!!.runOnUiThread {
                    try {
                        if (mSocket != null) { // && !mSocket?.connected()!!
                            val connectObject = JSONObject()
                            try {
                                connectObject.put(FROM_ID, fromId)
                                connectObject.put(TO_ID, toId)
                                connectObject.put(STATUS, status)
                                connectObject.put(REASON, reasonRejectCall)
                                mSocket?.emit(USER_ACCEPT_REJECT_REQUEST, connectObject)
                                Log.e(
                                    "SWAPLOGSOCKET",
                                    "emit socket user $USER_ACCEPT_REJECT_REQUEST Called"
                                )
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun emitAlertNotificationResponse(userId: String, response: String, timeSlot: String) {
        try {
            if (mSocket != null) {  // && mSocket?.connected()!!
                activity!!.runOnUiThread {
                    try {
                        if (mSocket != null) { // && !mSocket?.connected()!!
                            val connectObject = JSONObject()
                            try {
                                connectObject.put(USERID, userId)
                                connectObject.put(RESPONSE, response)
                                connectObject.put(TIMESLOT, timeSlot)
                                mSocket?.emit(ALERT_EMIT_RESPONSE)
                                Log.e(
                                    "SWAPLOGSOCKET",
                                    "emit socket user $ALERT_EMIT_RESPONSE Called"
                                )
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun emitRemoveUserFromRejoin(fromId: String, friendId: String, reason: String) {
        try {
            if (mSocket != null) {  // && mSocket?.connected()!!
                activity!!.runOnUiThread {
                    try {
                        if (mSocket != null) { // && !mSocket?.connected()!!
                            val connectObject = JSONObject()
                            try {
                                connectObject.put(USERID, fromId)
                                connectObject.put(FRIEND, friendId)
                                connectObject.put(REASON, reason)
                                mSocket?.emit(REMOVE_USER_REQUEST, connectObject, Ack {
                                    val ackData = it[0] as JSONObject
                                    val id = ackData.getString("friend");

                                    val templist: ArrayList<OnlineOfflineResponse> =
                                        ArrayList<OnlineOfflineResponse>()
                                    templist.addAll(arrayListUsers)

                                    if (arrayListUsers.size > 0) {
                                        for (i in 0..arrayListUsers.size - 1) {
                                            if (id.equals(templist.get(i).userid)) {
                                                templist.removeAt(i)
                                                Log.d(
                                                    "removedUser",
                                                    "ackData" + " " + arrayListUsers.size
                                                )
                                                break
                                            }
                                        }
                                    }
                                    arrayListUsers = templist
                                    Log.d("friendOffline", "ackData" + " " + arrayListUsers.size)
                                    callBack?.friendOffline(removeDuplicacy())

                                })
                                Log.e(
                                    "SWAPLOGSOCKET",
                                    "emit socket user $REMOVE_USER_REQUEST Called"
                                )
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun emitRemoveUserFromBestMatch(fromId: String, friendId: String, reason: String) {
        try {
            if (mSocket != null) {  // && mSocket?.connected()!!
                activity!!.runOnUiThread {
                    try {
                        if (mSocket != null) { // && !mSocket?.connected()!!
                            val connectObject = JSONObject()
                            try {
                                connectObject.put(USERID, fromId)
                                connectObject.put(FRIEND, friendId)
                                connectObject.put(REASON, reason)
                                mSocket?.emit(BEST_MATCH_REMOVE_REQUEST,connectObject)
                                Log.e(
                                    "SWAPLOGSOCKET",
                                    "emit socket user $BEST_MATCH_REMOVE_REQUEST Called "+ connectObject.toString()
                                )
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * response from socket for accept, reject or timeout video call
     */
    private val onAcceptRejectResponse = Emitter.Listener {
        activity!!.runOnUiThread {
            try {
                val data = it[0] as JSONObject
                Log.e("ACK_SOCKET", "onAcceptRejectResponse : $data")
                val gson = GsonBuilder().create()
                val response =
                    gson.fromJson(data.toString(), SocketCallResponse::class.java)
                callBack!!.onAcceptRejectResponse(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private val onReceiveChatMessage = Emitter.Listener {
        activity!!.runOnUiThread {
            try {
                val ackData = it[0] as JSONObject
                Log.d("onReceiveMessage", ackData.toString())
                val chatResponse =
                    Gson().fromJson(ackData.toString(), ReceivedMessageResponse::class.java)
                callBack?.onChatMessageReceive(chatResponse)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val callMatching = Emitter.Listener {
        activity!!.runOnUiThread {
            try {
                Log.e("callMatching", "callMatching :")
                val data = it[0] as JSONObject
                Log.e("callMatching", "callMatching : $data")
                val gson = GsonBuilder().create()
                val response =
                    gson.fromJson(data.toString(), SocketCallResponse::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /*
        private val callRequest = Emitter.Listener {
            activity!!.runOnUiThread {
                try {
                    val data = it[0] as JSONObject
                    Log.e("callRequest", "callRequest :" + data.toString())
                    val callData = getCallData(activity!!.applicationContext)
                    val fromId = data.getString("fromId")
                    val toId = data.getString("toId")
                    if (callData?.getFromId() == fromId && callData?.getToId() == toId || callData?.getToId() == fromId &&
                            callData?.getFromId() == toId) {
                        callBack?.callDropWantToConnect(userId, friendId, firstName)
                    } else {
                        //clearCallData(activity!!.applicationContext)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    */
    private val friendOnlineResponse = Emitter.Listener {
        activity!!.runOnUiThread {
//            try {
//                val data = it[0] as JSONObject
//                Log.d("friendOnlineResponse", "friendOnlineResponse :" + data.toString())
//
//                if (data.has("userid")) {
//                    val id = data.getString("userid")
//                    val name = data.getString("firstName")
//                    val onlineOfflineResponse = OnlineOfflineResponse()
//                    onlineOfflineResponse.userid = id
//                    onlineOfflineResponse.firstName = name
//                    arrayListUsers.add(onlineOfflineResponse)
//                    callBack?.friendOnline(removeDuplicacy())
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
        }
    }


    private val friendOfflineResponse = Emitter.Listener {
        activity!!.runOnUiThread {
//            try {
//                val data = it[0] as JSONObject
//                Log.d("friendOfflineResponse", "friendOfflineResponse :" + data.toString())
//                val id = data.getString("userid")
//                val templist: ArrayList<OnlineOfflineResponse> = ArrayList<OnlineOfflineResponse>()
//                templist.addAll(arrayListUsers)
//
//                if (arrayListUsers.size > 0) {
//                    for (i in 0..arrayListUsers.size - 1) {
//                        if (id.equals(templist.get(i).userid)) {
//                            templist.removeAt(i)
//                            break
//                        }
//                    }
//                }
//                arrayListUsers = templist
//                callBack?.friendOffline(removeDuplicacy())
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
        }
    }

    private val rejoinRequestResponse = Emitter.Listener {
        activity!!.runOnUiThread {
            try {
                val data = it[0] as JSONObject
                Log.d("rejoinRequestResponse", "rejoinRequestResponse :" + data.toString())
                val friendId = data.getString("userid")
                val userId = data.getString("friend")
                val firstName = data.getString("firstName")
                val type = data.getString("type")
                val friendFirstName = data.getString("friendFirstName")
                callBack?.callDropWantToConnect(userId, friendId, firstName, friendFirstName, type)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private val rejoinResponse = Emitter.Listener {
        activity!!.runOnUiThread {
            try {
                val ackData = it[0] as JSONObject
                val ackStatus = ackData.getString("status")

                if (ackStatus.equals("accept")) {
                    val tokboxToken = ackData.getString("tokboxToken")
                    val tokboxApi = ackData.getString("tokboxApi")
                    val time = ackData.getLong("time")
                    val matchFound = ackData.getBoolean("matchFound")
                    val count = ackData.getInt("count")
                    val toSocketId = ackData.getString("fromsocketid")
                    val fromsocketid = ackData.getString("toSocketId")
                    val fromUserSessionId = ackData.getString("toUserSessionId")
                    val toId = ackData.getString("fromId")
                    val fromId = ackData.getString("toId")
                    val toUserSessionId = ackData.getString("fromUserSessionId")
                    val rejoin = ackData.getBoolean("rejoin")
                    callBack!!.callAccepted(
                        true,
                        tokboxToken,
                        time,
                        matchFound,
                        count,
                        fromsocketid,
                        toSocketId,
                        toUserSessionId,
                        fromId,
                        toId,
                        fromUserSessionId,
                        tokboxApi,
                        rejoin
                    )

                } else {
                    callBack!!.callAccepted(
                        false, "", 0L,
                        true, 0, "", "",
                        "", "", "", "", "", false
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun removeDuplicacy(): MutableList<OnlineOfflineResponse> {
        val distinct = arrayListUsers.distinctBy {
            Pair(it.userid, it.userid)
        }.toMutableList()
        return distinct
    }

    private fun removeDuplicacyBestMatch(): MutableList<OnlineOfflineResponse> {
        val distinct = arrayListBestMatch.distinctBy {
            Pair(it.userid, it.userid)
        }.toMutableList()
        return distinct
    }


    /**
     * emit generateToken
     */
    fun emitGenerateToken(
        fromId: String,
        toId: String,
        fromSocketID: String,
        toSocketID: String,
        count: Int
    ) {
        try {
            Log.e(
                "SWAPLOGSOCKET",
                "generateToken : fromId=$fromId, toId=$toId, fromSocketID=$fromSocketID, toSocketID=$toSocketID, count=$count"
            )
            if (mSocket != null) {  // && mSocket?.connected()!!
                activity!!.runOnUiThread {
                    try {
                        if (mSocket != null) { // && !mSocket?.connected()!!
                            val connectObject = JSONObject()
                            try {
                                connectObject.put(FROM_ID, fromId)
                                connectObject.put(TO_ID, toId)
                                connectObject.put(FROM_SOCKET_ID, fromSocketID)
                                connectObject.put(TO_SOCKET_ID, toSocketID)
                                connectObject.put(COUNT, count)
                                mSocket?.emit(GENERATE_TOKEN_REQUEST, connectObject)
                                Log.e(
                                    "SWAPLOGSOCKET",
                                    "emit socket user $GENERATE_TOKEN_REQUEST call"
                                )
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * response from socket for User Info with all details
     */
    private val onUserInfoResponse = Emitter.Listener {
        activity!!.runOnUiThread {
            try {
                val data = it[0] as JSONObject
                Log.e("ACK_SOCKET", "onUserInfoResponse : $data")
                val gson = GsonBuilder().create()
                val response =
                    gson.fromJson(data.toString(), SocketCallResponse::class.java)
                callBack!!.onUserInfoResponse(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * emit onlineactivity
     */
    fun emitOnlineActivity() {
        try {
            if (mSocket != null) {  // && mSocket?.connected()!!
                activity!!.runOnUiThread {
                    try {
                        if (mSocket != null) { // && !mSocket?.connected()!!
                            val connectObject = JSONObject()
                            try {
                                connectObject.put(USER_ID, getUserObject(activity!!).getId()!!)
                                mSocket?.emit(CALL_ONLINE_ACTIVITY_REQUEST, connectObject)
                                Log.e(
                                    "SWAPLOGSOCKET",
                                    "emit socket user $CALL_ONLINE_ACTIVITY_REQUEST call"
                                )
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun emitRejoinResponse(friendId: String, userId: String, firstName: String, status: String) {
        try {
            if (mSocket != null) {  // && mSocket?.connected()!!
                activity!!.runOnUiThread {
                    try {
                        if (mSocket != null) { // && !mSocket?.connected()!!
                            val connectObject = JSONObject()
                            try {
                                connectObject.put(USERID, userId)
                                connectObject.put(FRIEND, friendId)
                                connectObject.put(FIRST_NAME, firstName)
                                connectObject.put(STATUS, status)
                                mSocket?.emit(REJOIN_RESPONSE, connectObject, Ack {
                                    val ackData = it[0] as JSONObject
                                    Log.d(
                                        "emitRejoinResponse",
                                        "emitRejoinResponse :" + ackData.toString()
                                    )
                                    val ackStatus = ackData.getString("status")
                                    if (ackStatus.equals("accept")) {
                                        val tokboxToken = ackData.getString("tokboxToken")
                                        val tokboxApi = ackData.getString("tokboxApi")
                                        val time = ackData.getLong("time")
                                        val matchFound = ackData.getBoolean("matchFound")
                                        val count = ackData.getInt("count")
                                        val fromsocketid = ackData.getString("fromsocketid")
                                        val toSocketId = ackData.getString("toSocketId")
                                        val toUserSessionId = ackData.getString("toUserSessionId")
                                        val fromId = ackData.getString("fromId")
                                        val toId = ackData.getString("toId")
                                        val fromUserSessionId =
                                            ackData.getString("fromUserSessionId")
                                        val rejoin = ackData.getBoolean("rejoin")
                                        callBack?.connectedToCall(
                                            true,
                                            tokboxToken,
                                            time,
                                            matchFound,
                                            count,
                                            fromsocketid,
                                            toSocketId,
                                            toUserSessionId,
                                            fromId,
                                            toId,
                                            fromUserSessionId,
                                            tokboxApi,
                                            rejoin
                                        )
                                    } else if (ackStatus.equals("failed")) {
                                        callBack?.callReconnectIsUserOnline(false)
                                    } else {
                                        callBack?.connectedToCall(
                                            false, "", 0L, false, 0, "",
                                            "", "", "", "", "", "", false
                                        )
                                    }
                                })

                                Log.e(
                                    "SWAPLOGSOCKET",
                                    "emit socket user $REJOIN_RESPONSE call" + connectObject.toString()
                                )
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * emit in_screen
     */
    fun emitInScreenActivity(screenName: String) {
        try {
            if (mSocket != null) {  // && mSocket?.connected()!!
                activity!!.runOnUiThread {
                    try {
                        arrayListUsers = ArrayList()
                        if (mSocket != null) { // && !mSocket?.connected()!!
                            val connectObject = JSONObject()
                            try {
                                connectObject.put(USERID, getUserObject(activity!!).getId()!!)
                                connectObject.put(CURRENT_SCREEN, screenName)
                                Log.d(
                                    "emitInScreenActivity",
                                    "emitInScreenActivity :" + connectObject.toString()
                                )
                                mSocket?.emit(IN_SCREEN_REQUEST, connectObject, Ack {
                                    var jsonArray = JSONArray()
                                    jsonArray = it.get(0) as JSONArray
                                    var jsonObject = JSONObject()
                                    Log.d(
                                        "emitInScreenActivity",
                                        "emitInScreenActivity :" + jsonArray.toString()
                                    )

                                    if (jsonArray.length() > 0) {
                                        for (i in 0..jsonArray.length() - 1) {
                                            jsonObject = jsonArray.getJSONObject(i)
                                            val onlineOfflineResponse = OnlineOfflineResponse()
                                            onlineOfflineResponse.userid =
                                                jsonObject.getString("userid")
                                            onlineOfflineResponse.firstName =
                                                jsonObject.getString("firstName")
                                            onlineOfflineResponse.type =
                                                jsonObject.getString("type")
                                            arrayListUsers.add(onlineOfflineResponse)
                                        }
                                        callBack?.friendOnline(removeDuplicacy())
                                    } else {
                                        callBack?.friendOnline(removeDuplicacy())
                                    }
                                })

                                // clear rejoin list
                                if (!screenName.equals(WAITING_SCREEN))
                                    arrayListUsers.clear()


                                Log.e(
                                    "SWAPLOGSOCKET",
                                    "emit socket user $IN_SCREEN_REQUEST in screen " + connectObject.toString()
                                )
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * emit best_match
     */
    fun emitBestMatches() {
        try {
            if (mSocket != null) {  // && mSocket?.connected()!!
                activity!!.runOnUiThread {
                    try {
                        arrayListBestMatch = ArrayList()
                        if (mSocket != null) { // && !mSocket?.connected()!!
                            val connectObject = JSONObject()
                            try {
                                connectObject.put(USERID, getUserObject(activity!!).getId()!!)
                                //  connectObject.put(USERID, "5e7f1c96445da7190e907aee")
                                Log.d(
                                    "emitBestMatches",
                                    "emitBestMatches :" + connectObject.toString()
                                )
                                mSocket?.emit(BEST_MATCH_REQUEST, connectObject, Ack {
                                    var jsonArray = JSONArray()
                                    jsonArray = it.get(0) as JSONArray
                                    var jsonObject = JSONObject()
                                    Log.d("emitBestMatches", "emitBestMatches :" + jsonArray.toString())

                                    if (jsonArray.length() > 0) {
                                        for (i in 0 until jsonArray.length()) {
                                            jsonObject = jsonArray.getJSONObject(i)
                                            val onlineOfflineResponse = OnlineOfflineResponse()
                                            onlineOfflineResponse.userid =
                                                jsonObject.getString("userid")
                                            onlineOfflineResponse.firstName =
                                                jsonObject.getString("firstName")
                                            onlineOfflineResponse.gender =
                                                jsonObject.getString("gender")
                                            onlineOfflineResponse.unreadCount =
                                                jsonObject.getInt("unreadCount")
                                            arrayListBestMatch.add(onlineOfflineResponse)
                                        }
                                        callBack?.bestMatches(removeDuplicacyBestMatch())
                                    }
                                })
                                Log.e(
                                    "SWAPLOGSOCKET",
                                    "emit socket user $IN_SCREEN_REQUEST in screen " + connectObject.toString()
                                )
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //Emit Best Match Refresh

    fun emitBestMatchRefresh() {
        try {
            if (mSocket != null) {  // && mSocket?.connected()!!
                activity!!.runOnUiThread {
                    try {
                        arrayListBestMatch = ArrayList()
                        if (mSocket != null) { // && !mSocket?.connected()!!
                            val connectObject = JSONObject()
                            try {
                                connectObject.put(USER_ID, getUserObject(activity!!).getId()!!)
                                mSocket?.emit(BEST_MATCH_REFRESH_REQUEST, connectObject, Ack {
                                    var jsonObject = JSONObject()
                                    jsonObject = it.get(0) as JSONObject
                                    val status = jsonObject.getString("status")
                                    // if (status.equals("success"))
                                    callBack?.onRefreshBestMatch(status)
                                    Log.d(
                                        "emitBestMatcheRefresh",
                                        "emitBestMatcheRefresh :" +connectObject.toString()+"  "+ jsonObject.toString()
                                    )

                                })
                            } catch (e: JSONException) {
                                Log.e("BestMatchRefresh", "BestMatchRefresh exception")
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("BestMatchRefresh", "BestMatchRefresh exception")
                    }
                }
                //clear users rejoin list
                arrayListUsers.clear()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("BestMatchRefresh", "BestMatchRefresh exception")
        }
    }


    // EMIT FOR RECALL IF CALL DROPPED
    fun emitCallRequestActivity(id: String) {
        val fromId = AppInstance.userObj?.getId()
        val toId = id
        try {
            if (mSocket != null) {  // && mSocket?.connected()!!
                activity!!.runOnUiThread {
                    try {
                        if (mSocket != null) { // && !mSocket?.connected()!!
                            val connectObject = JSONObject()
                            try {
                                connectObject.put(FROM_ID, fromId)
                                connectObject.put(TO_ID, toId)
                                mSocket?.emit(RECALL_REQUEST, connectObject, Ack {
                                    val ackData = it[0] as JSONObject
                                    Log.d(
                                        "emitCallRequestActivity",
                                        "emitCallRequestActivity :" + ackData.toString()
                                    )
                                    val status = ackData.getString("status")
                                    if (status.equals("success")) {
                                        callBack?.callReconnectIsUserOnline(true)
                                    } else {
                                        callBack?.callReconnectIsUserOnline(false)
                                    }
                                    //Log.e("SWAPLOGSOCKET", "ackData $ackData")
                                    //SUCCESS OR FALSE
                                })
                                Log.e("SWAPLOGSOCKET", "emit socket user $RECALL_REQUEST call")
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun emitMobileLog(event: String, event_description: String, type: String, comment: String) {
        val userId = AppInstance.userObj?.getId();
        val mobile_os = "Android";
        try {
            if (mSocket != null) {
                activity!!.runOnUiThread {
                    try {
                        if (mSocket != null) {
                            val connectObject = JSONObject()
                            try {
                                connectObject.put("mobile_os", mobile_os);
                                connectObject.put("user_id", userId);
                                connectObject.put("event", event);
                                connectObject.put("event_description", event_description);
                                connectObject.put("type", type);
                                connectObject.put("comment", comment);
                                mSocket?.emit(MOBILE_LOG, connectObject)
                                Log.e("MOBILE_LOG", event + "   " + connectObject.toString());
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun setCallBack(mActivity: Activity, mSocketCallBackListeners: SocketCallBackListeners) {
        this.callBack = mSocketCallBackListeners
        this.activity = mActivity
    }
    //--Socket connection
}