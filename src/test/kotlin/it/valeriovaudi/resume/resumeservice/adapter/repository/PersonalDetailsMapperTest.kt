package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.adapter.repository.mapper.PersonalDetailsMapper
import org.junit.Test
import java.time.LocalDate

class PersonalDetailsMapperTest{

    @Test
    fun test(){
        println(PersonalDetailsMapper.dateFormatter.format(LocalDate.now()))
    }
}