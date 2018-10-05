package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.Education
import it.valeriovaudi.resume.resumeservice.domain.repository.EducationRepository
import org.bson.Document
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import reactor.core.publisher.Mono

class MongoEducationRepository(private val mongoTemplate: ReactiveMongoTemplate) : EducationRepository {

    companion object {
        fun collectionName() = "education"
        fun findOneQueryByResumeAndEducationId(resumeId: String, educationId: String) =
                Query.query(Criteria.where("resumeId").isEqualTo(resumeId).and("_id").isEqualTo(educationId))

        fun findOneQueryByResume(resumeId: String) = Query.query(Criteria.where("resumeId").isEqualTo(resumeId))
        fun findOneQuery(educationId: String) = Query.query(Criteria.where("_id").isEqualTo(educationId))
    }

    override fun findAll(resumeId: String) =
            mongoTemplate.find(findOneQueryByResume(resumeId), Document::class.java, collectionName())
                    .map { EducationMapper.fromDocumentToDomain(it) }


    override fun save(resumeId: String, education: Education) =
            mongoTemplate.upsert(findOneQuery(educationId = education.id),
                    Update.fromDocument(EducationMapper.fromDomainToDocument(resumeId, education)),
                    collectionName())
                    .map { education }


    override fun delete(resumeId: String, educationId: String) =
            mongoTemplate.remove(findOneQueryByResumeAndEducationId(resumeId, educationId), collectionName())
                    .flatMap { Mono.just(Unit) }
}