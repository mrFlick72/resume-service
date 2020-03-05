package it.valeriovaudi.resume.resumeservice.adapter.usecase.printer.pdf

import com.itextpdf.layout.element.Table
import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetails
import it.valeriovaudi.resume.resumeservice.domain.model.Skill
import java.time.format.DateTimeFormatter

class SkillsPdfSectionProducer() {


    fun newSkillCells(table: Table, skill: List<Skill>) {
        table.addCell(CellFactory.newSectionCell("Skills")).addCell(CellFactory.newSecondCell(""))

        skill.forEach {
            table.addCell(CellFactory.newFirstCell("")).addCell(CellFactory.newSecondCell(it.family, true))
            it.skills.forEach {
                table.addCell(CellFactory.newFirstCell("")).addCell(CellFactory.newSecondCell(it))
            }
            table.addCell(CellFactory.newFirstCell("")).addCell(CellFactory.newSecondCell(""))
            table.addCell(CellFactory.newFirstCell("")).addCell(CellFactory.newSecondCell(""))

        }
    }

}