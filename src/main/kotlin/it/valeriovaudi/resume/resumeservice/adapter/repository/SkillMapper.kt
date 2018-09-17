package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.Skill
import org.bson.Document

object SkillMapper {

    fun fromDomainToDocument(resumeId: String, skills: List<Skill>) =
            Document(mutableMapOf("resumeId" to resumeId,
                    "skills" to skills.map { mapOf("skillFamily" to it.family, "skills" to it.skills) }) as Map<String, Any>?)


    fun fromDocumentToDomain(document: Document) =
            (document.getValue("skills") as List<Document>)
                    .map { Skill(it.getString("skillFamily"), it.getValue("skills") as List<String>) }

}
