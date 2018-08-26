package it.valeriovaudi.resume.resumeservice.adapter.persistance

import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetails
import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetailsPhoto
import it.valeriovaudi.resume.resumeservice.domain.model.Sex
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "personalDetails")
data class PersonalDetailsPersistanceModel(@Id var id: String,
                                           var firstName: String,
                                           var lastName: String,
                                           var address: String,
                                           var zip: String,
                                           var city: String,
                                           var region: String,
                                           var mail: String,
                                           var mobile: String,
                                           var birthDate: LocalDate,
                                           var state: String,
                                           var sex: String,
                                           var taxCode: String) {
    companion object {
        fun fromDomainToPersistanceModel(resumeId : String, personalDetails: PersonalDetails) =
                PersonalDetailsPersistanceModel(
                        id = resumeId,
                        firstName = personalDetails.firstName,
                        lastName = personalDetails.lastName,
                        address = personalDetails.address,
                        birthDate = personalDetails.birthDate,
                        city = personalDetails.city,
                        mail = personalDetails.mail,
                        mobile = personalDetails.mobile,
                        region = personalDetails.region,
                        sex = personalDetails.sex.name,
                        state = personalDetails.state,
                        taxCode = personalDetails.taxCode,
                        zip = personalDetails.zip)

        fun fromPersistanceModelToDomain() {

        }
    }
}


