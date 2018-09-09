package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetails
import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetailsPhoto
import it.valeriovaudi.resume.resumeservice.domain.model.Sex
import org.bson.Document
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

object PersonalDetailsMapper {

    val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

    fun fromDomainToDocument(resumeId: String, personalDetails: PersonalDetails) =
            Document(mutableMapOf("resumeId" to resumeId,
                    "firstName" to personalDetails.firstName,
                    "lastName" to personalDetails.lastName,
                    "address" to personalDetails.address,
                    "zip" to personalDetails.zip,
                    "city" to personalDetails.city,
                    "region" to personalDetails.region,
                    "mail" to personalDetails.mail,
                    "mobile" to personalDetails.mobile,
                    "birthDate" to Optional.ofNullable(personalDetails.birthDate).map { dateFormatter.format(it) }.orElse(""),
                    "state" to personalDetails.state,
                    "sex" to personalDetails.sex.name,
                    "taxCode" to personalDetails.taxCode) as Map<String, Any>?)


    fun fromDocumentToDomain(photo: PersonalDetailsPhoto = PersonalDetailsPhoto.emptyPersonalDetailsPhoto(),
                             document: Document) =
            PersonalDetails(photo = photo,
                    zip = document.getString("zip"),
                    taxCode = document.getString("taxCode"),
                    state = document.getString("state"),
                    sex = Sex.valueOf(document.getString("sex")),
                    region = document.getString("region"),
                    mobile = document.getString("mobile"),
                    mail = document.getString("mail"),
                    city = document.getString("city"),
                    birthDate = formatOrNull(dateFormatter, document.getString("birthDate")),
                    address = document.getString("address"),
                    lastName = document.getString("lastName"),
                    firstName = document.getString("firstName"))

    private fun formatOrNull(dateFormatter: DateTimeFormatter, dateAsString: String): LocalDate? =
            try { LocalDate.from(dateFormatter.parse(dateAsString)) } catch (e: Exception) { null }
}
