package com.bignerdbranch.android.nerdlauncherx

import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "NerdLauncherXActivity"

class NerdLauncherXActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nerd_launcher_x)

        recyclerView = findViewById(R.id.app_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        setUpAdapter()
    }

    /**
     * Ultimately this function will create a RecyclerView.Adapter instance and set it on
    your RecyclerView object. For now, it will just generate a list of application data.
     */
    private fun setUpAdapter() {
        //region generate a list of application data. Every Activity that has the CATEGORY_LAUNCHER
        val startUpIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val activities = packageManager.queryIntentActivities(startUpIntent, 0)
        //arrange the [activities] in alphabetical order
        activities.sortWith(Comparator { a, b ->
            String.CASE_INSENSITIVE_ORDER.compare(
                a.loadLabel(packageManager).toString(),
                b.loadLabel(packageManager).toString()
            )
        })
        //endregion
        Log.i(TAG, "Found ${activities.size} activities")
        //set the recyclerView adapter
        recyclerView.adapter = ActivityAdapter(activities)
    }

    /**
     * Now define a ViewHolder that displays an activity’s label
     */
    private class ActivityHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private val appIconImageView = itemView.findViewById<AppCompatImageView>(R.id.appIconImageView)
        private val appIconLabel =  itemView.findViewById<AppCompatTextView>(R.id.appIconLabel)
        private lateinit var resolveInfo: ResolveInfo

        init {
            itemView.setOnClickListener(this)
        }

        fun bindActivity(resolveInfo: ResolveInfo) {
            this.resolveInfo = resolveInfo
            val packageManager = itemView.context.packageManager
            val appName = resolveInfo.loadLabel(packageManager).toString()
            val appIcon = resolveInfo.loadIcon(packageManager)

            appIconImageView.setImageDrawable(appIcon)
            appIconLabel.setText(appName)
        }

        /**
         * When the [nameTextView] is clicked, the application with that label is launched
         */
        override fun onClick(view: View?) {
            val activityInfo = resolveInfo.activityInfo
            val intent = Intent(Intent.ACTION_MAIN).apply {
                setClassName(
                    activityInfo.applicationInfo.packageName,
                    activityInfo.name
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val context = view!!.context
            context.startActivity(intent)
        }
    }


    /**
     * A RecyclerView.Adapter implementation
     */
    private class ActivityAdapter(val activities: List<ResolveInfo>) :
        RecyclerView.Adapter<ActivityHolder>() {

        override fun onCreateViewHolder(container: ViewGroup, viewType: Int): ActivityHolder {

            val layoutInflater = LayoutInflater.from(container.context)
            val view = layoutInflater.inflate(R.layout.app_icon_label, container, false)
            return ActivityHolder(view)
        }

        override fun onBindViewHolder(holder: ActivityHolder, position: Int) {
            val resolveInfo = activities[position]
            holder.bindActivity(resolveInfo)
        }

        override fun getItemCount(): Int {
            return activities.size
        }
    }
}