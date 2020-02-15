package it.valeriovaudi.resume.resumeservice.adapter.repository.mapper

import it.valeriovaudi.resume.resumeservice.domain.model.Clock
import it.valeriovaudi.resume.resumeservice.domain.model.Clock.dateFormatter
import it.valeriovaudi.resume.resumeservice.domain.model.Education
import it.valeriovaudi.resume.resumeservice.domain.model.EducationType
import it.valeriovaudi.resume.resumeservice.getStringOrDefault
import org.bson.Document
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object EducationMapper {

    fun fromDomainToDocument(resumeId: String, education: Education) =
            Document(mutableMapOf("_id" to education.id,
                    "resumeId" to resumeId,
                    "company" to education.company,
                    "title" to education.title,
                    "type" to education.type.name,
                    "dateFrom" to Clock.fromLocalDateToString(education.dateFrom),
                    "dateTo" to Clock.fromLocalDateToString(education.dateTo)) as Map<String, Any>?)


    fun fromDocumentToDomain(document: Document) =
            Education(id = document.getString("_id"),
                    company = document.getStringOrDefault("company", null),
                    type = EducationType.valueOf(document.getStringOrDefault("type")),
                    title = document.getStringOrDefault("title"),
                    dateTo = formatOrNull(dateFormatter, document.getStringOrDefault("dateTo")),
                    dateFrom = formatOrNull(dateFormatter, document.getStringOrDefault("dateFrom")))

    private fun formatOrNull(dateFormatter: DateTimeFormatter, dateAsString: String): LocalDate? =
            try {
                LocalDate.from(dateFormatter.parse(dateAsString))
            } catch (e: Exception) {
                null
            }
}