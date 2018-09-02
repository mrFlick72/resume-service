package it.valeriovaudi.resume.resumeservice.web.representation

import it.valeriovaudi.resume.resumeservice.domain.model.*

data class ResumeRepresentation(var id: String? = "",
                                var userName: String? = "",
                                var language: String? = Language.EN.name) {
    companion object {
        fun fromDomainToRepresentation(resume: Resume) =
                ResumeRepresentation(id = resume.id, userName = resume.userName, language = resume.language.name)
    }
}


