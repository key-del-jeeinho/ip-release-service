package com.iplease.server.ip.release.global.log.type

enum class LoggingActType {
    RESERVE_JOB_DEMAND_LOGGER,
    RESERVE_JOB_DELETE_LOGGER,
    RESERVE_SCHEDULER_LOGGER,

    RESERVE_REQUEST_LOGGER,
    CANCEL_RESERVE_REQUEST_LOGGER,
    DEMAND_REQUEST_LOGGER,
    CANCEL_DEMAND_REQUEST_LOGGER,

    EVENT_PUBLISH_LOGGER,
    EVENT_SUBSCRIBE_LOGGER
    ;
}
