package it.valeriovaudi.resume.resumeservice.adapter.repository

import com.mongodb.client.result.UpdateResult
import it.valeriovaudi.resume.resumeservice.domain.model.*
import it.valeriovaudi.resume.resumeservice.domain.repository.ResumeRepository
import org.bson.Document
import org.reactivestreams.Publisher
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux
import reactor.core.publisher.toMono

class MongoResumeRepository(private val mongoTemplate: ReactiveMongoTemplate,
                            private val personalDetailsRepository: MongoPersonalDetailsRepository,
                            private val skillsRepository: MongoSkillsRepository,
                            private val educationRepository: MongoEducationRepository,
                            private val workExperienceRepository: MongoWorkExperienceRepository) : ResumeRepository {

    companion object {
        fun collectionName() = "resume"
        fun findOneQuery(resumeId: String) = Query.query(Criteria.where("_id").isEqualTo(resumeId))
        fun findByUserNameAndLanguageQuery(userName: String, language: Language) =
                Query.query(Criteria.where("userName").`is`(userName).and("language").isEqualTo(language.name))
    }

    override fun findOne(resumeId: String): Publisher<Resume> =
            mongoTemplate.findOne(findOneQuery(resumeId), Document::class.java, collectionName())
                    .map { ResumeMapper.fromDocumentToDomain(it) }
                    .flatMap { resume -> println("OK"); loadResumeData(resume) }

    override fun findOneByUserName(userName: String, language: Language): Publisher<Resume> =
            mongoTemplate.findOne(findByUserNameAndLanguageQuery(userName, language), Document::class.java, collectionName())
                    .map { ResumeMapper.fromDocumentToDomain(it) }
                    .flatMap { resume -> loadResumeData(resume) }


    override fun save(resume: Resume): Publisher<Resume> =
            mongoTemplate.upsert(findOneQuery(resumeId = resume.id),
                    Update.fromDocument(ResumeMapper.fromDomainToDocument(resume)), collectionName())
                    .onErrorResume { println("Error at ${it}"); Mono.just(UpdateResult.unacknowledged()) }
                    .flatMap { saveOtherResumeData(resume) }


    override fun delete(resumeId: String): Publisher<Unit> = mongoTemplate.remove(findOneQuery(resumeId), collectionName()).flatMap { Mono.just(Unit) }


    private fun saveOtherResumeData(resume: Resume) = resume.let {
        Mono.zip(personalDetailsRepository.save(resumeId = resume.id, personalDetails = resume.personalDetails).toMono(),
                if (resume.skill.isNotEmpty()) skillsRepository.save(resume.id, resume.skill).toFlux().collectList() else Mono.just(listOf<Skill>()),
                if (resume.educations.isNotEmpty()) resume.educations.toFlux().flatMap { educationRepository.save(resume.id, it) }.collectList() else Mono.just(listOf<Education>()),
                if (resume.workExperience.isNotEmpty()) resume.workExperience.toFlux().flatMap { workExperienceRepository.save(resume.id, it) }.collectList() else Mono.just(listOf<WorkExperience>()))
                .map { tuple -> resume }

    }

    private fun loadResumeData(resume: Resume) = resume.let {
        Mono.zip(personalDetailsRepository.findOne(it.id).toMono(),
                skillsRepository.findAll(it.id).toFlux().collectList())
        { personalDetails: PersonalDetails, skills: List<Skill> ->
            Resume(it.id, it.userName, it.language, personalDetails, listOf(), skills, listOf())
        }
    }

}