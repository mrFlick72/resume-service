package it.valeriovaudi.resume.resumeservice.domain.model

import java.time.LocalDate

data class WorkExperience(val startDate : LocalDate,
                          val endDate : LocalDate? = null,
                          val company : String,
                          val commitments: List<String>,
                          val jobDescription: String,
                          val technologies: List<String>)