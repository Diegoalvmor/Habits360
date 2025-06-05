package com.example.habits360.features.profile

import com.example.habits360.features.profile.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun saveUserProfile(profile: UserProfile): Boolean {
        if (profile.userId.isBlank()) return false
        return try {
            db.collection("users").document(profile.userId).collection("Profile").document("data")
                .set(profile).await()
            true
        } catch (e: Exception) {
            false
        }
    }



    suspend fun isUserProfileComplete(): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        return try {
            val doc = db.collection("users").document(uid)
                .collection("Profile").document("data")
                .get().await()
            doc.exists()
        } catch (e: Exception) {
            false
        }
    }

}