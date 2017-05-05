package jkapp.zyronator.releasedetails

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ReleaseService : IntentService(_name)
{
    companion object
    {
        private val _name: String = ReleaseService::class.java.simpleName
        val EXTRA_PENDING_RESULT: String = "pend"
        val EXTRA_BASE_URL: String = "url"
        val EXTRA_USER_AGENT = "agent"
        val EXTRA_RELEASE_ID = "relid"

        val RESULT_CODE: Int = 0
        val EXTRA_RELEASE_RESULT = "release"
        val EXTRA_RESULT_RECEIVER = "resrec"
    }

    override fun onHandleIntent(intent: Intent)
    {
        val baseUrl : String = intent.getStringExtra(EXTRA_BASE_URL)
        val userAgent : String = intent.getStringExtra(EXTRA_USER_AGENT)
        val releaseId : String = intent.getStringExtra(EXTRA_RELEASE_ID)

        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

        val releaseApi = retrofit.create(ReleaseApi::class.java)
        val releaseApiCall : Call<ReleaseApiCall> = releaseApi.getReleaseCall(releaseId, userAgent)
        val response = releaseApiCall.execute()

        if(response.isSuccessful)
        {
            val release = response.body()
            val bundle = Bundle()
            bundle.putParcelable(EXTRA_RELEASE_RESULT,  release)

            val resultReceiver = intent.getParcelableExtra<ResultReceiver>(EXTRA_RESULT_RECEIVER)
            resultReceiver.send(RESULT_CODE, bundle)
        }
    }
}
