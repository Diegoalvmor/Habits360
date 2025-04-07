package com.example.habits360.googleAuth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await


object GoogleAuthUIClient {
    fun getSignInIntent(context: Context): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("637237112740-eaubivmvppp7ng1vr2em5fklcf0b19do.apps.googleusercontent.com") // El cliente id del googleservice.json
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        googleSignInClient.signOut()
        return googleSignInClient.signInIntent
    }

    suspend fun handleSignInResult(intent: Intent?): FirebaseUser? {
        val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
        val account = task.await()

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        val authResult = FirebaseAuth.getInstance().signInWithCredential(credential).await()

        return authResult.user
    }
}