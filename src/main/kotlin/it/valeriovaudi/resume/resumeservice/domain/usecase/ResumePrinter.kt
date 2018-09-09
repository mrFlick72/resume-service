package it.valeriovaudi.resume.resumeservice.domain.usecase

import it.valeriovaudi.resume.resumeservice.domain.model.Resume
import java.io.InputStream

interface ResumePrinter {

    fun printResumeFor(resumeId: String): InputStream

}