package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.Education
import it.valeriovaudi.resume.resumeservice.domain.repository.EducationRepository
import org.reactivestreams.Publisher
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

class MongoEducationRepository(private val mongoTemplate: ReactiveMongoTemplate) : EducationRepository {

    companion object {
        fun collectionName() = "education"
        fun findOneQuery(resumeId: String) = Query.query(Criteria.where("resumeId").`is`(resumeId))
    }

    override fun findAll(resumeId: String): Publisher<Education> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun save(resumeId: String, education: Education): Publisher<Education> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(resumeId: String, educationTitle: String): Publisher<Unit> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}