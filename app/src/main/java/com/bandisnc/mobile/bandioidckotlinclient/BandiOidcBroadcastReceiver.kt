package com.bandisnc.mobile.bandioidckotlinclient

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BandiOidcBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d( "client", "수신이 되나?")

        if ( "com.example.serviceapplication.BANDI_OIDC_LOGOUT".equals(intent?.action) ) {

            sendToActivity( context, "logout")

            // todo : 로그아웃 처리( 저장된 ID토큰 지우기)
        } else if ( "com.example.serviceapplication.BANDI_OIDC_LOGIN".equals(intent?.action) ) {

            val token = intent?.getStringExtra("ID_TOKEN")

            // todo : 로그인 처리( ID토큰 저장)

            sendToActivity( context, token?:"")

        }
    }

    private fun sendToActivity(context: Context, contents: String) {

        val intent = Intent(context, MainActivity::class.java);
        intent.putExtra("contents", contents);

        // todo: 단말의 화면의 최상단에 해당 앱이 떠 있는지 확인해서, 최상위 일 경우에만 pendingintent 처리 필요

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            pendingIntent.send();
        } catch (e: Throwable) {
            Log.d("client", e. message.toString ())
        }
    }
}