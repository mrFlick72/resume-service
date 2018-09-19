package it.valeriovaudi.resume.resumeservice.domain.model

import java.time.LocalDate

data class Education(val company : String? = null,
                     val title : String,
                     val type : EducationType,
                     val dateFrom : LocalDate,
                     val dateTo : LocalDate? = null)


enum class EducationType { HING_SCOOL, BARCHELOR_DEGREE, MASTER_DEGREE, PROFESSIONAL_COURSE, CERTIFICATION }
