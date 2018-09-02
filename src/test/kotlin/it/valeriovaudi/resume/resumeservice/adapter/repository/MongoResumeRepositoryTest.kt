package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.TestCase
import it.valeriovaudi.resume.resumeservice.domain.model.Language
import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetails
import it.valeriovaudi.resume.resumeservice.domain.model.Resume
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

    @Before
    fun setUp() {
        mongoPersonalDetailsRepository = MongoPersonalDetailsRepository(mongoTemplate, gridFsTemplate)
        mongoResumeRepository = MongoResumeRepository(mongoTemplate, mongoPersonalDetailsRepository)
    }

    @Test
    fun `save a resume`() {
        val resumeId = UUID.randomUUID().toString()
        val emptyResume = Resume(resumeId, "A_USER", Language.EN, TestCase.personalDetailsWithPhoto())

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
    fun `find a resume by id`() {
        val resumeId = UUID.randomUUID().toString()
        val emptyResume = Resume(resumeId, "A_USER", Language.EN, TestCase.personalDetailsWithPhoto())

        val actualResume = mongoResumeRepository.save(emptyResume).toMono()
                .then(mongoResumeRepository.findOne(resumeId).toMono())
                .block(Duration.ofMinutes(1))

        Assert.assertNotNull(actualResume)
        Assert.assertThat(actualResume?.personalDetails?.firstName, Is.`is`("firstName"))
    }

    @Test
    fun `find a resume by user name`() {
        val resumeId = UUID.randomUUID().toString()
        val emptyResume = Resume(resumeId, "A_USER", Language.EN, TestCase.personalDetailsWithPhoto())

        val actualResume = mongoResumeRepository.save(emptyResume).toMono()
                .then(mongoResumeRepository.findOneByUserName("A_USER", Language.EN).toMono())
                .block(Duration.ofMinutes(1))

        Assert.assertNotNull(actualResume)
        Assert.assertThat(actualResume?.personalDetails?.firstName, Is.`is`("firstName"))
    }

    @Test
    fun `delete a resume`() {
        val resumeId = UUID.randomUUID().toString()
        val emptyResume = Resume(resumeId, "A_USER", Language.EN, TestCase.personalDetailsWithPhoto())

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