package com.example.habits360.utils

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

suspend fun logout(context: Context) {
    // Cerrar sesión de Firebase
    Firebase.auth.signOut()

    // Cerrar sesión de Google y revocar acceso para evitar login automático
    val googleSignInClient = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
    )

    googleSignInClient.signOut().await()
    googleSignInClient.revokeAccess().await()
}
