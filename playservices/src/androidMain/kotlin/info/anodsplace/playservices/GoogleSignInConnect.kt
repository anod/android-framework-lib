package info.anodsplace.playservices

import android.accounts.Account
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Tasks
import info.anodsplace.context.ApplicationContext
import java.util.concurrent.ExecutionException

/**
 * @author Alex Gavrishev
 * @date 28/11/2017
 */
class GoogleSignInConnect(private val context: ApplicationContext, private val signInOptions: GoogleSignInOptions) {

    constructor(context: Context, signInOptions: GoogleSignInOptions) : this(ApplicationContext(context), signInOptions)

    private fun createGoogleApiSignInClient(): GoogleSignInClient {
        return GoogleSignIn.getClient(context.actual, signInOptions)
    }

    interface Result {
        fun onSuccess(account: Account)
        fun onError(errorCode: Int, errorMessage: String, signInIntent: Intent)
    }

    interface SignOutResult {
        fun onResult()
    }

    @Throws(ApiException::class, ExecutionException::class, InterruptedException::class)
    fun connectLocked(): Account {
        val client = createGoogleApiSignInClient()
        val googleAccount = Tasks.await(client.silentSignIn())
        return googleAccount.account!!
    }

    fun connect(completion: Result) {
        val client = createGoogleApiSignInClient()

        val task = client.silentSignIn()
        if (task.isSuccessful) {
            completion.onSuccess(task.result.account!!)
        } else {
            task.addOnCompleteListener {
                try {
                    val signInAccount = task.getResult(ApiException::class.java)
                    completion.onSuccess(signInAccount.account!!)
                } catch (apiException: ApiException) {
                    // You can get from apiException.getStatusCode() the detailed error code
                    // e.g. GoogleSignInStatusCodes.SIGN_IN_REQUIRED means user needs to take
                    // explicit action to finish sign-in;
                    // Please refer to GoogleSignInStatusCodes Javadoc for details
                    completion.onError(apiException.statusCode, GoogleSignInStatusCodes.getStatusCodeString(apiException.statusCode), client.signInIntent)
                }
            }
        }
    }

    fun disconnect(completion: SignOutResult) {
        val client = createGoogleApiSignInClient()
        val task = client.signOut()
        if (task.isSuccessful) {
            completion.onResult()
        } else {
            task.addOnCompleteListener {
                completion.onResult()
            }
        }
    }
}