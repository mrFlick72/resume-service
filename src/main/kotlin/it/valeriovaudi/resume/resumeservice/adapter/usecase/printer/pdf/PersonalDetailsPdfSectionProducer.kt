package it.valeriovaudi.resume.resumeservice.adapter.usecase.printer.pdf

import com.itextpdf.layout.element.Table
import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetails
import java.time.format.DateTimeFormatter
import java.util.*

class PersonalDetailsPdfSectionProducer(val dateTimeFormatter: DateTimeFormatter = PdfResumePrinter.dateTimeFormatter, val label: Map<String, String> = ResumeLabelRepository.resumeDefaultLabel()) {

    fun newPersonalDetailsCells(table: Table, personalDetails: PersonalDetails) {
        table.addCell(CellFactory.newSectionCell("Resume")).addCell(CellFactory.photoCell(personalDetails.photo.content))
        table.addCell(CellFactory.newSectionCell("Personal Details")).addCell(CellFactory.newSecondCell(""))

        table.addCell(CellFactory.newFirstCell(label.getOrDefault("firstName", ""))).addCell(CellFactory.newSecondCell(personalDetails.firstName))
        table.addCell(CellFactory.newFirstCell(label.getOrDefault("lastName", ""))).addCell(CellFactory.newSecondCell(personalDetails.lastName))
        table.addCell(CellFactory.newFirstCell(label.getOrDefault("address", ""))).addCell(CellFactory.newSecondCell(personalDetails.address))
        table.addCell(CellFactory.newFirstCell(label.getOrDefault("zip", ""))).addCell(CellFactory.newSecondCell(personalDetails.zip))
        table.addCell(CellFactory.newFirstCell(label.getOrDefault("city", ""))).addCell(CellFactory.newSecondCell(personalDetails.city))
        table.addCell(CellFactory.newFirstCell(label.getOrDefault("region", ""))).addCell(CellFactory.newSecondCell(personalDetails.region))
        table.addCell(CellFactory.newFirstCell(label.getOrDefault("mail", ""))).addCell(CellFactory.newSecondCell(personalDetails.mail))
        table.addCell(CellFactory.newFirstCell(label.getOrDefault("mobile", ""))).addCell(CellFactory.newSecondCell(personalDetails.mobile))

        Optional.ofNullable(personalDetails.birthDate)
                .ifPresent { table.addCell(CellFactory.newFirstCell(label.getOrDefault("birthDate", ""))).addCell(CellFactory.newSecondCell(dateTimeFormatter.format(it))) }

        table.addCell(CellFactory.newFirstCell(label.getOrDefault("country", ""))).addCell(CellFactory.newSecondCell(personalDetails.country))
        table.addCell(CellFactory.newFirstCell(label.getOrDefault("sex", ""))).addCell(CellFactory.newSecondCell(personalDetails.sex.name))
        table.addCell(CellFactory.newFirstCell(label.getOrDefault("taxCode", ""))).addCell(CellFactory.newSecondCell(personalDetails.taxCode))
    }
}