package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.Skill
import it.valeriovaudi.resume.resumeservice.domain.repository.SkillsRepository
import org.bson.Document
import org.reactivestreams.Publisher
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update.fromDocument
import org.springframework.data.mongodb.core.query.isEqualTo
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux

class MongoSkillsRepository(private val mongoTemplate: ReactiveMongoTemplate) : SkillsRepository {

    companion object {
        fun collectionName() = "skill"
        fun findOneQuery(resumeId: String) = Query.query(Criteria.where("resumeId").`is`(resumeId))
        fun findOneQuery(resumeId: String, skillFamily: String) =
                Query.query(Criteria.where("_id").isEqualTo(Document(mapOf("resumeId" to resumeId, "skillFamily" to skillFamily))))
    }

    override fun findAll(resumeId: String): Publisher<Skill> =
            mongoTemplate.find(findOneQuery(resumeId = resumeId), Document::class.java, collectionName())
                    .map { SkillMapper.fromDocumentToDomain(it) }


    override fun save(resumeId: String, skill: Skill): Publisher<Skill> =
            mongoTemplate.upsert(findOneQuery(resumeId = resumeId, skillFamily = skill.family),
                    fromDocument(SkillMapper.fromDomainToDocument(resumeId, skill)), collectionName())
                    .map { skill }

    override fun save(resumeId: String, skill: List<Skill>): Publisher<Skill> =
            skill.toFlux().flatMap { save(resumeId, it) }

    override fun delete(resumeId: String, skillFamily: String): Publisher<Unit> =
            mongoTemplate.remove(findOneQuery(resumeId, skillFamily), collectionName()).flatMap { Mono.just(Unit) }

}