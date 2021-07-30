package shiddush.view.com.mmvsd.utill

import android.text.format.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Sumit Kumar.
 * Use this class for Date conversion formats as per the requirement
 */

var serverDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
var alohaDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
var checkInDate = SimpleDateFormat("yyyy-MM-dd HH:mm")
var currentDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm")
private val dayOnlyFormat = SimpleDateFormat("EEE")
var reservationDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
var reservationDateToShow = SimpleDateFormat("MM/dd/yyyy")
var suggestionDateWeGet = SimpleDateFormat("MM/dd/yyyy HH:mm")
var suggestionDateToShow = SimpleDateFormat("MM/dd/yyyy hh:mm a")
var reservationTime = SimpleDateFormat("h:mm a")
var reservationTime12 = SimpleDateFormat("h:mma")
var reservationTime24 = SimpleDateFormat("HH")
var TIME_FORMAT = SimpleDateFormat("HH:mm:ss")
var reservationTime12WithoutMin = SimpleDateFormat("hh a")
var DOBsmall = SimpleDateFormat("MMMM dd")
var DOB = DOBsmall
var DOBfacebook = SimpleDateFormat("MM/dd/yyyy")
var dayHour = SimpleDateFormat("hh")
var month = SimpleDateFormat("MMM")


@Throws(ParseException::class)
fun parseToDOBfromFb(fbDate: String): Date {
    return DOBfacebook.parse(fbDate)

}

fun getCurrentDate(): String {
    val today = Date()
    val getDate = alohaDate.format(today)
    //val cDate = dateFormat.parse(getDate)
    return getDate
}

fun currentHourDay(): Int {
    return Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
}

fun currentDate(): Int {
    return Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
}

fun currentAmPm(): String {
    return Calendar.getInstance().get(Calendar.AM_PM).toString()
}


fun currentMonth(): String {
    return month.format(Calendar.getInstance().time)
}

fun getMonth(date: Date): String {
    return month.format(date)
}

fun getDayName(time: Date): String {
    return dayOnlyFormat.format(time)
}

fun getDayName(date: String): String {

    try {
        val date1 = serverDate.parse(date)
        return dayOnlyFormat.format(date1)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return getDayName(Calendar.getInstance().time)
}

fun getDateFromTimeStampForPayment(time: Long): String {
    val cal = Calendar.getInstance(Locale.ENGLISH)
    cal.timeInMillis = time * 1000
    return DateFormat.format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", cal).toString()
}

fun getDateMonthYear(time: Long): String {
    val cal = Calendar.getInstance(Locale.ENGLISH)
    cal.timeInMillis = time
    return DateFormat.format("dd/MM/yyyy", cal).toString()
}

fun displayReservationDate(appointment_date: String?): String? {
    try {
        if (appointment_date == null) {
            return ""
        }
        val date1 = serverDate.parse(appointment_date)
        return reservationDateToShow.format(date1)
    } catch (e: ParseException) {
        try {
            val date1 = checkInDate.parse(appointment_date)
            return reservationDateToShow.format(date1)
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

    }

    return appointment_date

}

fun displayAlohaDate(appointment_date: String?): String? {
    try {
        if (appointment_date == null) {
            return ""
        }
        val date1 = alohaDate.parse(appointment_date)
        return reservationTime.format(date1)
    } catch (e: ParseException) {
        try {
            val date1 = alohaDate.parse(appointment_date)
            return reservationTime.format(date1)
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

    }

    return appointment_date

}

fun displaySuggestionDate(appointment_date: String?): String? {
    try {
        if (appointment_date == null) {
            return ""
        }
        val date1 = suggestionDateWeGet.parse(appointment_date)
        return suggestionDateToShow.format(date1)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return appointment_date

}

fun isExpiredDate(appointmentDate: String): Boolean {
    try {
        val instance = Calendar.getInstance()
        instance.timeInMillis = System.currentTimeMillis()
        return reservationDate.parse(appointmentDate).before(instance.time)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return false
}

fun getServerDate(appointment_date: String): String {
    try {
        return serverDate.format(checkInDate.parse(appointment_date))
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return appointment_date
}

fun getCurrentDateTime(): String {
    return currentDateTime.format(Date())
}

fun getWeekDay(): String {
    val sdf = SimpleDateFormat("EEEE")
    val d = Date()
    val dayOfTheWeek = sdf.format(d)
    when {
        dayOfTheWeek.equals("Thursday") -> return "Sunday"
        dayOfTheWeek.equals("Sunday") -> return "Monday"
        dayOfTheWeek.equals("Monday") -> return "Tuesday"
        dayOfTheWeek.equals("Tuesday") -> return "Wednesday"
        dayOfTheWeek.equals("Wednesday") -> return "Thursday"
        dayOfTheWeek.equals("Friday") -> return "Sunday"
        dayOfTheWeek.equals("Saturday") -> return "Sunday"
        else -> return dayOfTheWeek
    }
}

@Throws(ParseException::class)
fun getHourIn24(date12Hr: String): Int {
    return Integer.parseInt(reservationTime24.format(reservationTime12.parse(date12Hr)))
}

@Throws(ParseException::class)
fun getHourIn12(date24Hr: Int): String {
    return reservationTime12WithoutMin.format(reservationTime24.parse(date24Hr.toString() + ""))
}

fun timeBetweenInterval(
        openTime: String,
        closeTime: String
): Boolean {
    try {
        val dateFormat = TIME_FORMAT
        val afterCalendar = Calendar.getInstance().apply {
            time = dateFormat.parse(openTime)
            add(Calendar.DATE, 1)
        }
        val beforeCalendar = Calendar.getInstance().apply {
            time = dateFormat.parse(closeTime)
            add(Calendar.DATE, 1)
        }

        val current = Calendar.getInstance().apply {
            val localTime = dateFormat.format(timeInMillis)
            time = dateFormat.parse(localTime)
            add(Calendar.DATE, 1)
        }
        return current.time.after(afterCalendar.time) && current.time.before(beforeCalendar.time)
    } catch (e: ParseException) {
        e.printStackTrace()
        return false
    }
}



