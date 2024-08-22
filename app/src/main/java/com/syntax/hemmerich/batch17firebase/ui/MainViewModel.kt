package com.syntax.hemmerich.batch17firebase.ui

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
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
    private lateinit var firebaseAnalytics : FirebaseAnalytics

    lateinit var profileRef: DocumentReference

    private var _currentUser = MutableLiveData<FirebaseUser?>(firebaseAuth.currentUser)
    val currentUser: LiveData<FirebaseUser?>
        get() = _currentUser


    init {
        if(firebaseAuth.currentUser != null){
            setUpUserEnv()
        }
        firebaseAnalytics = Firebase.analytics
    }

    //Diese Funktion nutzt Firebase Auth um einen Nutzer ein zuloggen und logged ein Analytics Event (Login)
    fun login(email : String, pass: String){
        firebaseAuth.signInWithEmailAndPassword(email,pass)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    Log.d("MainViewModel","Login done")
                    setUpUserEnv()
                    //hier loggen wir das Login Even des Nutzers
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN) {
                        param(FirebaseAnalytics.Param.ITEM_ID, _currentUser.value?.email!!)
                        param(FirebaseAnalytics.Param.ITEM_NAME, "Email")
                    }



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
                    //Beim registrieren eines Nutzers müssen wir ein Leeres Document erstellen
                    setupNewProfile()
                }else{
                    Log.d("MainViewModel","Register failed")
                }
            }
    }

    //Holt sich denn aktuellen Auth user und holen uns die Referenze
    private fun setUpUserEnv(){
        _currentUser.value = firebaseAuth.currentUser
        profileRef = firebaseFireStore.collection("profile").document(firebaseAuth.currentUser?.uid!!)
    }

    //Initialisiert ein neues leeres Profile Doc im Firestore
    private fun setupNewProfile(){
        profileRef.set(Profile())
    }

    fun logout(){
        firebaseAuth.signOut()
        _currentUser.value = null

    }

    //Updated das Profile im Firestore
    fun updateProfile(profile: Profile){
        profileRef.set(profile)
    }

    fun deleteUser(){

    }
    fun uploadImage(uri: Uri){
        val imageRef = firebaseStrorage.reference.child("images/${currentUser.value?.uid}/test")
        val uploadTask = imageRef.putFile(uri)
        //Um die URL des hochgeladen Images in unseren user zu schreiben müssen wir wissen wann der Upload fertig ist :)
        uploadTask.addOnCompleteListener{
            imageRef.downloadUrl.addOnCompleteListener {
                if(it.isSuccessful){
                    //Sobald wir die URl herruntergeladen haben können wir diese in das User Object schreiben :)
                    setImage(it.result)
                }
            }
        }
    }

    private fun setImage(uri: Uri){
        //Updaten des Image Pfades im Firestore :)
        profileRef.update("profilePicture",uri.toString())
    }
}