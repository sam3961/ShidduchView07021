package shiddush.view.com.mmvsd.repository

/**
 * Created by Sumit Kumar.
 * it contain the common method  @getServerResponse ,getError for handling the api response of api.
 */
interface ServiceListener<T> {
    abstract fun getServerResponse(response: T, requestcode: Int)
    abstract fun getError(error: shiddush.view.com.mmvsd.repository.ErrorModel, requestcode: Int)
}