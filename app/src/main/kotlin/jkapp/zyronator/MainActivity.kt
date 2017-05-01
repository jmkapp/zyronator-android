package jkapp.zyronator

import android.app.FragmentTransaction
import android.app.PendingIntent
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import jkapp.zyronator.listdetails.ListDetailsActivity
import jkapp.zyronator.listdetails.ListDetailsFragment
import jkapp.zyronator.listsummary.ListListener
import jkapp.zyronator.listsummary.ListSummaryFragment
import jkapp.zyronator.listsummary.ListSummaryService
import jkapp.zyronator.listsummary.Pagination

class MainActivity : AppCompatActivity(), ListListener
{
    private val _apiRequestCode = 0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        Log.i(MainActivity::class.java.simpleName, "onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pendingResult: PendingIntent = createPendingResult(_apiRequestCode, Intent(), 0)
        val intent = Intent(applicationContext, ListSummaryService::class.java)

        val userAgent : String = getString(R.string.app_name) + "/" + getString(R.string.version)
        val user : String = getString(R.string.default_user)
        val baseUrl : String = getString(R.string.base_url)
        val perPageDefault : String = getString(R.string.per_page_default)

        intent.putExtra(ListSummaryService.EXTRA_PENDING_RESULT, pendingResult)
        intent.putExtra(ListSummaryService.EXTRA_BASE_URL, baseUrl)
        intent.putExtra(ListSummaryService.EXTRA_USER_AGENT, userAgent)
        intent.putExtra(ListSummaryService.EXTRA_USER, user)
        intent.putExtra(ListSummaryService.EXTRA_PER_PAGE_DEFAULT, perPageDefault)

        startService(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        Log.i(MainActivity::class.java.simpleName, "onCreateOptionsMenu()")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onStart()
    {
        Log.i(MainActivity::class.java.simpleName, "onStart()")
        super.onStart()
    }

    override fun onActivityResult(requestCode: Int, resultCode : Int, data : Intent)
    {
        Log.i(MainActivity::class.java.simpleName, "onActivityResult()")
        if(requestCode == _apiRequestCode)
        {
            val bundle : Bundle = data.getBundleExtra(ListSummaryService.EXTRA_BUNDLE_RESULT)
            val pagination : Pagination = bundle.getParcelable(ListSummaryService.PAGINATION_RESULT)
            val lists: java.util.ArrayList<jkapp.zyronator.listsummary.List> = bundle.getParcelableArrayList(ListSummaryService.LISTS_RESULT)

            val listSummaryFragment = fragmentManager.findFragmentById(R.id.list_frag) as ListSummaryFragment
            listSummaryFragment.setData(lists)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun itemClicked(listId: Long)
    {
        val fragmentContainer = findViewById(R.id.fragment_container)
        if(fragmentContainer != null)
        {
            val listDetails = ListDetailsFragment()
            val ft = fragmentManager.beginTransaction()
            listDetails.setListId(listId)
            ft.replace(R.id.fragment_container, listDetails)
            ft.addToBackStack(null)
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ft.commit()
        }
        else
        {
            val intent = Intent(this, ListDetailsActivity::class.java)
            intent.putExtra(ListDetailsActivity.EXTRA_LIST_ID, listId)
            startActivity(intent)
        }
    }
}
