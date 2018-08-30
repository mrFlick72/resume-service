package it.valeriovaudi.resume.resumeservice.web.route

import it.valeriovaudi.resume.resumeservice.TestCase
import it.valeriovaudi.resume.resumeservice.adapter.repository.MongoPersonalDetailsRepository
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
import java.util.*

@ContextConfiguration(initializers = [TestContextInitializer::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner::class)
class PersonalDetailsRouteTest {


    @Autowired
    private lateinit var webClient: WebTestClient

    @Autowired
    private lateinit var personalDetailsRepository: MongoPersonalDetailsRepository

    @Test
    @WithMockUser(username = "user")
    fun `save basic personal details data`() {
        val resumeId = UUID.randomUUID().toString();
        webClient.post()
                .uri("/resume/${resumeId}/personal-details")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(TestCase.personalDetails())).exchange()
                .expectStatus().isCreated
    }

    @Test
    @WithMockUser(username = "user")
    fun `read basic personal details data`() {
        personalDetailsRepository.save("RESUME_ID", TestCase.personalDetails()).toMono().block();

        val expectedJson = TestCase.readFileAsString("personal-details.json")
        val resumeId = UUID.randomUUID().toString();
        webClient.get()
                .uri("/resume/RESUME_ID/personal-details")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody().json(expectedJson)
    }

}