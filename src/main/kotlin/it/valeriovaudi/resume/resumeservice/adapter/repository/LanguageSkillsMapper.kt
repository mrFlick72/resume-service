package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.Speaking
import it.valeriovaudi.resume.resumeservice.domain.model.Understanding
import it.valeriovaudi.resume.resumeservice.domain.model.LanguageCapabilityLevel
import it.valeriovaudi.resume.resumeservice.domain.model.LanguageSkill
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

    fun fromDocumentToDomain(document: Document) =
            LanguageSkills(nativeLanguage = document.getString("nativeLanguage"),
                    otherLanguages = (document.getValue("otherLanguages") as List<Map<String, String>>)
                            .map {
                                LanguageSkill(language = it.getOrDefault("language", ""),
                                        understanding = Understanding(listening = getLanguageCapabilityLevel(it, "understanding", "listening"),
                                                reading = getLanguageCapabilityLevel(it, "understanding", "reading")),
                                        speaking = Speaking(interaction = getLanguageCapabilityLevel(it, "understanding", "listening"),
                                                production = getLanguageCapabilityLevel(it, "understanding", "listening")),
                                        writing = LanguageCapabilityLevel.valueOf(it.getOrDefault("writing", "A1"))
                                )
                            })

    private fun getLanguageCapabilityLevel(languageSkill: Map<String, String>, languageSkilFsmily: String, kindOfSkill: String) =
            LanguageCapabilityLevel.valueOf((languageSkill[languageSkilFsmily] as Map<String, String>).getOrDefault(kindOfSkill, "A1"))
}
