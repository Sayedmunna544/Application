package uk.ac.tees.mad.petcaretracker

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    val loading = mutableStateOf(false)

    fun signUp(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        context: Context
    ) {
        val fullName = firstName + " " + lastName;
        viewModelScope.launch {
            loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        firestore.collection("users").document(auth.currentUser!!.uid).set(
                            hashMapOf(
                                "fullName" to fullName,
                                "email" to email,
                                "password" to password
                            )
                        )
                    }
                    loading.value = false
                    Toast.makeText(context, "Sign up successful", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    loading.value = false
                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
        }
    }
}