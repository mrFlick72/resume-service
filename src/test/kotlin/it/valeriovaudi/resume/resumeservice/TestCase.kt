package it.valeriovaudi.resume.resumeservice

import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetails
import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetailsPhoto
import it.valeriovaudi.resume.resumeservice.domain.model.Sex
import it.valeriovaudi.resume.resumeservice.web.representation.PersonalDetailsRepresentation
import java.nio.charset.Charset
import java.time.LocalDate

object TestCase {
    val birthDate = LocalDate.of(2018, 8, 30)
    
    fun personalDetailsRepresentation() = PersonalDetailsRepresentation("firstName",
            "lastName", "address", "zip", "city",
            "region", "mail", "mobile", birthDate,
            "country", Sex.NONE, "taxCode")

    fun personalDetails() = PersonalDetails(PersonalDetailsPhoto.emptyPersonalDetailsPhoto(), "firstName",
            "lastName", "address", "zip", "city",
            "region", "mail", "mobile", birthDate,
            "country", Sex.NONE, "taxCode")

    fun personalDetailsWithPhoto() = this::class.java.classLoader.getResourceAsStream("barca-a-vela.jpg").use {
        
        PersonalDetails(PersonalDetailsPhoto(it.readAllBytes(), "jpg"), "firstName",
                "lastName", "address", "zip", "city",
                "region", "mail", "mobile", birthDate,
                "country", Sex.NONE, "taxCode")
    }

    fun readFileAsString(location : String) = this::class.java.classLoader.getResourceAsStream(location).use { it.readAllBytes() }.toString(Charset.defaultCharset())

}