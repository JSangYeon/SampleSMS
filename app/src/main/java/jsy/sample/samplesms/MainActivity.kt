package jsy.sample.samplesms

import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.auth.api.phone.SmsRetriever
import jsy.sample.samplesms.api.SMSReceiver
import jsy.sample.samplesms.ui.theme.SampleSMSTheme
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Arrays


class MainActivity : ComponentActivity() {

    //앱 파이어베이스 등록해서 google-services.json 구해볼 것
    // 키해시 구하기
    private  val HASH_TYPE = "SHA-256"
     val NUM_HASHED_BYTES = 9
     val NUM_BASE64_CHAR = 11
    val smsReceiver = SMSReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsReceiver, intentFilter)

        startSmsRetriver(this)

        setContent {
            SampleSMSTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }



    fun startSmsRetriver(context: Context) {
        val TAG = "startSmsRetriver"
        val task = SmsRetriever.getClient(this)
            .startSmsRetriever()

        task.addOnSuccessListener {
            Log.d(TAG, "addOnSuccessListener ${getAppSignatures(context)}")
        }

        task.addOnFailureListener {
            Log.e(TAG, "addOnFailureListener $it")
        }
    }

    fun getAppSignatures(context: Context): List<String> {
        val TAG = "getAppSignatures"
        val appCodes = mutableListOf<String>()

        try {
            val packageName = context.packageName
            val packageManager = context.packageManager
            val signatures = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures

            for (signature in signatures) {
                val hash = getHash(packageName, signature.toCharsString())

                if (hash != null) {
                    appCodes.add(String.format("%s", hash))
                }

                Log.d(TAG, String.format("이 값을 SMS 뒤에 써서 보내주면 됩니다 : %s", hash))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d(TAG, "Unable to find package to obtain hash. : $e")
        }

        return appCodes
    }

    private fun getHash(packageName: String, signature: String): String? {
        val TAG = "getHash"
        val appInfo = "$packageName $signature"

        try {
            val messageDigest = MessageDigest.getInstance(HASH_TYPE)

            // minSdkVersion이 19이상이면 체크 안해도 됨
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                messageDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
            }

            val hashSignature = Arrays.copyOfRange(messageDigest.digest(), 0, NUM_HASHED_BYTES)
            val base64Hash = Base64
                .encodeToString(hashSignature, Base64.NO_PADDING or Base64.NO_WRAP)
                .substring(0, NUM_BASE64_CHAR)

            Log.d(TAG, String.format("\nPackage : %s\nHash : %s", packageName, base64Hash))

            return base64Hash

        } catch (e: NoSuchAlgorithmException) {
            Log.d(TAG, "hash:NoSuchAlgorithm : $e")
        }

        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(smsReceiver)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SampleSMSTheme {
        Greeting("Android")
    }
}
