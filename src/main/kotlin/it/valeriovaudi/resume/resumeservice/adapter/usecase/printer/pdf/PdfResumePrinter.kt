package it.valeriovaudi.resume.resumeservice.adapter.usecase.printer.pdf

import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Table
import it.valeriovaudi.resume.resumeservice.adapter.repository.MongoResumeRepository
import it.valeriovaudi.resume.resumeservice.domain.model.*
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
import javax.swing.text.html.Option

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

                    newPersonalDetailsCells(table, it.personalDetails, ResumeLabelRepository.resumeDefaultLabel())
                    newEmptyCells(table)
                    newSkillCells(table, it.skill)
                    newEmptyCells(table)
                    newEducationsCells(table, it.educations, ResumeLabelRepository.resumeDefaultLabel())
                    newWorkExperienceCells(table, it.workExperience, ResumeLabelRepository.resumeDefaultLabel())

                    document.add(table)

                    Mono.just(Unit)
                }.block()
    }

    fun newPersonalDetailsCells(table: Table, personalDetails: PersonalDetails, label: Map<String, String>) {
        table.addCell(CellFactory.newSectionCell("Resume")).addCell(CellFactory.photoCell(personalDetails.photo.content))
        table.addCell(CellFactory.newSectionCell("Personal Details")).addCell(CellFactory.newSecondCell(""))

        table.addCell(CellFactory.newFirstCell("firstName")).addCell(CellFactory.newSecondCell(personalDetails.firstName))
        table.addCell(CellFactory.newFirstCell("lastName")).addCell(CellFactory.newSecondCell(personalDetails.lastName))
        table.addCell(CellFactory.newFirstCell("address")).addCell(CellFactory.newSecondCell(personalDetails.address))
        table.addCell(CellFactory.newFirstCell("zip")).addCell(CellFactory.newSecondCell(personalDetails.zip))
        table.addCell(CellFactory.newFirstCell("city")).addCell(CellFactory.newSecondCell(personalDetails.city))
        table.addCell(CellFactory.newFirstCell("region")).addCell(CellFactory.newSecondCell(personalDetails.region))
        table.addCell(CellFactory.newFirstCell("mail")).addCell(CellFactory.newSecondCell(personalDetails.mail))
        table.addCell(CellFactory.newFirstCell("mobile")).addCell(CellFactory.newSecondCell(personalDetails.mobile))
        table.addCell(CellFactory.newFirstCell("birthDate")).addCell(CellFactory.newSecondCell(dateTimeFormatter.format(personalDetails.birthDate)))
        table.addCell(CellFactory.newFirstCell("country")).addCell(CellFactory.newSecondCell(personalDetails.country))
        table.addCell(CellFactory.newFirstCell("sex")).addCell(CellFactory.newSecondCell(personalDetails.sex.name))
        table.addCell(CellFactory.newFirstCell("taxCode")).addCell(CellFactory.newSecondCell(personalDetails.taxCode))
    }

    fun newEducationsCells(table: Table, educations: List<Education>, label: Map<String, String>) {
        table.addCell(CellFactory.newSectionCell("Educations")).addCell(CellFactory.newSecondCell(""))

        educations.forEach { education ->
            Optional.ofNullable(education.company).ifPresent({ table.addCell(CellFactory.newFirstCell("company")).addCell(CellFactory.newSecondCell(it)) })
            table.addCell(CellFactory.newFirstCell("title")).addCell(CellFactory.newSecondCell(education.title))
            table.addCell(CellFactory.newFirstCell("type")).addCell(CellFactory.newSecondCell(education.type.name))
            table.addCell(CellFactory.newFirstCell("dateFrom")).addCell(CellFactory.newSecondCell(dateTimeFormatter.format(education.dateFrom)))
            Optional.ofNullable(education.dateTo).ifPresent({ table.addCell(CellFactory.newFirstCell("dateTo")).addCell(CellFactory.newSecondCell(dateTimeFormatter.format(it))) })
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

    fun newWorkExperienceCells(table: Table, workExperience: List<WorkExperience>, label: Map<String, String>) {
        table.addCell(CellFactory.newSectionCell("Work Experiences")).addCell(CellFactory.newSecondCell(""))
        workExperience.forEach { workExperience ->
            table.addCell(CellFactory.newFirstCell("company")).addCell(CellFactory.newSecondCell(workExperience.company))
            table.addCell(CellFactory.newFirstCell("startDate")).addCell(CellFactory.newSecondCell(dateTimeFormatter.format(workExperience.startDate)))
            Optional.ofNullable(workExperience.endDate).ifPresent({ table.addCell(CellFactory.newFirstCell("endDate")).addCell(CellFactory.newSecondCell(dateTimeFormatter.format(it))) })
//            table.addCell(CellFactory.newFirstCell("commitments")).addCell(newListOfItemCell(workExperience.commitments))
            table.addCell(CellFactory.newFirstCell("jobDescription")).addCell(CellFactory.newSecondCell(workExperience.jobDescription))
//            table.addCell(CellFactory.newFirstCell("technologies")).addCell(CellFactory.newSecondCell(workExperience.technologies))

            newEmptyCells(table)
        }
    }

    fun newEmptyCells(table: Table) {
        table.addCell(CellFactory.newFirstCell("")).addCell(CellFactory.newSecondCell(""))
    }
}