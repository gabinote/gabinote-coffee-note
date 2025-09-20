package com.gabinote.coffeenote.template.service

import com.gabinote.coffeenote.common.util.exception.service.ResourceNotFound
import com.gabinote.coffeenote.common.util.exception.service.ResourceNotValid
import com.gabinote.coffeenote.template.domain.template.Template
import com.gabinote.coffeenote.template.domain.template.TemplateRepository
import com.gabinote.coffeenote.template.domain.templateField.TemplateField
import com.gabinote.coffeenote.template.dto.template.service.*
import com.gabinote.coffeenote.template.dto.templateField.service.TemplateFieldCreateReqServiceDto
import com.gabinote.coffeenote.template.mapping.template.TemplateMapper
import com.gabinote.coffeenote.template.service.template.TemplateService
import com.gabinote.coffeenote.template.service.template.strategy.*
import com.gabinote.coffeenote.template.service.templateField.TemplateFieldService
import com.gabinote.coffeenote.testSupport.testTemplate.ServiceTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.data.template.TemplateTestDataHelper.createTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.page.TestPageableUtil
import com.gabinote.coffeenote.testSupport.testUtil.page.TestSliceUtil.toSlice
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.bson.types.ObjectId
import org.junit.jupiter.api.assertThrows
import java.util.*

class TemplateServiceTest : ServiceTestTemplate() {

    private lateinit var templateService: TemplateService

    @MockK
    private lateinit var templateRepository: TemplateRepository

    @MockK
    private lateinit var templateMapper: TemplateMapper

    @MockK
    private lateinit var getTemplateByExternalIdStrategyFactory: GetTemplateByExternalIdStrategyFactory

    @MockK
    private lateinit var templateFieldService: TemplateFieldService

    init {
        beforeTest {
            clearAllMocks()
            templateService = TemplateService(
                templateRepository = templateRepository,
                templateMapper = templateMapper,
                getTemplateByExternalIdStrategyFactory = getTemplateByExternalIdStrategyFactory,
                templateFieldService = templateFieldService
            )
        }

        describe("[Template] TemplateService Test") {
            describe("TemplateService.fetchByExternalId") {
                context("존재하는 올바른 externalId가 주어지면") {
                    val validExternalId = UUID.randomUUID()
                    val template = createTestTemplate(externalId = validExternalId)

                    beforeTest {
                        every { templateRepository.findByExternalId(validExternalId.toString()) } returns template
                    }

                    it("해당 externalId에 매핑되는 템플릿을 반환한다") {
                        val result = templateService.fetchByExternalId(validExternalId)
                        result shouldBe template

                        verify(exactly = 1) { templateRepository.findByExternalId(validExternalId.toString()) }
                    }
                }

                context("존재하지 않는 잘못된 externalId가 주어지면") {
                    val invalidExternalId = UUID.randomUUID()

                    beforeTest {
                        every { templateRepository.findByExternalId(invalidExternalId.toString()) } returns null
                    }

                    it("ResourceNotFound 예외를 던진다") {
                        val ex = assertThrows<ResourceNotFound> {
                            templateService.fetchByExternalId(invalidExternalId)
                        }
                        ex.name shouldBe "Template"
                        ex.identifier shouldBe invalidExternalId.toString()
                        ex.identifierType shouldBe "externalId"

                        verify(exactly = 1) { templateRepository.findByExternalId(invalidExternalId.toString()) }
                    }
                }
            }

            describe("TemplateService.getByExternalId") {
                describe("ALL strategy") {
                    val allStrategy = mockk<GetAllStrategy>()
                    beforeTest {
                        every { getTemplateByExternalIdStrategyFactory.getStrategy(GetTemplateByExternalIdStrategyType.ALL) } returns allStrategy
                    }

                    context("올바른 externalId와 requestor가 주어지면") {
                        val validExternalId = UUID.randomUUID()
                        val requestor = "test-user"
                        val template = createTestTemplate(externalId = validExternalId)
                        val expectedDto = mockk<TemplateResServiceDto>()

                        beforeTest {
                            every { templateRepository.findByExternalId(validExternalId.toString()) } returns template
                            every { allStrategy.validate(requestor, template) } returns Unit
                            every { templateMapper.toResServiceDto(template) } returns expectedDto
                        }

                        it("해당 externalId에 매핑되는 템플릿을 반환한다") {
                            val result = templateService.getByExternalId(
                                validExternalId,
                                requestor,
                                GetTemplateByExternalIdStrategyType.ALL
                            )
                            result shouldBe expectedDto

                            verify(exactly = 1) {
                                templateRepository.findByExternalId(validExternalId.toString())
                                getTemplateByExternalIdStrategyFactory.getStrategy(GetTemplateByExternalIdStrategyType.ALL)
                                allStrategy.validate(requestor, template)
                                templateMapper.toResServiceDto(template)
                            }
                        }
                    }
                }

                describe("DEFAULT strategy") {
                    val defaultStrategy = mockk<GetDefaultStrategy>()
                    beforeTest {
                        every { getTemplateByExternalIdStrategyFactory.getStrategy(GetTemplateByExternalIdStrategyType.DEFAULT) } returns defaultStrategy
                    }

                    context("올바른 기본 템플릿의 externalId이 주어지면") {
                        val validExternalId = UUID.randomUUID()
                        val requestor = "test-user"
                        val template = createTestTemplate(isDefault = true, externalId = validExternalId)
                        val expectedDto = mockk<TemplateResServiceDto>()

                        beforeTest {
                            every { templateRepository.findByExternalId(validExternalId.toString()) } returns template
                            every { defaultStrategy.validate(requestor, template) } returns Unit
                            every { templateMapper.toResServiceDto(template) } returns expectedDto
                        }

                        it("해당 externalId에 매핑되는 템플릿을 반환한다") {
                            val result = templateService.getByExternalId(
                                validExternalId,
                                requestor,
                                GetTemplateByExternalIdStrategyType.DEFAULT
                            )
                            result shouldBe expectedDto

                            verify(exactly = 1) {
                                templateRepository.findByExternalId(validExternalId.toString())
                                getTemplateByExternalIdStrategyFactory.getStrategy(GetTemplateByExternalIdStrategyType.DEFAULT)
                                defaultStrategy.validate(requestor, template)
                                templateMapper.toResServiceDto(template)
                            }
                        }
                    }

                    context("기본 템플릿이 아닌 externalId가 주어지면") {
                        val invalidExternalId = UUID.randomUUID()
                        val requestor = "test-user"
                        val template = createTestTemplate(isDefault = false, externalId = invalidExternalId)

                        beforeTest {
                            every { templateRepository.findByExternalId(invalidExternalId.toString()) } returns template
                            every { defaultStrategy.validate(requestor, template) } throws ResourceNotFound(
                                name = "Default Template",
                                identifierType = "externalId",
                                identifier = invalidExternalId.toString()
                            )
                        }

                        it("ResourceNotFound 예외를 던진다") {
                            val ex = assertThrows<ResourceNotFound> {
                                templateService.getByExternalId(
                                    invalidExternalId,
                                    requestor,
                                    GetTemplateByExternalIdStrategyType.DEFAULT
                                )
                            }
                            ex.name shouldBe "Default Template"

                            verify(exactly = 1) {
                                templateRepository.findByExternalId(invalidExternalId.toString())
                                getTemplateByExternalIdStrategyFactory.getStrategy(GetTemplateByExternalIdStrategyType.DEFAULT)
                                defaultStrategy.validate(requestor, template)
                            }
                        }
                    }
                }

                describe("OPENED strategy") {
                    val openStrategy = mockk<GetOpenStrategy>()
                    beforeTest {
                        every { getTemplateByExternalIdStrategyFactory.getStrategy(GetTemplateByExternalIdStrategyType.OPENED) } returns openStrategy
                    }

                    context("올바른 공개 템플릿의 externalId가 주어지면") {
                        val validExternalId = UUID.randomUUID()
                        val requestor = "test-user"
                        val template = createTestTemplate(isOpen = true, externalId = validExternalId)
                        val expectedDto = mockk<TemplateResServiceDto>()

                        beforeTest {
                            every { templateRepository.findByExternalId(validExternalId.toString()) } returns template
                            every { openStrategy.validate(requestor, template) } returns Unit
                            every { templateMapper.toResServiceDto(template) } returns expectedDto
                        }

                        it("해당 externalId에 매핑되는 템플릿을 반환한다") {
                            val result = templateService.getByExternalId(
                                validExternalId,
                                requestor,
                                GetTemplateByExternalIdStrategyType.OPENED
                            )
                            result shouldBe expectedDto

                            verify(exactly = 1) {
                                templateRepository.findByExternalId(validExternalId.toString())
                                getTemplateByExternalIdStrategyFactory.getStrategy(GetTemplateByExternalIdStrategyType.OPENED)
                                openStrategy.validate(requestor, template)
                                templateMapper.toResServiceDto(template)
                            }
                        }
                    }

                    context("공개 상태가 아닌 템플릿의 externalId가 주어지면") {
                        val invalidExternalId = UUID.randomUUID()
                        val requestor = "test-user"
                        val template = createTestTemplate(isOpen = false, externalId = invalidExternalId)

                        beforeTest {
                            every { templateRepository.findByExternalId(invalidExternalId.toString()) } returns template
                            every { openStrategy.validate(requestor, template) } throws ResourceNotFound(
                                name = "Opened Template",
                                identifierType = "externalId",
                                identifier = invalidExternalId.toString()
                            )
                        }

                        it("ResourceNotFound 예외를 던진다") {
                            val ex = assertThrows<ResourceNotFound> {
                                templateService.getByExternalId(
                                    invalidExternalId,
                                    requestor,
                                    GetTemplateByExternalIdStrategyType.OPENED
                                )
                            }
                            ex.name shouldBe "Opened Template"

                            verify(exactly = 1) {
                                templateRepository.findByExternalId(invalidExternalId.toString())
                                getTemplateByExternalIdStrategyFactory.getStrategy(GetTemplateByExternalIdStrategyType.OPENED)
                                openStrategy.validate(requestor, template)
                            }
                        }
                    }
                }

                describe("OWNED strategy") {
                    val ownedStrategy = mockk<GetOwnedStrategy>()
                    beforeTest {
                        every { getTemplateByExternalIdStrategyFactory.getStrategy(GetTemplateByExternalIdStrategyType.OWNED) } returns ownedStrategy
                    }

                    context("올바른 소유 템플릿의 externalId와 requestor가 주어지면") {
                        val validExternalId = UUID.randomUUID()
                        val requestor = "test-owner"
                        val template = createTestTemplate(owner = requestor, externalId = validExternalId)
                        val expectedDto = mockk<TemplateResServiceDto>()

                        beforeTest {
                            every { templateRepository.findByExternalId(validExternalId.toString()) } returns template
                            every { ownedStrategy.validate(requestor, template) } returns Unit
                            every { templateMapper.toResServiceDto(template) } returns expectedDto
                        }

                        it("해당 externalId에 매핑되는 템플릿을 반환한다") {
                            val result = templateService.getByExternalId(
                                validExternalId,
                                requestor,
                                GetTemplateByExternalIdStrategyType.OWNED
                            )
                            result shouldBe expectedDto

                            verify(exactly = 1) {
                                templateRepository.findByExternalId(validExternalId.toString())
                                getTemplateByExternalIdStrategyFactory.getStrategy(GetTemplateByExternalIdStrategyType.OWNED)
                                ownedStrategy.validate(requestor, template)
                                templateMapper.toResServiceDto(template)
                            }
                        }
                    }

                    context("requestor가 소유자가 아닌 externalId가 주어지면") {
                        val invalidExternalId = UUID.randomUUID()
                        val requestor = "other-user"
                        val template = createTestTemplate(owner = "others", externalId = invalidExternalId)

                        beforeTest {
                            every { templateRepository.findByExternalId(invalidExternalId.toString()) } returns template
                            every { ownedStrategy.validate(requestor, template) } throws ResourceNotFound(
                                name = "Owned Template",
                                identifierType = "externalId",
                                identifier = invalidExternalId.toString()
                            )
                        }

                        it("ResourceNotFound 예외를 던진다") {
                            val ex = assertThrows<ResourceNotFound> {
                                templateService.getByExternalId(
                                    invalidExternalId,
                                    requestor,
                                    GetTemplateByExternalIdStrategyType.OWNED
                                )
                            }
                            ex.name shouldBe "Owned Template"

                            verify(exactly = 1) {
                                templateRepository.findByExternalId(invalidExternalId.toString())
                                getTemplateByExternalIdStrategyFactory.getStrategy(GetTemplateByExternalIdStrategyType.OWNED)
                                ownedStrategy.validate(requestor, template)
                            }
                        }
                    }
                }
            }

            describe("TemplateService.getAll") {
                context("pageable이 주어지면") {
                    val pageable = TestPageableUtil.createPageable()
                    val templates = listOf(
                        createTestTemplate(),
                    ).toSlice(pageable)
                    val expected = listOf(mockk<TemplateResServiceDto>()).toSlice(pageable)

                    beforeTest {
                        every { templateRepository.findAllBy(pageable) } returns templates
                        every { templateMapper.toResServiceDto(templates.content[0]) } returns expected.content[0]
                    }

                    it("템플릿 목록을 반환한다") {
                        val result = templateService.getAll(pageable)
                        result shouldNotBe null
                        result.content.size shouldBe 1
                        result.content[0] shouldBe expected.content[0]

                        verify(exactly = 1) {
                            templateRepository.findAllBy(pageable)
                            templateMapper.toResServiceDto(templates.content[0])
                        }
                    }
                }
            }

            describe("TemplateService.getAllOwned") {
                context("owner와 pageable이 주어지면") {
                    val owner = "test-owner"
                    val pageable = TestPageableUtil.createPageable()
                    val templates = listOf(mockk<Template>()).toSlice(pageable)
                    val expected = listOf(mockk<TemplateResServiceDto>()).toSlice(pageable)

                    beforeTest {
                        every { templateRepository.findAllByOwner(owner, pageable) } returns templates
                        every { templateMapper.toResServiceDto(templates.content[0]) } returns expected.content[0]
                    }

                    it("해당 owner가 소유한 템플릿 목록을 반환한다") {
                        val result = templateService.getAllOwned(owner, pageable)
                        result shouldNotBe null
                        result.content.size shouldBe 1
                        result.content[0] shouldBe expected.content[0]

                        verify(exactly = 1) {
                            templateRepository.findAllByOwner(owner, pageable)
                            templateMapper.toResServiceDto(templates.content[0])
                        }
                    }
                }
            }

            describe("TemplateService.getAllDefault") {
                context("pageable이 주어지면") {
                    val pageable = TestPageableUtil.createPageable()
                    val templates = listOf(mockk<Template>()).toSlice(pageable)
                    val expected = listOf(mockk<TemplateResServiceDto>()).toSlice(pageable)

                    beforeTest {
                        every { templateRepository.findAllByIsDefault(pageable = pageable) } returns templates
                        every { templateMapper.toResServiceDto(templates.content[0]) } returns expected.content[0]
                    }

                    it("기본 템플릿 목록을 반환한다") {
                        val result = templateService.getAllDefault(pageable)
                        result shouldNotBe null
                        result.content.size shouldBe 1
                        result.content[0] shouldBe expected.content[0]

                        verify(exactly = 1) {
                            templateRepository.findAllByIsDefault(pageable = pageable)
                            templateMapper.toResServiceDto(templates.content[0])
                        }
                    }
                }
            }

            describe("TemplateService.createOwned") {
                context("올바른 값이 주어지면") {
                    val dto = TemplateCreateReqServiceDto(
                        name = "New Template",
                        icon = "new-icon",
                        description = "New Description",
                        isOpen = false,
                        owner = "test-owner",
                        fields = listOf(mockk<TemplateFieldCreateReqServiceDto>())
                    )
                    val template = mockk<Template>()
                    val templateFields = listOf(mockk<TemplateField>())
                    val savedTemplate = mockk<Template>()
                    val expected = mockk<TemplateResServiceDto>()

                    beforeTest {
                        every { templateMapper.toTemplate(dto) } returns template
                        every { templateFieldService.create(dto.fields) } returns templateFields
                        every { template.changeFields(templateFields) } returns Unit
                        every { templateRepository.save(template) } returns savedTemplate
                        every { templateMapper.toResServiceDto(savedTemplate) } returns expected
                    }

                    it("템플릿을 생성한다") {
                        val result = templateService.createOwned(dto)
                        result shouldBe expected

                        verify(exactly = 1) {
                            templateMapper.toTemplate(dto)
                            templateFieldService.create(dto.fields)
                            template.changeFields(templateFields)
                            templateRepository.save(template)
                            templateMapper.toResServiceDto(savedTemplate)
                        }
                    }
                }

                context("올바르지 않은 TemplateField 값이 주어지면") {
                    val dto = TemplateCreateReqServiceDto(
                        name = "Invalid Template",
                        icon = "invalid-icon",
                        description = "Invalid Description",
                        isOpen = false,
                        owner = "test-owner",
                        fields = listOf(mockk<TemplateFieldCreateReqServiceDto>())
                    )
                    val template = mockk<Template>()

                    beforeTest {
                        every { templateMapper.toTemplate(dto) } returns template
                        every { templateFieldService.create(dto.fields) } throws ResourceNotValid(
                            name = "TemplateField",
                            reasons = listOf("Invalid field configuration")
                        )
                    }

                    it("ResourceNotValid 예외를 던진다") {
                        val ex = assertThrows<ResourceNotValid> {
                            templateService.createOwned(dto)
                        }
                        ex.name shouldBe "TemplateField"

                        verify(exactly = 1) {
                            templateMapper.toTemplate(dto)
                            templateFieldService.create(dto.fields)
                        }
                    }
                }
            }

            describe("TemplateService.createDefault") {
                context("올바른 값이 주어지면") {
                    val dto = TemplateCreateDefaultReqServiceDto(
                        name = "Default Template",
                        icon = "default-icon",
                        description = "Default Description",
                        fields = listOf(mockk<TemplateFieldCreateReqServiceDto>())
                    )
                    val template = mockk<Template>()
                    val templateFields = listOf(mockk<TemplateField>())
                    val savedTemplate = mockk<Template>()
                    val expected = mockk<TemplateResServiceDto>()

                    beforeTest {
                        every { templateMapper.toDefaultTemplate(dto) } returns template
                        every { templateFieldService.create(dto.fields) } returns templateFields
                        every { template.changeFields(templateFields) } returns Unit
                        every { templateRepository.save(template) } returns savedTemplate
                        every { templateMapper.toResServiceDto(savedTemplate) } returns expected
                    }

                    it("템플릿을 생성한다") {
                        val result = templateService.createDefault(dto)
                        result shouldBe expected

                        verify(exactly = 1) {
                            templateMapper.toDefaultTemplate(dto)
                            templateFieldService.create(dto.fields)
                            template.changeFields(templateFields)
                            templateRepository.save(template)
                            templateMapper.toResServiceDto(savedTemplate)
                        }
                    }
                }

                context("올바르지 않은 TemplateField 값이 주어지면") {
                    val dto = TemplateCreateDefaultReqServiceDto(
                        name = "Invalid Default Template",
                        icon = "invalid-icon",
                        description = "Invalid Description",
                        fields = listOf(mockk<TemplateFieldCreateReqServiceDto>())
                    )
                    val template = mockk<Template>()

                    beforeTest {
                        every { templateMapper.toDefaultTemplate(dto) } returns template
                        every { templateFieldService.create(dto.fields) } throws ResourceNotValid(
                            name = "TemplateField",
                            reasons = listOf("Invalid field configuration")
                        )
                    }

                    it("ResourceNotValid 예외를 던진다") {
                        val ex = assertThrows<ResourceNotValid> {
                            templateService.createDefault(dto)
                        }
                        ex.name shouldBe "TemplateField"

                        verify(exactly = 1) {
                            templateMapper.toDefaultTemplate(dto)
                            templateFieldService.create(dto.fields)
                        }
                    }
                }
            }

            describe("TemplateService.updateDefault") {
                context("올바른 값이 주어지면") {
                    val externalId = UUID.randomUUID()
                    val dto = TemplateUpdateDefaultReqServiceDto(
                        externalId = externalId,
                        name = "Updated Default Template",
                        icon = "updated-icon",
                        description = "Updated Description",
                        fields = listOf(mockk<TemplateFieldCreateReqServiceDto>())
                    )
                    val existingTemplate = Template(
                        id = mockk<ObjectId>(),
                        externalId = externalId.toString(),
                        name = "Old Template",
                        icon = "old-icon",
                        description = "Old Description",
                        isOpen = false,
                        owner = null,
                        isDefault = true,
                        fields = emptyList()
                    )
                    val updatedFields = listOf(mockk<TemplateField>())
                    val savedTemplate = mockk<Template>()
                    val expected = mockk<TemplateResServiceDto>()

                    beforeTest {
                        every { templateRepository.findByExternalId(externalId.toString()) } returns existingTemplate
                        every { templateMapper.updateDefaultFromDto(dto, existingTemplate) } returns existingTemplate
                        every { templateFieldService.create(dto.fields) } returns updatedFields
                        every { templateRepository.save(existingTemplate) } returns savedTemplate
                        every { templateMapper.toResServiceDto(savedTemplate) } returns expected
                    }

                    it("템플릿을 수정한다") {
                        val result = templateService.updateDefault(dto)
                        result shouldBe expected

                        verify(exactly = 1) {
                            templateRepository.findByExternalId(externalId.toString())
                            templateMapper.updateDefaultFromDto(dto, existingTemplate)
                            templateFieldService.create(dto.fields)
                            templateRepository.save(existingTemplate)
                            templateMapper.toResServiceDto(savedTemplate)
                        }
                    }
                }

                describe("올바르지 않은 값이 주어지면 실패한다.") {
                    context("존재하지 않는 externalId가 주어지면") {
                        val invalidExternalId = UUID.randomUUID()
                        val dto = TemplateUpdateDefaultReqServiceDto(
                            externalId = invalidExternalId,
                            name = "Updated Template",
                            icon = "updated-icon",
                            description = "Updated Description",
                            fields = emptyList()
                        )

                        beforeTest {
                            every { templateRepository.findByExternalId(invalidExternalId.toString()) } returns null
                        }

                        it("ResourceNotFound 예외를 던진다") {
                            val ex = assertThrows<ResourceNotFound> {
                                templateService.updateDefault(dto)
                            }
                            ex.name shouldBe "Template"
                            ex.identifier shouldBe invalidExternalId.toString()

                            verify(exactly = 1) { templateRepository.findByExternalId(invalidExternalId.toString()) }
                        }
                    }

                    context("기본 템플릿이 아닌 externalId가 주어지면") {
                        val externalId = UUID.randomUUID()
                        val dto = TemplateUpdateDefaultReqServiceDto(
                            externalId = externalId,
                            name = "Updated Template",
                            icon = "updated-icon",
                            description = "Updated Description",
                            fields = emptyList()
                        )
                        val nonDefaultTemplate = Template(
                            id = mockk<ObjectId>(),
                            externalId = externalId.toString(),
                            name = "Non-Default Template",
                            icon = "icon",
                            description = "Description",
                            isOpen = false,
                            owner = "owner",
                            isDefault = false,
                            fields = emptyList()
                        )

                        beforeTest {
                            every { templateRepository.findByExternalId(externalId.toString()) } returns nonDefaultTemplate
                        }

                        it("ResourceNotFound 예외를 던진다") {
                            val ex = assertThrows<ResourceNotFound> {
                                templateService.updateDefault(dto)
                            }
                            ex.name shouldBe "Default Template"
                            ex.identifier shouldBe externalId.toString()

                            verify(exactly = 1) { templateRepository.findByExternalId(externalId.toString()) }
                        }
                    }

                    context("올바르지 않은 TemplateField 값이 주어지면") {
                        val externalId = UUID.randomUUID()
                        val dto = TemplateUpdateDefaultReqServiceDto(
                            externalId = externalId,
                            name = "Updated Template",
                            icon = "updated-icon",
                            description = "Updated Description",
                            fields = listOf(mockk<TemplateFieldCreateReqServiceDto>())
                        )
                        val existingTemplate = Template(
                            id = mockk<ObjectId>(),
                            externalId = externalId.toString(),
                            name = "Default Template",
                            icon = "icon",
                            description = "Description",
                            isOpen = false,
                            owner = null,
                            isDefault = true,
                            fields = emptyList()
                        )

                        beforeTest {
                            every { templateRepository.findByExternalId(externalId.toString()) } returns existingTemplate
                            every {
                                templateMapper.updateDefaultFromDto(
                                    dto,
                                    existingTemplate
                                )
                            } returns existingTemplate
                            every { templateFieldService.create(dto.fields) } throws ResourceNotValid(
                                name = "TemplateField",
                                reasons = listOf("Invalid field configuration")
                            )
                        }

                        it("ResourceNotValid 예외를 던진다") {
                            val ex = assertThrows<ResourceNotValid> {
                                templateService.updateDefault(dto)
                            }
                            ex.name shouldBe "TemplateField"

                            verify(exactly = 1) {
                                templateRepository.findByExternalId(externalId.toString())
                                templateMapper.updateDefaultFromDto(dto, existingTemplate)
                                templateFieldService.create(dto.fields)
                            }
                        }
                    }
                }
            }

            describe("TemplateService.updateOwned") {
                context("올바른 값이 주어지면") {
                    val externalId = UUID.randomUUID()
                    val owner = "test-owner"
                    val dto = TemplateUpdateReqServiceDto(
                        externalId = externalId,
                        name = "Updated Owned Template",
                        icon = "updated-icon",
                        description = "Updated Description",
                        owner = owner,
                        isOpen = true,
                        fields = listOf(mockk<TemplateFieldCreateReqServiceDto>())
                    )
                    val existingTemplate = Template(
                        id = mockk<ObjectId>(),
                        externalId = externalId.toString(),
                        name = "Old Template",
                        icon = "old-icon",
                        description = "Old Description",
                        isOpen = false,
                        owner = owner,
                        isDefault = false,
                        fields = emptyList()
                    )
                    val updatedFields = listOf(mockk<TemplateField>())
                    val savedTemplate = mockk<Template>()
                    val expected = mockk<TemplateResServiceDto>()

                    beforeTest {
                        every { templateRepository.findByExternalId(externalId.toString()) } returns existingTemplate
                        every { templateMapper.updateFromDto(dto, existingTemplate) } returns existingTemplate
                        every { templateFieldService.create(dto.fields) } returns updatedFields
                        every { templateRepository.save(existingTemplate) } returns savedTemplate
                        every { templateMapper.toResServiceDto(savedTemplate) } returns expected
                    }

                    it("템플릿을 수정한다") {
                        val result = templateService.updateOwned(dto)
                        result shouldBe expected

                        verify(exactly = 1) {
                            templateRepository.findByExternalId(externalId.toString())
                            templateMapper.updateFromDto(dto, existingTemplate)
                            templateFieldService.create(dto.fields)
                            templateRepository.save(existingTemplate)
                            templateMapper.toResServiceDto(savedTemplate)
                        }
                    }
                }

                describe("올바르지 않은 값이 주어지면 실패한다.") {
                    context("존재하지 않는 externalId가 주어지면") {
                        val invalidExternalId = UUID.randomUUID()
                        val owner = "test-owner"
                        val dto = TemplateUpdateReqServiceDto(
                            externalId = invalidExternalId,
                            name = "Updated Template",
                            icon = "updated-icon",
                            owner = owner,
                            isOpen = true,
                            description = "Updated Description",
                            fields = emptyList()
                        )

                        beforeTest {
                            every { templateRepository.findByExternalId(invalidExternalId.toString()) } returns null
                        }

                        it("ResourceNotFound 예외를 던진다") {
                            val ex = assertThrows<ResourceNotFound> {
                                templateService.updateOwned(dto)
                            }
                            ex.name shouldBe "Template"
                            ex.identifier shouldBe invalidExternalId.toString()

                            verify(exactly = 1) { templateRepository.findByExternalId(invalidExternalId.toString()) }
                        }
                    }

                    context("소유자가 아닌 requestor가 주어지면") {
                        val externalId = UUID.randomUUID()
                        val owner = "other-owner"
                        val dto = TemplateUpdateReqServiceDto(
                            externalId = externalId,
                            name = "Updated Template",
                            icon = "updated-icon",
                            description = "Updated Description",
                            owner = owner,
                            isOpen = true,
                            fields = emptyList()
                        )
                        val template = Template(
                            id = mockk<ObjectId>(),
                            externalId = externalId.toString(),
                            name = "Template",
                            icon = "icon",
                            description = "Description",
                            isOpen = false,
                            owner = "actual-owner",
                            isDefault = false,
                            fields = emptyList()
                        )

                        beforeTest {
                            every { templateRepository.findByExternalId(externalId.toString()) } returns template
                        }

                        it("ResourceNotFound 예외를 던진다") {
                            val ex = assertThrows<ResourceNotFound> {
                                templateService.updateOwned(dto)
                            }
                            ex.name shouldBe "Owned Template"
                            ex.identifier shouldBe externalId.toString()

                            verify(exactly = 1) { templateRepository.findByExternalId(externalId.toString()) }
                        }
                    }

                    context("올바르지 않은 TemplateField 값이 주어지면") {
                        val externalId = UUID.randomUUID()
                        val owner = "test-owner"
                        val dto = TemplateUpdateReqServiceDto(
                            externalId = externalId,
                            name = "Updated Template",
                            icon = "updated-icon",
                            description = "Updated Description",
                            owner = owner,
                            isOpen = true,
                            fields = listOf(mockk<TemplateFieldCreateReqServiceDto>())
                        )
                        val existingTemplate = Template(
                            id = mockk<ObjectId>(),
                            externalId = externalId.toString(),
                            name = "Template",
                            icon = "icon",
                            description = "Description",
                            isOpen = false,
                            owner = owner,
                            isDefault = false,
                            fields = emptyList()
                        )

                        beforeTest {
                            every { templateRepository.findByExternalId(externalId.toString()) } returns existingTemplate
                            every {
                                templateMapper.updateFromDto(
                                    dto,
                                    existingTemplate
                                )
                            } returns existingTemplate
                            every { templateFieldService.create(dto.fields) } throws ResourceNotValid(
                                name = "TemplateField",
                                reasons = listOf("Invalid field configuration")
                            )
                        }

                        it("ResourceNotValid 예외를 던진다") {
                            val ex = assertThrows<ResourceNotValid> {
                                templateService.updateOwned(dto)
                            }
                            ex.name shouldBe "TemplateField"

                            verify(exactly = 1) {
                                templateRepository.findByExternalId(externalId.toString())
                                templateMapper.updateFromDto(dto, existingTemplate)
                                templateFieldService.create(dto.fields)
                            }
                        }
                    }
                }
            }

            describe("TemplateService.deleteOwned") {
                context("올바른 값이 주어지면") {
                    val externalId = UUID.randomUUID()
                    val owner = "test-owner"
                    val existingTemplate = createTestTemplate(externalId = externalId, owner = owner)

                    beforeTest {
                        every { templateRepository.findByExternalId(externalId.toString()) } returns existingTemplate
                        every { templateRepository.delete(existingTemplate) } returns Unit
                    }

                    it("템플릿을 삭제한다") {
                        templateService.deleteOwned(externalId, owner)

                        verify(exactly = 1) {
                            templateRepository.findByExternalId(externalId.toString())
                            templateRepository.delete(existingTemplate)
                        }
                    }
                }

                context("존재하지 않는 externalId가 주어지면") {
                    val invalidExternalId = UUID.randomUUID()
                    val owner = "test-owner"

                    beforeTest {
                        every { templateRepository.findByExternalId(invalidExternalId.toString()) } returns null
                    }

                    it("ResourceNotFound 예외를 던진다") {
                        val ex = assertThrows<ResourceNotFound> {
                            templateService.deleteOwned(invalidExternalId, owner)
                        }
                        ex.name shouldBe "Template"
                        ex.identifier shouldBe invalidExternalId.toString()

                        verify(exactly = 1) { templateRepository.findByExternalId(invalidExternalId.toString()) }
                    }
                }

                context("소유자가 아닌 requestor가 주어지면") {
                    val externalId = UUID.randomUUID()
                    val requestor = "other-user"
                    val existingTemplate = createTestTemplate(externalId = externalId, owner = "others")

                    beforeTest {
                        every { templateRepository.findByExternalId(externalId.toString()) } returns existingTemplate
                    }

                    it("ResourceNotFound 예외를 던진다") {
                        val ex = assertThrows<ResourceNotFound> {
                            templateService.deleteOwned(externalId, requestor)
                        }
                        ex.name shouldBe "Owned Template"
                        ex.identifier shouldBe externalId.toString()

                        verify(exactly = 1) { templateRepository.findByExternalId(externalId.toString()) }
                    }
                }
            }

            describe("TemplateService.deleteDefault") {
                context("올바른 값이 주어지면") {
                    val externalId = UUID.randomUUID()
                    val existingTemplate = createTestTemplate(
                        externalId = externalId,
                        isDefault = true,
                    )
                    beforeTest {
                        every { templateRepository.findByExternalId(externalId.toString()) } returns existingTemplate
                        every { templateRepository.delete(existingTemplate) } returns Unit
                    }

                    it("템플릿을 삭제한다") {
                        templateService.deleteDefault(externalId)

                        verify(exactly = 1) {
                            templateRepository.findByExternalId(externalId.toString())
                            templateRepository.delete(existingTemplate)
                        }
                    }
                }

                context("존재하지 않는 externalId가 주어지면") {
                    val invalidExternalId = UUID.randomUUID()

                    beforeTest {
                        every { templateRepository.findByExternalId(invalidExternalId.toString()) } returns null
                    }

                    it("ResourceNotFound 예외를 던진다") {
                        val ex = assertThrows<ResourceNotFound> {
                            templateService.deleteDefault(invalidExternalId)
                        }
                        ex.name shouldBe "Template"
                        ex.identifier shouldBe invalidExternalId.toString()

                        verify(exactly = 1) { templateRepository.findByExternalId(invalidExternalId.toString()) }
                    }
                }

                context("기본 템플릿이 아닌 externalId가 주어지면") {
                    val externalId = UUID.randomUUID()
                    val nonDefaultTemplate = createTestTemplate(
                        externalId = externalId,
                        isDefault = false,
                    )

                    beforeTest {
                        every { templateRepository.findByExternalId(externalId.toString()) } returns nonDefaultTemplate
                    }

                    it("ResourceNotFound 예외를 던진다") {
                        val ex = assertThrows<ResourceNotFound> {
                            templateService.deleteDefault(externalId)
                        }
                        ex.name shouldBe "Default Template"
                        ex.identifier shouldBe externalId.toString()

                        verify(exactly = 1) { templateRepository.findByExternalId(externalId.toString()) }
                    }
                }

            }
        }
    }
}