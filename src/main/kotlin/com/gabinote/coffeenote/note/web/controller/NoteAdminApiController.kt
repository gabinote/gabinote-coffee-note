package com.gabinote.coffeenote.note.web.controller

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@Validated
@RequestMapping("/api/v1/admin")
@RestController
class NoteAdminApiController