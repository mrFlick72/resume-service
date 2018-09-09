package it.valeriovaudi.resume.resumeservice.adapter.usecase.printer.pdf

import it.valeriovaudi.resume.resumeservice.domain.model.Resume
import it.valeriovaudi.resume.resumeservice.domain.usecase.ResumePrinter
import java.io.InputStream

class PdfResumePrinter : ResumePrinter {
    override fun printResumeFor(resumeId: String): InputStream {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}