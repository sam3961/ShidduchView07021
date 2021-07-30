package shiddush.view.com.mmvsd.utill

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateFunctions {

    var timeFormatYYYmmdd="yyyy-mm-dd HH:mm"
    val yyyy_MM_dd_hh_mm_ss = "yyyy-MM-dd HH:mm:ss"
    val yyyy_MM_dd_hh_mm_ss_sss_z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    val dd_MMM_yyyy_comma = "dd MMM,yyyy"
    val dd_MMM_yyyy_hh_mm_a_comma = "dd MMM,yyyy hh:mm a"
    val dd_MMM= "dd MMM"
    val dd_MMM_yyyy = "dd MMM yyyy"
    val yyyy_MM_dd = "yyyy-MM-dd"
    val hh_mm_a = "hh:mm a"
    val HH_mm = "HH:mm"
    val HH_mm_ss = "HH:mm:ss"


    fun tweleveHoursFormat(date: String): String? {
        val inputPattern = "HH:mm"
        val outputPattern = "hh:mm a"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)

        var newDate: Date? = null
        var str: String? = null

        try {
            newDate = inputFormat.parse(date)
            str = outputFormat.format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return str
    }


    fun timeStampToDate(timeStamp: Long, timeFormat: String):String{
        val sdf = SimpleDateFormat("dd/MM/yy hh:mm a")
        val netDate = Date(timeStamp)
        val date =sdf.format(netDate)
        return date

    }

    fun getFormattedDate(
        originalFormatString: String,
        targetFormatString: String,
        inputDate: String
    ): String {
        val originalFormat = SimpleDateFormat(originalFormatString)
        val targetFormat = SimpleDateFormat(targetFormatString)
        targetFormat.timeZone = TimeZone.getDefault()
        return targetFormat.format(originalFormat.parse(inputDate))
    }


    fun age(birthday: Date?, date: Date?): Int {
        val formatter: DateFormat = SimpleDateFormat("yyyyMMdd")
        val d1: Int = formatter.format(birthday).toInt()
        val d2: Int = formatter.format(date).toInt()
        return (d1 - d2) / 10000
    }

    @Throws(ParseException::class)
    fun stringDateToDate(date: String?,format: String): Date? {
        val sdf = SimpleDateFormat(format)
        val strDate = date
        return sdf.parse(strDate)
    }

    fun getCurrentDate(targetFormatString: String): String {
        val c = Calendar.getInstance().time
        println("Current time => $c")

     //   val df = SimpleDateFormat(targetFormatString, Locale.getDefault())
        val df = SimpleDateFormat(targetFormatString, Locale.getDefault())
        df.timeZone=TimeZone.getTimeZone("America/Los_Angeles")
        return df.format(c)
    }

}