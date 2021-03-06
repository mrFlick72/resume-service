package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.TestCase
import it.valeriovaudi.resume.resumeservice.TestableS3AsyncClient
import it.valeriovaudi.resume.resumeservice.domain.model.*
import org.bson.Document
import org.hamcrest.core.Is
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import software.amazon.awssdk.services.s3.S3AsyncClient
import java.time.Duration
import java.time.LocalDate
import java.util.*

@DataMongoTest
@RunWith(SpringRunner::class)
class MongoResumeRepositoryTest {

    @Autowired
    lateinit var mongoTemplate: ReactiveMongoTemplate

    lateinit var mongoResumeRepository: MongoResumeRepository

    lateinit var mongoPersonalDetailsRepository: MongoPersonalDetailsRepository

    lateinit var mongoSkillsRepository: MongoSkillsRepository

    lateinit var mongoEducationRepository: MongoEducationRepository

    lateinit var mongoWorkExperienceRepository: MongoWorkExperienceRepository


    @Before
    fun setUp() {
        mongoWorkExperienceRepository = MongoWorkExperienceRepository(mongoTemplate)
        mongoEducationRepository = MongoEducationRepository(mongoTemplate)
        mongoSkillsRepository = MongoSkillsRepository(mongoTemplate)
        mongoPersonalDetailsRepository = MongoPersonalDetailsRepository(mongoTemplate, TestableS3AsyncClient.bucket,TestableS3AsyncClient.s3AsyncClient())
        mongoResumeRepository = MongoResumeRepository(mongoTemplate,
                mongoPersonalDetailsRepository,
                mongoSkillsRepository,
                mongoEducationRepository,
                mongoWorkExperienceRepository)
    }

    @Test
    fun `save a resume`() {
        val resumeId = UUID.randomUUID().toString()
        val emptyResume = anEmptyResume(resumeId)

        mongoResumeRepository.save(emptyResume).toMono().block(Duration.ofMinutes(1))

        val actualResumeDocument = mongoTemplate.findOne(Query.query(Criteria.where("_id").`is`(resumeId)),
                Document::class.java, "resume")
                .block(Duration.ofMinutes(2))

        Assert.assertNotNull(mongoTemplate.findOne(Query.query(Criteria.where("resumeId").`is`(resumeId)),
                Document::class.java, "personalDetails")
                .block(Duration.ofMinutes(2)))

        Assert.assertNotNull(actualResumeDocument)
    }

    @Test
    fun `save a not empty resume`() {
        val resumeId = UUID.randomUUID().toString()
        val resume = aResume(resumeId)
        mongoResumeRepository.save(resume!!).toMono().block(Duration.ofMinutes(1))

        val actualResumeDocument = mongoTemplate.findOne(Query.query(Criteria.where("_id").`is`(resumeId)),
                Document::class.java, "resume")
                .block(Duration.ofMinutes(2))

        Assert.assertNotNull(mongoTemplate.findOne(Query.query(Criteria.where("resumeId").`is`(resumeId)),
                Document::class.java, "personalDetails")
                .block(Duration.ofMinutes(2)))

        Assert.assertNotNull(actualResumeDocument)

        val actualSkills = mongoTemplate.find(Query.query(Criteria.where("resumeId").`is`(resumeId)),
                Document::class.java, "skill").collectList()
                .block(Duration.ofMinutes(2))
        Assert.assertNotNull(actualSkills)
        Assert.assertThat((actualSkills as MutableList).size, Is.`is`(1))

        val actualEducation = mongoTemplate.find(Query.query(Criteria.where("resumeId").`is`(resumeId)),
                Document::class.java, "education").collectList()
                .block(Duration.ofMinutes(2))
        Assert.assertNotNull(actualEducation)
        Assert.assertThat((actualEducation as MutableList).size, Is.`is`(2))

        val actualWorkExperience = mongoTemplate.find(Query.query(Criteria.where("resumeId").`is`(resumeId)),
                Document::class.java, "workExperience").collectList()
                .block(Duration.ofMinutes(2))
        Assert.assertNotNull(actualWorkExperience)
        Assert.assertThat((actualWorkExperience as MutableList).size, Is.`is`(2))
    }

    @Test
    fun `find a resume by id`() {
        val resumeId = UUID.randomUUID().toString()
        val resume = aResume(resumeId)!!

        val actualResume = mongoResumeRepository.save(resume).toMono()
                .flatMap { mongoResumeRepository.findOne(resumeId).toMono() }
                .block(Duration.ofMinutes(1))

        Assert.assertNotNull(actualResume!!.personalDetails)
        Assert.assertNotNull(actualResume.personalDetails.photo)

        val actualSkills = actualResume.skill
        Assert.assertNotNull(actualSkills)
        Assert.assertThat((actualSkills as MutableList).size, Is.`is`(1))

        val actualEducation = actualResume.educations
        Assert.assertNotNull(actualEducation)
        Assert.assertThat((actualEducation as MutableList).size, Is.`is`(2))

        val actualWorkExperience = actualResume.workExperience
        Assert.assertNotNull(actualWorkExperience)
        Assert.assertThat((actualWorkExperience as MutableList).size, Is.`is`(2))
    }

    @Test
    fun `find an empty resume by id`() {
        val resumeId = UUID.randomUUID().toString()
        val resume = anEmptyResume(resumeId)

        val actualResume = mongoResumeRepository.save(resume).toMono()
                .then(mongoResumeRepository.findOne(resumeId).toMono())
                .block(Duration.ofMinutes(1))

        Assert.assertNotNull(actualResume)
        Assert.assertThat(actualResume?.personalDetails?.firstName, Is.`is`("firstName"))
    }

    @Test
    fun `find a resume by user name`() {
        val resumeId = UUID.randomUUID().toString()
        val emptyResume = anEmptyResume(resumeId)

        val actualResume = mongoResumeRepository.save(emptyResume).toMono()
                .then(mongoResumeRepository.findOneByUserName("A_USER", Language.EN).toMono())
                .block(Duration.ofMinutes(1))

        Assert.assertNotNull(actualResume)
        Assert.assertThat(actualResume?.personalDetails?.firstName, Is.`is`("firstName"))
    }

    @Test
    fun `delete a resume`() {
        val resumeId = UUID.randomUUID().toString()
        val emptyResume = anEmptyResume(resumeId)

        mongoResumeRepository.save(emptyResume).toMono()
                .then(mongoResumeRepository.delete(resumeId).toMono())
                .block(Duration.ofMinutes(1))

        Assert.assertNotNull(mongoTemplate.findOne(Query.query(Criteria.where("resumeId").`is`(resumeId)),
                Document::class.java, "personalDetails")
                .block(Duration.ofMinutes(2)))
    }

    private fun anEmptyResume(resumeId: String) =
            Resume(resumeId, "A_USER", Language.EN, TestCase.personalDetailsWithPhoto(), listOf(), listOf())

    private fun aResume(resumeId: String) =
            Mono.zip(Mono.just(listOf(Skill("FAMILY", listOf("SKILL_1")))),
                    Mono.just(listOf(Education(id = "1", dateFrom = LocalDate.of(2018, 1, 1), title = "A_TITLE", type = EducationType.CERTIFICATION), Education(id = "2", dateFrom = LocalDate.of(2018, 1, 1), title = "A_TITLE", type = EducationType.CERTIFICATION))),
                    Mono.just(listOf(WorkExperience(id = UUID.randomUUID().toString(), startDate = LocalDate.of(2018, 1, 1), company = "A_COMPANY", jobDescription = "A_JOB_DESCRIPTION", technologies = listOf("TAEH_1", "TAEH_2"), commitments = listOf("COMMITMENTS_1", "COMMITMENTS_2")),
                            WorkExperience(id = UUID.randomUUID().toString(), startDate = LocalDate.of(2018, 1, 1), company = "A_COMPANY", jobDescription = "A_JOB_DESCRIPTION", technologies = listOf("TAEH_1", "TAEH_2"), commitments = listOf("COMMITMENTS_1", "COMMITMENTS_2")))))
                    .map {
                        Resume(resumeId, "A_USER", Language.EN, TestCase.personalDetailsWithPhoto(),
                                skill = it.t1, educations = it.t2, workExperience = it.t3)
                    }.block()
}