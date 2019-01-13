package it.valeriovaudi.resume.resumeservice.domain.usecase

import java.io.InputStream

interface ResumePrinter {

    fun printResumeFor(resumeId: String): InputStream

}