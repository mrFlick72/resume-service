package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.TestCase
import it.valeriovaudi.resume.resumeservice.domain.model.*
import it.valeriovaudi.resume.resumeservice.domain.repository.EducationRepository
import it.valeriovaudi.resume.resumeservice.domain.repository.SkillsRepository
import org.assertj.core.api.Assertions
import org.bson.Document
import org.hamcrest.core.Is
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.toMono
import java.time.Duration
import java.time.LocalDate
import java.util.*

@DataMongoTest
@RunWith(SpringRunner::class)
class MongoResumeRepositoryTest {

    @Autowired
    lateinit var mongoTemplate: ReactiveMongoTemplate

    @Autowired
    lateinit var gridFsTemplate: GridFsTemplate

    lateinit var mongoResumeRepository: MongoResumeRepository

    lateinit var mongoPersonalDetailsRepository: MongoPersonalDetailsRepository

    lateinit var mongoSkillsRepository: MongoSkillsRepository

    lateinit var educationRepository: MongoEducationRepository

    @Before
    fun setUp() {
        educationRepository = MongoEducationRepository(mongoTemplate)
        mongoSkillsRepository = MongoSkillsRepository(mongoTemplate)
        mongoPersonalDetailsRepository = MongoPersonalDetailsRepository(mongoTemplate, gridFsTemplate)
        mongoResumeRepository = MongoResumeRepository(mongoTemplate, mongoPersonalDetailsRepository, mongoSkillsRepository, educationRepository)
    }

    @Test
    fun `save a resume`() {
        val resumeId = UUID.randomUUID().toString()
        val emptyResume = Resume(resumeId, "A_USER", Language.EN, TestCase.personalDetailsWithPhoto(), listOf(), listOf())

        mongoResumeRepository.save(emptyResume).toMono().block(Duration.ofMinutes(1))

        val actualResumeDocument = mongoTemplate.findOne(Query.query(Criteria.where("_id").`is`(resumeId)),
                Document::class.java, "resume")
                .block(Duration.ofMinutes(2))

        println("details")
        Assert.assertNotNull(mongoTemplate.findOne(Query.query(Criteria.where("resumeId").`is`(resumeId)),
                Document::class.java, "personalDetails")
                .block(Duration.ofMinutes(2)))

        println("photo")
        Assert.assertNotNull(gridFsTemplate.findOne(Query.query(Criteria.where("metadata.resumeId").`is`(resumeId))))
        Assert.assertNotNull(gridFsTemplate.getResource(resumeId))
        Assert.assertNotNull(actualResumeDocument)
    }

    @Test
    fun `save a not empty resume`() {
        val resumeId = UUID.randomUUID().toString()
        val skills = listOf(Skill("FAMILY", listOf("SKILL_1")))
        val educations = listOf(Education(id = "1", dateFrom = LocalDate.of(2018, 1, 1), title = "A_TITLE", type = EducationType.CERTIFICATION), Education(id = "2", dateFrom = LocalDate.of(2018, 1, 1), title = "A_TITLE", type = EducationType.CERTIFICATION))
        val workExperiences = listOf(WorkExperience(id = "1", startDate = LocalDate.of(2018, 1, 1), company = "A_COMPANY", jobDescription = "A_JOB_DESCRIPTION", technologies = listOf("TAEH_1", "TAEH_2"), commitments = listOf("COMMITMENTS_1", "COMMITMENTS_2")))
        val resume = Resume(resumeId, "A_USER", Language.EN, TestCase.personalDetailsWithPhoto(),
                skill = skills,
                educations = educations,
                workExperience = listOf())

        mongoResumeRepository.save(resume).toMono().block(Duration.ofMinutes(1))


        val actualResumeDocument = mongoTemplate.findOne(Query.query(Criteria.where("_id").`is`(resumeId)),
                Document::class.java, "resume")
                .block(Duration.ofMinutes(2))

        println("details")
        Assert.assertNotNull(mongoTemplate.findOne(Query.query(Criteria.where("resumeId").`is`(resumeId)),
                Document::class.java, "personalDetails")
                .block(Duration.ofMinutes(2)))

        println("photo")
        Assert.assertNotNull(gridFsTemplate.findOne(Query.query(Criteria.where("metadata.resumeId").`is`(resumeId))))
        Assert.assertNotNull(gridFsTemplate.getResource(resumeId))
        Assert.assertNotNull(actualResumeDocument)

        println("skills")
        val actualSkills = mongoTemplate.find(Query.query(Criteria.where("resumeId").`is`(resumeId)),
                Document::class.java, "skill").collectList()
                .block(Duration.ofMinutes(2))
        Assert.assertNotNull(actualSkills)
        Assert.assertThat((actualSkills as MutableList).size, Is.`is`(1))

        println("education")
        val actualEducation = mongoTemplate.find(Query.query(Criteria.where("resumeId").`is`(resumeId)),
                Document::class.java, "education").collectList()
                .block(Duration.ofMinutes(2))
        Assert.assertNotNull(actualEducation)
        Assert.assertThat((actualEducation as MutableList).size, Is.`is`(2))

        println("workExperience")
        Assert.assertNotNull(mongoTemplate.find(Query.query(Criteria.where("resumeId").`is`(resumeId)),
                Document::class.java, "workExperience").collectList()
                .block(Duration.ofMinutes(2)))

    }

    @Test
    fun `find a resume by id`() {
        val resumeId = UUID.randomUUID().toString()
        val emptyResume = Resume(resumeId, "A_USER", Language.EN, TestCase.personalDetailsWithPhoto(), listOf(), listOf())

        val actualResume = mongoResumeRepository.save(emptyResume).toMono()
                .then(mongoResumeRepository.findOne(resumeId).toMono())
                .block(Duration.ofMinutes(1))

        Assert.assertNotNull(actualResume)
        Assert.assertThat(actualResume?.personalDetails?.firstName, Is.`is`("firstName"))
    }

    @Test
    fun `find a resume by user name`() {
        val resumeId = UUID.randomUUID().toString()
        val emptyResume = Resume(resumeId, "A_USER", Language.EN, TestCase.personalDetailsWithPhoto(), listOf(), listOf())

        val actualResume = mongoResumeRepository.save(emptyResume).toMono()
                .then(mongoResumeRepository.findOneByUserName("A_USER", Language.EN).toMono())
                .block(Duration.ofMinutes(1))

        Assert.assertNotNull(actualResume)
        Assert.assertThat(actualResume?.personalDetails?.firstName, Is.`is`("firstName"))
    }

    @Test
    fun `delete a resume`() {
        val resumeId = UUID.randomUUID().toString()
        val emptyResume = Resume(resumeId, "A_USER", Language.EN, TestCase.personalDetailsWithPhoto(), listOf(), listOf())

        mongoResumeRepository.save(emptyResume).toMono()
                .then(mongoResumeRepository.delete(resumeId).toMono())
                .block(Duration.ofMinutes(1))

        println("details")
        Assert.assertNotNull(mongoTemplate.findOne(Query.query(Criteria.where("resumeId").`is`(resumeId)),
                Document::class.java, "personalDetails")
                .block(Duration.ofMinutes(2)))

        println("photo")
        Assert.assertNotNull(gridFsTemplate.findOne(Query.query(Criteria.where("metadata.resumeId").`is`(resumeId))))
        Assert.assertNotNull(gridFsTemplate.getResource(resumeId))
    }
}