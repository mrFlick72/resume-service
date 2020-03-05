package it.valeriovaudi.resume.resumeservice.domain.usecase

import reactor.core.publisher.Mono
import java.io.InputStream

interface ResumePrinter {

    fun printResumeFor(resumeId: String): Mono<ByteArray>

}