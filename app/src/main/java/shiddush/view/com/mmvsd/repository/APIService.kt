package shiddush.view.com.mmvsd.repository

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import shiddush.view.com.mmvsd.model.MainModel
import shiddush.view.com.mmvsd.model.SingleChatResponse
import shiddush.view.com.mmvsd.model.chat.FileUploadResponse
import shiddush.view.com.mmvsd.model.forgotpassword.ForgotPasswordRequest
import shiddush.view.com.mmvsd.model.forgotpassword.ForgotPasswordResponse
import shiddush.view.com.mmvsd.model.login.LoginRequest
import shiddush.view.com.mmvsd.model.login.LoginResponse
import shiddush.view.com.mmvsd.model.radio.RadioListResponse
import shiddush.view.com.mmvsd.model.review.AddReviewRequest
import shiddush.view.com.mmvsd.model.review.AddReviewResponse
import shiddush.view.com.mmvsd.model.review.CheckReviewRequest
import shiddush.view.com.mmvsd.model.review.CheckReviewResponse
import shiddush.view.com.mmvsd.model.signup.SignUpRequest
import shiddush.view.com.mmvsd.model.signup.SignUpResponse
import shiddush.view.com.mmvsd.model.signupbiblequiz.*
import shiddush.view.com.mmvsd.model.sociallogin.SocialLoginRequest
import shiddush.view.com.mmvsd.model.sociallogin.SocialLoginResponse
import shiddush.view.com.mmvsd.model.subscription.CheckSubscriptionRequest
import shiddush.view.com.mmvsd.model.subscription.CheckSubscriptionResponse
import shiddush.view.com.mmvsd.model.subscription.SubscriptionRequest
import shiddush.view.com.mmvsd.model.timer.CountDownTimerResponse
import shiddush.view.com.mmvsd.model.videocall.*
import shiddush.view.com.mmvsd.model.waitingscreenmodels.TrainingTheAIPostRequest
import shiddush.view.com.mmvsd.model.waitingscreenmodels.TrainingTheAIResponse
import shiddush.view.com.mmvsd.model.waitingscreenmodels.YoutubeLinksResponse
import java.util.HashMap


interface APIService
{

    /**
     * Created by Sumit Kumar
     * @Base APIService interface :  This interface contain the all the methods
        of apis (Communicate to  servers with predefined parameters ).
     **/

    @POST(WebConstants.LOGIN_WS)
    fun login(@Body requestData: LoginRequest): Observable<LoginResponse>

    @POST(WebConstants.SOCIAL_LOGIN_WS)
    fun socialLogin(@Body requestData: SocialLoginRequest): Observable<SocialLoginResponse>

    @POST(WebConstants.FORGOT_PASS_WS)
    fun forgotPassword(@Body requestData: ForgotPasswordRequest): Observable<ForgotPasswordResponse>

    @POST(WebConstants.SIGNUP_WS)
    fun signUpWS(@Body requestData: SignUpRequest): Observable<SignUpResponse>

    @POST(WebConstants.GET_NORMAL_QUESTIONS_WS)
    fun getNormalQuestions(@Header("Authorization") token: String, @Body requestData: SignUpNormalQuizRequest): Observable<SignUpBibleQuizResponse>

    @POST(WebConstants.POST_NORMAL_QUESTIONS_WS)
    fun postNormalQuestions(@Header("Authorization") token: String, @Body requestData: SignUpNormalQuizPostRequest): Observable<MainModel>

    @POST(WebConstants.GET_BIBLE_QUESTIONS_WS)
    fun getBibleQuestions(@Header("Authorization") token: String, @Body requestData: SignUpBibleQuizGetRequest): Observable<SignUpBibleQuizResponse>

    @POST(WebConstants.POST_BIBLE_QUESTIONS_WS)
    fun postBibleQuestions(@Header("Authorization") token: String, @Body requestData: SignUpBibleQuizPostRequest): Observable<SignUpResponse>

    @GET(WebConstants.GET_YOUTUBE_LINKS_WS)
    fun getYoutubeLinks(): Observable<YoutubeLinksResponse>

    @POST(WebConstants.GET_TRAIN_THE_AI_QUES_WS)
    fun getTrainTheAIQuestions(@Header("Authorization") token: String, @Body requestData: CheckReviewRequest): Observable<TrainingTheAIResponse>

    @POST(WebConstants.POST_TRAIN_THE_AI_QUES_WS)
    fun addWaitingScreenQuestions(@Header("Authorization") token: String, @Body requestData: TrainingTheAIPostRequest): Observable<MainModel>

    @GET(WebConstants.GET_BACKGROUND_MUSIC_WS)
    fun getRadioMusicList(): Observable<RadioListResponse>

    @POST(WebConstants.PICK_RANDOM_MATCH_WS)
    fun pickRandomMatch(@Header("Authorization") token: String, @Body requestData: VideoCallGetRequest): Observable<VideoCallResponse>

    @POST(WebConstants.MATCH_USER_DETAIL_WS)
    fun getMatchUserDetails(@Header("Authorization") token: String, @Body requestData: MatchedUserDetailsRequest): Observable<MatchedUserDetailsResponse>

    //this api call 3 times from different places if there is no data of opentok
    @POST(WebConstants.RE_GENERATE_TOKEN_WS)
    fun reGenerateToken(@Header("Authorization") token: String, @Body requestData: MatchedUserDetailsRequest): Observable<ReGenerateTokenResponse>

    @POST(WebConstants.CHECK_FOR_REVIEW_WS)
    fun checkForReview(@Header("Authorization") token: String, @Body requestData: CheckReviewRequest): Observable<CheckReviewResponse>

    @POST(WebConstants.ADD_REVIEW_WS)
    fun addReviewWebServiceCall(@Header("Authorization") token: String, @Body requestData: AddReviewRequest): Observable<AddReviewResponse>

    @POST(WebConstants.MATCH_WHILE_REGISTRATION_WS)
    fun matchingWhileRegistration(@Header("Authorization") token: String, @Body requestData: CheckReviewRequest): Observable<MainModel>

    @POST(WebConstants.POST_TRIGGER_ALARM_WS)
    fun postTriggerAlarm(@Header("Authorization") token: String, @Body requestData: TriggerAlarmRequest): Observable<MainModel>

    @POST(WebConstants.REPORT_USER_WS)
    fun reportUser(@Header("Authorization") token: String, @Body requestData: ReportUserRequest): Observable<MainModel>

    @POST(WebConstants.END_CALL_WS)
    fun endCall(@Header("Authorization") token: String, @Body requestData: EndCallRequest): Observable<MainModel>

    @POST(WebConstants.BLOCKED_USER_WS)
    fun blockedUser(@Header("Authorization") token: String, @Body requestData: BlockUserRequest): Observable<MainModel>

    @POST(WebConstants.ARCHIVING_VIDEO_CALL_WS)
    fun archiveVideoCall(@Header("Authorization") token: String, @Body requestData: ArchiveVideoCallRequest): Observable<MainModel>

    @POST(WebConstants.CALL_DROP_AFTER_TWO_MIN_WS)
    fun callDropAfterTwoMinApi(@Header("Authorization") token: String, @Body requestData: CallDropAfterTwoMinRequest): Observable<MainModel>

    @POST(WebConstants.POST_LOGOUT_WS)
    fun logoutCall(@Header("Authorization") token: String, @Body requestData: CheckReviewRequest): Observable<MainModel>

    @POST(WebConstants.APP_SUBSCRIPTION_DONE_WS)
    fun subscriptionDone(@Header("Authorization") token: String, @Body requestData: SubscriptionRequest): Observable<LoginResponse>

    @GET(WebConstants.GET_TIMER_WS)
    fun getTimerCall(): Observable<CountDownTimerResponse>

    @POST(WebConstants.APP_SUBSCRIPTION_DETAIL_WS)
    fun appSubscriptionDetail(@Header("Authorization")
                              token: String, @Body requestData: CheckSubscriptionRequest)
    : Observable<CheckSubscriptionResponse>


    @Multipart
    @POST(WebConstants.UPLOAD_CHAT_IMAGE)
    fun sendChatMessage(
        @Part file: MultipartBody.Part
    ): Observable<FileUploadResponse>

}