package com.gabinote.coffeenote.field.domain

import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.field.domain.field.Field
import com.gabinote.coffeenote.field.domain.field.FieldRepository
import com.gabinote.coffeenote.testSupport.testTemplate.RepositoryTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.page.TestPageableUtil
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import java.util.*


class FieldRepositoryTest : RepositoryTestTemplate() {

    @Autowired
    private lateinit var fieldRepository: FieldRepository

    init {

        describe("[Field] FieldRepository") {

            describe("FieldRepository.findByExternalId") {
                context("유효한 externalId가 주어지면") {
                    testDataHelper.setData("/testsets/field/domain/base-field.json")
                    val validExternalId = "8fd344e0-e074-49a4-ae22-ff8653ba02f2"
                    it("해당 externalId를 가진 Field를 반환한다") {
                        val res = fieldRepository.findByExternalId(validExternalId)

                        res shouldNotBe null
                        res!!.externalId shouldBe validExternalId
                    }
                }

                context("유효하지 않은 externalId가 주어지면") {
                    testDataHelper.setData("/testsets/field/domain/base-field.json")
                    val invalidExternalId = UUID.randomUUID().toString()
                    it("null을 반환한다") {
                        val res = fieldRepository.findByExternalId(invalidExternalId)

                        res shouldBe null
                    }
                }
            }

            describe("FieldRepository.findAllByDefault") {
                context("default가 true가 주어지면") {
                    testDataHelper.setData("/testsets/field/domain/base-field.json")
                    it("default가 true인 Field들을 반환한다") {
                        val res = fieldRepository.findAllByDefault(true, pageable = TestPageableUtil.createPageable())

                        res.content.all { it.default } shouldBe true
                        res.content.all { it.owner == null } shouldBe true
                    }
                }
            }

            describe("FieldRepository.findAllByDefaultOrOwner") {
                context("default가 true이거나 owner가 주어진 owner인 Field들을 반환한다") {
                    testDataHelper.setData("/testsets/field/domain/base-field.json")
                    val owner = "695b8482-c259-4493-82e2-8988599d21e9"
                    it("조건에 맞는 Field들을 반환한다") {
                        val res = fieldRepository.findAllByDefaultOrOwner(
                            true,
                            owner,
                            pageable = TestPageableUtil.createPageable()
                        )

                        res.content.all { it.default || it.owner == owner } shouldBe true
                    }
                }
            }

            describe("FieldRepository.findAllByOwner") {
                context("owner가 주어진 owner인 Field들을 반환한다") {
                    testDataHelper.setData("/testsets/field/domain/base-field.json")
                    val owner = "695b8482-c259-4493-82e2-8988599d21e9"
                    it("조건에 맞는 Field들을 반환한다") {
                        val res = fieldRepository.findAllByOwner(owner, pageable = TestPageableUtil.createPageable())

                        res.content.all { it.owner == owner } shouldBe true
                    }
                }
            }

            describe("FieldRepository.save(신규)") {
                context("attributes가 없는 신규 Field 객체가 주어지면") {
                    testDataHelper.setData("/testsets/field/domain/base-field.json")
                    val newField = Field(
                        name = "New Field",
                        type = "TEXT",
                        default = false,
                        owner = "cafb043b-f8f6-4a89-bd11-a1780d319980",
                        icon = "default"
                    )
                    it("새로운 Field 객체를 저장하고, 저장된 객체를 반환한다") {
                        val savedField = fieldRepository.save(newField)

                        savedField.name shouldBe newField.name
                        savedField.type shouldBe newField.type
                        savedField.default shouldBe newField.default
                        savedField.owner shouldBe newField.owner

                        testDataHelper.assertData("/testsets/field/domain/save-expected-field.json")
                    }
                }

                context("attributes가 있는 신규 Field 객체가 주어지면") {
                    testDataHelper.setData("/testsets/field/domain/base-field.json")
                    val newField = Field(
                        name = "New Field",
                        type = "TEST",
                        default = false,
                        owner = "cafb043b-f8f6-4a89-bd11-a1780d319980",
                        icon = "default",
                        attributes = setOf(
                            Attribute(
                                key = "maxLength",
                                value = setOf("1000")
                            )
                        )
                    )
                    it("새로운 Field 객체를 저장하고, 저장된 객체를 반환한다") {
                        val savedField = fieldRepository.save(newField)

                        savedField.externalId shouldBe newField.externalId
                        savedField.name shouldBe newField.name
                        savedField.type shouldBe newField.type
                        savedField.default shouldBe newField.default
                        savedField.owner shouldBe newField.owner
                        savedField.attributes shouldBe newField.attributes

                        testDataHelper.assertData("/testsets/field/domain/save-expected-field-with-attributes.json")
                    }
                }
            }

            describe("FieldRepository.save(수정)") {
                context("기존 Field 객체를 올바르게 수정하면") {
                    testDataHelper.setData("/testsets/field/domain/base-field.json")
                    val existingField = fieldRepository.findByExternalId("8fd344e0-e074-49a4-ae22-ff8653ba02f2")!!
                    existingField.name = "Modified Field"
                    existingField.icon = "modified_icon"
                    it("기존 Field 객체를 수정하고, 수정된 객체를 반환한다") {
                        val savedField = fieldRepository.save(existingField)

                        savedField.externalId shouldBe existingField.externalId
                        savedField.name shouldBe existingField.name
                        savedField.icon shouldBe existingField.icon
                        savedField.type shouldBe existingField.type
                        savedField.default shouldBe existingField.default
                        savedField.owner shouldBe existingField.owner
                        savedField.attributes shouldBe existingField.attributes

                        testDataHelper.assertData("/testsets/field/domain/update-expected-field.json")
                    }
                }
            }

            describe("FieldRepository.deleteByExternalId") {
                context("유효한 externalId가 주어지면") {
                    testDataHelper.setData("/testsets/field/domain/base-field.json")
                    val validExternalId = "8fd344e0-e074-49a4-ae22-ff8653ba02f2"
                    it("해당 externalId를 가진 Field를 삭제한다") {
                        val res = fieldRepository.deleteByExternalId(validExternalId)

                        res.size shouldBe 1
                        testDataHelper.assertData("/testsets/field/domain/delete-expected-field.json")
                    }
                }
            }
        }
    }
}