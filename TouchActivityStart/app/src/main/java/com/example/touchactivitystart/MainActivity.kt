package com.example.touchactivitystart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import android.widget.Toast.makeText
import com.android.volley.AuthFailureError
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private var mVelocityTracker: VelocityTracker? = null
    private var userInstance: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getLastUserInstance()
        Toast.makeText(applicationContext, "Fetched user instance $userInstance", Toast.LENGTH_LONG).show()
        val editText = findViewById<EditText> (R.id.editText)
        val button = findViewById<Button> (R.id.button)
        editText.setOnClickListener {
            makeText(this,"Click",Toast.LENGTH_LONG).show()
            val str: String = editText.text.toString()
            val res: String = str.plus(":Click:")
            editText.setText(res).toString()
            addAction("Click")
        }
        editText.setOnLongClickListener {
            makeText(this,"Long Click",Toast.LENGTH_LONG).show()
            val str: String = editText.text.toString()
            val res: String = str.plus(":Long Click:")
            editText.setText(res).toString()
            addAction("Long Click")
            true
        }
        editText.setOnFocusChangeListener { view: View, b: Boolean ->
            makeText(this,"Focus Change",Toast.LENGTH_LONG).show()
            val str: String = editText.text.toString()
            val res: String = str.plus(":Focus Change:")
            editText.setText(res).toString()
            addAction("Focus Change")
        }
        editText.setOnKeyListener { view: View, i: Int, keyEvent: KeyEvent ->
            makeText(this,"Focus item and press",Toast.LENGTH_LONG).show()
            val str: String = editText.text.toString()
            val res: String = str.plus(":Focus item and press:")
            editText.setText(res).toString()
            addAction("Focus item and press")
            true
        }
//        button.setOnCreateContextMenuListener { menu, v, menuInfo ->  }
//        tv.setOnTouchListener {
//            makeText(this,"Touch Event",Toast.LENGTH_LONG).show()
//            val str: String = tv.text.toString()
//            val res: String = str.plus(":Touch Event:")
//            tv.setText(res).toString()
//            true
//        }
    }

    private fun addAction(action: String) {
        val stringRequest = object : StringRequest(Request.Method.POST, EndPoints.URL_ADD_ACTION,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_LONG).show()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { volleyError -> Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_LONG).show() }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["user_instance"] = "$userInstance"
                params["action"] = action
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {


        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // Reset the velocity tracker back to its initial state.
                mVelocityTracker?.clear()
                // If necessary retrieve a new VelocityTracker object to watch the
                // velocity of a motion.
                mVelocityTracker = mVelocityTracker ?: VelocityTracker.obtain()
                // Add a user's movement to the tracker.
                mVelocityTracker?.addMovement(event)
                addMotionEvent("Action Down", "0", "0")
            }
            MotionEvent.ACTION_MOVE -> {
                mVelocityTracker?.apply {
                    val pointerId: Int = event.getPointerId(event.actionIndex)
                    addMovement(event)
                    val pointer = findViewById<TextView> (R.id.pointer)
                    // When you want to determine the velocity, call
                    // computeCurrentVelocity(). Then call getXVelocity()
                    // and getYVelocity() to retrieve the velocity for each pointer ID.
                    computeCurrentVelocity(1000)
                    // Log velocity of pixels per second
                    // Best practice to use VelocityTrackerCompat where possible.
                    var x = getXVelocity(pointerId)
                    var y = getYVelocity(pointerId)
                    pointer.setText("X velocity: ${getXVelocity(pointerId)}\nY velocity: ${getYVelocity(pointerId)}").toString()
//                    Log.d("", "X velocity: ${getXVelocity(pointerId)}")
//                    Log.d("", "Y velocity: ${getYVelocity(pointerId)}")
                    addMotionEvent("Action Move", "$x", "$y")
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // Return a VelocityTracker object back to be re-used by others.
                mVelocityTracker?.recycle()
                mVelocityTracker = null
                addMotionEvent("Action Up/Cancel", "0", "0")
            }
        }
        return true
    }

    private fun addMotionEvent(motion_event: String, x_velocity: String, y_velocity: String) {
        val stringRequest = object : StringRequest(Request.Method.POST, EndPoints.URL_ADD_MOTION_EVENT,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_LONG).show()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { volleyError -> Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_LONG).show() }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["user_instance"] = "$userInstance"
                params["motion_event"] = motion_event
                params["x_velocity"] = x_velocity
                params["y_velocity"] = y_velocity
                return params
            }
        }

        VolleySingleton.instance?.addToRequestQueue(stringRequest)

    }

    private fun getLastUserInstance() {
        val stringRequest = StringRequest(Request.Method.GET, EndPoints.URL_GET_LAST_USER_INSTANCE,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    val instance = obj.getJSONArray("user_instance")
                    val instanceObj = instance.getJSONObject(0)
                    userInstance = instanceObj.getInt("user_instance")+1
                    Log.d("User Instance", "$userInstance")
                    Toast.makeText(applicationContext, "Instance number $userInstance", Toast.LENGTH_LONG).show()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { volleyError -> Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_LONG).show() })
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add<String>(stringRequest)

    }

}


