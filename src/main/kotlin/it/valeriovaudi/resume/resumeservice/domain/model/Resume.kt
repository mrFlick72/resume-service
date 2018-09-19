package it.valeriovaudi.resume.resumeservice.domain.model

data class Resume(val id: String,
                  val userName: String,
                  val language: Language,
                  val personalDetails: PersonalDetails,
                  val skill: List<Skill>,
                  val workExperience: List<WorkExperience>) {
    companion object {
        fun emptyResume(id: String, userName: String, language: Language) =
                Resume(id, userName, language, PersonalDetails.emptyPersonalDetails(), listOf(), listOf())
    }
}