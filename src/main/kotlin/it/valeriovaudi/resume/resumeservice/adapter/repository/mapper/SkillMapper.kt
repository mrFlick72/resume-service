package it.valeriovaudi.resume.resumeservice.adapter.repository.mapper

import it.valeriovaudi.resume.resumeservice.domain.model.Skill
import org.bson.Document

object SkillMapper {

    fun fromDomainToDocument(resumeId: String, skill: Skill) =
            Document(mutableMapOf("_id" to Document(mapOf("resumeId" to resumeId, "skillFamily" to skill.family)),
                    "resumeId" to resumeId,
                    "skills" to skill.skills) as Map<String, Any>?)


    fun fromDocumentToDomain(document: Document) = Skill(family = (document.getValue("_id") as Document).getString("skillFamily"), skills = document.getValue("skills") as List<String>)


}
