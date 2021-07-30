package shiddush.view.com.mmvsd.ui.chat

import CommonFunctions
import android.app.Activity
import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import okhttp3.MultipartBody
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.model.chat.ChatListResponse
import shiddush.view.com.mmvsd.model.chat.Messages
import shiddush.view.com.mmvsd.model.chat.SingleChatResponse
import shiddush.view.com.mmvsd.model.matchedQuestions.MatchedQuestionsResponse
import shiddush.view.com.mmvsd.model.matchedQuestions.QuestionsItem
import shiddush.view.com.mmvsd.utill.DateFunctions
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Sumit Kumar.
 */
class ChatViewModel : ViewModel() {

    lateinit var chatAdapter: ChatAdapter
    lateinit var matchedQuestionsAdapter: MatchedQuestionsAdapter
    var imageAttachmentUrl = ""
    var attachmentImageClick: MutableLiveData<Boolean> = MutableLiveData()
    var documentUrl = ""
    var documentType = ""
    var documentName = ""
    var newMessageClick: MutableLiveData<Boolean> = MutableLiveData()
    var viewDocumentClick: MutableLiveData<Boolean> = MutableLiveData()
    var downloadDocumentClick: MutableLiveData<Boolean> = MutableLiveData()
    var sendMessageClick: MutableLiveData<Boolean> = MutableLiveData()
    var attachmentClick: MutableLiveData<Boolean> = MutableLiveData()
    var backClick: MutableLiveData<Boolean> = MutableLiveData()
    var videoCallClick: MutableLiveData<Boolean> = MutableLiveData()
    var sendMessageObserver: MutableLiveData<Boolean> = MutableLiveData()
    var getMessageObserver: MutableLiveData<Boolean> = MutableLiveData()
    var getMatchedQuestionObserver: MutableLiveData<Boolean> = MutableLiveData()
    var message: ObservableField<String> = ObservableField()
    var singleChatList: MutableList<Messages>? = ArrayList()
    var matchedQuestionList: MutableList<QuestionsItem>? = ArrayList()
    var fileBody: MultipartBody.Part? = null


    fun getTimeFormatted(date: String): String {
        if (date.isEmpty()){
            return DateFunctions.getCurrentDate(DateFunctions.HH_mm)
        }
        val dateWithoutT = date.replace("T", " ")
        val dateWithoutZ = dateWithoutT.substring(0, dateWithoutT.length - 5)

        val df = SimpleDateFormat( DateFunctions.yyyy_MM_dd_hh_mm_ss, Locale.ENGLISH)
        df.setTimeZone(TimeZone.getTimeZone("UTC"))
        val date: Date = df.parse(dateWithoutZ)
        df.setTimeZone(TimeZone.getDefault())
        val formattedDate: String = df.format(date)

        return DateFunctions.getFormattedDate(
            DateFunctions.yyyy_MM_dd_hh_mm_ss,
            DateFunctions.HH_mm,
            formattedDate
        )
    }

    fun getDateFormatted(date: String): String {
        if (date.isEmpty()){
         //   val currentDate = DateFunctions.getCurrentDate(DateFunctions.yyyy_MM_dd)
            return "Today"
           // return ""
        }
        val dateWithoutT = date.replace("T", " ")
        val dateWithoutZ = dateWithoutT.substring(0, dateWithoutT.length - 5)
        val apiDate = DateFunctions.getFormattedDate(
            DateFunctions.yyyy_MM_dd_hh_mm_ss,
            DateFunctions.yyyy_MM_dd,
            dateWithoutZ
        )
        val currentDate = DateFunctions.getCurrentDate(DateFunctions.yyyy_MM_dd)

        if (apiDate == currentDate) {
            return "Today"
        }

        return DateFunctions.getFormattedDate(
            DateFunctions.yyyy_MM_dd_hh_mm_ss,
            DateFunctions.dd_MMM,
            dateWithoutZ
        )
    }

    fun attachmentImage(url: String) {
        imageAttachmentUrl = url
        attachmentImageClick.value = true
    }

    fun downloadDocument(url: String, type: String, name: String) {
        documentName = name
        documentUrl = url
        documentType = type
        downloadDocumentClick.value = true
    }

    fun viewDocument(url: String, type: String) {
        documentUrl = url
        documentType = type
        viewDocumentClick.value = true
    }

    fun attachment(view: View) {
        attachmentClick.value = true
    }

    fun onBackClick(view: View) {
        backClick.value = true

    }

    fun onVideoCallClick(view: View) {
        videoCallClick.value = true

    }

    fun sendMessageCallBack(data: SingleChatResponse) {
        val messages = Messages()
        messages.message = data.messages?.message
        messages.userid = data.messages?.userid
        messages.id = data.messages?.id
        messages.messageType = data.messages?.messageType
        messages.createdAt = getCurrentDate()
        messages.datetimeTz = getCurrentDate()
        singleChatList?.add(messages)
        sendMessageObserver.postValue(true)
    }

    fun chatListCallBack(chatResponse: ChatListResponse) {
        singleChatList = ArrayList()
        singleChatList?.addAll(0, chatResponse.response!!.messages!!)
        getMessageObserver.postValue(true)
    }
    fun matchedQuestionCallback(chatResponse: MatchedQuestionsResponse) {
        matchedQuestionList = ArrayList()
        matchedQuestionList?.addAll(chatResponse.questions!!)
        getMatchedQuestionObserver.postValue(true)
    }


    fun sendMessage(view: View) {
        if (message.get() == null || message.get().toString().isNullOrEmpty()) {
            CommonFunctions.showFeedbackMessage(
                view, view.context.getString(R.string.empty_message))
        } else
            sendMessageClick.value = true
    }

    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        Log.d("getCurrentDate",sdf.format(Date())+"Z")
        return sdf.format(Date())+"Z"
    }

    fun setUpChatAdapter(activity: Activity, recyclerView: RecyclerView) {
        recyclerView.isNestedScrollingEnabled = false
      //  recyclerView.setItemViewCacheSize(YourList.size());
        chatAdapter = ChatAdapter(activity, this)
        chatAdapter.setHasStableIds(true)
        recyclerView.adapter = chatAdapter
    }

    fun setupMatchQuestionAdapter(activity: Activity, recyclerView: RecyclerView) {
        matchedQuestionsAdapter = MatchedQuestionsAdapter(matchedQuestionList,activity )
        recyclerView.adapter = matchedQuestionsAdapter
    }


}
