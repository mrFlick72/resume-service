package it.valeriovaudi.resume.resumeservice.web.route

import it.valeriovaudi.resume.resumeservice.domain.model.Sex
import it.valeriovaudi.resume.resumeservice.web.representation.PersonalDetailsRepresentation
import it.valeriovaudi.todolist.TestContextInitializer
import org.junit.Assert.*
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
import java.time.LocalDate
import java.util.*

@ContextConfiguration(initializers = [TestContextInitializer::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner::class)
class PersonalDetailsRouteTest {

    @Autowired
    private lateinit var webClient: WebTestClient

    @Test
    @WithMockUser(username = "user")
    fun `save basic personal details data`() {
        val resumeId = UUID.randomUUID().toString();
        webClient.post()
                .uri("/resume/${resumeId}/personal-details")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(PersonalDetailsRepresentation("firstName",
                        "lastName", "address", "zip", "city",
                        "region", "mail", "mobile", LocalDate.now(),
                        "state", Sex.NONE, "taxCode"))).exchange()
                .expectStatus().isCreated
    }
}