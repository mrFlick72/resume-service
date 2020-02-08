package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.adapter.repository.mapper.LanguageSkillsMapper
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
        fun findOneQueryByResume(resumeId: String) = Query.query(Criteria.where("resumeId").isEqualTo(resumeId))
    }

    override fun findOne(resumeId: String): Publisher<LanguageSkills> =
            mongoTemplate.find(MongoSkillsRepository.findOneQuery(resumeId = resumeId), Document::class.java, collectionName())
                    .map { LanguageSkillsMapper.fromDocumentToDomain(it) }
                    .onErrorResume { println("Error at ${it}"); Mono.just(LanguageSkills("", listOf())) }


    override fun save(resumeId: String, languageSkills: LanguageSkills): Mono<LanguageSkills> =
            mongoTemplate.upsert(findOneQueryByResume(resumeId),
                    Update.fromDocument(LanguageSkillsMapper.fromDomainToDocument(resumeId, languageSkills)), collectionName())
                    .flatMap { Mono.just(languageSkills) }

    override fun delete(resumeId: String): Publisher<Unit> =
            mongoTemplate.remove(findOneQueryByResume(resumeId), collectionName())
                    .flatMap { Mono.just(Unit) }
}