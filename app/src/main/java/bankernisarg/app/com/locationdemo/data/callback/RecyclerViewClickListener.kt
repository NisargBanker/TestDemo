package bankernisarg.app.com.locationdemo.data.callback

import android.view.View
import bankernisarg.app.com.locationdemo.data.db.entities.Trip

interface RecyclerViewClickListener {
    fun onRecyclerViewItemClick(view: View, trip: Trip)
}