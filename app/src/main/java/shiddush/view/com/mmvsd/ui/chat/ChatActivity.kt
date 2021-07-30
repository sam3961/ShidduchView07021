package shiddush.view.com.mmvsd.ui.chat

import CommonFunctions
import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.app.DownloadManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.smartlook.sdk.smartlook.Smartlook
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.FilePickerConst.REQUEST_CODE_PHOTO
import droidninja.filepicker.utils.ContentUriUtils
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.bottom_sheet_drop_call.*
import kotlinx.android.synthetic.main.dialog_reconnect_call.*
import kotlinx.android.synthetic.main.dialog_waiting_before_date.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import shiddush.view.com.mmvsd.MyApplication
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.ActivityChatBinding
import shiddush.view.com.mmvsd.model.chat.FileUploadResponse
import shiddush.view.com.mmvsd.model.chat.Messages
import shiddush.view.com.mmvsd.model.chat.ReceivedMessageResponse
import shiddush.view.com.mmvsd.model.onlineOffline.OnlineOfflineResponse
import shiddush.view.com.mmvsd.model.videocall.SocketCallResponse
import shiddush.view.com.mmvsd.repository.*
import shiddush.view.com.mmvsd.socket.SocketCallBackListeners
import shiddush.view.com.mmvsd.socket.SocketCommunication
import shiddush.view.com.mmvsd.ui.services.NetworkSchedulerService
import shiddush.view.com.mmvsd.ui.videocall.OpenTokConfig
import shiddush.view.com.mmvsd.ui.videocall.VideoCallActivity
import shiddush.view.com.mmvsd.ui.waitingscreen.WaitingActivity
import shiddush.view.com.mmvsd.utill.*
import shiddush.view.com.mmvsd.widget.loader.UtilLoader
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidAlertDialog
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidAnimation
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidColorConstants
import shiddush.view.com.mmvsd.widget.swapdroid.SwapdroidIcon
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity(), SocketCallBackListeners,
    NetworkSchedulerService.onNetworkConnectionListener {

    lateinit var binding: ActivityChatBinding
    private var attachmentDialog: BottomSheetDialog? = null
    lateinit var mSocketCallBackListeners: SocketCallBackListeners
    var friendId = ""
    var friendName = ""
    var rejoinType = ""
    var chatUserType = ""
    private var waitingTimeDuration: Long = 60000
    var isFirstTimeLoad = false
    private lateinit var viewModel: ChatViewModel
    private var photoPaths = ArrayList<Uri>()
    private var docPaths = ArrayList<Uri>()
    private lateinit var mUtilLoader: UtilLoader
    private lateinit var mBottomSheetCallBehaviour: BottomSheetBehavior<*>
    private var reconnectStatus = ""
    private var reconnectDialog: Dialog? = null
    private var dialogContacting: Dialog? = null
    private var waitingDialog: Dialog? = null
    private var timerCallReconnect: CountDownTimer? = null
    private var mediaPlayer: MediaPlayer? = null
    private var isInternetConnected: Boolean = false
    private var listOfflineMessages: ArrayList<Messages> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.purple_bar)
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        viewModel = ChatViewModel()
        binding.viewModel = viewModel
        NetworkSchedulerService.registerForNetworkService(this, this)
        isInternetConnected = IS_INTERNET_CONNECTED

        mUtilLoader = UtilLoader(this)
        friendId = intent.getStringExtra(REJOIN_USERID)!!
        friendName = intent.getStringExtra(REJOIN_USERNAME)!!
        rejoinType = intent.getStringExtra(REJOIN_TYPE)!!
        chatUserType = intent.getStringExtra(CHAT_USER_TYPE)!!

        textViewName.text = friendName
        initObserver()

        viewModel.setUpChatAdapter(this, rvChats)

        viewModel.setupMatchQuestionAdapter(this, rvMatchedQuestions)

        if (AppInstance.userObj?.getGender()!! == 1) //Female
            binding.ivThumbnail.setImageResource(R.drawable.ic_male)
        else
            binding.ivThumbnail.setImageResource(R.drawable.ic_female)

        bottomSheetDropCallView()

        try {
            //creating socket connection
            socketCommunication()

            if (!isInternetConnected) {
                viewModel.singleChatList = getChatData(this)
                viewModel.chatAdapter.customNotify(
                    viewModel.singleChatList,
                    viewModel.chatAdapter.itemCount!!
                )
                if (isFirstTimeLoad) {
                    isFirstTimeLoad = false
                    scrollView.post {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                    }
                }
                progressBar.visibility = View.GONE
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun socketCommunication() {
        try {

            //creating socket callback
            setOnSocketCallBackListener(this)
            //creating socket connection
            SocketCommunication.connectSocket(this@ChatActivity, mSocketCallBackListeners)
            if (SocketCommunication.isSocketConnected()) {
                isFirstTimeLoad = true
                SocketCommunication.emitInScreenActivity(WAITING_SCREEN)
                SocketCommunication.emitReceiveChatMessage(friendId, viewModel)
                SocketCommunication.emitReadChatMessage(friendId, viewModel)
                SocketCommunication.emitMatchingQuestions(friendId, viewModel)
            } else {
                setOnSocketCallBackListener(this)
                // SocketCommunication.connectSocket(this, mSocketCallBackListeners)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun initObserver() {

        MyApplication.mLiveAnyChatNotification.observe(
            this,
            Observer { uri ->
                if (!uri.isNullOrBlank()) {
                    viewModel.singleChatList = ArrayList()
                    SocketCommunication.emitReceiveChatMessage(friendId, viewModel)
                }
            })

        viewModel.sendMessageClick.observe(this, Observer {
            if (isInternetConnected) {
                progressBar.visibility = View.VISIBLE
                SocketCommunication.emitSendChatMessage(
                    friendId,
                    viewModel.message.get() + "",
                    MESSAGE_TYPE_TEXT, viewModel
                )
            } else {
                val messages: Messages? = Messages()
                messages?.id = friendId
                messages?.userid = getUserObject(this).getId()!!
                messages?.opentokSession = ""
                messages?.message = viewModel.message.get() + ""
                messages?.messageType = MESSAGE_TYPE_TEXT
                messages?.noInternet = true
                messages?.createdAt = viewModel.getCurrentDate()
                messages?.datetimeTz = viewModel.getCurrentDate()
                viewModel.singleChatList?.add(messages!!)

                saveChatData(this, viewModel.singleChatList!!)

                viewModel.chatAdapter.customNotify(
                    viewModel.singleChatList,
                    viewModel.chatAdapter.itemCount!!
                )

                scrollView.post {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                }
            }
            viewModel.message.set("")

        })

        viewModel.attachmentClick.observe(this, Observer {
            showBottomSheetDialog()
        })

        viewModel.viewDocumentClick.observe(this, Observer {
            // val browserIntent = Intent(Intent.ACTION_VIEW,Uri.parse(viewModel.documentUrl))
            val browserIntent = Intent(Intent.ACTION_VIEW)
            browserIntent.setDataAndType(
                Uri.parse(viewModel.documentUrl),
                viewModel.documentType
            )

            val chooser = Intent.createChooser(browserIntent, getString(R.string.chooser_title))
            chooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK // optional

            startActivity(chooser)
        })

        viewModel.downloadDocumentClick.observe(this, Observer {

            CommonFunctions.showFeedbackMessage(rootLayout, getString(R.string.downlaoding))

            val request: DownloadManager.Request =
                DownloadManager.Request(Uri.parse(viewModel.documentUrl))
            request.setTitle(viewModel.documentName)
            request.setDescription(resources.getString(R.string.app_name))
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                viewModel.documentName
            )
            val manager: DownloadManager =
                getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
        })

        viewModel.attachmentImageClick.observe(this, Observer {
            startActivity(
                Intent(this, FullScreenActivity::class.java)
                    .putExtra(MESSAGE_IMAGE_URL, viewModel.imageAttachmentUrl)
            )
        })
        viewModel.backClick.observe(this, Observer {
            //   startActivity(Intent(this, WaitingActivity::class.java))
            setResult(Activity.RESULT_OK)
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)    // for close
        })
        viewModel.videoCallClick.observe(this, Observer {
            bottomSideDropCallData(friendName, friendId, rejoinType)
            mBottomSheetCallBehaviour.state = BottomSheetBehavior.STATE_EXPANDED

        })

        viewModel.sendMessageObserver.observe(this, Observer {
            progressBar.visibility = View.GONE
            viewModel.chatAdapter.customNotify(
                viewModel.singleChatList,
                viewModel.chatAdapter.itemCount!!
            )
            // saveChatData(this, viewModel.singleChatList!!)

            scrollView.post {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                edMessage.isFocusableInTouchMode = true
                edMessage.isFocusable = true
                edMessage.requestFocus()
            }

//            Timer().schedule(500) {
//                scrollView.scrollToBottom()
//            }

//            rvChats.postDelayed(
//                { rvChats.smoothScrollToPosition(rvChats.adapter?.itemCount!!- 1) },
//                500
//            )
        })
        viewModel.getMatchedQuestionObserver.observe(this, Observer {
            binding.skLoader.visibility = View.GONE
            viewModel.matchedQuestionsAdapter.customNotify(
                viewModel.matchedQuestionList!!
            )

        })
        viewModel.getMessageObserver.observe(this, Observer {
            progressBar.visibility = View.GONE
            viewModel.chatAdapter.customNotify(ArrayList(), 0)
            viewModel.chatAdapter.customNotify(
                viewModel.singleChatList,
                viewModel.chatAdapter.itemCount!!
            )

            saveChatData(this, viewModel.singleChatList!!)

            if (isFirstTimeLoad) {
                isFirstTimeLoad = false
//                rvChats.postDelayed(
//                    { rvChats.smoothScrollToPosition(rvChats.adapter?.itemCount!!- 1) },
//                    500
//                )

//                Timer().schedule(500) {
//                    scrollView.scrollToBottom()
//                }

                scrollView.post {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                }

            }
        })

        KeyboardVisibilityEvent.setEventListener(this)
        { isOpen ->
            if (isOpen) {
                scrollView.post {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                    edMessage.isFocusableInTouchMode = true
                    edMessage.isFocusable = true
                    edMessage.requestFocus()
                }
            }
        }

    }


    private fun setOnSocketCallBackListener(mSocketCallBackListeners: SocketCallBackListeners) {
        this.mSocketCallBackListeners = mSocketCallBackListeners
    }


    private fun showBottomSheetDialog() {
        val layoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.bottomsheet_attachment_dialog, null)
        attachmentDialog = BottomSheetDialog(this, R.style.SheetDialog)
        attachmentDialog?.setContentView(view)
        attachmentDialog?.window?.findViewById<View>(R.id.design_bottom_sheet)
            ?.setBackgroundColor(resources.getColor(android.R.color.transparent))

        val textViewGallery =
            attachmentDialog!!.findViewById<TextView>(R.id.textViewPhotoLibrary)
        val textViewDocument = attachmentDialog!!.findViewById<TextView>(R.id.textViewDocument)
        val textViewCancel = attachmentDialog!!.findViewById<TextView>(R.id.textViewCancel)


        textViewDocument?.setOnClickListener {
            attachmentDialog!!.dismiss()

            val permission = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

            if (checkPermission(permission) > 0) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission[0], permission[1]),
                    REQUEST_CODE_STORAGE_PERMISSIONS
                )
            } else {
                FilePickerBuilder.instance
                    .setMaxCount(1) //optional
                    // .setActivityTheme(R.style.AppTheme) //optional
                    .pickFile(this)
            }

        }
        textViewGallery?.setOnClickListener {
            attachmentDialog!!.dismiss()
            val permission = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )

            if (checkPermission(permission) > 0) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission[0], permission[1]),
                    REQUEST_CODE_CAMERA_STORAGE_PERMISSIONS
                )
            } else {
                FilePickerBuilder.instance
                    .setMaxCount(1) //optional
                    // .setActivityTheme(R.style.AppTheme) //optional
                    .pickPhoto(this, REQUEST_CODE_PHOTO)
            }
        }
        textViewCancel?.setOnClickListener {
            attachmentDialog!!.dismiss()
        }

        attachmentDialog?.show()
        val lp = WindowManager.LayoutParams()
        val window = attachmentDialog?.window
        lp.copyFrom(window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = lp
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_PHOTO -> if (resultCode == RESULT_OK && data != null) {
                photoPaths = ArrayList()
                photoPaths.addAll(data.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_MEDIA)!!)
                val filePath = ContentUriUtils.getFilePath(this, photoPaths.get(0))
                val file = File(filePath)
                val mediaType =
                    "application/" + filePath!!.substring(filePath.lastIndexOf('.') + 1).trim()

                val requestFile = RequestBody.create(
                    mediaType.toMediaTypeOrNull(),
                    file
                )
                viewModel.fileBody = MultipartBody.Part.createFormData(
                    "file",
                    file.name,
                    requestFile
                )
                uploadDocImageServiceCall(MESSAGE_TYPE_IMAGE)
            }
            FilePickerConst.REQUEST_CODE_DOC -> if (resultCode == RESULT_OK && data != null) {
                docPaths = ArrayList()
                docPaths.addAll(data.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_DOCS)!!)

                val filePath = ContentUriUtils.getFilePath(this, docPaths.get(0));
                val file = File(filePath)
                val mediaType =
                    "application/" + filePath!!.substring(filePath.lastIndexOf('.') + 1).trim()
                val requestFile = RequestBody.create(
                    //getMimeType(file.getAbsolutePath())!!.toMediaTypeOrNull(),
                    mediaType.toMediaTypeOrNull(),
                    file
                )
                viewModel.fileBody = MultipartBody.Part.createFormData(
                    "file",
                    file.name,
                    requestFile
                )
                uploadDocImageServiceCall(MESSAGE_TYPE_DOCUMENT)
            }
        }
    }


    private fun uploadDocImageServiceCall(messageType: String) {
        try {
            if (isNetworkAvailable(this)) {
                mUtilLoader.startLoader(this)
                //  val token = AppInstance.userObj!!.getToken()!!
                val manager = NetworkManager()
                manager.createApiRequest(ApiUtilities.getAPIService()
                    .sendChatMessage(viewModel.fileBody!!), object :
                    ServiceListener<FileUploadResponse> {
                    override fun getServerResponse(
                        response: FileUploadResponse,
                        requestcode: Int
                    ) {
                        try {

                            SocketCommunication.emitSendChatMessage(
                                friendId,
                                response.data?.filename!!, messageType, viewModel
                            )
                            mUtilLoader.stopLoader()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun getError(error: ErrorModel, requestcode: Int) {
                        Log.e("SWAPLOG", error.error_message)
                        mUtilLoader.stopLoader()
                    }
                })
            } else {
                showDialogNoInternet(
                    this,
                    getString(R.string.ooops),
                    getString(R.string.check_internet),
                    R.drawable.ic_nointernet_icon
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mUtilLoader.stopLoader()
            Log.e("SWAPLOG", e.message!!)
            showDialogNoInternet(
                this@ChatActivity,
                getString(R.string.failure_response), "", R.drawable.ic_alert_icon
            )
        }
    }


    fun checkPermission(permission: Array<String>): Int {
        var permissionNeeded = 0
        if (Build.VERSION.SDK_INT >= 23) {
            for (i in permission.indices) {
                val result = ContextCompat.checkSelfPermission(this, permission[i])
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionNeeded++
                }
            }
        }
        return permissionNeeded
    }

    fun getMimeType(url: String?): String? {
        var type: String? = null
        val extension: String = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    override fun onMatchedResponse(data: SocketCallResponse) {
    }

    override fun onUserInfoResponse(data: SocketCallResponse) {
    }

    override fun onAcceptRejectResponse(data: SocketCallResponse) {
    }

    override fun onSocketConnected() {
        //  Toast.makeText(this, "Socket Connected 529", Toast.LENGTH_SHORT).show()
        listOfflineMessages = ArrayList()
        for (i in 0 until viewModel.singleChatList?.size!!) {
            if (viewModel.singleChatList!![i].noInternet)
                listOfflineMessages.add(viewModel.singleChatList!![i])
        }

        for (i in 0 until listOfflineMessages.size) {
            Handler(Looper.getMainLooper()).postDelayed({
                SocketCommunication.emitSendChatMessage(
                    friendId,
                    listOfflineMessages[i].message + "",
                    listOfflineMessages[i].messageType + "",
                    viewModel
                )
            }, 5000)
        }
        isFirstTimeLoad = true
        viewModel.singleChatList = ArrayList()
        viewModel.chatAdapter.customNotify(viewModel.singleChatList, 0)
        SocketCommunication.emitReceiveChatMessage(friendId, viewModel)
        SocketCommunication.emitReadChatMessage(friendId, viewModel)
    }

    override fun onSocketDisconnected() {
    }

    override fun onRefreshBestMatch(status: String) {

    }

    override fun callAccepted(
        status: Boolean,
        tokboxToken: String,
        time: Long,
        matchFound: Boolean,
        count: Int,
        fromsocketid: String,
        toSocketId: String,
        toUserSessionId: String,
        fromId: String,
        toId: String,
        fromUserSessionId: String,
        tokboxApi: String,
        rejoin: Boolean
    ) {
        runOnUiThread(Runnable {
            if (status && reconnectStatus == "accept") {
                navigateToCall(
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
                //show Dialog
                if (friendName.isEmpty())
                    friendName = "User"
                if (dialogContacting != null && dialogContacting!!.isShowing)
                    dialogContacting?.dismiss()

                showDialogUserNotRespond(friendName + " " + getString(R.string.is_currently_unavailable))
            }
        })

    }

    override fun callReconnectIsUserOnline(status: Boolean) {
        runOnUiThread {
            if (status) {
                showReconnectWaitingDialog()
                callDropReconnectTimer()
            } else {
                if (reconnectDialog != null && reconnectDialog!!.isShowing) {
                    reconnectDialog!!.dismiss()
                }
                // clearAnimation()
                //     clearReconnectTimer()
                callDropReconnectTimer()
                showDialogContactingUser(
                    "Contacting $friendName...",
                    getString(R.string.please_wait)
                )
            }
        }
    }

    override fun callDropWantToConnect(
        userId: String,
        friendId: String,
        firstName: String,
        friendFirstName: String,
        type: String
    ) {
        runOnUiThread {
            playRejoinSound()
            showCallReconnectDialog(userId, friendId, friendName, type)
        }

    }

    override fun connectedToCall(
        status: Boolean,
        tokboxToken: String,
        time: Long,
        matchFound: Boolean,
        count: Int,
        fromsocketid: String,
        toSocketId: String,
        toUserSessionId: String,
        fromId: String,
        toId: String,
        fromUserSessionId: String,
        tokboxApi: String,
        rejoin: Boolean
    ) {
        runOnUiThread(Runnable {
            if (status && reconnectStatus.equals("accept")) {
                navigateToCall(
                    tokboxToken, time, matchFound, count,
                    fromsocketid, toSocketId, toUserSessionId, fromId, toId,
                    fromUserSessionId, tokboxApi, rejoin
                )
            } else {
                waitingDialog?.dismiss()
                clearReconnectTimer()
                //  clearAnimation()
                // clearCallData(applicationContext)
            }
        })

    }

    override fun friendOnline(arrayListUsers: MutableList<OnlineOfflineResponse>) {
    }

    override fun friendOffline(arrayListUsers: MutableList<OnlineOfflineResponse>) {
    }

    override fun bestMatches(arrayListUsers: MutableList<OnlineOfflineResponse>) {

    }

    override fun onNotifyEndCall(data: JSONObject) {
    }

    override fun onChatMessageReceive(data: ReceivedMessageResponse) {
        val messages = Messages()
        messages.message = data.message
        messages.userid = data.userid
        messages.id = data.friend
        messages.messageType = data.messageType
        messages.createdAt = viewModel.getCurrentDate()
        messages.datetimeTz = viewModel.getCurrentDate()
        viewModel.singleChatList?.add(messages!!)
        viewModel.chatAdapter.customNotify(
            viewModel.singleChatList,
            viewModel.chatAdapter.itemCount!!
        )
//        rvChats.postDelayed(
//            { rvChats.smoothScrollToPosition(rvChats.adapter?.itemCount!!- 1) },
//            500
//        )

//        Timer().schedule(500) {
//            scrollView.scrollToBottom()
//        }
        scrollView.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }


    }

    private fun navigateToCall(
        tokboxToken: String, time: Long, matchFound: Boolean, count: Int,
        fromsocketid: String, toSocketId: String, toUserSessionId: String, fromId: String,
        toId: String, fromUserSessionId: String, tokboxApi: String, rejoin: Boolean
    ) {

        try {
            Smartlook.stopRecording()
            //timer
            OpenTokConfig.API_KEY = tokboxApi
            OpenTokConfig.MATCHED_USER_NAME = friendName
            OpenTokConfig.SESSION_ID = fromUserSessionId
            OpenTokConfig.TOKEN = tokboxToken

            //   viewModel.setPauseCommunicator(true)
            val mIntent = Intent(this, VideoCallActivity::class.java)
            mIntent.putExtra(FROMID, fromId)
            mIntent.putExtra(TOID, toId)
            mIntent.putExtra(COUNT, count)
            mIntent.putExtra(CALLDURATION, time)
            mIntent.putExtra(FROMUSER_SOCKET_ID, fromsocketid)
            mIntent.putExtra(TOUSER_SOCKET_ID, toSocketId)
            mIntent.putExtra(REJOIN, rejoin)
            startActivity(mIntent)
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // fo
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_CAMERA_STORAGE_PERMISSIONS -> {
                if (grantResults.size > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        FilePickerBuilder.instance
                            .setMaxCount(1) //optional
                            //.setActivityTheme(R.style.AppTheme) //optional
                            .pickPhoto(this, REQUEST_CODE_PHOTO)
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED) {
                        alertDialogWithOKButton(
                            getString(R.string.permission),
                            getString(R.string.storage_camera_permission)
                        )
                    } else {
                        if (Build.VERSION.SDK_INT >= 23) {
                            val showRationale =
                                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            val showRationale2 =
                                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                            if (!showRationale) {
                                alertDialogWithOKButton(
                                    getString(R.string.permission),
                                    getString(R.string.storage_camera_permission)
                                )
                            } else if (!showRationale2) {
                                alertDialogWithOKButton(
                                    getString(R.string.permission),
                                    getString(R.string.storage_camera_permission)
                                )
                            }
                        }
                    }
                }
            }
            REQUEST_CODE_STORAGE_PERMISSIONS -> {
                if (grantResults.size > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        FilePickerBuilder.instance
                            .setMaxCount(1) //optional
                            //      .setActivityTheme(R.style.AppTheme) //optional
                            .pickFile(this)
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED) {
                        alertDialogWithOKButton(
                            getString(R.string.permission),
                            getString(R.string.storage_permission)
                        )
                    } else {
                        if (Build.VERSION.SDK_INT >= 23) {
                            val showRationale =
                                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            val showRationale2 =
                                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                            if (!showRationale) {
                                alertDialogWithOKButton(
                                    getString(R.string.permission),
                                    getString(R.string.storage_permission)
                                )
                            } else if (!showRationale2) {
                                alertDialogWithOKButton(
                                    getString(R.string.permission),
                                    getString(R.string.storage_camera_permission)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun alertDialogWithOKButton(title: String, message: String) {
        val builder = AlertDialog.Builder(this!!)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(getString(R.string.ok)) { dialogInterface, i ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", this!!.packageName, null)
            intent.data = uri
            startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
            dialogInterface.dismiss()
        }
        builder.show()
    }

    fun NestedScrollView.scrollToBottom() {
        val lastChild = getChildAt(childCount - 1)
        val bottom = lastChild.bottom + paddingBottom
        val delta = bottom - (scrollY + height)
        smoothScrollBy(0, delta)
    }

    private fun bottomSheetDropCallView() {
        mBottomSheetCallBehaviour = BottomSheetBehavior.from(dropCallView)
        mBottomSheetCallBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun bottomSideDropCallData(userName: String, id: String, type: String) {
        if (AppInstance.userObj?.getGender()!! == 1) { //Female
            ivUserImage.setImageResource(R.drawable.ic_male)
            ivGenderSign.setImageResource(R.drawable.ic_male_sign)
        } else {
            ivUserImage.setImageResource(R.drawable.ic_female)
            ivGenderSign.setImageResource(R.drawable.ic_female_sign)
        }

        // if (type.equals("rejoin")) {
        if (chatUserType.equals(REJOIN)) {
            textViewTitle.text = resources.getString(R.string.rejoin_call_now)
            tvRejoinMessage.text = resources.getString(R.string.went_wrong_with_previous_call)
        } else {
            textViewTitle.visibility = View.GONE
            textViewTitle.text = resources.getString(R.string.both_mutual_yes)
            tvRejoinMessage.text =
                "You can choose to go out at anytime with " + userName + "\nWould you like to go out now?"
        }

        tvUsername.setText(userName)

        positiveButton.setOnClickListener {
            reconnectStatus = "accept"
            //   SocketCommunication.emitCallRequestActivity(id)
            SocketCommunication.emitRejoinRequest(id, userName, type)
            mBottomSheetCallBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED

        }

        backView.setOnClickListener {
            mBottomSheetCallBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED

        }
        negativeButton.setOnClickListener {
            mBottomSheetCallBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED

        }

        imageViewClose.setOnClickListener {
            //binding.dropCallButton.visibility = View.GONE
            //binding.imageViewRightClick.visibility = View.GONE
            //clearAnimation()
            //clearCallData(applicationContext)
            mBottomSheetCallBehaviour!!.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun showReconnectWaitingDialog() {
        try {
            waitingDialog = Dialog(this, R.style.myDialog)
            waitingDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            try {
                waitingDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                waitingDialog?.window!!.setLayout(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            waitingDialog?.setCancelable(false)
            waitingDialog?.setContentView(R.layout.dialog_waiting_before_date)

            waitingDialog?.textViewMessage?.text =
                getString(R.string.date_reconnect_momentarily)

            val oa1 = ObjectAnimator.ofFloat(waitingDialog?.imageViewLogo, "scaleX", 1f, 0f);
            val oa2 = ObjectAnimator.ofFloat(waitingDialog?.imageViewLogo, "scaleX", 0f, 1f);

            oa1.duration = 1500
            oa2.duration = 1500

            oa1.repeatMode = ValueAnimator.REVERSE
            oa2.repeatMode = ValueAnimator.REVERSE

            oa1.repeatCount = ValueAnimator.INFINITE
            oa2.repeatCount = ValueAnimator.INFINITE

            oa1.interpolator = DecelerateInterpolator()
            oa2.interpolator = AccelerateDecelerateInterpolator()
            oa1.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    waitingDialog?.imageViewLogo?.setImageResource(R.drawable.group)
                    oa2.start()
                }
            })

            oa1.start()

            waitingDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callDropReconnectTimer() {
        timerCallReconnect = object : CountDownTimer(waitingTimeDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                if (waitingDialog != null && waitingDialog!!.isShowing)
                    waitingDialog?.dismiss()
                if (dialogContacting != null && dialogContacting!!.isShowing)
                    dialogContacting?.dismiss()
                //    showDialogUserNotRespond(matcheUserName + " " + getString(R.string.not_respond_to_call))
                showDialogUserNotRespond(friendName + " " + getString(R.string.not_respond_to_call))
                //clearCallData(applicationContext)
            }
        }
        timerCallReconnect?.start()
    }


    private fun showDialogContactingUser(title: String, message: String) {
        try {
            dialogContacting = Dialog(this, R.style.myDialog)
            dialogContacting!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            try {
                dialogContacting!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                dialogContacting!!.window!!.setLayout(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            dialogContacting!!.setCancelable(false)
            dialogContacting!!.setContentView(R.layout.dialog_contacting_rejoin)

            //getting resources
            val textViewTitle =
                dialogContacting!!.findViewById<View>(R.id.titleContacting) as TextView
            val cancel_icon_click =
                dialogContacting!!.findViewById<View>(R.id.closeContacting) as ImageView
            val imageView = dialogContacting!!.findViewById<View>(R.id.icon) as ImageView

            Glide.with(this).load(R.drawable.video_call).into(imageView)

            textViewTitle.setText(title)

            cancel_icon_click.setOnClickListener {
                clearReconnectTimer()
                dialogContacting!!.dismiss()
            }

            //dialog show
            dialogContacting!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun showCallReconnectDialog(
        userId: String,
        friendId: String,
        firstName: String,
        type: String
    ) {
        try {
            reconnectDialog = Dialog(this, R.style.myDialog)
            reconnectDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            try {
                reconnectDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                reconnectDialog!!.window!!.setLayout(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            reconnectDialog!!.setCancelable(false)
            reconnectDialog!!.setContentView(R.layout.dialog_reconnect_call)

            val imageView = reconnectDialog!!.findViewById<ImageView>(R.id.icon)

            var matchedUserName = "User"

            if (!firstName.isEmpty())
                matchedUserName = firstName

            reconnectDialog!!.textTitle.text =
                matchedUserName + " would like to have a Video Date Now."
            reconnectDialog!!.tvMessage.text =
                "Would you like to Accept and have a Video Date Now?"

            Glide.with(this).load(R.drawable.video_call).into(imageView);

            reconnectDialog!!.acceptCall.setOnClickListener({
                reconnectStatus = "accept"
                SocketCommunication.emitRejoinResponse(
                    friendId!!,
                    userId,
                    firstName,
                    reconnectStatus
                )
                reconnectDialog!!.dismiss()
                stopRejoinSound()
            })
            reconnectDialog!!.currentlyUnavail.setOnClickListener(View.OnClickListener {
                reconnectStatus = "later"
                SocketCommunication.emitRejoinResponse(
                    friendId!!,
                    userId,
                    firstName,
                    reconnectStatus
                )
                reconnectDialog!!.dismiss()
                stopRejoinSound()
            })
            reconnectDialog!!.closeCallDialog.setOnClickListener(View.OnClickListener {
                //clearCallData(applicationContext)
                reconnectStatus = "later"
                SocketCommunication.emitRejoinResponse(
                    friendId!!,
                    userId,
                    firstName,
                    reconnectStatus
                )
                reconnectDialog!!.dismiss()
                stopRejoinSound()
            })
            reconnectDialog!!.permanentRejectBtn.setOnClickListener(View.OnClickListener {
                //clearCallData(applicationContext)
                reconnectStatus = "reject"
                reconnectDialog!!.dismiss()
                dialogRemoveUserFromRejoin(friendId, type, matchedUserName, false)
                stopRejoinSound()
            })

            reconnectDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun showDialogUserNotRespond(message: String) {
        try {
            SwapdroidAlertDialog.Builder(this)
                .setTitle(getString(R.string.ooops))
                .setMessage(message)
                .setBackgroundColor(Color.parseColor(SwapdroidColorConstants.BACK_COLOR))
                .setNegativeBtnText(getString(R.string.no))
                .isNegativeVisible(false)
                .setPositiveBtnBackground(Color.parseColor(SwapdroidColorConstants.POSITIVE_COLOR))
                .setPositiveBtnText(getString(R.string.ok))
                .isPositiveVisible(true)
                .setNegativeBtnBackground(Color.parseColor(SwapdroidColorConstants.NEGATIVE_COLOR))
                .setAnimation(SwapdroidAnimation.POP)
                .isCancellable(false)
                .showCancelIcon(false)
                .setIcon(
                    R.drawable.ic_error_icon,
                    SwapdroidIcon.Visible
                )  //ic_star_border_black_24dp
                .OnPositiveClicked {
                    waitingDialog?.dismiss()
                    clearReconnectTimer()
                }
                .build()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun dialogRemoveUserFromRejoin(
        toId: String,
        rejoinType: String,
        matchingUserName: String,
        userClicked: Boolean
    ) {
        try {
            var matchedUserName = matchingUserName
            if (matchingUserName.contains(" "))
                matchedUserName = matchingUserName.split(" ")[0]

            val dialog = Dialog(this, R.style.myDialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            try {
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                dialog.window!!.setLayout(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            dialog.setCancelable(false)
            dialog.setContentView(R.layout.report_and_end_call_layout)

            //getting resources
            val textViewTitle = dialog.findViewById<View>(R.id.textViewtitle) as TextView
            val cancel_icon_click =
                dialog.findViewById<View>(R.id.cancel_icon_click) as ImageView
            val editTextMessage = dialog.findViewById<View>(R.id.editTextmessage) as EditText
            val textViewPositiveBtn =
                dialog.findViewById<View>(R.id.textViewPositiveBtn) as TextView

            if (rejoinType.equals("rejoin")) {
                textViewTitle.text =
                    "Clicking this will PERMANENTLY REMOVE " + matchedUserName + " from " +
                            "your \"Rejoin\" list as a match, You will NOT be able to go out again with " +
                            "" + matchedUserName + " on video on another day/time...Are you sure you want to proceed?";
            } else {
                textViewTitle.text =
                    "Clicking this will PERMANENTLY REMOVE " + matchedUserName + " from " +
                            "your \"Previous Date/s\" list as a match, You will NOT be able to go out again with " +
                            "" + matchedUserName + " on video on another day/time...Are you sure you want to proceed?"
            }
            val fromId = AppInstance.userObj?.getId()

            textViewPositiveBtn.setOnClickListener {
                val reasonRemove = editTextMessage.text.toString()
                if (reasonRemove.isEmpty()) {
                    showDialogNoInternet(
                        this,
                        getString(R.string.please_enter_reason),
                        "",
                        R.drawable.ic_alert_icon
                    )
                } else {
                    SocketCommunication.emitRemoveUserFromRejoin(fromId!!, toId, reasonRemove)
                    dialog.dismiss()
                    hideSoftKeyboard(this, editTextMessage)

                    Handler(Looper.getMainLooper()).postDelayed({
                        SocketCommunication.emitInScreenActivity(WAITING_SCREEN)
                    }, 3000)

                }
            }
            cancel_icon_click.setOnClickListener {
                dialog.dismiss()
                hideSoftKeyboard(this, editTextMessage)
                if (!userClicked)
                    SocketCommunication.emitRejoinResponse(
                        toId!!,
                        fromId!!,
                        matchedUserName,
                        reconnectStatus
                    )
            }

            //dialog show
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun playRejoinSound() {
        stopRejoinSound()
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.mutualring)
            mediaPlayer?.isLooping = true
        }
        mediaPlayer?.start()
    }

    private fun stopRejoinSound() {
        if (mediaPlayer != null && mediaPlayer?.isPlaying!!) {
            mediaPlayer?.stop()
            mediaPlayer = null
        }
    }


    private fun clearReconnectTimer() {
        if (timerCallReconnect != null) {
            timerCallReconnect!!.cancel()
            timerCallReconnect = null
        }
    }


    override fun onBackPressed() {
        //   startActivity(Intent(this, WaitingActivity::class.java))
        setResult(Activity.RESULT_OK)
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)    // for close
    }

    override fun onDestroy() {
        super.onDestroy()
        clearReconnectTimer()
        saveChatData(this, viewModel.singleChatList!!)
    }

    override fun onNetworkListener(isConnected: Boolean) {
        if (!isConnected)
            saveChatData(this, viewModel.singleChatList!!)
        isInternetConnected = isConnected

/*
        Handler(Looper.getMainLooper()).postDelayed({
            if (isConnected) {
                for (i in 0 until viewModel.singleChatList?.size!!) {
                    if (viewModel.singleChatList!![i].noInternet) {

                        SocketCommunication.emitSendChatMessage(
                            friendId,
                            viewModel.singleChatList!![i].message + "",
                            viewModel.singleChatList!![i].messageType + "",
                            viewModel
                        )
                    }
                }
//                if (SocketCommunication.isSocketConnected()) {
//                    isFirstTimeLoad = true
//                    SocketCommunication.emitInScreenActivity(WAITING_SCREEN)
//                    SocketCommunication.emitReceiveChatMessage(friendId, viewModel)
//                    viewModel.singleChatList = ArrayList()
//                    Toast.makeText(this, "Socket Connected 1242", Toast.LENGTH_SHORT).show()
//                }

            }
        }, 3000)
*/
    }

}
