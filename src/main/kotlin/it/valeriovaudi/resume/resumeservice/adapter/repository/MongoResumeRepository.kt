package it.valeriovaudi.resume.resumeservice.adapter.repository

import com.mongodb.client.result.UpdateResult
import it.valeriovaudi.resume.resumeservice.domain.model.Language
import it.valeriovaudi.resume.resumeservice.domain.model.Resume
import it.valeriovaudi.resume.resumeservice.domain.repository.ResumeRepository
import org.reactivestreams.Publisher
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import reactor.core.publisher.Mono

class MongoResumeRepository(private val mongoTemplate: ReactiveMongoTemplate) : ResumeRepository {

    companion object {
        fun collectionName() = "resume"
        fun findOneQuery(resumeId: String) = Query.query(Criteria.where("_id").`is`(resumeId))
    }


    override fun findOne(resumeId: String): Publisher<Resume> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findOneByUserName(userName: String, language: Language): Publisher<Resume> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun save(resume: Resume): Publisher<Resume> =
            mongoTemplate.upsert(findOneQuery(resumeId = resume.id),
                    Update.fromDocument(ResumeMapper.fromDomainToDocument(resume)), collectionName())
                    .onErrorResume { println("Error at ${it}"); Mono.just(UpdateResult.unacknowledged()) }
                    .map { resume }


    override fun delete(resumeId: String): Publisher<Unit> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}