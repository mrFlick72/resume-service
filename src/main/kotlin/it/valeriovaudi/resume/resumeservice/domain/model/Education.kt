package it.valeriovaudi.resume.resumeservice.domain.model

import java.time.LocalDate

data class Education(val id: String,
                     val company: String? = null,
                     val title: String,
                     val type: EducationType,
                     val dateFrom: LocalDate? = null,
                     val dateTo: LocalDate? = null)


enum class EducationType { HING_SCHOOL, BACHELOR_DEGREE, MASTER_DEGREE, PROFESSIONAL_COURSE, CERTIFICATION }
