package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.Skill
import org.assertj.core.api.Assertions
import org.bson.Document
import org.hamcrest.core.Is
import org.junit.Assert
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.toFlux
import reactor.core.publisher.toMono
import java.time.Duration
import java.util.*


@DataMongoTest
@RunWith(SpringRunner::class)
class MongoSkillsRepositoryTest {

    @Autowired
    lateinit var mongoTemplate: ReactiveMongoTemplate

    @Test
    fun `save skill family`() {

        val mongoSkillsRepository = MongoSkillsRepository(mongoTemplate)

        val resumeId = UUID.randomUUID().toString()
        val save = mongoSkillsRepository.save(resumeId, listOf(Skill("A_FAMILY", listOf("Skill_1", "Skill_2", "Skill_3"))))
                .toMono()
                .block(Duration.ofMinutes(1))

        Assert.assertNotNull(save)
        Assert.assertNotNull(mongoTemplate.findOne(Query.query(Criteria.where("resumeId").`is`(resumeId)), Document::class.java, "skill")
                .block(Duration.ofMinutes(1)))
    }

    @Test
    fun `save skill family more that once`() {

        val mongoSkillsRepository = MongoSkillsRepository(mongoTemplate)

        val resumeId = UUID.randomUUID().toString()
        mongoSkillsRepository.save(resumeId, listOf(Skill("A_FAMILY", listOf("Skill_1_1", "Skill_2", "Skill_3"))))
                .toMono()
                .block(Duration.ofMinutes(1))

        mongoSkillsRepository.save(resumeId, listOf(Skill("A_FAMILY", listOf("Skill_1_2", "Skill_2_2", "Skill_3_2"))))
                .toMono()
                .block(Duration.ofMinutes(1))

        mongoSkillsRepository.save(resumeId, listOf(Skill("A_FAMILY", listOf("Skill_1_3", "Skill_2_3", "Skill_3_3"))))
                .toMono()
                .block(Duration.ofMinutes(1))

        val acutal = mongoTemplate.find(Query.query(Criteria.where("resumeId").`is`(resumeId)), Document::class.java, "skill")
                .collectList().block(Duration.ofMinutes(1))
        println(acutal)

        Assert.assertThat((acutal as List<Document>).size, Is.`is`(1))
        Assert.assertNotNull(acutal)
    }

    @Test
    fun `find all skills of a not exist resume`() {
        val mongoSkillsRepository = MongoSkillsRepository(mongoTemplate)

        val resumeId = UUID.randomUUID().toString()

        val allSkills = mongoSkillsRepository.findAll(resumeId).toFlux().collectList()
                .block(Duration.ofMinutes(1))

        Assert.assertTrue(allSkills!!.isEmpty())
    }

    @Test
    fun `find all skills of a resume`() {
        val mongoSkillsRepository = MongoSkillsRepository(mongoTemplate)

        val resumeId = UUID.randomUUID().toString()
        mongoSkillsRepository.save(resumeId, listOf(Skill("A_FAMILY", listOf("Skill_1", "Skill_2", "Skill_3")),
                Skill("ANOTHER_FAMILY", listOf("Skill_1", "Skill_2", "Skill_3"))))
                .toMono()
                .block(Duration.ofMinutes(1))

        val allSkills = mongoSkillsRepository.findAll(resumeId).toFlux().collectList()
                .block(Duration.ofMinutes(1))

        Assert.assertNotNull(allSkills)
        Assert.assertThat(allSkills, Is.`is`(listOf(Skill("A_FAMILY", listOf("Skill_1", "Skill_2", "Skill_3")),
                Skill("ANOTHER_FAMILY", listOf("Skill_1", "Skill_2", "Skill_3")))))
    }

    @Test
    fun `delete a specific skill family of a resume`() {
        val mongoSkillsRepository = MongoSkillsRepository(mongoTemplate)

        val resumeId = UUID.randomUUID().toString()
        mongoSkillsRepository.save(resumeId, listOf(Skill("A_FAMILY", listOf("Skill_1", "Skill_2", "Skill_3"))))
                .toMono()
                .block(Duration.ofMinutes(1))

        mongoSkillsRepository.save(resumeId, listOf(Skill("ANOTHER_FAMILY", listOf("Skill_1", "Skill_2", "Skill_3"))))
                .toMono()
                .block(Duration.ofMinutes(1))

        mongoSkillsRepository.delete(resumeId, "A_FAMILY").toMono().block(Duration.ofMinutes(1))
        val allSkills = mongoSkillsRepository.findAll(resumeId).toFlux().collectList()
                .block(Duration.ofMinutes(1))

        Assert.assertNotNull(allSkills)
        Assert.assertThat(allSkills, Is.`is`(listOf(Skill("ANOTHER_FAMILY", listOf("Skill_1", "Skill_2", "Skill_3")))))
    }
}