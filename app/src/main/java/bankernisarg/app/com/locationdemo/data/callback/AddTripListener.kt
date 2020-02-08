package bankernisarg.app.com.locationdemo.data.callback

import bankernisarg.app.com.locationdemo.data.db.entities.Trip

interface AddTripListener {
    fun onStarted()
    fun onSuccess()
    fun onFailure(message: String)
}