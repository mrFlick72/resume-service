package it.valeriovaudi.resume.resumeservice.web.representation

import it.valeriovaudi.resume.resumeservice.domain.model.Education
import it.valeriovaudi.resume.resumeservice.domain.model.EducationType
import java.time.LocalDate
import java.util.*

data class EducationRepresentation(var company: String? = null,
                                   var title: String,
                                   var type: EducationType,
                                   var dateFrom: LocalDate,
                                   var dateTo: LocalDate? = null) {
    companion object {
        fun fromRepresentationToDomain(educationRepresentation: EducationRepresentation, educationId: String? = null) =
                Education(id = Optional.ofNullable(educationId).orElse(UUID.randomUUID().toString()), title = educationRepresentation.title,
                        type = educationRepresentation.type, company = educationRepresentation.company,
                        dateFrom = educationRepresentation.dateFrom, dateTo = educationRepresentation.dateTo)

        @Deprecated("not used and probably useles")
        fun fromDomainToRepresentation(education: Education) =
                EducationRepresentation(title = education.title,
                        type = education.type, company = education.company,
                        dateFrom = education.dateFrom, dateTo = education.dateTo)
    }
}