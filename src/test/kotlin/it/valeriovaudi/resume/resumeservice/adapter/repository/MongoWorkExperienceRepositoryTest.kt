package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.WorkExperience
import org.bson.Document
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
}
