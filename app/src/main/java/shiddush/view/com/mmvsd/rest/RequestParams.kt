package shiddush.view.com.mmvsd.rest

import java.util.*

/**
 * Created by Sutha
 */
class RequestParams {
    var params: ArrayList<DataPair>

    fun add(key: String?, value: String?) {
        params.add(DataPair(key!!, value!!))
    }
    fun add(key: String?, value: Boolean?) {
        params.add(DataPair(key!!, value.toString()!!))
    }

    fun add(key: String?, value: Int) {
        params.add(DataPair(key!!, value.toString()))
    }
    fun add(key: String?, value: Long) {
        params.add(DataPair(key!!, value.toString()))
    }

    init {
        params = ArrayList()
    }
}