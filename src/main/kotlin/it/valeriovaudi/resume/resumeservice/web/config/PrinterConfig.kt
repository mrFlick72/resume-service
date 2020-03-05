package it.valeriovaudi.resume.resumeservice.web.config

import it.valeriovaudi.resume.resumeservice.adapter.usecase.printer.pdf.*
import org.springframework.context.support.beans

object PrinterConfig {

    fun beans() = beans {
        bean<PdfResumePrinter>()
        bean<PersonalDetailsPdfSectionProducer>()
        bean<EducationsPdfSectionProducer>()
        bean<SkillsPdfSectionProducer>()
        bean<WorkExperiencePdfSectionProducer>()
    }
}