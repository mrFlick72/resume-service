package it.valeriovaudi.resume.resumeservice

import java.net.URI

fun URI.extractId() = this.toString().split("/").run { this[this.size - 1] }