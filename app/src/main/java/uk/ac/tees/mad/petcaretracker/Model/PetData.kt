package uk.ac.tees.mad.petcaretracker.Model

data class PetData(
    val documentID: String = "",
    val petName: String = "",
    val species: String = "",
    val breed: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val weight: String = "",
    val notes: String = "",
    val vaccinations: List<String> = emptyList(),
    val image: String = ""
)
