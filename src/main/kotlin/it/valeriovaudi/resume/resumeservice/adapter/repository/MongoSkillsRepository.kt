package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.Skill
import it.valeriovaudi.resume.resumeservice.domain.repository.SkillsRepository
import org.bson.Document
import org.reactivestreams.Publisher
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update.fromDocument
import reactor.core.publisher.toFlux

class MongoSkillsRepository(private val mongoTemplate: ReactiveMongoTemplate) : SkillsRepository {

    companion object {
        fun collectionName() = "skill"
        fun findOneQuery(resumeId: String) = Query.query(Criteria.where("resumeId").`is`(resumeId))
        fun findOneQuery(resumeId: String, skillFamily: String) = Query.query(Criteria.where("resumeId").`is`(resumeId).and("skillFamily").`is`(skillFamily))
    }

    override fun findOne(resumeId: String): Publisher<Skill> =
            mongoTemplate.find(findOneQuery(resumeId = resumeId), Document::class.java, collectionName())
                    .map { SkillMapper.fromDocumentToDomain(it) }

    override fun save(resumeId: String, vararg skills: Skill): Publisher<Skill> =
            skills.toFlux().flatMap { skill ->
                mongoTemplate.upsert(findOneQuery(resumeId = resumeId, skillFamily = skill.family), fromDocument(SkillMapper.fromDomainToDocument(resumeId, skill)), collectionName())
                        .map { skill }
            }


    override fun delete(resumeId: String, skillFamily: String): Publisher<Unit> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}