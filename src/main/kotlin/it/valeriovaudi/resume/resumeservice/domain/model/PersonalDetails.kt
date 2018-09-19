package it.valeriovaudi.resume.resumeservice.domain.model

import java.time.LocalDate
import java.util.*

data class PersonalDetails(val photo: PersonalDetailsPhoto,
                           val firstName: String,
                           val lastName: String,
                           val address: String,
                           val zip: String,
                           val city: String,
                           val region: String,
                           val mail: String,
                           val mobile: String,
                           val birthDate: LocalDate? = null,
                           val country: String,
                           val sex: Sex,
                           val taxCode: String) {
    companion object {
        fun emptyPersonalDetails() = PersonalDetails(photo = PersonalDetailsPhoto.emptyPersonalDetailsPhoto(),
                firstName = "", lastName = "", address = "", zip = "", city = "", region = "", mail = "",
                mobile = "", birthDate = null, country = "", sex = Sex.NONE, taxCode = "")
    }

    fun isEmpty() = this.photo.isEmpty() && firstName.isEmpty() && lastName.isEmpty() && address.isEmpty()
            && zip.isEmpty() && city.isEmpty() && region.isEmpty() && mail.isEmpty() && mobile.isEmpty()
            && birthDate == null && country.isEmpty() && sex == Sex.NONE && taxCode.isEmpty()
}


data class PersonalDetailsPhoto(val content: ByteArray,
                                val fileExtension: String) {

    companion object {
        fun emptyPersonalDetailsPhoto() = PersonalDetailsPhoto(ByteArray(0), "jpeg")
    }

    fun isEmpty() = this.content.isEmpty();

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PersonalDetailsPhoto

        if (!Arrays.equals(content, other.content)) return false
        if (fileExtension != other.fileExtension) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(content)
        result = 31 * result + fileExtension.hashCode()
        return result
    }
}

enum class Sex { M, F, NONE }