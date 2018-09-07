package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.Language
import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetails
import it.valeriovaudi.resume.resumeservice.domain.model.Resume
import org.bson.Document

object ResumeMapper {

    fun fromDomainToDocument(resume: Resume) =
        Document(mutableMapOf("_id" to resume.id,
                "userName" to resume.userName,
                "language" to resume.language.name) as Map<String, Any>?)


    fun fromDocumentToDomain(document: Document) =
        Resume(id = document.getString("_id"),
                userName = document.getString("userName"),
                language = Language.valueOf(document.getString("language")),
                personalDetails = PersonalDetails.emptyPersonalDetails(),
                skill = listOf())

}
