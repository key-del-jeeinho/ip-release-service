package com.iplease.server.ip.release.domain.request.service

import com.iplease.server.ip.release.domain.request.data.dto.IpReleaseDemandDto
import com.iplease.server.ip.release.domain.request.data.type.DemandStatusType
import com.iplease.server.ip.release.global.request.service.IpReleaseDemandQueryService
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

class DummyIpReleaseDemandQueryService: IpReleaseDemandQueryService {
    override fun getDemandByUuid(uuid: Mono<Long>) = IpReleaseDemandDto(0L, 0L, 0L, DemandStatusType.CREATED).toMono()
    override fun existsDemandByUuid(uuid: Mono<Long>) = true.toMono()
}