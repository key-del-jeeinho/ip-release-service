package com.iplease.server.ip.release.domain.reserve.controller

import com.iplease.server.ip.release.domain.reserve.data.dto.IpReleaseReserveDto
import com.iplease.server.ip.release.domain.reserve.service.IpReleaseReserveService
import com.iplease.server.ip.release.global.common.data.type.Permission
import com.iplease.server.ip.release.global.common.data.type.Role
import com.iplease.server.ip.release.infra.log.service.LoggingService
import com.iplease.server.ip.release.infra.policy.service.PolicyCheckService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDate
import kotlin.properties.Delegates
import kotlin.random.Random

class IpReleaseReserveControllerTest {
    private lateinit var ipReleaseReserveService: IpReleaseReserveService
    private lateinit var policyCheckService: PolicyCheckService
    private lateinit var loggingService: LoggingService
    private lateinit var target: IpReleaseReserveController
    private var monoJustAny = Mono.just(Any())
    private var reserveUuid by Delegates.notNull<Long>()
    private var assignedIpUuid by Delegates.notNull<Long>()
    private var operatorUuid by Delegates.notNull<Long>()
    private lateinit var releaseAt: LocalDate

    @BeforeEach
    fun setUp() {
        loggingService = mock() { on { withLog(any<Any>(), any<Mono<Any>>(), any()) }.thenAnswer { return@thenAnswer it.arguments[1] } }
        ipReleaseReserveService = mock()
        policyCheckService = mock() {
            on { checkDemandExists(any()) } doReturn monoJustAny
            on { checkDemandAccess(any(), any()) } doReturn monoJustAny
            on { checkPermission(any(), any()) } doReturn monoJustAny
            on { checkAssignedIpExists(any())} doReturn monoJustAny
            on { checkAssignedIpAccess(any(), any()) } doReturn monoJustAny
            on { checkReserveExists(any())} doReturn monoJustAny
            on { checkReserveAccess(any(), any()) } doReturn monoJustAny
        }
        target = IpReleaseReserveController(ipReleaseReserveService, policyCheckService, loggingService)
        reserveUuid = Random.nextLong()
        assignedIpUuid = Random.nextLong()
        operatorUuid = Random.nextLong()
        releaseAt = LocalDate.now().plusDays(1)
    }
    //IP 할당 해제 예약 취소 조건
    //예약이 존재해야하며 -> PolicyCheckService 단에 위임
    //예약의 소유자가 신청자여야 하며 -> PolicyCheckService 단에 위임
    //해제일이 오늘이 아니어야 한다. -> Service 단에 위임
    @Test @DisplayName("IP 할당 해제 예약 취소 - 취소 성공")
    fun cancelReserveReleaseIpSuccess() {
        val role = Role.values().filter { it.hasPermission(Permission.IP_RELEASE_RESERVE_CANCEL) }.random()
        whenever(ipReleaseReserveService.cancelReserve(reserveUuid)).thenReturn(Mono.just(Unit))

        val response = target.cancelReserveReleaseIp(reserveUuid, operatorUuid, role).block()!!

        assert(response.statusCode.is2xxSuccessful)
        verify(policyCheckService, times(1)).checkPermission(role, Permission.IP_RELEASE_RESERVE_CANCEL)
        verify(policyCheckService, times(1)).checkReserveExists(reserveUuid)
        verify(policyCheckService, times(1)).checkReserveAccess(reserveUuid, operatorUuid)
    }
    //IP 할당 해제 예약 조건
    //IP_RELEASE_RESERVE 권한을 가지고 있어야하며 -> PolicyCheckService 단에 위임
    //할당IP 가 존재해야 하며 -> PolicyCheckService 단에 위임
    //할당IP의 소유자가 신청자여야 하며 -> PolicyCheckService 단에 위임`
    //할당IP가 해제신청되지 않았어야 하며 -> Service 단에 위임
    //이미 에약이 존재하지 않아야하며 -> Service 단에 위임
    //예약일이 내일 - 현재 년도 마지막일 사이여한다. -> Service 단에 위임
    @Test @DisplayName("IP 할당 해제 예약 - 예약 성공")
    fun reserveReleaseIpSuccess() {
        val dto = IpReleaseReserveDto(reserveUuid, assignedIpUuid, operatorUuid, releaseAt)
        val role = Role.values().filter { it.hasPermission(Permission.IP_RELEASE_RESERVE) }.random()
        whenever(ipReleaseReserveService.reserve(assignedIpUuid, operatorUuid, releaseAt)).thenReturn(dto.toMono())

        val response = target.reserveReleaseIp(assignedIpUuid, releaseAt, operatorUuid, role).block()!!
        val body = response.body!!

        assert(response.statusCode.is2xxSuccessful)
        assert(body.uuid == reserveUuid)
        assert(body.assignedIpUuid == assignedIpUuid)
        assert(body.issuerUuid == operatorUuid)
        assert(body.releaseAt == releaseAt)
        verify(policyCheckService, times(1)).checkPermission(role, Permission.IP_RELEASE_RESERVE)
        verify(policyCheckService, times(1)).checkAssignedIpExists(assignedIpUuid)
        verify(policyCheckService, times(1)).checkAssignedIpAccess(assignedIpUuid, operatorUuid)
        verify(ipReleaseReserveService, times(1)).reserve(assignedIpUuid, operatorUuid, releaseAt)
    }
}