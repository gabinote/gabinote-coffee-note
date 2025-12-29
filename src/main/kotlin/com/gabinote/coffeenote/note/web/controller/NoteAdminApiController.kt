package com.gabinote.coffeenote.note.web.controller

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@Validated
@RequestMapping("/admin/api/v1")
@RestController
class NoteAdminApiController