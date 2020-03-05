package it.valeriovaudi.resume.resumeservice.adapter.usecase.printer.pdf

import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Table
import it.valeriovaudi.resume.resumeservice.adapter.repository.MongoResumeRepository
import it.valeriovaudi.resume.resumeservice.domain.model.Resume
import it.valeriovaudi.resume.resumeservice.domain.usecase.ResumePrinter
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Files
import java.time.format.DateTimeFormatter
import java.util.*

class PdfResumePrinter(private val resumeRepository: MongoResumeRepository,
                       private val personalDetailsPdfSectionProducer: PersonalDetailsPdfSectionProducer,
                       private val educationsPdfSectionProducer: EducationsPdfSectionProducer,
                       private val skillsPdfSectionProducer: SkillsPdfSectionProducer,
                       private val workExperiencePdfSectionProducer: WorkExperiencePdfSectionProducer) : ResumePrinter {

    companion object {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        fun newEmptyCells(table: Table) {
            table.addCell(CellFactory.newFirstCell("")).addCell(CellFactory.newSecondCell(""))
        }
    }

    override fun printResumeFor(resumeId: String): Mono<ByteArray>{
        println("resumeId: $resumeId")
        return this.resumeRepository.findOne(resumeId).toMono()
                .map { resume ->
                    println(resume)
                    val pdfPath = Files.createTempFile(UUID.randomUUID().toString(), ".pdf")

                    Files.newOutputStream(pdfPath).use { pdfStream ->
                        PdfWriter(pdfStream).use { pdfWriter ->
                            PdfDocument(pdfWriter).use { pdfDocument ->
                                val document = Document(pdfDocument)
                                this.makePdf(resume, document)
                                document.close()
                            }
                        }

                        println("OK")
                        FileInputStream(pdfPath.toFile()).readAllBytes()
                    }
                }

    }
    private fun makePdf(resume: Resume, document: Document) {
            document.setFontSize(14f)

            val table = Table(2).setWidth(PageSize.A4.width * 0.80f).setAutoLayout()
            table.setPaddingRight(25f)
            table.setMarginRight(25f)

            personalDetailsPdfSectionProducer.newPersonalDetailsCells(table, resume.personalDetails)
            newEmptyCells(table)
            skillsPdfSectionProducer.newSkillCells(table, resume.skill)
            newEmptyCells(table)
            educationsPdfSectionProducer.newEducationsCells(table, resume.educations)
            workExperiencePdfSectionProducer.newWorkExperienceCells(table, resume.workExperience)

            document.add(table)
    }

}