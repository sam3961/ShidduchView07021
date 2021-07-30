@file:JvmName("RadioPlayer")
@file:JvmMultifileClass
@file:Suppress("DEPRECATION")

package shiddush.view.com.mmvsd.radioplayer

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.util.Log
import shiddush.view.com.mmvsd.model.radio.RadioListResponse


/**
 * Created by Sumit Kumar
 *  This class use to perform background music related functions
 */
@SuppressLint("StaticFieldLeak")
object RadioPlayer {

    lateinit var mediaPlayer: MediaPlayer
    private var musicList: ArrayList<RadioListResponse.MusicUrl> = ArrayList<RadioListResponse.MusicUrl>()
    private var currentPos: Int = 0
    private var isInWaitingScreen: Boolean = false

    /**
     * to setup radio player
     */
    fun setupRadioPlayer(musicList: ArrayList<RadioListResponse.MusicUrl>) {
        this.musicList = musicList
        initRadioPlayer(musicList[currentPos].musicUrl!!)
        radioPlayerListener()
    }

    /**
     * to initialize radio player
     */
    fun initRadioPlayer(track: String) {
        /*try {
            mediaPlayer = MediaPlayer()
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer.setDataSource(track)
            mediaPlayer.prepare()
            Log.e("SWAPLOGRP", "initRadioPlayer()")
        } catch (e: IOException) {
            Log.e("SWAPLOGRP", "initRadioPlayer() failed")
        }*/

    }

    fun radioPlayerListener() {
       /* mediaPlayer.setOnPreparedListener(MediaPlayer.OnPreparedListener { })
        mediaPlayer.setOnCompletionListener(MediaPlayer.OnCompletionListener { playNextTrack() })*/
    }

    /**
     * set is in waiting screen
     */
    fun setIsInWaitingScreen(isInWaitingScreen: Boolean) {
        try {
            this.isInWaitingScreen = isInWaitingScreen
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * to play radio
     */
    fun playRadio() {
        /*try {
            if (isInWaitingScreen) {
                mediaPlayer.start()
                Log.e("SWAPLOGRP", "playRadio()")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }*/
    }

    /**
     * to pause radio
     */
    fun pauseRadio() {
        /*try {
            mediaPlayer.pause()
            Log.e("SWAPLOGRP", "pauseRadio()")
        } catch (e: Exception) {
            e.printStackTrace()
        }*/
    }

    /**
     * to stop radio
     */
    fun stopRadio() {
        /*try {
            if (mediaPlayer != null) {
                mediaPlayer.pause()
                mediaPlayer.release()
            }
            Log.e("SWAPLOGRP", "stopRadio()")
        } catch (e: Exception) {
            e.printStackTrace()
        }*/
    }

    /**
     * to play next track
     */
    fun playNextTrack() {
        try {
            mediaPlayer.reset()
            Log.e("SWAPLOGRP", "playNextTrack()")
            try {
                if (musicList.size == currentPos + 1) {
                    currentPos = 0
                } else {
                    currentPos += 1
                }
            } catch (e: Exception) {
                currentPos = 0
            }
            initRadioPlayer(musicList[currentPos].musicUrl!!)
            playRadio()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}