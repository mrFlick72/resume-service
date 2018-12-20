package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.LanguageSkills
import org.bson.Document

object LanguageSkillsMapper {
    fun fromDomainToDocument(resumeId: String, languageSkills: LanguageSkills): Document =
            Document(mutableMapOf("resumeId" to resumeId,
                    "nativeLanguage" to languageSkills.nativeLanguage,
                    "otherLanguages" to languageSkills.otherLanguages
                            .map {
                                mapOf("language" to it.language,
                                        "speaking" to mapOf("interaction" to it.speaking.interaction, "production" to it.speaking.production),
                                        "understanding" to mapOf("listening" to it.understanding.listening, "reading" to it.understanding.reading),
                                        "writing" to it.writing)
                            }) as Map<String, Any>?)
}
