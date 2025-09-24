package com.gabinote.coffeenote.template.integration

import com.gabinote.api.testSupport.testUtil.json.jsonBuilder
import com.gabinote.coffeenote.testSupport.testTemplate.IntegrationTestTemplate
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.Matchers.equalTo

class TemplateAdminApiIntegrationTest : IntegrationTestTemplate() {
    override val apiPrefix: String = "/api/v1/admin"

    init {
        feature("[Template] Template Admin API Integration Test") {

            feature("[POST] /admin/api/v1/template/default") {
                scenario("관리자 권한으로 올바른 기본 템플릿 생성 요청이 주어지면, 새로운 기본 템플릿을 생성하여 반환해야 한다.") {
                    Given {
                        testDataHelper.setData("/testsets/template/integration/admin/post-admin-api-v1-template-default-before.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
                        header("X-Token-Roles", "admin")
                        contentType("application/json")
                        body(
                            jsonBuilder {
                                "name" to "Admin Created Template"
                                "icon" to "admin-created"
                                "description" to "A template created by admin"
                                "fields" arr {
                                    +obj {
                                        "id" to "00000000-0000-0000-0000-000000000000"
                                        "name" to "Admin Field"
                                        "icon" to "admin-field"
                                        "type" to "DROP_DOWN"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr {
                                            +obj {
                                                "key" to "values"
                                                "value" arr {
                                                    +"Admin Option A"
                                                    +"Admin Option B"
                                                }
                                            }
                                            +obj {
                                                "key" to "allowAddValue"
                                                "value" arr {
                                                    +"true"
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        )
                    }.When {
                        post("/template/default")
                    }.Then {
                        statusCode(201)
                        testDataHelper.assertData("/testsets/template/integration/admin/post-admin-api-v1-template-default-after.json")
                        body("name", equalTo("Admin Created Template"))
                        body("icon", equalTo("admin-created"))
                        body("description", equalTo("A template created by admin"))
                        body("is_open", equalTo(true))
                        body("is_default", equalTo(true))
                        body("owner", equalTo(null))
                        body("external_id", equalTo("00000000-0000-0000-0000-000000000000"))
                        body("fields.size()", equalTo(1))
                        body("fields[0].name", equalTo("Admin Field"))
                        body("fields[0].type", equalTo("DROP_DOWN"))
                    }
                }
            }

            feature("[PUT] /admin/api/v1/template/default/{externalId}") {
                scenario("관리자 권한으로 올바른 기본 템플릿 수정 요청이 주어지면, 해당 기본 템플릿을 수정하여 반환해야 한다.") {
                    Given {
                        testDataHelper.setData("/testsets/template/integration/admin/put-admin-api-v1-template-default-before.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
                        header("X-Token-Roles", "admin")
                        contentType("application/json")
                        body(
                            jsonBuilder {
                                "name" to "Admin Updated Template"
                                "icon" to "admin-updated-icon"
                                "description" to "Updated description by admin"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to "00000000-0000-0000-0000-000000000001"
                                        "name" to "Updated Field Name"
                                        "icon" to "updated-icon"
                                        "type" to "SHORT_TEXT"
                                        "order" to 1
                                        "is_display" to false
                                        "attributes" arr { }
                                    }
                                    +obj {
                                        "id" to "00000000-0000-0000-0000-000000000000"
                                        "name" to "Admin Added Field"
                                        "icon" to "admin-added"
                                        "type" to "LONG_TEXT"
                                        "order" to 2
                                        "is_display" to false
                                        "attributes" arr { }
                                    }
                                }
                            }
                        )
                    }.When {
                        put("/template/default/d1e2f3a4-b5c6-7890-abcd-ef1234567890")
                    }.Then {
                        statusCode(200)
                        testDataHelper.assertData("/testsets/template/integration/admin/put-admin-api-v1-template-default-after.json")
                        body("name", equalTo("Admin Updated Template"))
                        body("icon", equalTo("admin-updated-icon"))
                        body("description", equalTo("Updated description by admin"))
                        body("is_open", equalTo(true))
                        body("is_default", equalTo(true))
                        body("owner", equalTo(null))
                        body("external_id", equalTo("d1e2f3a4-b5c6-7890-abcd-ef1234567890"))
                        body("fields.size()", equalTo(2))
                        body("fields[0].name", equalTo("Updated Field Name"))
                        body("fields[0].is_display", equalTo(false))
                        body("fields[1].name", equalTo("Admin Added Field"))
                        body("fields[1].type", equalTo("LONG_TEXT"))
                    }
                }
            }

            feature("[DELETE] /admin/api/v1/template/default/{externalId}") {
                scenario("관리자 권한으로 기본 템플릿 삭제를 시도하면, 해당 템플릿을 삭제하고 204를 반환한다.") {
                    Given {
                        testDataHelper.setData("/testsets/template/integration/admin/delete-admin-api-v1-template-default-before.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
                        header("X-Token-Roles", "admin")
                    }.When {
                        delete("/template/default/a1b2c3d4-e5f6-7890-abcd-ef1234567890")
                    }.Then {
                        statusCode(204)
                        testDataHelper.assertData("/testsets/template/integration/admin/delete-admin-api-v1-template-default-after.json")
                    }
                }
            }
        }
    }
}