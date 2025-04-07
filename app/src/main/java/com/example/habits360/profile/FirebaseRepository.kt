package com.example.habits360.profile

import com.example.habits360.profile.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun saveUserProfile(profile: UserProfile): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        return try {
            db.collection("users").document(uid).collection("profile").document("data")
                .set(profile).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUserProfile(): UserProfile? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            db.collection("users").document(uid).collection("profile").document("data")
                .get().await().toObject(UserProfile::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun isUserProfileComplete(): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        return try {
            val doc = db.collection("users").document(uid)
                .collection("profile").document("data")
                .get().await()
            doc.exists()
        } catch (e: Exception) {
            false
        }
    }

}