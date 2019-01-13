package it.valeriovaudi.resume.resumeservice.web.route

import it.valeriovaudi.resume.resumeservice.TestCase
import it.valeriovaudi.resume.resumeservice.adapter.repository.MongoLanguageSkillsRepository
import it.valeriovaudi.resume.resumeservice.domain.model.LanguageCapabilityLevel.*
import it.valeriovaudi.resume.resumeservice.domain.model.LanguageSkill
import it.valeriovaudi.resume.resumeservice.domain.model.LanguageSkills
import it.valeriovaudi.resume.resumeservice.domain.model.Speaking
import it.valeriovaudi.resume.resumeservice.domain.model.Understanding
import it.valeriovaudi.todolist.TestContextInitializer
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.toMono
import java.time.Duration
import java.util.*

@ContextConfiguration(initializers = [TestContextInitializer::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner::class)
class LanguageSkillRouteTest {

    @Autowired
    private lateinit var webClient: WebTestClient

    @Autowired
    private lateinit var languageSkillsRepository: MongoLanguageSkillsRepository

    val languageSkills = LanguageSkills("A_NATIVE_LANGUAGE",
            listOf(LanguageSkill("A_LANGUAGE",
                    Understanding(B1, C1),
                    Speaking(A1, C1), C1)))

    @Test
    @WithMockUser(username = "user")
    fun `save language skill`() {
        val resumeId = UUID.randomUUID().toString()

        webClient.put()
                .uri("/resume/${resumeId}/language-skills")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(languageSkills))
                .exchange()
                .expectStatus().isNoContent

    }

    @Test
    @WithMockUser(username = "user")
    fun `read language skill`() {
        val resumeId = UUID.randomUUID().toString()

        languageSkillsRepository.save("$resumeId", languageSkills).toMono().block(Duration.ofMinutes(1));

        val expectedJson = TestCase.readFileAsString("language-skills.json")
        webClient.get()
                .uri("/resume/$resumeId/language-skills")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody().json(expectedJson)
    }

    @Test
    @WithMockUser(username = "user")
    fun `delete language skill`() {
//        personalDetailsRepository.save("RESUME_ID", TestCase.personalDetails()).toMono().block();
//
//        webClient.delete()
//                .uri("/resume/RESUME_ID/personal-details")
//                .exchange()
//                .expectStatus().isNoContent
//
//
//        Assert.assertTrue(personalDetailsRepository.findOne("RESUME_ID").toMono().block()!!.isEmpty())
    }
}