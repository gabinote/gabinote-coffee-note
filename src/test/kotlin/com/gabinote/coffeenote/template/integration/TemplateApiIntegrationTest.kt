package com.gabinote.coffeenote.template.integration

import com.gabinote.api.testSupport.testUtil.json.jsonBuilder
import com.gabinote.coffeenote.testSupport.testTemplate.IntegrationTestTemplate
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.Matchers.equalTo

class TemplateApiIntegrationTest : IntegrationTestTemplate() {
    override val apiPrefix: String = "/api/v1"

    init {
        feature("[Template] Template API Integration Test") {

            feature("[GET] /api/v1/templates/default") {
                scenario("기본 템플릿 목록을 요청하면, 모든 기본 템플릿을 반환해야 한다.") {
                    Given {
                        testDataHelper.setData("/testsets/template/integration/get-api-v1-templates-default.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "user")
                    }.When {
                        get("/templates/default")
                    }.Then {
                        statusCode(200)
                        body("content.size()", equalTo(2))
                        body("content[0].is_default", equalTo(true))
                        body("content[1].is_default", equalTo(true))

                    }
                }
            }

            feature("[GET] /api/v1/templates/me") {
                scenario("내 템플릿 목록을 요청하면, 요청자가 소유한 템플릿만 반환해야 한다.") {
                    Given {
                        testDataHelper.setData("/testsets/template/integration/get-api-v1-templates-me.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "user")
                    }.When {
                        get("/templates/me")
                    }.Then {
                        statusCode(200)
                        body("content.size()", equalTo(1))

                        body("content[0].external_id", equalTo("d1e2f3a4-b5c6-7890-abcd-ef1234567890"))
                        body("content[0].owner", equalTo("d15cdbf8-22bc-47e2-9e9a-4d171cb6522e"))

                    }
                }
            }

            feature("[GET] /api/v1/template/default/{externalId}") {
                scenario("기본 템플릿을 외부 ID로 조회하면, 해당 기본 템플릿을 반환해야 한다.") {
                    Given {
                        testDataHelper.setData("/testsets/template/integration/get-api-v1-template-default-ext.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "user")
                    }.When {
                        get("/template/default/3fcdd722-93ca-4693-9bef-496616dd6af3")
                    }.Then {
                        statusCode(200)
                        body("external_id", equalTo("3fcdd722-93ca-4693-9bef-496616dd6af3"))
                        body("name", equalTo("Default Coffee Review Template"))
                        body("icon", equalTo("coffee"))
                        body("description", equalTo("A standard template for coffee reviews"))
                        body("is_default", equalTo(true))
                        body("is_open", equalTo(true))
                        body("owner", equalTo(null))
                        body("fields.size()", equalTo(1))
                        body("fields[0].name", equalTo("Coffee Name"))
                    }
                }
            }

            feature("[GET] /api/v1/template/me/{externalId}") {
                scenario("내 템플릿을 외부 ID로 조회하면, 해당 템플릿을 반환해야 한다.") {
                    Given {
                        testDataHelper.setData("/testsets/template/integration/get-api-v1-template-me-ext.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "user")
                    }.When {
                        get("/template/me/d1e2f3a4-b5c6-7890-abcd-ef1234567890")
                    }.Then {
                        statusCode(200)
                        body("external_id", equalTo("d1e2f3a4-b5c6-7890-abcd-ef1234567890"))
                        body("name", equalTo("My Personal Coffee Template"))
                        body("icon", equalTo("personal-coffee"))
                        body("description", equalTo("My personal coffee tasting notes template"))
                        body("is_default", equalTo(false))
                        body("is_open", equalTo(true))
                        body("owner", equalTo("d15cdbf8-22bc-47e2-9e9a-4d171cb6522e"))
                        body("fields.size()", equalTo(1))
                        body("fields[0].name", equalTo("Coffee Origin"))
                    }
                }
            }

            feature("[GET] /api/v1/template/open/{externalId}") {
                scenario("공개 템플릿을 외부 ID로 조회하면, 해당 공개 템플릿을 반환해야 한다.") {
                    Given {
                        testDataHelper.setData("/testsets/template/integration/get-api-v1-template-open-ext.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "user")
                    }.When {
                        get("/template/open/a1b2c3d4-e5f6-7890-abcd-ef1234567890")
                    }.Then {
                        statusCode(200)
                        body("external_id", equalTo("a1b2c3d4-e5f6-7890-abcd-ef1234567890"))
                        body("name", equalTo("Open Coffee Template"))
                        body("icon", equalTo("open-coffee"))
                        body("description", equalTo("An open template for public use"))
                        body("is_default", equalTo(false))
                        body("is_open", equalTo(true))
                        body("owner", equalTo("695b8482-c259-4493-82e2-8988599d21e9"))
                        body("fields.size()", equalTo(1))
                        body("fields[0].name", equalTo("Taste Notes"))
                        body("fields[0].type", equalTo("LONG_TEXT"))
                    }
                }
            }

            feature("[POST] /api/v1/template/me") {
                scenario("올바른 템플릿 생성 요청이 주어지면, 새로운 템플릿을 생성하여 반환해야 한다.") {
                    Given {
                        testDataHelper.setData("/testsets/template/integration/post-api-v1-template-me-before.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "user")
                        contentType("application/json")
                        body(
                            jsonBuilder {
                                "name" to "New Coffee Template"
                                "icon" to "new-coffee-icon"
                                "description" to "A brand new coffee template"
                                "is_open" to true
                                "fields" arr {
                                    +obj {
                                        "id" to "00000000-0000-0000-0000-000000000000"
                                        "name" to "New Coffee Field"
                                        "icon" to "new-coffee-icon"
                                        "type" to "SHORT_TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr { }
                                    }
                                }
                            }
                        )
                    }.When {
                        post("/template/me")
                    }.Then {
                        statusCode(201)
                        testDataHelper.assertData("/testsets/template/integration/post-api-v1-template-me-after.json")
                        body("name", equalTo("New Coffee Template"))
                        body("icon", equalTo("new-coffee-icon"))
                        body("description", equalTo("A brand new coffee template"))
                        body("is_open", equalTo(true))
                        body("is_default", equalTo(false))
                        body("owner", equalTo("d15cdbf8-22bc-47e2-9e9a-4d171cb6522e"))
                        body("external_id", equalTo("00000000-0000-0000-0000-000000000000"))
                        body("fields.size()", equalTo(1))
                        body("fields[0].name", equalTo("New Coffee Field"))
                    }
                }
            }

            feature("[PUT] /api/v1/template/me/{externalId}") {
                scenario("올바른 템플릿 수정 요청이 주어지면, 해당 템플릿을 수정하여 반환해야 한다.") {
                    Given {
                        testDataHelper.setData("/testsets/template/integration/put-api-v1-template-me-before.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "user")
                        contentType("application/json")
                        body(
                            jsonBuilder {
                                "name" to "Updated Personal Coffee Template"
                                "icon" to "updated-coffee-icon"
                                "description" to "Updated description for my personal coffee template"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to "00000000-0000-0000-0000-000000000001"
                                        "name" to "Updated Coffee Origin"
                                        "icon" to "updated-globe"
                                        "type" to "SHORT_TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr { }
                                    }
                                    +obj {
                                        "id" to "00000000-0000-0000-0000-000000000000"
                                        "name" to "New Field Added"
                                        "icon" to "new-field-icon"
                                        "type" to "LONG_TEXT"
                                        "order" to 2
                                        "is_display" to false
                                        "attributes" arr { }
                                    }
                                }
                            }
                        )
                    }.When {
                        put("/template/me/d1e2f3a4-b5c6-7890-abcd-ef1234567890")
                    }.Then {
                        statusCode(200)
                        testDataHelper.assertData("/testsets/template/integration/put-api-v1-template-me-after.json")
                        body("name", equalTo("Updated Personal Coffee Template"))
                        body("icon", equalTo("updated-coffee-icon"))
                        body("description", equalTo("Updated description for my personal coffee template"))
                        body("is_open", equalTo(false))
                        body("is_default", equalTo(false))
                        body("owner", equalTo("d15cdbf8-22bc-47e2-9e9a-4d171cb6522e"))
                        body("external_id", equalTo("d1e2f3a4-b5c6-7890-abcd-ef1234567890"))
                        body("fields.size()", equalTo(2))
                        body("fields[0].name", equalTo("Updated Coffee Origin"))
                        body("fields[1].name", equalTo("New Field Added"))
                        body("fields[1].is_display", equalTo(false))
                    }
                }
            }

            feature("[DELETE] /api/v1/template/me/{externalId}") {
                scenario("올바른 자신의 템플릿 외부 ID를 제공하면, 해당 템플릿을 삭제한다.") {
                    Given {
                        testDataHelper.setData("/testsets/template/integration/delete-api-v1-template-me-before.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "user")
                    }.When {
                        delete("/template/me/d1e2f3a4-b5c6-7890-abcd-ef1234567890")
                    }.Then {
                        statusCode(204)
                        testDataHelper.assertData("/testsets/template/integration/delete-api-v1-template-me-after.json")
                    }
                }
            }
        }
    }
}