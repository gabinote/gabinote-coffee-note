package com.gabinote.coffeenote.field.integration

import com.gabinote.api.testSupport.testUtil.json.jsonBuilder
import com.gabinote.coffeenote.testSupport.testTemplate.IntegrationTestTemplate
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.Matchers.equalTo

class FieldApiIntegrationTest : IntegrationTestTemplate() {
    override val apiPrefix: String = "/api/v1"

    init {
        feature("[Field] Field API Integration Test") {
            feature("[GET] /api/v1/fields") {
                scenario("ALL Scope로 요청하면, 요청자의 필드와 기본 필드를 반환해야 한다.") {
                    Given {
                        testDataHelper.setData("/testsets/field/integration/get-api-v1-fields.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "admin")
                    }.When {
                        get("/fields?scope=ALL")
                    }.Then {
                        statusCode(200)
                        body("content.size()", equalTo(2))

                        body("content[0].external_id", equalTo("3fcdd722-93ca-4693-9bef-496616dd6af3"))
                        body("content[0].default", equalTo(true))

                        body("content[1].external_id", equalTo("8fd344e0-e074-49a4-ae22-ff8653ba02f2"))
                        body("content[1].default", equalTo(false))
                        body("content[1].owner", equalTo("d15cdbf8-22bc-47e2-9e9a-4d171cb6522e"))
                    }

                }

                scenario("OWNED Scope로 요청하면, 요청자의 필드만 반환해야 한다.") {
                    Given {
                        testDataHelper.setData("/testsets/field/integration/get-api-v1-fields.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "admin")
                    }.When {
                        get("/fields?scope=OWNED")
                    }.Then {
                        statusCode(200)
                        body("content.size()", equalTo(1))

                        body("content[0].external_id", equalTo("8fd344e0-e074-49a4-ae22-ff8653ba02f2"))
                        body("content[0].default", equalTo(false))
                        body("content[0].owner", equalTo("d15cdbf8-22bc-47e2-9e9a-4d171cb6522e"))
                    }

                }

                scenario("DEFAULT Scope로 요청하면, 기본 필드만 반환해야 한다.") {
                    Given {
                        testDataHelper.setData("/testsets/field/integration/get-api-v1-fields.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "admin")
                    }.When {
                        get("/fields?scope=DEFAULT")
                    }.Then {
                        statusCode(200)
                        body("content.size()", equalTo(1))

                        body("content[0].external_id", equalTo("3fcdd722-93ca-4693-9bef-496616dd6af3"))
                        body("content[0].default", equalTo(true))

                    }
                }
            }
            feature("[GET] /api/v1/field/me/{extId}") {
                scenario("자신의 필드를 조회하면, 해당 필드를 반환해야 한다.") {
                    Given {
                        testDataHelper.setData("/testsets/field/integration/get-api-v1-field-me-ext.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "admin")
                    }.When {
                        get("/field/me/8fd344e0-e074-49a4-ae22-ff8653ba02f2")
                    }.Then {
                        statusCode(200)
                        body("external_id", equalTo("8fd344e0-e074-49a4-ae22-ff8653ba02f2"))
                        body("default", equalTo(false))
                        body("owner", equalTo("d15cdbf8-22bc-47e2-9e9a-4d171cb6522e"))
                    }
                }
                scenario("기본값 필드를 조회하면, 404 에러를 리턴한다.") {
                    Given {
                        testDataHelper.setData("/testsets/field/integration/get-api-v1-field-me-ext.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "admin")
                    }.When {
                        get("/field/me/3fcdd722-93ca-4693-9bef-496616dd6af3")
                    }.Then {
                        statusCode(404)
                    }
                }
                scenario("타인의 필드를 조회하면, 404 에러를 리턴한다.") {
                    Given {
                        testDataHelper.setData("/testsets/field/integration/get-api-v1-field-me-ext.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "admin")
                    }.When {
                        get("/field/me/088eb9c8-a340-4c98-9c64-a869d7e1a2ac")
                    }.Then {
                        statusCode(404)
                    }
                }
            }
            feature("[GET] /api/v1/field/default/{extId}") {

                scenario("기본값 필드를 조회하면, 해당 필드를 반환해야 한다.") {
                    Given {
                        testDataHelper.setData("/testsets/field/integration/get-api-v1-field-default-ext.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "admin")
                    }.When {
                        get("/field/default/3fcdd722-93ca-4693-9bef-496616dd6af3")
                    }.Then {
                        statusCode(200)
                        body("external_id", equalTo("3fcdd722-93ca-4693-9bef-496616dd6af3"))
                        body("default", equalTo(true))
                    }
                }

                scenario("기본 값이 아닌 필드를 조회하면, 404 에러를 리턴한다.") {
                    Given {
                        testDataHelper.setData("/testsets/field/integration/get-api-v1-field-default-ext.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "admin")
                    }.When {
                        get("/field/default/8fd344e0-e074-49a4-ae22-ff8653ba02f2")
                    }.Then {
                        statusCode(404)
                    }
                }
            }
            feature("[POST] /api/v1/field/me/{extId}") {
                scenario("올바른 요청이 주어지면, 해당 필드를 생성하여 리턴한다.") {
                    Given {
                        testDataHelper.setData("/testsets/field/integration/post-api-v1-field-me-before.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "admin")
                        contentType("application/json")
                        body(
                            jsonBuilder {
                                "name" to "new"
                                "icon" to "new_icon"
                                "type" to "DROP_DOWN"
                                "attributes" arr {
                                    +obj {
                                        "key" to "values"
                                        "value" arr {
                                            +"Option A"
                                            +"Option B"
                                            +"Option C"
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
                        post("/field/me")
                    }.Then {
                        statusCode(201)
                        testDataHelper.assertData("/testsets/field/integration/post-api-v1-field-me-after.json")
                        body("name", equalTo("new"))
                        body("icon", equalTo("new_icon"))
                        body("type", equalTo("DROP_DOWN"))
                        body("attributes.size()", equalTo(2))
                        body("default", equalTo(false))
                        body("owner", equalTo("d15cdbf8-22bc-47e2-9e9a-4d171cb6522e"))
                        body("external_id", equalTo("00000000-0000-0000-0000-000000000000"))
                    }
                }
            }
            feature("[PUT] /api/v1/field/me/{extId}") {
                scenario("올바른 요청이 주어지면, 해당 필드를 수정하여 리턴한다.") {
                    Given {
                        testDataHelper.setData("/testsets/field/integration/put-api-v1-field-me-before.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "695b8482-c259-4493-82e2-8988599d21e9")
                        header("X-Token-Roles", "admin")
                        contentType("application/json")
                        body(
                            jsonBuilder {
                                "name" to "updated field"
                                "icon" to "updated_icon"
                                "attributes" arr {
                                    +obj {
                                        "key" to "values"
                                        "value" arr {
                                            +"Option 가"
                                            +"Option 나"
                                            +"Option 다"
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
                        put("/field/me/088eb9c8-a340-4c98-9c64-a869d7e1a2ac")
                    }.Then {
                        statusCode(200)
                        testDataHelper.assertData("/testsets/field/integration/put-api-v1-field-me-after.json")
                        body("name", equalTo("updated field"))
                        body("icon", equalTo("updated_icon"))
                        body("type", equalTo("DROP_DOWN"))
                        body("attributes.size()", equalTo(2))
                        body("default", equalTo(false))
                        body("owner", equalTo("695b8482-c259-4493-82e2-8988599d21e9"))
                        body("external_id", equalTo("088eb9c8-a340-4c98-9c64-a869d7e1a2ac"))
                    }
                }

            }
            feature("[DELETE] /api/v1/field/me/{extId}") {
                scenario("올바른 자신의 필드의 외부id를 제공하면, 해당 필드를 삭제한다.") {
                    Given {
                        testDataHelper.setData("/testsets/field/integration/delete-api-v1-field-me-before.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "admin")
                    }.When {
                        delete("/field/me/8fd344e0-e074-49a4-ae22-ff8653ba02f2")
                    }.Then {
                        statusCode(204)
                        testDataHelper.assertData("/testsets/field/integration/delete-api-v1-field-me-after.json")
                    }
                }
            }
        }
    }
}