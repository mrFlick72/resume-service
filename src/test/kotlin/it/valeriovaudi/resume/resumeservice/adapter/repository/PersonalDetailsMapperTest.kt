package it.valeriovaudi.resume.resumeservice.adapter.repository

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.util.*

class PersonalDetailsMapperTest{

    @Test
    fun test(){
        println(PersonalDetailsMapper.dateFormatter.format(LocalDate.now()))
    }
}