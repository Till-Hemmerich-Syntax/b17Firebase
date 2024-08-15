package com.syntax.hemmerich.batch17firebase.ui

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.syntax.hemmerich.batch17firebase.MainActivity
import kotlin.system.measureTimeMillis

class MainViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private var _currentUser = MutableLiveData<FirebaseUser?>(firebaseAuth.currentUser)
    val currentUser: LiveData<FirebaseUser?>
        get() = _currentUser

    fun login(email : String, pass: String){
        firebaseAuth.signInWithEmailAndPassword(email,pass)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    Log.d("MainViewModel","Login done")
                    _currentUser.value = it.result.user
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
                    _currentUser.value = it.result.user
                }else{
                    Log.d("MainViewModel","Register failed")
                }
            }
    }

    fun logout(){
        firebaseAuth.signOut()
        _currentUser.value = null

    }
}