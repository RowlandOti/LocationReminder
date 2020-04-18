package com.udacity.project4.locationreminders

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthenticationActivity
import com.udacity.project4.authentication.AuthenticationViewModel
import kotlinx.android.synthetic.main.activity_reminders.*
import org.koin.android.ext.android.inject

/**
 * The RemindersActivity that holds the reminders fragments
 */
class RemindersActivity : AppCompatActivity() {

    private val viewModel: AuthenticationViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminders)


        viewModel.authenticationState.observe(this, Observer { authenticationState ->
            when (authenticationState) {
                AuthenticationViewModel.AuthenticationState.AUTHENTICATED -> {
                    Log.e(
                            AuthenticationActivity.TAG,
                            "User is already logged in $authenticationState")
                }
                AuthenticationViewModel.AuthenticationState.INVALID_AUTHENTICATION -> {
                    launchAuthenticationActivity()
                }
                else -> {
                    launchAuthenticationActivity()
                    Log.e(
                            AuthenticationActivity.TAG,
                            "Authentication state that doesn't require any UI change $authenticationState")
                }
            }
        })
    }

    private fun launchAuthenticationActivity() {
        val i = Intent(this, AuthenticationActivity::class.java)
        startActivity(i)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                (nav_host_fragment as NavHostFragment).navController.popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
