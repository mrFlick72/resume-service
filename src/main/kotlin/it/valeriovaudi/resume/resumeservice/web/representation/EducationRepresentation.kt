package it.valeriovaudi.resume.resumeservice.web.representation

import it.valeriovaudi.resume.resumeservice.domain.model.Clock.fromLocalDateToString
import it.valeriovaudi.resume.resumeservice.domain.model.Clock.fromStringToLocalDate
import it.valeriovaudi.resume.resumeservice.domain.model.Education
import it.valeriovaudi.resume.resumeservice.domain.model.EducationType
import java.util.*

data class EducationRepresentation(var company: String? = null,
                                   var title: String,
                                   var type: EducationType,
                                   var dateFrom: String? = null,
                                   var dateTo: String? = null) {
    companion object {
        fun fromRepresentationToDomain(educationRepresentation: EducationRepresentation, educationId: String? = null) =
                Education(id = Optional.ofNullable(educationId).orElse(UUID.randomUUID().toString()),
                        title = educationRepresentation.title,
                        type = educationRepresentation.type,
                        company = educationRepresentation.company,
                        dateFrom = fromStringToLocalDate(educationRepresentation.dateFrom),
                        dateTo = fromStringToLocalDate(educationRepresentation.dateTo)
                )

        @Deprecated("not used and probably useles")
        fun fromDomainToRepresentation(education: Education) =
                EducationRepresentation(title = education.title,
                        type = education.type,
                        company = education.company,
                        dateFrom = fromLocalDateToString(education.dateFrom),
                        dateTo = fromLocalDateToString(education.dateTo)
                )
    }
}