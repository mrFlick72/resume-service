package it.valeriovaudi.resume.resumeservice.adapter.repository

import com.mongodb.client.result.UpdateResult
import it.valeriovaudi.resume.resumeservice.domain.model.Language
import it.valeriovaudi.resume.resumeservice.domain.model.Resume
import it.valeriovaudi.resume.resumeservice.domain.repository.ResumeRepository
import org.bson.Document
import org.reactivestreams.Publisher
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

class MongoResumeRepository(private val mongoTemplate: ReactiveMongoTemplate,
                            private val personalDetailsRepository: MongoPersonalDetailsRepository) : ResumeRepository {

    companion object {
        fun collectionName() = "resume"
        fun findOneQuery(resumeId: String) = Query.query(Criteria.where("_id").`is`(resumeId))
        fun findByUserNameAndLanguageQuery(userName: String, language: Language) =
                Query.query(Criteria.where("userName").`is`(userName).and("language").`is`(language.name))
    }

    override fun findOne(resumeId: String): Publisher<Resume> =
            mongoTemplate.findOne(findOneQuery(resumeId), Document::class.java, collectionName())
                    .map { ResumeMapper.fromDocumentToDomain(it) }
                    .flatMap { resume ->
                        loadPersonalDetailsFor(resumeId).toMono()
                                .map { Resume(id = resume.id, userName = resume.userName, language = resume.language, personalDetails = it) }
                    }

    override fun findOneByUserName(userName: String, language: Language): Publisher<Resume> =
            mongoTemplate.findOne(findByUserNameAndLanguageQuery(userName, language), Document::class.java, collectionName())
                    .map { ResumeMapper.fromDocumentToDomain(it) }
                    .flatMap { resume ->
                        loadPersonalDetailsFor(resume.id).toMono()
                                .map { Resume(id = resume.id, userName = resume.userName, language = resume.language, personalDetails = it) }
                    }


    override fun save(resume: Resume): Publisher<Resume> =
            mongoTemplate.upsert(findOneQuery(resumeId = resume.id),
                    Update.fromDocument(ResumeMapper.fromDomainToDocument(resume)), collectionName())
                    .onErrorResume { println("Error at ${it}"); Mono.just(UpdateResult.unacknowledged()) }
                    .flatMap { personalDetailsRepository.save(resumeId = resume.id, personalDetails = resume.personalDetails).toMono() }
                    .map { resume }


    override fun delete(resumeId: String): Publisher<Unit> = mongoTemplate.remove(findOneQuery(resumeId), collectionName()).flatMap { Mono.just(Unit) }


    private fun loadPersonalDetailsFor(resumeId: String) = personalDetailsRepository.findOneWithoutPhoto(resumeId)
}