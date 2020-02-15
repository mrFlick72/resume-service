package it.valeriovaudi.resume.resumeservice

import it.valeriovaudi.resume.resumeservice.domain.model.*
import it.valeriovaudi.resume.resumeservice.web.representation.PersonalDetailsRepresentation
import reactor.core.publisher.Mono
import java.nio.charset.Charset
import java.time.LocalDate

object TestCase {
    val birthDate = LocalDate.of(2018, 8, 30)
    val birthDateStr = "2018-08-30"

    fun personalDetailsRepresentation() = PersonalDetailsRepresentation("firstName",
            "lastName", "address", "zip", "city",
            "region", "mail", "mobile", birthDateStr,
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

    fun readFileAsString(location: String) = this::class.java.classLoader.getResourceAsStream(location).use { it.readAllBytes() }.toString(Charset.defaultCharset())

    fun anEmptyResume(resumeId: String) =
            Resume(resumeId, "A_USER", Language.EN, personalDetailsWithPhoto(), listOf(), listOf())

    fun aResume(resumeId: String) =
            Mono.zip(Mono.just(listOf(Skill("FAMILY", listOf("SKILL_1")))),
                    Mono.just(listOf(Education(id = "1", dateFrom = LocalDate.of(2018, 1, 1), title = "A_TITLE", type = EducationType.CERTIFICATION), Education(id = "2", dateFrom = LocalDate.of(2018, 1, 1), title = "A_TITLE", type = EducationType.CERTIFICATION))),
                    Mono.just(listOf(WorkExperience(id = "1", startDate = LocalDate.of(2018, 1, 1), company = "A_COMPANY", jobDescription = "A_JOB_DESCRIPTION", technologies = listOf("TAEH_1", "TAEH_2"), commitments = listOf("COMMITMENTS_1", "COMMITMENTS_2")), WorkExperience(id = "2", startDate = LocalDate.of(2018, 1, 1), company = "A_COMPANY", jobDescription = "A_JOB_DESCRIPTION", technologies = listOf("TAEH_1", "TAEH_2"), commitments = listOf("COMMITMENTS_1", "COMMITMENTS_2")))))
                    .map {
                        Resume(resumeId, "A_USER", Language.EN, personalDetailsWithPhoto(),
                                skill = it.t1,
                                educations = it.t2,
                                workExperience = it.t3)
                    }.block()

}