package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.LanguageSkills
import it.valeriovaudi.resume.resumeservice.domain.repository.LanguageSkillsRepository
import org.bson.Document
import org.reactivestreams.Publisher
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import reactor.core.publisher.Mono

class MongoLanguageSkillsRepository(private val mongoTemplate: ReactiveMongoTemplate) : LanguageSkillsRepository {
    companion object {
        fun collectionName() = "languageSkill"
        fun findOneQueryByResumeAndLanguageSkillsId(resumeId: String, languageSkillsId: String) =
                Query.query(Criteria.where("resumeId").isEqualTo(resumeId).and("_id").isEqualTo(languageSkillsId))

        fun findOneQueryByResume(resumeId: String) = Query.query(Criteria.where("resumeId").isEqualTo(resumeId))
        fun findOneQuery(languageSkillsId: String) = Query.query(Criteria.where("_id").isEqualTo(languageSkillsId))
    }

    override fun findOne(resumeId: String): Publisher<LanguageSkills> =
            mongoTemplate.findOne(MongoLanguageSkillsRepository.findOneQueryByResume(resumeId), Document::class.java, MongoLanguageSkillsRepository.collectionName())
                    .map { LanguageSkillsMapper.fromDocumentToDomain(it) }

    override fun save(resumeId: String, languageSkills: LanguageSkills): Mono<LanguageSkills> =
            mongoTemplate.upsert(MongoLanguageSkillsRepository.findOneQuery(resumeId),
                    Update.fromDocument(LanguageSkillsMapper.fromDomainToDocument(resumeId, languageSkills)), MongoLanguageSkillsRepository.collectionName())
                    .flatMap { Mono.just(languageSkills) }

    override fun delete(resumeId: String): Publisher<Unit> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}