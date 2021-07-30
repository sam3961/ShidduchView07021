package shiddush.view.com.mmvsd.rest

import android.app.Activity
import android.app.ProgressDialog
import android.os.AsyncTask
import android.util.Log
import androidx.fragment.app.Fragment
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import shiddush.view.com.mmvsd.BuildConfig
import shiddush.view.com.mmvsd.repository.ErrorModel
import java.net.URLEncoder
import java.util.concurrent.TimeUnit


/**
 * Created by Sutha
 */
class RestClientReferral : AsyncTask<String?, Int?, AsyncResponse> {
    var caller: OnAsyncRequestComplete
    var callerError: OnAsyncRequestError? = null
    var context: Activity
    var fg: Fragment? = null
    var is_fr = false
    var method = RestMethod.GET
    var parameters: RequestParams? = null
    var isProgress = true
    var pDialog: ProgressDialog? = null
    var json = ""
    var label = "default"
    var extra = Any()
    private var token = ""
    fun setToken(token: String) {
        this.token = token
    }

    constructor(a: Activity, m: RestMethod, json: String) {
        caller = a as OnAsyncRequestComplete
        callerError = a as OnAsyncRequestError
        context = a
        method = m
        this.json = json
    }

    constructor(a: Activity, m: RestMethod, p: RequestParams?) {
        caller = a as OnAsyncRequestComplete
        callerError = a as OnAsyncRequestError
        context = a
        method = m
        parameters = p
    }


    constructor(a: Fragment, m: RestMethod, p: RequestParams?) {
        caller = a as OnAsyncRequestComplete
        callerError = a as OnAsyncRequestError
        context = a.activity!!
        method = m
        parameters = p
    }

    constructor(a: Activity, m: RestMethod, p: RequestParams?, label: String) {
        caller = a as OnAsyncRequestComplete
        callerError = a as OnAsyncRequestError
        context = a
        method = m
        parameters = p
        this.label = label
    }

    constructor(a: Activity, m: RestMethod, p: RequestParams?, obj: Any) {
        caller = a as OnAsyncRequestComplete
        callerError = a as OnAsyncRequestError
        context = a
        method = m
        parameters = p
        extra = obj
    }

    constructor(a: Fragment, m: RestMethod, p: RequestParams?, obj: Any) {
        caller = a as OnAsyncRequestComplete
        callerError = a as OnAsyncRequestError
        context = a.activity!!
        method = m
        parameters = p
        extra = obj
    }

    constructor(a: Activity, m: RestMethod) {
        caller = a as OnAsyncRequestComplete
        callerError = a as OnAsyncRequestError
        context = a
        method = m
    }

    constructor(a: Activity) {
        caller = a as OnAsyncRequestComplete
        context = a
    }

    // Interface to be implemented by calling activity
    interface OnAsyncRequestComplete {
        fun asyncResponse(responseData: String?, label: String?, `object`: Any?)
    }

    interface OnAsyncRequestError {
        fun asyncError(responseData: ErrorModel?, label: String?, `object`: Any?)
    }

    override fun doInBackground(vararg urls: String?): AsyncResponse { // get url pointing to entry point of API
        label = urls[0]!!
        val result = AsyncResponse()
        try {
            val address = BuildConfig.REFERRAL_URL + urls[0]!!
            if (method === RestMethod.POST) {
                val data = post(address)
                result.taskResult(data)
            }
            if (method === RestMethod.GET) {
                val data = get(address)
                result.taskResult(data)
            }
        } catch (e: Exception) {
            result.taskResult(e)
        }
        return result
    }

    public override fun onPreExecute() {
        if (isProgress) {
            pDialog = ProgressDialog(context)
            pDialog!!.setMessage("Please wait..") // typically you will define such
            // strings in a remote file.
//pDialog.show();
        }
    }

    override fun onProgressUpdate(vararg progress: Int?) { // you can implement some progressBar and update it in this record
// setProgressPercent(progress[0]);
    }

    public override fun onPostExecute(response: AsyncResponse) {
        if (response.isSuccess) caller.asyncResponse(response.getResult(), label, extra)
        else {
            Log.d("errorrss",callerError.toString()+"   "+label+"   "+extra+"   "+response.errors())
            callerError!!.asyncError(response.errors(), label, extra)
        }
    }

    override fun onCancelled(response: AsyncResponse) {
        if (pDialog != null && pDialog!!.isShowing) {
            pDialog!!.dismiss()
        }
        callerError!!.asyncError(response.errors(), label, extra)
    }

    @Throws(Exception::class)
    private operator fun get(address: String): String {
        var address: String? = address
        val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
        val params = parameters!!.params
        if (params != null) {
            var query = ""
            val EQ = "="
            val AMP = "&"
            for (param in params) {
                query += param.name + EQ + URLEncoder.encode(param.value) + AMP
            }
            if (query !== "") {
                address += "?$query"
            }
        }
        val builder = Request.Builder().header("Authorization", token)
        builder.url(address!!)
        val request = builder.build()
        return try {
            val response = client.newCall(request).execute()
            response.body!!.string()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.v("REST", e.message!!)
            throw e
        }
    }

    @Throws(Exception::class)
    private fun post(address: String): String {
        val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
        val builder = FormBody.Builder()

        val params = parameters!!.params
        for (i in params.indices) {
            val v = params[i]
            builder.add(v.name, v.value)

        }
        val requestBody: RequestBody = builder.build()

        val request = Request.Builder().header("Authorization", token)
                .url(address)
                .post(requestBody)
                .build()
        return try {
            val response = client.newCall(request).execute()
            response.body!!.string()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.v("REST", e.message!!)
            throw e
        }
    }
}