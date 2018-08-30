package it.valeriovaudi.resume.resumeservice.web.route

import it.valeriovaudi.resume.resumeservice.domain.repository.PersonalDetailsRepository
import it.valeriovaudi.resume.resumeservice.web.representation.PersonalDetailsRepresentation
import org.springframework.context.support.beans
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.toMono

object PersonalDetailsRoute {

    fun routes() = beans {
        bean {
            val personalDetailsRepository = ref<PersonalDetailsRepository>()

            router {

                GET("/resume/{resumeId}/personal-details")
                {
                    personalDetailsRepository.findOneWithoutPhoto(it.pathVariable("resumeId")).toMono()
                            .map { PersonalDetailsRepresentation.fromDomainToRepresentation(it).toMono() }
                            .flatMap { ServerResponse.ok().body(it, PersonalDetailsRepresentation::class.java) }
                }

                POST("/resume/{resumeId}/personal-details")
                {
                    val resumeId = it.pathVariable("resumeId")
                    it.bodyToMono(PersonalDetailsRepresentation::class.java)
                            .map { PersonalDetailsRepresentation.fromRepresentationToDomain(it) }
                            .flatMap { personalDetailsRepository.save(resumeId, it).toMono() }
                            .flatMap { ServerResponse.status(HttpStatus.CREATED).build() }
                }


            }
        }
    }
}