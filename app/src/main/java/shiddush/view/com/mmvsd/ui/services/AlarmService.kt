package shiddush.view.com.mmvsd.ui.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class AlarmService : JobService() {

    override fun onStartJob(parameters: JobParameters?): Boolean {
        // runs on the main thread, so this Toast will appear
        Log.e("JobService", "JobService onStartJob")
        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        Log.e("JobService", "JobService onStopJob")
        return true
    }
}