package it.valeriovaudi.resume.resumeservice.domain.model

import java.time.LocalDate

data class PersonalDetails(val photo: PersonalDetailsPhoto,
                           val firstName: String,
                           val lastName: String,
                           val address: String,
                           val zip: String,
                           val city: String,
                           val region: String,
                           val mail: String,
                           val mobile: String,
                           val birthDate: LocalDate,
                           val state: String,
                           val sex: Sex,
                           val taxCode: String) {
    companion object {
        fun emptyPersonalDetails() = PersonalDetails(photo = PersonalDetailsPhoto.emptyPersonalDetailsPhoto(),
                firstName = "",
                lastName = "",
                address = "",
                zip = "",
                city = "",
                region = "",
                mail = "",
                mobile = "",
                birthDate = LocalDate.MIN,
                state = "",
                sex = Sex.NONE,
                taxCode = "")
    }
}


