package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.WorkExperience
import org.bson.Document
import org.hamcrest.core.Is
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux
import java.time.Duration
import java.time.LocalDate
import java.util.*


@DataMongoTest
@RunWith(SpringRunner::class)
class MongoWorkExperienceRepositoryTest {

    @Autowired
    lateinit var mongoTemplate: ReactiveMongoTemplate

    @Test
    fun `save a new work experience`() {
        val workExperienceRepository = MongoWorkExperienceRepository(mongoTemplate)

        val resumeId = UUID.randomUUID().toString()
        val workExperienceId = UUID.randomUUID().toString()
        val workExperience = WorkExperience(workExperienceId,
                LocalDate.of(2018, 1, 1),
                null, "A_COMPANY", listOf(),
                "A_JOB_DESCRIPTION", listOf())

        workExperienceRepository.save(resumeId, workExperience).block(Duration.ofMinutes(1))

        val actualWorkExperience = mongoTemplate.findOne(Query.query(Criteria.where("_id").isEqualTo(workExperienceId)
                .and("resumeId").isEqualTo(resumeId)), Document::class.java, "workExperience")
                .block(Duration.ofMinutes(1))

        println(actualWorkExperience)
        Assert.assertNotNull(actualWorkExperience)
    }


    @Test
    fun `find all work experiences`() {
        val workExperienceRepository = MongoWorkExperienceRepository(mongoTemplate)

        val resumeId = UUID.randomUUID().toString()
        val aWorkExperience = WorkExperience(UUID.randomUUID().toString(),
                LocalDate.of(2018, 1, 1),
                null, "A_COMPANY", listOf(),
                "A_JOB_DESCRIPTION", listOf())

        val anotherWorkExperience = WorkExperience(UUID.randomUUID().toString(),
                LocalDate.of(2018, 1, 1),
                null, "ANOTHER_COMPANY", listOf(),
                "ANOTHER_JOB_DESCRIPTION", listOf())

        Mono.zip(workExperienceRepository.save(resumeId, aWorkExperience),
                workExperienceRepository.save(resumeId, anotherWorkExperience))
                .block(Duration.ofMinutes(1))

        val actualWorkExperience = workExperienceRepository.findAll(resumeId).toFlux().collectList()
                .block(Duration.ofMinutes(1))

        println(actualWorkExperience)
        Assert.assertNotNull(actualWorkExperience)
        Assert.assertThat((actualWorkExperience as MutableList).size, Is.`is`(2))
    }

    @Test
    fun `find all work experiences of a resume that do not exist`() {
        val workExperienceRepository = MongoWorkExperienceRepository(mongoTemplate)

        val resumeId = UUID.randomUUID().toString()

        val actualWorkExperience = workExperienceRepository.findAll(resumeId).toFlux().collectList()
                .block(Duration.ofMinutes(1))

        println(actualWorkExperience)
        Assert.assertNotNull(actualWorkExperience)
        Assert.assertThat((actualWorkExperience as MutableList).size, Is.`is`(0))
    }

    @Test
    fun `delete a work experience i a resume`() {
        val workExperienceRepository = MongoWorkExperienceRepository(mongoTemplate)

        val resumeId = UUID.randomUUID().toString()
        val aWorkExperience = WorkExperience(UUID.randomUUID().toString(),
                LocalDate.of(2018, 1, 1),
                null, "A_COMPANY", listOf(),
                "A_JOB_DESCRIPTION", listOf())

        val anotherWorkExperience = WorkExperience(UUID.randomUUID().toString(),
                LocalDate.of(2018, 1, 1),
                null, "ANOTHER_COMPANY", listOf(),
                "ANOTHER_JOB_DESCRIPTION", listOf())

        Mono.zip(workExperienceRepository.save(resumeId, aWorkExperience),
                workExperienceRepository.save(resumeId, anotherWorkExperience))
                .block(Duration.ofMinutes(1))

        workExperienceRepository.delete(aWorkExperience.id).block(Duration.ofMinutes(1))
        val actualWorkExperienceList = workExperienceRepository.findAll(resumeId).toFlux().collectList()
                .block(Duration.ofMinutes(1))

        val actualWorkExperience = mongoTemplate.findOne(Query.query(Criteria.where("_id").isEqualTo(UUID.randomUUID().toString())
                .and("resumeId").isEqualTo(resumeId)), Document::class.java, "workExperience")
                .block(Duration.ofMinutes(1))

        println(actualWorkExperience)
        Assert.assertNull(actualWorkExperience)
        Assert.assertThat((actualWorkExperienceList as MutableList).size, Is.`is`(1))
    }
}
