package it.valeriovaudi.resume.resumeservice.domain.repository

import it.valeriovaudi.resume.resumeservice.domain.model.LanguageSkills
import org.reactivestreams.Publisher

interface LanguageSkillsRepository {

    fun findOne(resumeId: String): Publisher<LanguageSkills>

    fun save(resumeId: String, languageSkills: LanguageSkills): Publisher<LanguageSkills>

    fun delete(resumeId: String): Publisher<Unit>

}