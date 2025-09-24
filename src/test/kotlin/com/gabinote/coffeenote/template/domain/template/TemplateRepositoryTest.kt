package com.gabinote.coffeenote.template.domain.template

import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.template.domain.templateField.TemplateField
import com.gabinote.coffeenote.testSupport.testTemplate.RepositoryTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.page.TestPageableUtil
import com.gabinote.coffeenote.testSupport.testUtil.uuid.TestUuidSource
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class TemplateRepositoryTest : RepositoryTestTemplate() {

    @Autowired
    lateinit var templateRepository: TemplateRepository

    init {
        describe("[Template] TemplateRepository") {

            describe("TemplateRepository.findByExternalId") {
                context("유효한 externalId가 주어지면") {
                    testDataHelper.setData("/testsets/template/domain/base-template.json")
                    val validExternalId = "e5f0d358-ae1a-4f9b-9c2e-0c3e1268ca9d"

                    it("해당 externalId를 가진 Template을 반환한다") {
                        val res = templateRepository.findByExternalId(validExternalId)

                        res shouldNotBe null
                        res!!.externalId shouldBe validExternalId
                        res.name shouldBe "Default Coffee Template"
                        res.description shouldBe "A default template for coffee notes"
                        res.icon shouldBe "coffee"
                        res.isOpen shouldBe true
                        res.isDefault shouldBe true
                        res.owner shouldBe null

                        res.fields.size shouldBe 2

                        val firstField = res.fields[0]
                        firstField.id shouldBe "d70f81e9-5842-40f8-b71b-6bd34b40bf18"
                        firstField.name shouldBe "Coffee Type"
                        firstField.icon shouldBe "coffee-bean"
                        firstField.type shouldBe "TEXT"
                        firstField.attributes shouldBe emptySet()
                        firstField.order shouldBe 1
                        firstField.isDisplay shouldBe true

                        val secondField = res.fields[1]
                        secondField.id shouldBe "1e6f4c3a-2b4d-4e5f-8a9b-0c1d2e3f4a5b"
                        secondField.name shouldBe "Brewing Method"
                        secondField.icon shouldBe "coffee-machine"
                        secondField.type shouldBe "DROPDOWN"
                        secondField.order shouldBe 2
                        secondField.isDisplay shouldBe true
                        secondField.attributes.size shouldBe 1

                        val optionsAttribute = secondField.attributes.first()
                        optionsAttribute.key shouldBe "options"
                        optionsAttribute.value shouldBe listOf("Pour Over", "French Press", "Espresso", "AeroPress")
                    }
                }

                context("유효하지 않은 externalId가 주어지면") {
                    testDataHelper.setData("/testsets/template/domain/base-template.json")
                    val invalidExternalId = UUID.randomUUID().toString()

                    it("null을 반환한다") {
                        val res = templateRepository.findByExternalId(invalidExternalId)

                        res shouldBe null
                    }
                }
            }

            describe("TemplateRepository.findAllByOwner") {
                context("owner가 주어지면") {
                    testDataHelper.setData("/testsets/template/domain/base-template.json")
                    val owner = "695b8482-c259-4493-82e2-8988599d21e9"

                    it("해당 owner의 Template들을 반환한다") {
                        val res = templateRepository.findAllByOwner(owner, pageable = TestPageableUtil.createPageable())

                        res.content.all { it.owner == owner } shouldBe true
                        res.content.size shouldBe 1

                        val template = res.content[0]
                        template.externalId shouldBe "a3b4c5d6-e7f8-9a0b-1c2d-3e4f5a6b7c8d"
                        template.owner shouldBe owner
                    }
                }

                context("존재하지 않는 owner가 주어지면") {
                    testDataHelper.setData("/testsets/template/domain/base-template.json")
                    val nonExistentOwner = UUID.randomUUID().toString()

                    it("빈 리스트를 반환한다") {
                        val res = templateRepository.findAllByOwner(
                            nonExistentOwner,
                            pageable = TestPageableUtil.createPageable()
                        )

                        res.content.isEmpty() shouldBe true
                    }
                }
            }

            describe("TemplateRepository.findAllByIsDefault") {
                context("isDefault가 true가 주어지면") {
                    testDataHelper.setData("/testsets/template/domain/base-template.json")
                    it("isDefault가 true인 Template들을 반환한다") {
                        val res =
                            templateRepository.findAllByIsDefault(true, pageable = TestPageableUtil.createPageable())

                        res.content.all { it.isDefault } shouldBe true
                        res.content.all { it.owner == null } shouldBe true
                    }
                }
            }

            describe("TemplateRepository.findAllByOwnerOrIsDefault") {

                context("isDefault가 true에 존재하는 owner가 주어지면") {
                    testDataHelper.setData("/testsets/template/domain/base-template.json")
                    val owner = "695b8482-c259-4493-82e2-8988599d21e9"

                    it("조건에 맞는 Template들을 반환한다") {
                        val res = templateRepository.findAllByOwnerOrIsDefault(
                            owner,
                            true,
                            pageable = TestPageableUtil.createPageable()
                        )

                        res.content.size shouldBe 2

                        res.content.all { it.isDefault || it.owner == owner } shouldBe true
                    }
                }


                context("isDefault가 true에 존재하지 않는 owner가 주어지면") {
                    testDataHelper.setData("/testsets/template/domain/base-template.json")
                    val nonExistentOwner = UUID.randomUUID().toString()

                    it("기본 Template들만 반환한다") {
                        val res = templateRepository.findAllByOwnerOrIsDefault(
                            nonExistentOwner,
                            true,
                            pageable = TestPageableUtil.createPageable()
                        )
                        res.content.all { it.isDefault } shouldBe true

                    }
                }
            }

            describe("TemplateRepository.save(신규)") {
                context("신규 Template 객체가 주어지면") {
                    testDataHelper.setData("/testsets/template/domain/base-template.json")

                    val templateFields = listOf(
                        TemplateField(
                            id = "6e844351-d486-4031-9aab-0f289a372839",
                            name = "Tea Origin",
                            icon = "tea-leaf",
                            type = "TEXT",
                            attributes = emptySet(),
                            order = 1,
                            isDisplay = true
                        ),
                        TemplateField(
                            id = "f5246465-6e2c-449d-ab4a-6176deba5c60",
                            name = "Water Temperature",
                            icon = "thermometer",
                            type = "NUMBER",
                            attributes = setOf(
                                Attribute(
                                    key = "unit",
                                    value = setOf("°C")
                                )
                            ),
                            order = 2,
                            isDisplay = true
                        ),
                        TemplateField(
                            id = "35a6e051-c3ca-4066-9b18-17648758830a",
                            name = "Whisking Technique",
                            icon = "whisk",
                            type = "DROPDOWN",
                            attributes = setOf(
                                Attribute(
                                    key = "options",
                                    value = setOf("Traditional", "Modern", "Zig-zag", "Circular")
                                )
                            ),
                            order = 3,
                            isDisplay = true
                        )
                    )

                    val newTemplate = Template(
                        name = "Matcha Tea Template",
                        description = "Template for matcha tea notes",
                        icon = "matcha",
                        isOpen = false,
                        isDefault = false,
                        owner = "cafb043b-f8f6-4a89-bd11-a1780d319980",
                        fields = templateFields
                    )

                    it("새로운 Template 객체를 저장한다.") {
                        val savedTemplate = templateRepository.save(newTemplate)


                        savedTemplate.id shouldNotBe null
                        savedTemplate.externalId shouldBe TestUuidSource.UUID_STRING.toString()
                        savedTemplate.name shouldBe newTemplate.name
                        savedTemplate.description shouldBe newTemplate.description
                        savedTemplate.icon shouldBe newTemplate.icon
                        savedTemplate.isOpen shouldBe newTemplate.isOpen
                        savedTemplate.isDefault shouldBe newTemplate.isDefault
                        savedTemplate.owner shouldBe newTemplate.owner

                        val firstField = savedTemplate.fields[0]
                        firstField.id shouldBe "6e844351-d486-4031-9aab-0f289a372839"
                        firstField.name shouldBe "Tea Origin"
                        firstField.icon shouldBe "tea-leaf"
                        firstField.type shouldBe "TEXT"
                        firstField.attributes shouldBe emptySet()
                        firstField.order shouldBe 1
                        firstField.isDisplay shouldBe true

                        val secondField = savedTemplate.fields[1]
                        secondField.id shouldBe "f5246465-6e2c-449d-ab4a-6176deba5c60"
                        secondField.name shouldBe "Water Temperature"
                        secondField.icon shouldBe "thermometer"
                        secondField.type shouldBe "NUMBER"
                        secondField.order shouldBe 2
                        secondField.isDisplay shouldBe true
                        secondField.attributes.size shouldBe 1

                        val unitAttribute = secondField.attributes.first()
                        unitAttribute.key shouldBe "unit"
                        unitAttribute.value shouldBe listOf("°C")

                        val thirdField = savedTemplate.fields[2]
                        thirdField.id shouldBe "35a6e051-c3ca-4066-9b18-17648758830a"
                        thirdField.name shouldBe "Whisking Technique"
                        thirdField.icon shouldBe "whisk"
                        thirdField.type shouldBe "DROPDOWN"
                        thirdField.order shouldBe 3
                        thirdField.isDisplay shouldBe true
                        thirdField.attributes.size shouldBe 1

                        val optionsAttribute = thirdField.attributes.first()
                        optionsAttribute.key shouldBe "options"
                        optionsAttribute.value shouldBe listOf("Traditional", "Modern", "Zig-zag", "Circular")

                        testDataHelper.assertData("/testsets/template/domain/save-expected-template.json")
                    }
                }
            }

            describe("TemplateRepository.save(수정)") {
                context("기존 Template 객체가 주어지면") {
                    testDataHelper.setData("/testsets/template/domain/base-template.json")
                    val targetExternalId = "a3b4c5d6-e7f8-9a0b-1c2d-3e4f5a6b7c8d"
                    val existingTemplate = templateRepository.findByExternalId(targetExternalId)
                    val updatedTemplateFields = listOf(
                        TemplateField(
                            id = "60bd213b-f560-4f7a-90e2-019eef5d0082",
                            name = "Coffee Origin",
                            icon = "coffee-origin",
                            type = "TEXT",
                            attributes = emptySet(),
                            order = 1,
                            isDisplay = true
                        ),
                        TemplateField(
                            id = "c25bf62d-2625-444b-881b-273e2a6b58ec",
                            name = "Extraction Time",
                            icon = "timer-updated",
                            type = "NUMBER",
                            attributes = setOf(
                                Attribute(
                                    key = "unit",
                                    value = setOf("seconds")
                                )
                            ),
                            order = 2,
                            isDisplay = true
                        ),
                        TemplateField(
                            id = "0fdb5826-c37b-4915-85ed-7ccf6432776a",
                            name = "Taste Profile",
                            icon = "taste",
                            type = "DROPDOWN",
                            attributes = setOf(
                                Attribute(
                                    key = "options",
                                    value = setOf("Fruity", "Chocolatey", "Nutty", "Floral")
                                )
                            ),
                            order = 3,
                            isDisplay = true
                        )
                    )

                    existingTemplate!!.name = "Updated Espresso Template"
                    existingTemplate.description = "Updated template for specialty espresso"
                    existingTemplate.icon = "updated-coffee"
                    existingTemplate.isOpen = true
                    existingTemplate.fields = updatedTemplateFields
                    it("변경된 Template 객체를 저장한다.") {

                        val savedTemplate = templateRepository.save(existingTemplate)


                        savedTemplate.id shouldBe existingTemplate.id
                        savedTemplate.externalId shouldBe existingTemplate.externalId

                        savedTemplate.name shouldBe "Updated Espresso Template"
                        savedTemplate.description shouldBe "Updated template for specialty espresso"
                        savedTemplate.icon shouldBe "updated-coffee"
                        savedTemplate.isOpen shouldBe true
                        savedTemplate.owner shouldBe existingTemplate.owner
                        savedTemplate.isDefault shouldBe existingTemplate.isDefault

                        val firstField = savedTemplate.fields[0]
                        firstField.id shouldBe "60bd213b-f560-4f7a-90e2-019eef5d0082"
                        firstField.name shouldBe "Coffee Origin"
                        firstField.icon shouldBe "coffee-origin"
                        firstField.type shouldBe "TEXT"
                        firstField.attributes shouldBe emptySet()
                        firstField.order shouldBe 1
                        firstField.isDisplay shouldBe true

                        val secondField = savedTemplate.fields[1]
                        secondField.id shouldBe "c25bf62d-2625-444b-881b-273e2a6b58ec"
                        secondField.name shouldBe "Extraction Time"
                        secondField.icon shouldBe "timer-updated"
                        secondField.type shouldBe "NUMBER"
                        secondField.order shouldBe 2
                        secondField.isDisplay shouldBe true
                        secondField.attributes.size shouldBe 1

                        val unitAttribute = secondField.attributes.first()
                        unitAttribute.key shouldBe "unit"
                        unitAttribute.value shouldBe listOf("seconds")

                        val thirdField = savedTemplate.fields[2]
                        thirdField.id shouldBe "0fdb5826-c37b-4915-85ed-7ccf6432776a"
                        thirdField.name shouldBe "Taste Profile"
                        thirdField.icon shouldBe "taste"
                        thirdField.type shouldBe "DROPDOWN"
                        thirdField.order shouldBe 3
                        thirdField.isDisplay shouldBe true
                        thirdField.attributes.size shouldBe 1

                        val optionsAttribute = thirdField.attributes.first()
                        optionsAttribute.key shouldBe "options"
                        optionsAttribute.value shouldBe listOf("Fruity", "Chocolatey", "Nutty", "Floral")

                        // 데이터베이스에 정상 저장됐는지 확인
                        testDataHelper.assertData("/testsets/template/domain/update-expected-template.json")
                    }
                }
            }

            describe("TemplateRepository.delete") {
                context("삭제할 Template 객체가 주어지면") {
                    testDataHelper.setData("/testsets/template/domain/base-template.json")
                    val targetExternalId = "b4c5d6e7-f8a9-0b1c-2d3e-4f5a6b7c8d9e"
                    val targetTemplate = templateRepository.findByExternalId(targetExternalId)

                    it("템플릿을 성공적으로 삭제한다") {
                        templateRepository.delete(targetTemplate!!)
                        testDataHelper.assertData("/testsets/template/domain/delete-expected-template.json")
                    }
                }

            }
        }
    }

}