package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.WorkExperience
import it.valeriovaudi.resume.resumeservice.domain.repository.WorkExperienceRepository
import org.bson.Document
import org.reactivestreams.Publisher
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
        fun findOneQuery(workExperienceId: String) = Query.query(Criteria.where("_id").isEqualTo(workExperienceId))
    }

    override fun findOne(workExperienceId: String): Publisher<WorkExperience> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findAll(resumeId: String) =
            mongoTemplate.find(findOneQueryByResume(resumeId), Document::class.java, collectionName())
                    .map { WorkExperienceMapper.fromDocumentToDomain(it) }

    override fun save(resumeId: String, workExperience: WorkExperience) =
            mongoTemplate.upsert(findOneQuery(workExperience.id),
                    Update.fromDocument(WorkExperienceMapper.fromDomainToDocument(resumeId, workExperience)),
                    collectionName())
                    .map { workExperience }


    override fun delete(workExperienceId: String) =
            mongoTemplate.remove(findOneQuery(workExperienceId), collectionName())
                    .flatMap { Mono.just(Unit) }


}