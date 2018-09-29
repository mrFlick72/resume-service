package it.valeriovaudi.resume.resumeservice.adapter.usecase.printer.pdf

import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Table
import it.valeriovaudi.resume.resumeservice.adapter.repository.MongoResumeRepository
import it.valeriovaudi.resume.resumeservice.domain.model.Education
import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetails
import it.valeriovaudi.resume.resumeservice.domain.model.Skill
import it.valeriovaudi.resume.resumeservice.domain.usecase.ResumePrinter
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.beans.Introspector
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Files
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class PdfResumePrinter(private val resumeRepository: MongoResumeRepository) : ResumePrinter {

    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    override fun printResumeFor(resumeId: String): InputStream {
        val pdfPath = Files.createTempFile(UUID.randomUUID().toString(), ".pdf")

        Files.newOutputStream(pdfPath).use { pdfStream ->
            PdfWriter(pdfStream).use { pdfWriter ->
                PdfDocument(pdfWriter).use { pdfDocument ->
                    val document = Document(pdfDocument)
                    this.makePdf(resumeId, document)
                    document.close()
                }
            }

            return FileInputStream(pdfPath.toFile())
        }
    }

    fun makePdf(resumeId: String, document: Document): Unit? {
        return this.resumeRepository.findOne(resumeId).toMono()
                .flatMap {
                    document.setFontSize(14f)

                    val table = Table(2).setWidth(PageSize.A4.width * 0.80f).setAutoLayout()
                    table.setPaddingRight(25f)
                    table.setMarginRight(25f)

                    newPersonalDetailsCells(table, it.personalDetails)
                    newEmptyCells(table)
                    newSkillCells(table, it.skill)
                    newEmptyCells(table)
                    newEducationsCells(table, it.educations)

                    document.add(table)

                    Mono.just(Unit)
                }.block()
    }

    fun newPersonalDetailsCells(table: Table, personalDetails: PersonalDetails) {
        table.addCell(CellFactory.newSectionCell("Resume")).addCell(CellFactory.photoCell(personalDetails.photo.content))
        table.addCell(CellFactory.newSectionCell("Personal Details")).addCell(CellFactory.newSecondCell(""))

        Introspector.getBeanInfo(personalDetails::class.java).propertyDescriptors.map {
            val data = it.readMethod.invoke(personalDetails)
            val label = ResumeLabelRepository.resumeDefaultLabel().getOrDefault(it.name, "")

            when (data) {
                is String -> table.addCell(CellFactory.newFirstCell(label)).addCell(CellFactory.newSecondCell(data))
                is LocalDate -> table.addCell(CellFactory.newFirstCell(label)).addCell(CellFactory.newSecondCell(dateTimeFormatter.format(data)))
                else -> {
                }
            }
        }
    }

    fun newEducationsCells(table: Table, educations: List<Education>) {
        table.addCell(CellFactory.newSectionCell("Educations")).addCell(CellFactory.newSecondCell(""))

        educations.forEach { education ->
            Introspector.getBeanInfo(Education::class.java).propertyDescriptors.filter { it.name != "id" }.map {
                val data = it.readMethod.invoke(education)
                val label = ResumeLabelRepository.resumeDefaultLabel().getOrDefault(it.name, "")

                when (data) {
                    is String -> table.addCell(CellFactory.newFirstCell(label)).addCell(CellFactory.newSecondCell(data))
                    is LocalDate -> table.addCell(CellFactory.newFirstCell(label)).addCell(CellFactory.newSecondCell(dateTimeFormatter.format(data)))
                    else -> {
                    }
                }
            }
            newEmptyCells(table)
        }
    }

    fun newSkillCells(table: Table, skill: List<Skill>) {
        table.addCell(CellFactory.newSectionCell("Skills")).addCell(CellFactory.newSecondCell(""))

        skill.forEach {
            table.addCell(CellFactory.newFirstCell("")).addCell(CellFactory.newSecondCell(it.family))
            it.skills.forEach {
                table.addCell(CellFactory.newFirstCell("")).addCell(CellFactory.newSecondCell(it))
            }
        }
    }

    fun newEmptyCells(table: Table) {
        table.addCell(CellFactory.newFirstCell("")).addCell(CellFactory.newSecondCell(""))
    }

}