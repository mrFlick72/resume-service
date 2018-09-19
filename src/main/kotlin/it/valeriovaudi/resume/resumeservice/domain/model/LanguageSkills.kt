package it.valeriovaudi.resume.resumeservice.domain.model

data class LanguageSkills(val nativeLanguage: String, val otherLanguages: List<LanguageSkill>)

data class LanguageSkill(val language: String, val understanding: Understanding, val speaking: Speaking, val writing: LanguageCapabilityLevel)

data class Understanding(val listening: LanguageCapabilityLevel, val reading: LanguageCapabilityLevel)

data class Speaking(val interaction: LanguageCapabilityLevel, val production: LanguageCapabilityLevel)

enum class LanguageCapabilityLevel { A1, A2, B1, B2, C1, C2 }