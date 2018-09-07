package it.valeriovaudi.resume.resumeservice.web.route

import it.valeriovaudi.resume.resumeservice.domain.model.Language
import it.valeriovaudi.resume.resumeservice.domain.model.Resume
import it.valeriovaudi.resume.resumeservice.domain.repository.ResumeRepository
import it.valeriovaudi.resume.resumeservice.web.representation.ResumeRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerResponse.created
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.toMono
import java.net.URI
import java.util.*

@Configuration
class ResumeRoute {

    @Bean
    fun resumeRoutes(@Value("\${baseServer:http://localhost:8080}") baseServer: String, resumeRepository: ResumeRepository) = router {
        GET("/resume/{resumeId}")
        {
            val resumeId = it.pathVariable("resumeId")
            resumeRepository.findOne(resumeId).toMono()
                    .flatMap { ok().body(BodyInserters.fromObject(ResumeRepresentation.fromDomainToRepresentation(it))) }
        }

        POST("/resume")
        {
            it.principal().flatMap {
                resumeRepository.save(Resume.emptyResume(UUID.randomUUID().toString(), it.name, Language.EN)).toMono()
            }.flatMap { created(URI("${baseServer}/resume/${it.id}")).build() }
        }

    }
}