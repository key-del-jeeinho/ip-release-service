package com.iplease.server.ip.release.domain.request.exception

class AlreadyDemandedAssignedIpException(val assignedIpUuid: Long) : RuntimeException("이미 해제 요청된 AssignedIp 입니다! - assignedIp") {

}
