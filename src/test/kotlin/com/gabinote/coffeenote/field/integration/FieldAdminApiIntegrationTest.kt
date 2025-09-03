package com.gabinote.coffeenote.field.integration

import com.gabinote.api.testSupport.testUtil.json.jsonBuilder
import com.gabinote.coffeenote.testSupport.testTemplate.IntegrationTestTemplate
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.Matchers.equalTo

class FieldAdminApiIntegrationTest : IntegrationTestTemplate() {
    override val apiPrefix: String = "/admin/api/v1"

    init {
        feature("[Field] Field Admin API Integration Test") {
            feature("[GET] /admin/api/v1/fields") {
                scenario("관리자 권한으로 요청하면, 모든 필드를 반환해야 한다.") {
                    Given {
                        testDataHelper.setData("/testsets/field/integration/admin/get-admin-api-v1-fields.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
                        header("X-Token-Roles", "admin")
                    }.When {
                        get("/fields")
                    }.Then {
                        statusCode(200)
                        body("content.size()", equalTo(3))

                        body("content[0].external_id", equalTo("3fcdd722-93ca-4693-9bef-496616dd6af3"))
                        body("content[0].default", equalTo(true))

                        body("content[1].external_id", equalTo("8fd344e0-e074-49a4-ae22-ff8653ba02f2"))
                        body("content[1].default", equalTo(false))
                        body("content[1].owner", equalTo("d15cdbf8-22bc-47e2-9e9a-4d171cb6522e"))

                        body("content[2].external_id", equalTo("088eb9c8-a340-4c98-9c64-a869d7e1a2ac"))
                        body("content[2].default", equalTo(false))
                        body("content[2].owner", equalTo("695b8482-c259-4493-82e2-8988599d21e9"))
                    }
                }
            }

            feature("[GET] /admin/api/v1/field/{extId}") {
                scenario("관리자 권한으로 기본 필드를 조회하면, 해당 필드를 반환해야 한다.") {
                    Given {
                        testDataHelper.setData("/testsets/field/integration/admin/get-admin-api-v1-field-ext.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
                        header("X-Token-Roles", "admin")
                    }.When {
                        get("/field/3fcdd722-93ca-4693-9bef-496616dd6af3")
                    }.Then {
                        statusCode(200)
                        body("external_id", equalTo("3fcdd722-93ca-4693-9bef-496616dd6af3"))
                        body("default", equalTo(true))
                    }
                }

                scenario("관리자 권한으로 사용자 필드를 조회하면, 해당 필드를 반환해야 한다.") {
                    Given {
                        testDataHelper.setData("/testsets/field/integration/admin/get-admin-api-v1-field-ext.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
                        header("X-Token-Roles", "admin")
                    }.When {
                        get("/field/8fd344e0-e074-49a4-ae22-ff8653ba02f2")
                    }.Then {
                        statusCode(200)
                        body("external_id", equalTo("8fd344e0-e074-49a4-ae22-ff8653ba02f2"))
                        body("default", equalTo(false))
                        body("owner", equalTo("d15cdbf8-22bc-47e2-9e9a-4d171cb6522e"))
                    }
                }

                scenario("존재하지 않는 필드를 조회하면, 404 에러를 리턴한다.") {
                    Given {
                        testDataHelper.setData("/testsets/field/integration/admin/get-admin-api-v1-field-ext.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
                        header("X-Token-Roles", "admin")
                    }.When {
                        get("/field/0878af0a-d537-43ea-b1b9-8f3ff3812ef2")
                    }.Then {
                        statusCode(404)
                    }
                }
            }

            feature("[POST] /admin/api/v1/field/default") {
                scenario("관리자 권한으로 올바른 요청이 주어지면, 해당 기본 필드를 생성하여 리턴한다.") {
                    Given {
                        testDataHelper.setData("/testsets/field/integration/admin/post-admin-api-v1-field-before.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
                        header("X-Token-Roles", "admin")
                        contentType("application/json")
                        body(
                            jsonBuilder {
                                "name" to "Admin Created Field"
                                "icon" to "admin_icon"
                                "type" to "DROP_DOWN"
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
                        )
                    }.When {
                        post("/field/default")
                    }.Then {
                        statusCode(201)
                        testDataHelper.assertData("/testsets/field/integration/admin/post-admin-api-v1-field-after.json")
                        body("name", equalTo("Admin Created Field"))
                        body("icon", equalTo("admin_icon"))
                        body("type", equalTo("DROP_DOWN"))
                        body("default", equalTo(true))
                        body("owner", equalTo(null))
                        body("external_id", equalTo("00000000-0000-0000-0000-000000000000"))
                    }
                }
            }

            feature("[PUT] /admin/api/v1/field/default/{extId}") {
                scenario("관리자 권한으로 기본 필드 수정 요청이 주어지면, 해당 필드를 수정하여 리턴한다.") {
                    Given {
                        testDataHelper.setData("/testsets/field/integration/admin/put-admin-api-v1-field-before.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
                        header("X-Token-Roles", "admin")
                        contentType("application/json")
                        body(
                            jsonBuilder {
                                "name" to "Admin Updated Field"
                                "icon" to "admin_updated_icon"
                                "attributes" arr {
                                    +obj {
                                        "key" to "values"
                                        "value" arr {
                                            +"Admin Updated Option A"
                                            +"Admin Updated Option B"
                                            +"Admin Updated Option C"
                                        }
                                    }
                                    +obj {
                                        "key" to "allowAddValue"
                                        "value" arr {
                                            +"false"
                                        }
                                    }
                                }
                            }
                        )
                    }.When {
                        put("/field/default/088eb9c8-a340-4c98-9c64-a869d7e1a2ac")
                    }.Then {
                        statusCode(200)
                        testDataHelper.assertData("/testsets/field/integration/admin/put-admin-api-v1-field-after.json")
                        body("name", equalTo("Admin Updated Field"))
                        body("icon", equalTo("admin_updated_icon"))
                        body("type", equalTo("DROP_DOWN"))
                        body("default", equalTo(true))
                        body("owner", equalTo(null))
                        body("external_id", equalTo("088eb9c8-a340-4c98-9c64-a869d7e1a2ac"))
                    }
                }
            }

            feature("[DELETE] /admin/api/v1/field/default/{extId}") {

                scenario("관리자 권한으로 기본 필드 삭제를 시도하면, 해당 필드를 삭제하고, 204를 리턴한다.") {
                    Given {
                        testDataHelper.setData("/testsets/field/integration/admin/delete-admin-api-v1-field-before.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
                        header("X-Token-Roles", "admin")
                    }.When {
                        delete("/field/default/3fcdd722-93ca-4693-9bef-496616dd6af3")
                    }.Then {
                        statusCode(204)
                    }
                }
            }
        }
    }
}