package it.valeriovaudi.resume.resumeservice.adapter.repository.mapper

import it.valeriovaudi.resume.resumeservice.domain.model.Clock.dateFormatter
import it.valeriovaudi.resume.resumeservice.domain.model.WorkExperience
import it.valeriovaudi.resume.resumeservice.getStringOrDefault
import org.bson.Document
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

object WorkExperienceMapper {

    fun fromDomainToDocument(resumeId: String, workExperience: WorkExperience) =
            Document(mutableMapOf("_id" to workExperience.id,
                    "resumeId" to resumeId,
                    "company" to workExperience.company,
                    "jobDescription" to workExperience.jobDescription,
                    "startDate" to dateFormatter.format(workExperience.startDate),
                    "endDate" to Optional.ofNullable(workExperience.endDate).map { dateFormatter.format(it) }.orElse(""),
                    "commitments" to workExperience.commitments,
                    "technologies" to workExperience.technologies) as Map<String, Any>?)


    fun fromDocumentToDomain(document: Document) =
            WorkExperience(id = document.getString("_id"),
                    startDate = LocalDate.from(dateFormatter.parse(document.getStringOrDefault("startDate"))),
                    endDate = formatOrNull(dateFormatter, document.getStringOrDefault("endDate")),
                    company = document.getString("company"),
                    jobDescription = document.getString("jobDescription"),
                    commitments = document.getValue("commitments") as List<String>,
                    technologies = document.getValue("technologies") as List<String>)


    private fun formatOrNull(dateFormatter: DateTimeFormatter, dateAsString: String): LocalDate? =
            try {
                LocalDate.from(dateFormatter.parse(dateAsString))
            } catch (e: Exception) {
                null
            }
}