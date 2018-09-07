package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.Skill
import org.bson.Document

object SkillMapper {

    fun fromDomainToDocument(resumeId: String, skill: Skill) =
            Document(mutableMapOf("resumeId" to resumeId,
                    "skillFamily" to skill.family,
                    "skill" to skill.skills) as Map<String, Any>?)


    fun fromDocumentToDomain(document: Document) =
            Skill(document.getString("skillFamily"),
                    document.getValue("skill") as List<String>)

}
