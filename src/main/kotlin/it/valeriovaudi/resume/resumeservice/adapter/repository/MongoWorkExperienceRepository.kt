package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.adapter.repository.mapper.WorkExperienceMapper
import it.valeriovaudi.resume.resumeservice.domain.model.WorkExperience
import it.valeriovaudi.resume.resumeservice.domain.repository.WorkExperienceRepository
import org.bson.Document
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import reactor.core.publisher.Mono

class MongoWorkExperienceRepository(private val mongoTemplate: ReactiveMongoTemplate) : WorkExperienceRepository {

    companion object {
        fun collectionName() = "workExperience"
        fun findOneQueryByResume(resumeId: String) = Query.query(Criteria.where("resumeId").`is`(resumeId))
        fun findOneQuery(resumeId: String, workExperienceId: String) =
                Query.query(Criteria.where("_id").isEqualTo(workExperienceId).and("resumeId").isEqualTo(resumeId))
    }

    override fun findOne(resumeId: String, workExperienceId: String) =
            mongoTemplate.findOne(findOneQuery(resumeId, workExperienceId), Document::class.java, collectionName())
                    .map { WorkExperienceMapper.fromDocumentToDomain(it) }


    override fun findAll(resumeId: String) =
            mongoTemplate.find(findOneQueryByResume(resumeId), Document::class.java, collectionName())
                    .map { WorkExperienceMapper.fromDocumentToDomain(it) }

    override fun save(resumeId: String, workExperience: WorkExperience) =
            mongoTemplate.upsert(findOneQuery(resumeId = resumeId, workExperienceId = workExperience.id),
                    Update.fromDocument(WorkExperienceMapper.fromDomainToDocument(resumeId, workExperience)),
                    collectionName())
                    .map { workExperience }


    override fun delete(resumeId: String, workExperienceId: String) =
            mongoTemplate.remove(findOneQuery(resumeId = resumeId, workExperienceId = workExperienceId), collectionName())
                    .flatMap { Mono.just(Unit) }


}