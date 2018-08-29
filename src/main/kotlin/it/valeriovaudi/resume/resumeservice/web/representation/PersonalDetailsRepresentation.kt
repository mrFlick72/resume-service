package it.valeriovaudi.resume.resumeservice.web.representation

import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetails
import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetailsPhoto
import it.valeriovaudi.resume.resumeservice.domain.model.Sex
import java.time.LocalDate

data class PersonalDetailsRepresentation(var firstName: String? = "",
                                         var lastName: String? = "",
                                         var address: String? = "",
                                         var zip: String? = "",
                                         var city: String? = "",
                                         var region: String? = "",
                                         var mail: String? = "",
                                         var mobile: String? = "",
                                         var birthDate: LocalDate? = LocalDate.now(),
                                         var state: String? = "",
                                         var sex: Sex? = Sex.NONE,
                                         var taxCode: String? = "") {
    companion object {
        fun fromRepresentationToDomain(personalDetails: PersonalDetailsRepresentation): PersonalDetails  {
            println(personalDetails)
            return PersonalDetails(photo = PersonalDetailsPhoto.emptyPersonalDetailsPhoto(),
                    firstName = personalDetails.firstName!!,
                    lastName = personalDetails.lastName!!,
                    address = personalDetails.address!!,
                    birthDate = personalDetails.birthDate!!,
                    city = personalDetails.city!!,
                    mail = personalDetails.mail!!,
                    mobile = personalDetails.mobile!!,
                    region = personalDetails.region!!,
                    sex = personalDetails.sex!!,
                    state = personalDetails.state!!,
                    taxCode = personalDetails.taxCode!!,
                    zip = personalDetails.zip!!)

        }


        fun fromDomainToRepresentation(personalDetails: PersonalDetails) =
                PersonalDetailsRepresentation(firstName = personalDetails.firstName,
                        lastName = personalDetails.lastName,
                        address = personalDetails.address,
                        birthDate = personalDetails.birthDate,
                        city = personalDetails.city,
                        mail = personalDetails.mail,
                        mobile = personalDetails.mobile,
                        region = personalDetails.region,
                        sex = personalDetails.sex,
                        state = personalDetails.state,
                        taxCode = personalDetails.taxCode,
                        zip = personalDetails.zip)
    }
}


