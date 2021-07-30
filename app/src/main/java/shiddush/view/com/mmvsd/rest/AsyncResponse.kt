package shiddush.view.com.mmvsd.rest

import android.net.ParseException
import com.google.gson.stream.MalformedJsonException
import retrofit2.HttpException
import shiddush.view.com.mmvsd.repository.ErrorModel
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

class AsyncResponse {
    private var result: String? = null
    private var error: Exception? = null
    var isSuccess:Boolean = false;
    fun getResult(): String? {
        return result
    }

    fun getError(): ErrorModel? {
        return errors();
    }

    fun taskResult(result: String) {
        isSuccess = true;
        this.result = result
    }

    fun errors():ErrorModel{
        val t = error;
        val errorModel = ErrorModel()
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
        }
        return errorModel;
    }

    fun taskResult(error: Exception?) {
        isSuccess = false;
        this.error = error
    }
}