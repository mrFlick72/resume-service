package it.valeriovaudi.resume.resumeservice.domain.model

data class Resume(val id: String,
                  val userName: String,
                  val language: Language,
                  val personalDetails: PersonalDetails)