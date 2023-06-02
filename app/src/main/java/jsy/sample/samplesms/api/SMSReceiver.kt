package jsy.sample.samplesms.api

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SMSReceiver : BroadcastReceiver() {
    val TAG = "SMSReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        if(SmsRetriever.SMS_RETRIEVED_ACTION == intent.action){
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as? Status

            when(status?.statusCode){
                CommonStatusCodes.SUCCESS -> {
//                    val message = extras?.get(SmsRetriever.EXTRA_SMS_MESSAGE) as? String
//                    Log.d(TAG, "onReceive\$SUCCESS $message")

                    val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String?
                    Log.d( TAG, "SmsReceiver : onReceiver(CommonStatusCodes.SUCCESS)")
                    Log.d(TAG,"message : $message")


                }

                CommonStatusCodes.TIMEOUT -> {
                    Log.d(TAG, "onReceive\$TIMEOUT")
                }
            }
        }
    }

//    fun doFilter(): IntentFilter = IntentFilter().apply {
//        addAction(SmsRetriever.SMS_RETRIEVED_ACTION)을
//    }
}