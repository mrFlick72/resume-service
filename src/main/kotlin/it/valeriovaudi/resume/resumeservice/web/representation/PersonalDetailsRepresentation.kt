package it.valeriovaudi.resume.resumeservice.web.representation

import it.valeriovaudi.resume.resumeservice.domain.model.Clock.dateFormatter
import it.valeriovaudi.resume.resumeservice.domain.model.Clock.fromLocalDateToString
import it.valeriovaudi.resume.resumeservice.domain.model.Clock.fromStringToLocalDate
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
                                         var birthDate: String? = null,
                                         var country: String? = "",
                                         var sex: Sex? = Sex.NONE,
                                         var taxCode: String? = "") {
    companion object {
        fun fromRepresentationToDomain(personalDetails: PersonalDetailsRepresentation): PersonalDetails  {
            return PersonalDetails(photo = PersonalDetailsPhoto.emptyPersonalDetailsPhoto(),
                    firstName = personalDetails.firstName!!,
                    lastName = personalDetails.lastName!!,
                    address = personalDetails.address!!,
                    birthDate = fromStringToLocalDate(personalDetails.birthDate),
                    city = personalDetails.city!!,
                    mail = personalDetails.mail!!,
                    mobile = personalDetails.mobile!!,
                    region = personalDetails.region!!,
                    sex = personalDetails.sex!!,
                    country = personalDetails.country!!,
                    taxCode = personalDetails.taxCode!!,
                    zip = personalDetails.zip!!)

        }


        fun fromDomainToRepresentation(personalDetails: PersonalDetails) =
                PersonalDetailsRepresentation(firstName = personalDetails.firstName,
                        lastName = personalDetails.lastName,
                        address = personalDetails.address,
                        birthDate = fromLocalDateToString(personalDetails.birthDate),
                        city = personalDetails.city,
                        mail = personalDetails.mail,
                        mobile = personalDetails.mobile,
                        region = personalDetails.region,
                        sex = personalDetails.sex,
                        country = personalDetails.country,
                        taxCode = personalDetails.taxCode,
                        zip = personalDetails.zip)
    }
}


