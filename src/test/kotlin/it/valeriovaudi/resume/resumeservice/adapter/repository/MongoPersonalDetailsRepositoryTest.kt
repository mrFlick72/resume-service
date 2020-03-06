package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.TestCase
import it.valeriovaudi.resume.resumeservice.TestableS3AsyncClient
import org.bson.Document
import org.hamcrest.core.Is.`is`
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.toMono
import java.time.Duration
import java.util.*

@DataMongoTest
@RunWith(SpringRunner::class)
class MongoPersonalDetailsRepositoryTest {

    @Autowired
    lateinit var mongoTemplate: ReactiveMongoTemplate


    lateinit var mongoPersonalDetailsRepository: MongoPersonalDetailsRepository

    @Before
    fun setUp() {
        mongoPersonalDetailsRepository = MongoPersonalDetailsRepository(mongoTemplate,TestableS3AsyncClient.bucket,TestableS3AsyncClient.s3AsyncClient())
    }

    @Test
    fun `save a personal details without photo`() {

        val resumeId = UUID.randomUUID().toString()
        val personalDetails = TestCase.personalDetails()

        mongoPersonalDetailsRepository.save(resumeId, personalDetails)
                .toMono().blockOptional().ifPresent {
                    Assert.assertThat(it, `is`(personalDetails))
                }

        Assert.assertNotNull(mongoTemplate.findOne(query(Criteria.where("resumeId").`is`(resumeId)),
                Document::class.java, "personalDetails")
                .block(Duration.ofMinutes(2)))
    }

    @Test
    fun `save a personal details with photo`() {
        val resumeId = UUID.randomUUID().toString()
        val personalDetails = TestCase.personalDetailsWithPhoto()
        mongoPersonalDetailsRepository.save(resumeId, personalDetails)
                .toMono().blockOptional().ifPresent {
                    Assert.assertThat(it, `is`(personalDetails))
                }

        Assert.assertNotNull(mongoTemplate.findOne(query(Criteria.where("resumeId").`is`(resumeId)),
                Document::class.java, "personalDetails")
                .block(Duration.ofMinutes(2)))
    }

    @Test
    fun `find a personal details with photo`() {
        val resumeId = UUID.randomUUID().toString()
        val personalDetails = TestCase.personalDetailsWithPhoto()

        mongoPersonalDetailsRepository.save(resumeId, personalDetails)
                .toMono().blockOptional().ifPresent {
                    Assert.assertThat(it, `is`(personalDetails))
                }

        val actual = mongoPersonalDetailsRepository.findOne(resumeId)
                .toMono().block(Duration.ofMinutes(2))

        Assert.assertNotNull(actual)
    }

    @Test
    fun `find a personal details whitout  photo not in db`() {
        val resumeId = UUID.randomUUID().toString()

        val actual = mongoPersonalDetailsRepository.findOneWithoutPhoto(resumeId)
                .toMono().block(Duration.ofMinutes(2))

        Assert.assertNotNull(actual)
        Assert.assertTrue(actual!!.isEmpty())
    }

    @Test
    fun `find a personal details not in db`() {
        val resumeId = UUID.randomUUID().toString()

        val actual = mongoPersonalDetailsRepository.findOne(resumeId)
                .toMono().block(Duration.ofMinutes(2))

        Assert.assertNotNull(actual)
        Assert.assertTrue(actual!!.isEmpty())
    }

    @Test
    fun `delete an existing personal details`() {

        val resumeId = UUID.randomUUID().toString()
        val personalDetails = TestCase.personalDetailsWithPhoto()

        mongoPersonalDetailsRepository.save(resumeId, personalDetails)
                .toMono().blockOptional().ifPresent {
                    Assert.assertThat(it, `is`(personalDetails))
                }

        val actual = mongoPersonalDetailsRepository.findOne(resumeId)
                .toMono().block(Duration.ofMinutes(2))

        mongoPersonalDetailsRepository.delete(resumeId).toMono().block(Duration.ofMinutes(1))

        Assert.assertNull(mongoTemplate.findOne(query(Criteria.where("resumeId").`is`(resumeId)),
                Document::class.java, "personalDetails")
                .block(Duration.ofMinutes(2)))
    }
}