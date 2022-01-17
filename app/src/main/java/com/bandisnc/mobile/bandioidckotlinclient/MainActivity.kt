package com.bandisnc.mobile.bandioidckotlinclient

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.serviceapplication.IOidcAidlInterface

class MainActivity : AppCompatActivity(), ServiceConnection {

    var iRemoteService: IOidcAidlInterface? = null

    private lateinit var callOidcButton: Button
    private lateinit var logoutButton: Button

    private lateinit var bandiOidcBroadcastReceiver: BandiOidcBroadcastReceiver

    private lateinit var textToken: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        bandiOidcBroadcastReceiver = BandiOidcBroadcastReceiver()

        val filter = IntentFilter()
        filter.addAction( "com.example.serviceapplication.BANDI_OIDC_LOGIN")
        filter.addAction( "com.example.serviceapplication.BANDI_OIDC_LOGOUT")

        registerReceiver( bandiOidcBroadcastReceiver, filter)


        val intent = Intent("com.example.serviceapplication.OidcService")
        val pack = IOidcAidlInterface::class.java.`package`

        pack?.let {
            intent.setPackage(pack.name)
            applicationContext.bindService(intent,this, Context.BIND_AUTO_CREATE)
        }

        // -- 로그인 로그아웃 처리

        callOidcButton = findViewById( R.id.button_oidc)
        callOidcButton.setOnClickListener{
            sendLoginIntent()
        }

        logoutButton = findViewById( R.id.buttonLogout)
        logoutButton.setOnClickListener{
            sendLogoutIntent()
        }

        val passedIntent = intent

        val contents = passedIntent?.getStringExtra("contents")

        if( contents == null || "logout".equals(contents)) {
            callOidcButton.visibility = View.VISIBLE
            logoutButton.visibility = View.INVISIBLE
        } else {
            callOidcButton.visibility = View.INVISIBLE
            logoutButton.visibility = View.VISIBLE
        }

        textToken = findViewById( R.id.textToken)
        textToken.text = contents

    }

    @Override
    override fun onNewIntent(intent:Intent) {
        super.onNewIntent(intent);

        val contents = intent?.getStringExtra("contents")

        if( contents == null || "logout".equals(contents)) {
            callOidcButton.visibility = View.VISIBLE
            logoutButton.visibility = View.INVISIBLE
        } else {
            callOidcButton.visibility = View.INVISIBLE
            logoutButton.visibility = View.VISIBLE
        }

        textToken.text = contents
    }


    override fun onServiceConnected(componentName: ComponentName?, service: IBinder?) {
        iRemoteService = IOidcAidlInterface.Stub.asInterface(service)

        val isLogined = iRemoteService?.isLogined.toString()

        Log.d("kotiln_client",isLogined)

        val userInfo = iRemoteService?.userInfo

        Log.d("kotiln_client",userInfo.toString())

    }

    override fun onServiceDisconnected(componentName: ComponentName?) {
        iRemoteService = null
    }

    private fun sendBroadcastToBandiOidc( action:String) {
        val intent = Intent()

        intent.action = action

        intent.putExtra( "PACKAGE_NAME", applicationContext?.packageName)
        intent.putExtra( "client_id", "test_client")
        intent.putExtra( "client_secret", "test_secret")

        intent.component = ComponentName("com.example.serviceapplication", "com.example.serviceapplication.receiver.OidcBroadcastReceiver")

        sendBroadcast( intent)
    }

    fun sendLoginIntent() {

        sendBroadcastToBandiOidc( "com.example.serviceapplication.REMOTE_ACTION_LOGIN")

    }

    fun sendLogoutIntent() {
        sendBroadcastToBandiOidc( "com.example.serviceapplication.REMOTE_ACTION_LOGOUT")
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver( bandiOidcBroadcastReceiver)
    }


}