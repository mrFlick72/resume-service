package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.LanguageSkills
import it.valeriovaudi.resume.resumeservice.domain.repository.LanguageSkillsRepository
import org.reactivestreams.Publisher
import org.springframework.data.mongodb.core.ReactiveMongoTemplate

class MongoLanguageSkillsRepository(private val mongoTemplate: ReactiveMongoTemplate)  : LanguageSkillsRepository {
    override fun findOne(resumeId: String): Publisher<LanguageSkills> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun save(resumeId: String, languageSkills: LanguageSkills): Publisher<LanguageSkills> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(resumeId: String): Publisher<Unit> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}