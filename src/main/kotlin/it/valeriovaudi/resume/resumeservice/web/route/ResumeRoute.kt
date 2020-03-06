package it.valeriovaudi.resume.resumeservice.web.route

import it.valeriovaudi.resume.resumeservice.domain.model.Language
import it.valeriovaudi.resume.resumeservice.domain.model.Resume
import it.valeriovaudi.resume.resumeservice.domain.repository.ResumeRepository
import it.valeriovaudi.resume.resumeservice.domain.usecase.ResumePrinter
import it.valeriovaudi.resume.resumeservice.web.representation.ResumeRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.APPLICATION_PDF
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse.created
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.net.URI
import java.util.*

@Configuration
class ResumeRoute {

    @Bean
    fun resumeRoutes(@Value("\${baseServer:http://localhost:8080}") baseServer: String,
                     resumeRepository: ResumeRepository,
                     resumePrinter: ResumePrinter) = router {


        GET("/resume/{resumeId}").and(accept(APPLICATION_JSON))
                .invoke {
                    println("rotta json invocata")

                    val resumeId = it.pathVariable("resumeId")
                    resumeRepository.findOne(resumeId).toMono()
                            .flatMap { ok().body(BodyInserters.fromValue(ResumeRepresentation.fromDomainToRepresentation(it))) }
                }

        GET("/resume/{resumeId}").and(accept(APPLICATION_PDF))
                .invoke {
                    println("rotta invocata")
                    val resumeId = it.pathVariable("resumeId").removeSuffix(".pdf")
                    resumePrinter.printResumeFor(resumeId)
                            .flatMap { println("${it.size}");ok().body(BodyInserters.fromValue(it)) }
                }
        POST("/resume")
        {
            val userName = "valerio.vaudi@gmail.com" //todo remove it when auth will be introduced
            resumeRepository.save(Resume.emptyResume(UUID.randomUUID().toString(), userName, Language.EN)).toMono()
                    .flatMap { created(URI("${baseServer}/resume/${it.id}")).build() }
        }

    }
}