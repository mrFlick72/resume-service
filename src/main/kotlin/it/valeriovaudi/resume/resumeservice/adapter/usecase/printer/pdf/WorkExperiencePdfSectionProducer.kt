package it.valeriovaudi.resume.resumeservice.adapter.usecase.printer.pdf

import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import it.valeriovaudi.resume.resumeservice.domain.model.WorkExperience
import java.time.format.DateTimeFormatter
import java.util.*

class WorkExperiencePdfSectionProducer(val dateTimeFormatter: DateTimeFormatter = PdfResumePrinter.dateTimeFormatter,
                                       val label: Map<String, String> = ResumeLabelRepository.resumeDefaultLabel()) {

    fun newWorkExperienceCells(table: Table, workExperiences: List<WorkExperience>) {
        table.addCell(CellFactory.newSectionCell("Work Experiences")).addCell(CellFactory.newSecondCell(""))

        val innerTable = Table(2)

        workExperiences.forEach { workExperience ->
            innerTable.addCell(CellFactory.newSecondCell(label.getOrDefault("company", ""))).addCell(CellFactory.newSecondCell(workExperience.company))
            innerTable.addCell(CellFactory.newSecondCell(label.getOrDefault("startDate", ""))).addCell(CellFactory.newSecondCell(PdfResumePrinter.dateTimeFormatter.format(workExperience.startDate)))
            Optional.ofNullable(workExperience.endDate).ifPresent({ innerTable.addCell(CellFactory.newSecondCell(label.getOrDefault("endDate", ""))).addCell(CellFactory.newSecondCell(dateTimeFormatter.format(it))) })

            val commitmentsTable = Table(1)
            workExperience.commitments.forEach { commitmentsTable.addCell(CellFactory.newCell().add(Paragraph(it)).setBold()) }
            innerTable.addCell(CellFactory.newSecondCell(label.getOrDefault("commitments", "")))
                    .addCell(Cell().add(commitmentsTable).setBorder(Border.NO_BORDER))

            innerTable.addCell(CellFactory.newSecondCell(label.getOrDefault("jobDescription", ""))).addCell(CellFactory.newSecondCell(workExperience.jobDescription))

            val technologiesTable = Table(1)
            workExperience.technologies.forEach { technologiesTable.addCell(CellFactory.newCell().add(Paragraph(it)).setBold()) }
            innerTable.addCell(CellFactory.newSecondCell(label.getOrDefault("technologies", "")))
                    .addCell(Cell().add(technologiesTable).setBorder(Border.NO_BORDER))


            table.addCell(CellFactory.newFirstCell("")).addCell(CellFactory.newSecondCell(""))
        }

        table.addCell(CellFactory.newFirstCell("")).addCell(Cell().add(innerTable).setBorder(Border.NO_BORDER))
    }

}