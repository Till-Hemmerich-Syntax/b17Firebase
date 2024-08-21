package com.syntax.hemmerich.batch17firebase.ui

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.syntax.hemmerich.batch17firebase.MainActivity
import com.syntax.hemmerich.batch17firebase.model.Profile
import java.net.URI
import kotlin.system.measureTimeMillis

class MainViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseStrorage = FirebaseStorage.getInstance()
    private val firebaseFireStore = FirebaseFirestore.getInstance()


    lateinit var profileRef: DocumentReference

    private var _currentUser = MutableLiveData<FirebaseUser?>(firebaseAuth.currentUser)
    val currentUser: LiveData<FirebaseUser?>
        get() = _currentUser


    init {
        if(firebaseAuth.currentUser != null){
            setUpUserEnv()
        }
    }

    fun login(email : String, pass: String){
        firebaseAuth.signInWithEmailAndPassword(email,pass)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    Log.d("MainViewModel","Login done")
                    setUpUserEnv()
                }else{
                    Log.d("MainViewModel","Login failed")
                }
            }
    }

    fun register(email : String, pass: String){
        firebaseAuth.createUserWithEmailAndPassword(email,pass)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    Log.d("MainViewModel","Register done")
                    setUpUserEnv()
                    setupNewProfile()
                }else{
                    Log.d("MainViewModel","Register failed")
                }
            }
    }

    private fun setUpUserEnv(){
        _currentUser.value = firebaseAuth.currentUser
        profileRef = firebaseFireStore.collection("profile").document(firebaseAuth.currentUser?.uid!!)
    }

    private fun setupNewProfile(){
        profileRef.set(Profile())
    }

    fun logout(){
        firebaseAuth.signOut()
        _currentUser.value = null

    }

    fun updateProfile(profile: Profile){
        profileRef.set(profile)
    }

    fun deleteUser(){

    }
    fun uploadImage(uri: Uri){
        val imageRef = firebaseStrorage.reference.child("images/${currentUser.value?.uid}/test")
        val uploadTask = imageRef.putFile(uri)

        uploadTask.addOnCompleteListener{
            imageRef.downloadUrl.addOnCompleteListener {
                if(it.isSuccessful){
                    setImage(it.result)
                }
            }
        }
    }

    private fun setImage(uri: Uri){
        profileRef.update("profilePicture",uri.toString())
    }
}