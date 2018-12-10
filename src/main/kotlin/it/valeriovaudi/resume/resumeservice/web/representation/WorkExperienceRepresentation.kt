package it.valeriovaudi.resume.resumeservice.web.representation

import it.valeriovaudi.resume.resumeservice.domain.model.WorkExperience
import java.time.LocalDate
import java.util.*

data class WorkExperienceRepresentation(var startDate: LocalDate,
                                        var endDate: LocalDate? = null,
                                        var company: String = "",
                                        var commitments: List<String> = listOf(),
                                        var jobDescription: String = "",
                                        var technologies: List<String> = listOf()) {
    companion object {
        fun fromRepresentationToDomain(workExperienceRepresentation: WorkExperienceRepresentation, educationId: String? = null) =
                WorkExperience(id = Optional.ofNullable(educationId).orElse(UUID.randomUUID().toString()), startDate = workExperienceRepresentation.startDate,
                        endDate = workExperienceRepresentation.endDate,
                        company = workExperienceRepresentation.company,
                        commitments = workExperienceRepresentation.commitments,
                        jobDescription = workExperienceRepresentation.jobDescription,
                        technologies = workExperienceRepresentation.technologies)
    }
}