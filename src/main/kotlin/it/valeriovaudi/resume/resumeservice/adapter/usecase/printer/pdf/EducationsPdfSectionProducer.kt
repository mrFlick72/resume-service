package it.valeriovaudi.resume.resumeservice.adapter.usecase.printer.pdf

import com.itextpdf.layout.element.Table
import it.valeriovaudi.resume.resumeservice.domain.model.Education
import java.time.format.DateTimeFormatter
import java.util.*

class EducationsPdfSectionProducer(val dateTimeFormatter: DateTimeFormatter = PdfResumePrinter.dateTimeFormatter,
                                   val label: Map<String, String> = ResumeLabelRepository.resumeDefaultLabel()) {

    fun newEducationsCells(table: Table, educations: List<Education>) {
        table.addCell(CellFactory.newSectionCell("Educations")).addCell(CellFactory.newSecondCell(""))

        educations.forEach { education ->
            Optional.ofNullable(education.company)
                    .ifPresent({
                        if (it.isNotEmpty()) {
                            table.addCell(CellFactory.newFirstCell(label.getOrDefault("company", ""))).addCell(CellFactory.newSecondCell(it))
                        }
                    })

            table.addCell(CellFactory.newFirstCell(label.getOrDefault("title", ""))).addCell(CellFactory.newSecondCell(education.title))
            table.addCell(CellFactory.newFirstCell(label.getOrDefault("type", ""))).addCell(CellFactory.newSecondCell(education.type.name))
            table.addCell(CellFactory.newFirstCell(label.getOrDefault("dateFrom", ""))).addCell(CellFactory.newSecondCell(PdfResumePrinter.dateTimeFormatter.format(education.dateFrom)))

            Optional.ofNullable(education.dateTo)
                    .ifPresent({ table.addCell(CellFactory.newFirstCell(label.getOrDefault("dateTo", ""))).addCell(CellFactory.newSecondCell(dateTimeFormatter.format(it))) })

            PdfResumePrinter.newEmptyCells(table)
        }
    }
}