package it.valeriovaudi.resume.resumeservice.web.route

import it.valeriovaudi.resume.resumeservice.domain.model.Language
import it.valeriovaudi.resume.resumeservice.domain.model.Resume
import it.valeriovaudi.resume.resumeservice.domain.repository.ResumeRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.toMono
import java.net.URI
import java.util.*

@Configuration
class ResumeRoute {

    @Bean
    fun resumeRoutes(resumeRepository: ResumeRepository) = router {
        POST("/resume")
        {
            it.principal().flatMap {
                resumeRepository.save(Resume.emptyResume(UUID.randomUUID().toString(), it.name, Language.EN)).toMono()
            }.flatMap { ServerResponse.created(URI("http://localhost:8080/resume/${it.id}")).build() }
        }

    }
}