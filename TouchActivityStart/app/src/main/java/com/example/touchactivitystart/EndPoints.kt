package com.example.touchactivitystart

object EndPoints {
    private val URL_ROOT = "http://192.168.64.2/BehavioralData/v1/?op="
    val URL_ADD_ACTION = URL_ROOT + "addAction"
    val URL_ADD_MOTION_EVENT = URL_ROOT + "addMotionEvent"
    val URL_GET_LAST_USER_INSTANCE = URL_ROOT + "getLastUserInstance"
//    val URL_GET_ARTIST = URL_ROOT + "getartists"
}