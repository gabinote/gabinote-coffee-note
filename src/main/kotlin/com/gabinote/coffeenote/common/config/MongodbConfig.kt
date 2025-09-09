package com.gabinote.coffeenote.common.config

import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

/**
 * MongoDB 설정 클래스
 * MongoDB 감사 및 리포지토리 활성화를 담당
 * @author 황준서
 */
@EnableMongoAuditing
@EnableMongoRepositories
class MongodbConfig