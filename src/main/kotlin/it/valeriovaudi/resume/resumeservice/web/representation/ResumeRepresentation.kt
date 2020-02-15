package it.valeriovaudi.resume.resumeservice.web.representation

import it.valeriovaudi.resume.resumeservice.domain.model.*
import it.valeriovaudi.resume.resumeservice.web.representation.EducationRepresentation.Companion.fromDomainToRepresentation

data class ResumeRepresentation(var id: String? = "",
                                var userName: String? = "",
                                var language: String? = Language.EN.name,
                                var personalDetails: PersonalDetailsRepresentation,
                                var skill: List<Skill> = listOf(),
                                var education: List<EducationRepresentation> = listOf()) {
    companion object {
        fun fromDomainToRepresentation(resume: Resume) =
                ResumeRepresentation(id = resume.id, userName = resume.userName, language = resume.language.name,
                        personalDetails = PersonalDetailsRepresentation.fromDomainToRepresentation(resume.personalDetails),
                        skill = resume.skill, education = resume.educations.map { fromDomainToRepresentation(it) })
    }
}


