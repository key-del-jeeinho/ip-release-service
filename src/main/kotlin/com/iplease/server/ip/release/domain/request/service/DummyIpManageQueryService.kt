package com.iplease.server.ip.release.domain.request.service

import com.iplease.server.ip.release.domain.request.data.dto.AssignedIpDto
import com.iplease.server.ip.release.global.request.service.IpManageQueryService
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime

class DummyIpManageQueryService: IpManageQueryService {
    override fun existsAssignedIpByUuid(uuid: Mono<Long>) = true.toMono()
    override fun getAssignedIpByUuid(uuid: Mono<Long>) = uuid.map{ AssignedIpDto(it, 0L, 0L, LocalDateTime.MIN) }
}