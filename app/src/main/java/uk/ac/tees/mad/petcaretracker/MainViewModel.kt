package uk.ac.tees.mad.petcaretracker

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uk.ac.tees.mad.petcaretracker.Data.MeowResponse
import uk.ac.tees.mad.petcaretracker.Data.PetApi
import uk.ac.tees.mad.petcaretracker.Data.User
import uk.ac.tees.mad.petcaretracker.Model.PetData
import java.io.File
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MainViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val petApi: PetApi
) : ViewModel() {

    val _isLoggedIn = mutableStateOf(false)
    val petData = mutableStateOf<List<PetData>>(listOf());
    val facts = mutableStateOf(MeowResponse("", ""))
    val userData = mutableStateOf<User>(User())

    init {
        if (auth.currentUser != null) {
            _isLoggedIn.value = true
            fetchPetData()
            fetchUser()
        }
    }

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
                        firestore.collection("users").document(auth.currentUser!!.uid).collection("user_details").add(
                            hashMapOf(
                                "fullName" to fullName,
                                "email" to email,
                                "password" to password
                            )
                        ).addOnSuccessListener {
                            loading.value = false
                            Toast.makeText(context, "Sign up successful", Toast.LENGTH_SHORT).show()
                            _isLoggedIn.value = true
                            fetchPetData()
                        }.addOnFailureListener {
                            loading.value = false
                            Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .addOnFailureListener {
                    loading.value = false
                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun fetchUser() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            firestore.collection("users")
                .document(uid)
                .collection("user_details")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents.first()
                        val user = document.toObject(User::class.java)
                        if (user != null) {
                            userData.value = user
                            Log.d("User_Data", userData.value.toString())
                        }
                    } else {
                        Log.d("User_Data", "No documents found in user_details")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("error", "Error fetching user: ${exception.localizedMessage}")
                }
        } else {
            Log.d("error", "User ID is null")
        }
    }

    fun updateUserData(context: Context,userNamer: String){
        userData.value.fullName = userNamer
        firestore.collection("users").document(auth.currentUser!!.uid).collection("user_details").get().addOnSuccessListener {
            queryDocumentSnapshots ->
            queryDocumentSnapshots.documents.first().reference.update(
                "fullName", userNamer
            ).addOnSuccessListener {
                Toast.makeText(context, "User name updated successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun logOut() {
        auth.signOut()
        _isLoggedIn.value = false
    }

    fun updatePet(
        context: Context,
        documentID: String,
        petName: String,
        petBreed: String,
        petSpecies: String,
        petWeight: String,
        petNotes: String
    ) {
        firestore.collection("users").document(auth.currentUser!!.uid).collection("user_pets").document(documentID)
            .update( hashMapOf(
                "petName" to petName,
                "breed" to petBreed,
                "species" to petSpecies,
                "weight" to petWeight,
                "notes" to petNotes
            ) as Map<String, Any>
            ).addOnSuccessListener {
                Toast.makeText(context, "Pet updated successfully", Toast.LENGTH_SHORT).show()
                fetchPetData()
            }.addOnFailureListener {
                Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }

    fun logIn(email: String, password: String, context: Context) {
        viewModelScope.launch {
            loading.value = true

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        loading.value = false
                        _isLoggedIn.value = true
                        Toast.makeText(context, "Log in successful", Toast.LENGTH_SHORT).show()
                        fetchPetData()
                    }
                }
                .addOnFailureListener {
                    loading.value = false
                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun saveImageToDevice(context: Context,image: Bitmap, fileName: String): Uri?{
        return try {
            val file = File(context.filesDir, fileName)
            val outputStream = file.outputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
            null
        }
    }

    fun savePetData(
        context: Context,
        bitmap: Bitmap,
        petName: String,
        species: String,
        breed: String,
        dateOfBirth: String,
        gender: String,
        weight: String,
        notes: String,
        vaccinations: SnapshotStateList<String>,
        onSuccess: () -> Unit
    ){
        viewModelScope.launch {
            loading.value = true
            val uid = auth.currentUser!!.uid
            val fileName = uid + System.currentTimeMillis().toString() + ".jpg"
            val uri = saveImageToDevice(context, bitmap, fileName)
            if(uri != null){
                firestore.collection("users").document(auth.currentUser!!.uid).collection("user_pets").add(
                    hashMapOf(
                        "documentID" to null,
                        "petName" to petName,
                        "species" to species,
                        "breed" to breed,
                        "dateOfBirth" to dateOfBirth,
                        "gender" to gender,
                        "weight" to weight,
                        "notes" to notes,
                        "vaccinations" to vaccinations,
                        "image" to uri.toString()
                    )
                ).addOnSuccessListener {
                    it.update("documentID", it.id).addOnSuccessListener {
                    Toast.makeText(context, "Pet added successfully", Toast.LENGTH_SHORT).show()
                    loading.value = false
                    fetchPetData()
                        onSuccess()
                    }.addOnFailureListener {
                        Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
                        loading.value = false
                    }
                }.addOnFailureListener {
                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
                    loading.value = false
                }
            }
        }
    }

    fun fetchPetData(){
        viewModelScope.launch {
            firestore.collection("users").document(auth.currentUser!!.uid).collection("user_pets").get().addOnSuccessListener {
                petData.value = it.toObjects(PetData::class.java)
                Log.d("Pet_Data", it.toObjects(PetData::class.java).toString())
            }.addOnFailureListener {
                Log.d("error", it.localizedMessage)
            }
            fetchFacts()
        }
    }

    fun addVaccine(context: Context,documentId: String, vaccine: String, dosage: String){
        firestore.collection("users").document(auth.currentUser!!.uid).collection("user_pets").document(documentId).update(
            "vaccinations", FieldValue.arrayUnion(vaccine + " - " + dosage)
        ).addOnSuccessListener {
            Toast.makeText(context, "Vaccine added successfully", Toast.LENGTH_SHORT).show()
            fetchPetData()
        }.addOnFailureListener {
            Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    fun deletePet(context: Context, documentId: String){
        firestore.collection("users").document(auth.currentUser!!.uid).collection("user_pets").document(documentId).delete().addOnSuccessListener {
            Toast.makeText(context, "Pet deleted successfully", Toast.LENGTH_SHORT).show()
            fetchPetData()
        }.addOnFailureListener {
            Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }
    suspend fun fetchFacts(){
        val animalGuess = Random.nextInt(1,3)
        when(animalGuess){
            1 -> facts.value = petApi.getFacts("cat")
            2 -> facts.value = petApi.getFacts("dog")
            3 -> facts.value = petApi.getFacts("bird")
        }
    }
}