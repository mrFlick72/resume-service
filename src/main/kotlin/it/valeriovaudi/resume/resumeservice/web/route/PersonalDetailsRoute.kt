package it.valeriovaudi.resume.resumeservice.web.route

import it.valeriovaudi.resume.resumeservice.domain.repository.PersonalDetailsRepository
import it.valeriovaudi.resume.resumeservice.web.representation.PersonalDetailsRepresentation
import org.springframework.context.support.beans
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.status
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.toMono

object PersonalDetailsRoute {

    fun routes() = beans {
        bean {
            val personalDetailsRepository = ref<PersonalDetailsRepository>()

            router {
                POST("/resume/{resumeId}/personal-details")
                {
                    personalDetailsRepository.save(it.pathVariable("resumeId"),
                            it.bodyToMono(PersonalDetailsRepresentation::class.java)
                                    .map { PersonalDetailsRepresentation.fromRepresentationToDomain(it) })
                            .toMono().flatMap { status(HttpStatus.CREATED).build() }
                }

            }
        }
    }
}