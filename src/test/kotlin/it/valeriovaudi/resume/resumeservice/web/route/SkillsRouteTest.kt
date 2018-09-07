package it.valeriovaudi.resume.resumeservice.web.route

import com.fasterxml.jackson.databind.ObjectMapper
import it.valeriovaudi.resume.resumeservice.adapter.repository.MongoSkillsRepository
import it.valeriovaudi.resume.resumeservice.domain.model.Skill
import it.valeriovaudi.todolist.TestContextInitializer
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.toFlux
import reactor.core.publisher.toMono
import java.util.*

@ContextConfiguration(initializers = [TestContextInitializer::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner::class)
class SkillsRouteTest {

    @Autowired
    private lateinit var webClient: WebTestClient

    @Autowired
    private lateinit var skillsRepository: MongoSkillsRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper;

    @Test
    @WithMockUser(username = "user")
    fun `save skill family`() {

        val skill = Skill("FAMILY", listOf("SKILL_1", "SKILL_2"))
        val resumeId = UUID.randomUUID().toString()
        webClient.put()
                .uri("/resume/${resumeId}/skill")
                .body(BodyInserters.fromObject(skill))
                .exchange()
                .expectStatus().isNoContent

        Assert.assertNotNull(skillsRepository.findOne(resumeId).toMono().block())
    }

    @Test
    @WithMockUser(username = "user")
    fun `find all skill family by resume`() {

        val resumeId = UUID.randomUUID().toString()
        val skill = Skill("FAMILY", listOf("SKILL_1", "SKILL_2"))
        skillsRepository.save(resumeId, skill).toMono().block()

        webClient.get()
                .uri("/resume/${resumeId}/skill")
                .exchange()
                .expectStatus().isOk
                .expectBody().json(objectMapper.writeValueAsString(skill))
    }


    @Test
    @WithMockUser(username = "user")
    fun `delete a skill family of a resume`() {
        val resumeId = UUID.randomUUID().toString()
        skillsRepository.save(resumeId, Skill("FAMILY", listOf("SKILL_1", "SKILL_2"))).toMono().block()

        webClient.delete()
                .uri("/resume/${resumeId}/skill/FAMILY")
                .exchange()
                .expectStatus().isNoContent

        val block = skillsRepository.findOne(resumeId).toFlux().collectList().block()
        println("block $block")
        Assert.assertTrue(block.orEmpty().isEmpty())

    }

}