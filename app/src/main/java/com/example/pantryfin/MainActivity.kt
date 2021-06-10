package com.example.pantryfin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.pantryfin.ui.login.LoginActivity
import com.example.pantryfin.ui.login.LoginInfo
import com.example.pantryfin.ui.login.LoginViewModel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    // inflate icon (s?) and add them to toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        val loginItem = menu?.findItem(R.id.miProfile)

        MenuItemCompat.setActionView(loginItem, R.layout.login_button_layout);

        val menuLayout = loginItem?.actionView;

        val loginMenu = menuLayout?.findViewById<Button>(R.id.button_login);
        loginMenu?.setOnLongClickListener {
            logout()
            Toast.makeText(
                this,
                "Logged out.",
                Toast.LENGTH_SHORT
            ).show();
            false;
        }
        loginMenu?.setOnClickListener {
            onOptionsItemClick(loginItem)
        }
        return true
    }

    private fun onOptionsItemClick(item: MenuItem): Boolean {
        val sharedPref = getSharedPreferences(
            getString(R.string.auth_data_file),
            Context.MODE_PRIVATE
        )
        val codedInfo = sharedPref.getString(getString(R.string.access_token_key), null)
        val logged = if(codedInfo != null) Json.decodeFromString<LoginInfo>(codedInfo) else null

        if(logged == null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            Toast.makeText(
                this,
                getString(R.string.login_prompt),
                Toast.LENGTH_LONG).show()
        }
        else {
            Toast.makeText(
                this,
                getString(R.string.login_logged_in, logged.email),
                Toast.LENGTH_LONG).show()
        }

        return super.onOptionsItemSelected(item)
    }

    fun logout() {
        val sharedPref = getSharedPreferences(
            getString(R.string.auth_data_file),
            Context.MODE_PRIVATE
        )
        with (sharedPref.edit()) {
            remove(getString(R.string.access_token_key))
            apply()
        }
    }
}