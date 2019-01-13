package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.*
import it.valeriovaudi.resume.resumeservice.getStringOrDefault
import org.bson.Document
import java.util.*

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

    fun fromDocumentToDomain(document: Document): LanguageSkills =
            LanguageSkills(nativeLanguage = document.getStringOrDefault("nativeLanguage"),
                    otherLanguages = (document.getValue("otherLanguages") as List<Document>)
                            .map {
                                LanguageSkill(it.getStringOrDefault("language", ""),
                                        understanding = extractUnderstanding(it),
                                        speaking = extractSpeaking(it),
                                        writing = extractWriting(it))

                            })
}


fun Document.getLanguageCapabilityLevelOrDefault(key: String) = Optional.ofNullable(this[key]).map { it.toString() }.map { LanguageCapabilityLevel.valueOf(it) }.orElse(LanguageCapabilityLevel.A1)

fun String.getLanguageCapabilityLevelOrDefault() = Optional.ofNullable(this).map { LanguageCapabilityLevel.valueOf(it) }.orElse(LanguageCapabilityLevel.A1)

private fun extractUnderstanding(document: Document) =
        (document.getValue("understanding") as Document)
                .let {
                    Understanding(listening = it.getLanguageCapabilityLevelOrDefault("listening"), reading = it.getLanguageCapabilityLevelOrDefault("reading"))
                }

private fun extractSpeaking(document: Document) =
        (document.getValue("speaking") as Document)
                .let {
                    Speaking(interaction = it.getLanguageCapabilityLevelOrDefault("interaction"), production = it.getLanguageCapabilityLevelOrDefault("production"))
                }

private fun extractWriting(document: Document) = (document.getString("writing")).getLanguageCapabilityLevelOrDefault()
