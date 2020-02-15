package it.valeriovaudi.resume.resumeservice.web.representation

import it.valeriovaudi.resume.resumeservice.domain.model.Clock
import it.valeriovaudi.resume.resumeservice.domain.model.Clock.fromStringToLocalDate
import it.valeriovaudi.resume.resumeservice.domain.model.WorkExperience
import java.time.LocalDate
import java.util.*

data class WorkExperienceRepresentation(var startDate: String,
                                        var endDate: String? = null,
                                        var company: String = "",
                                        var commitments: List<String> = listOf(),
                                        var jobDescription: String = "",
                                        var technologies: List<String> = listOf()) {
    companion object {
        fun fromRepresentationToDomain(workExperienceRepresentation: WorkExperienceRepresentation, educationId: String? = null) =
                WorkExperience(id = Optional.ofNullable(educationId).orElse(UUID.randomUUID().toString()),
                        startDate = fromStringToLocalDate(workExperienceRepresentation.startDate),
                        endDate = fromStringToLocalDate(workExperienceRepresentation.endDate),
                        company = workExperienceRepresentation.company,
                        commitments = workExperienceRepresentation.commitments,
                        jobDescription = workExperienceRepresentation.jobDescription,
                        technologies = workExperienceRepresentation.technologies)
    }
}