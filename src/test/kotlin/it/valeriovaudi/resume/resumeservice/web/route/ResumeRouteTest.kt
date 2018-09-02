package it.valeriovaudi.resume.resumeservice.web.route

import it.valeriovaudi.resume.resumeservice.TestCase
import it.valeriovaudi.resume.resumeservice.adapter.repository.MongoPersonalDetailsRepository
import it.valeriovaudi.todolist.TestContextInitializer
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
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
import reactor.core.publisher.toMono
import java.util.*

@ContextConfiguration(initializers = [TestContextInitializer::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner::class)
class ResumeRouteTest {

    @Autowired
    private lateinit var webClient: WebTestClient

    @Autowired
    private lateinit var personalDetailsRepository: MongoPersonalDetailsRepository

    @Test
    @WithMockUser(username = "user")
    fun `save basic personal details data`() {
        val location = webClient.post()
                .uri("/resume")
                .exchange()
                .expectStatus().isCreated
                .returnResult<Any>().responseHeaders.location

        val locationChunck = location!!.toString().split("\\/")
        val resumeId = locationChunck[locationChunck.size - 1];

        assertNull(personalDetailsRepository.findOne(resumeId).toMono().block())
    }

}