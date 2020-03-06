package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.Skill
import org.bson.Document
import org.hamcrest.Matchers
import org.hamcrest.core.Is
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.annotation.DirtiesContext
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
    @DirtiesContext
    fun `save skill family`() {

        val mongoSkillsRepository = MongoSkillsRepository(mongoTemplate)

        val resumeId = UUID.randomUUID().toString()
        val save = mongoSkillsRepository.save(resumeId, listOf(Skill("A_FAMILY", listOf("Skill_1", "Skill_2", "Skill_3"))))
                .toMono()
                .block(Duration.ofMinutes(1))

        assertNotNull(save)
        assertNotNull(mongoTemplate.findOne(Query.query(Criteria.where("resumeId").`is`(resumeId)), Document::class.java, "skill")
                .block(Duration.ofMinutes(1)))
    }

    @Test
    @DirtiesContext
    fun `save skill family more that once`() {

        val mongoSkillsRepository = MongoSkillsRepository(mongoTemplate)

        val resumeId = UUID.randomUUID().toString()
        /*Mono.zip(mongoSkillsRepository.save(resumeId, listOf(Skill("A_FAMILY", listOf("Skill_1_1", "Skill_2", "Skill_3"))))
                .toMono(),
                mongoSkillsRepository.save(resumeId, listOf(Skill("A_FAMILY", listOf("Skill_1_2", "Skill_2_2", "Skill_3_2"))))
                        .toMono(),
                mongoSkillsRepository.save(resumeId, listOf(Skill("A_FAMILY", listOf("Skill_1_3", "Skill_2_3", "Skill_3_3"))))
                        .toMono(),
                mongoSkillsRepository.save(resumeId, listOf(Skill("A_FAMILY2", listOf("Skill_1_3", "Skill_2_3", "Skill_3_3"))))
                        .toMono())

                .block(Duration.ofMinutes(1))
*/
        mongoSkillsRepository.save(resumeId, listOf(Skill("A_FAMILY", listOf("Skill_1_1", "Skill_2", "Skill_3")),
                Skill("A_FAMILY", listOf("Skill_1_2", "Skill_2_2", "Skill_3_2")),
                Skill("A_FAMILY", listOf("Skill_1_3", "Skill_2_3", "Skill_3_3")),
                Skill("A_FAMILY2", listOf("Skill_1_3", "Skill_2_3", "Skill_3_3"))))
                .toMono()
                .block(Duration.ofMinutes(1))


        val actual = mongoTemplate.find(Query.query(Criteria.where("resumeId").`is`(resumeId)), Document::class.java, "skill")
                .collectList().block(Duration.ofMinutes(1))

        assertThat((actual as List<Document>).size, Is.`is`(2))
        assertNotNull(actual)
    }

    @Test
    @DirtiesContext
    fun `find all skills of a not exist resume`() {
        val mongoSkillsRepository = MongoSkillsRepository(mongoTemplate)

        val resumeId = UUID.randomUUID().toString()

        val allSkills = mongoSkillsRepository.findAll(resumeId).toFlux().collectList()
                .block(Duration.ofMinutes(1))

        assertTrue(allSkills!!.isEmpty())
    }

    @Test
    @DirtiesContext
    fun `find all skills of a resume`() {
        val mongoSkillsRepository = MongoSkillsRepository(mongoTemplate)

        val resumeId = UUID.randomUUID().toString()
        mongoSkillsRepository.save(resumeId, listOf(Skill("ANOTHER_FAMILY", listOf("Skill_1", "Skill_2", "Skill_3")),
                Skill("A_FAMILY", listOf("Skill_1", "Skill_2", "Skill_3"))))
                .toMono()
                .block(Duration.ofMinutes(2))

        val allSkills = mongoSkillsRepository.findAll(resumeId).toFlux().collectList()
                .block(Duration.ofMinutes(2))

        assertNotNull(allSkills)
        assertThat((allSkills as MutableList).size, Is.`is`(2))
        assertThat(allSkills, Matchers.containsInAnyOrder(Skill("A_FAMILY", listOf("Skill_1", "Skill_2", "Skill_3")),
                Skill("ANOTHER_FAMILY", listOf("Skill_1", "Skill_2", "Skill_3"))))
    }

    @Test
    @DirtiesContext
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


        assertNotNull(allSkills)
        assertThat((allSkills as MutableList).size, Is.`is`(1))
        assertThat(allSkills, Is.`is`(listOf(Skill("ANOTHER_FAMILY", listOf("Skill_1", "Skill_2", "Skill_3")))))
    }
}