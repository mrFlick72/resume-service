package it.valeriovaudi.resume.resumeservice.web.route

import it.valeriovaudi.resume.resumeservice.adapter.repository.MongoLanguageSkillsRepository
import it.valeriovaudi.resume.resumeservice.domain.model.LanguageCapabilityLevel.*
import it.valeriovaudi.resume.resumeservice.domain.model.LanguageSkill
import it.valeriovaudi.resume.resumeservice.domain.model.LanguageSkills
import it.valeriovaudi.resume.resumeservice.domain.model.Speaking
import it.valeriovaudi.resume.resumeservice.domain.model.Understanding
import it.valeriovaudi.todolist.TestContextInitializer
import org.hamcrest.core.Is
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import org.springframework.web.reactive.function.BodyInserters
import java.net.URI
import java.util.*

@ContextConfiguration(initializers = [TestContextInitializer::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner::class)
class LanguageSkillRouteTest {

    @Autowired
    private lateinit var webClient: WebTestClient

    @Autowired
    private lateinit var languageSkillsRepository: MongoLanguageSkillsRepository


    @Test
    @WithMockUser(username = "user")
    fun `save language skill`() {
        val resumeId = UUID.randomUUID().toString()
        val languageSkills = LanguageSkills("A_NATIVE_LANGUAGE",
                listOf(LanguageSkill("A_LANGUAGE",
                        Understanding(B1, C1),
                        Speaking(A1, C1), C1)))
        val location =  webClient.post()
                .uri("/resume/${resumeId}/language-skill")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(languageSkills))
                .exchange()
                .expectStatus().isCreated
                .returnResult<Any>().responseHeaders.location

        Assert.assertNotNull(location)
        Assert.assertThat(URI("http://localhost:8080/resume/$resumeId/language-skill") , Is.`is`(location))

    }

    @Test
    @WithMockUser(username = "user")
    fun `read language skill`() {
//        personalDetailsRepository.save("RESUME_ID", TestCase.personalDetails()).toMono().block();
//
//        val expectedJson = TestCase.readFileAsString("personal-details.json")
//        webClient.get()
//                .uri("/resume/RESUME_ID/personal-details")
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isOk
//                .expectBody().json(expectedJson)
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