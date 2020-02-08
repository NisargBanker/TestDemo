package bankernisarg.app.com.locationdemo

import android.app.Application
import bankernisarg.app.com.locationdemo.data.db.AppDatabase
import bankernisarg.app.com.locationdemo.data.repositories.AddNewTripRepository
import bankernisarg.app.com.locationdemo.data.repositories.AllTripRepository
import bankernisarg.app.com.locationdemo.ui.add_new_trip.AddNewTripViewModelFactory
import bankernisarg.app.com.locationdemo.ui.trip.AllTripViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class MyApplication : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@MyApplication))

        bind() from singleton { AppDatabase(instance()) }
        bind() from singleton { AllTripRepository(instance()) }
        bind() from provider { AllTripViewModelFactory(instance()) }

        bind() from singleton { AddNewTripRepository(instance()) }
        bind() from provider { AddNewTripViewModelFactory(instance()) }

    }

}