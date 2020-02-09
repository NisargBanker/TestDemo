package bankernisarg.app.com.locationdemo.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import bankernisarg.app.com.locationdemo.R
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    private var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        handler = Handler()

        handler?.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)

        val myanim1 = AnimationUtils.loadAnimation(this, R.anim.animation_from_right)
        logo.startAnimation(myanim1)

    }

    override fun onRestart() {
        super.onRestart()
        handler?.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }

    override fun onStop() {
        handler?.removeMessages(0)
        super.onStop()
    }

    override fun onDestroy() {
        handler?.removeMessages(0)
        super.onDestroy()
    }
}