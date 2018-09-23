package it.valeriovaudi.resume.resumeservice.domain.model

data class Resume(val id: String,
                  val userName: String,
                  val language: Language,
                  val personalDetails: PersonalDetails,
                  val educations: List<Education> = listOf(),
                  val skill: List<Skill> = listOf(),
                  val workExperience: List<WorkExperience> = listOf()) {
    companion object {
        fun emptyResume(id: String, userName: String, language: Language) =
                Resume(id, userName, language, PersonalDetails.emptyPersonalDetails(), listOf(), listOf(), listOf())
    }
}