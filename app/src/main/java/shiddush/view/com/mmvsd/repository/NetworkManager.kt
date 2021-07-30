package shiddush.view.com.mmvsd.repository

import android.app.Dialog

import android.net.ParseException
import android.util.Log
import com.google.gson.stream.MalformedJsonException
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException


/**
 * Created by Sumit Kumar.
 *
 * This class contain  methods for registration the observables for  handling the api responses
 * */


class NetworkManager() {
    // var context: Context= mcontext
    protected var compositeDisposable: Disposable? = null
    val TAG = shiddush.view.com.mmvsd.repository.NetworkManager::class.java.simpleName
    private var isProgress = true
    private var message = ""
    internal var mDialog: Dialog? = null

    /**
     * Constructor
     *
     * @param contextObj                 The Context from where the method is called
     * @param successRedirectionListener The listener interface for receiving action events
     * @return none
     */


    fun <V> createApiRequest(observables: Observable<V>, callBack: shiddush.view.com.mmvsd.repository.ServiceListener<V>) {
        // progressShow(context, message)
        compositeDisposable = observables
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<V>() {
                    override fun onNext(@io.reactivex.annotations.NonNull s: V) {

                        println(s);
                        callBack.getServerResponse(s, 0)
                    }

                    override fun onError(@io.reactivex.annotations.NonNull e: Throwable) {
                        callBack.getError(setUpErrors(e), 0)

                    }

                    override fun onComplete() {}
                })
    }


    fun showProgress(isProgress: Boolean) {
        this.isProgress = isProgress
    }

    fun progressValue(message: String) {
        isProgress = true
        this.message = message
    }


    // Handling Java Exceptions
    private fun setUpErrors(t: Throwable): shiddush.view.com.mmvsd.repository.ErrorModel {
        Log.e(TAG, "setUpError statusCode: " + "statusCode " + t.message)

        val errorModel = shiddush.view.com.mmvsd.repository.ErrorModel()
        try {
            // Exception comes by Java
            if (t is SocketTimeoutException) {
                errorModel.error_code = shiddush.view.com.mmvsd.repository.ResponseCodes.Companion.INTERNET_NOT_AVAILABLE
                errorModel.error_message = shiddush.view.com.mmvsd.repository.ResponseCodes.Companion.logErrorMessage(errorModel.error_code)
            } else if (t is TimeoutException) {
                errorModel.error_code = shiddush.view.com.mmvsd.repository.ResponseCodes.Companion.URL_CONNECTION_ERROR
                errorModel.error_message = shiddush.view.com.mmvsd.repository.ResponseCodes.Companion.logErrorMessage(errorModel.error_code)
            } else if (t is ClassCastException) {
                errorModel.error_code = shiddush.view.com.mmvsd.repository.ResponseCodes.Companion.MODEL_TYPE_CAST_EXCEPTION
                errorModel.error_message = shiddush.view.com.mmvsd.repository.ResponseCodes.Companion.logErrorMessage(errorModel.error_code)
            } else if (t is MalformedJsonException) {
                errorModel.error_code = shiddush.view.com.mmvsd.repository.ResponseCodes.Companion.MODEL_TYPE_CAST_EXCEPTION
                errorModel.error_message = shiddush.view.com.mmvsd.repository.ResponseCodes.Companion.logErrorMessage(errorModel.error_code)
            } else if (t is ParseException) {
                errorModel.error_code = shiddush.view.com.mmvsd.repository.ResponseCodes.Companion.MODEL_TYPE_CAST_EXCEPTION
                errorModel.error_message = shiddush.view.com.mmvsd.repository.ResponseCodes.Companion.logErrorMessage(errorModel.error_code)
            } else if (t is UnknownHostException) {
                errorModel.error_code = shiddush.view.com.mmvsd.repository.ResponseCodes.Companion.INTERNET_NOT_AVAILABLE
                errorModel.error_message = shiddush.view.com.mmvsd.repository.ResponseCodes.Companion.logErrorMessage(errorModel.error_code)
            } else {
                val errorMessage = (t as HttpException).response()?.errorBody()!!.string()
                val responseCode = t.response()?.code()
                errorModel.error_code = responseCode!!
                errorModel.error_message = errorMessage
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            errorModel.error_code = shiddush.view.com.mmvsd.repository.ResponseCodes.Companion.UNKNOWN_ERROR
            errorModel.error_message = shiddush.view.com.mmvsd.repository.ResponseCodes.Companion.logErrorMessage(errorModel.error_code)
        } finally {
            progressHide()
        }
        return errorModel
    }


    fun progressHide() {
        try {
            Log.e("", "Inside viewProgressGone()")
            if (mDialog != null)
                mDialog!!.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}