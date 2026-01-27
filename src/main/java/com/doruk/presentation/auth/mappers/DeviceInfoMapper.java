package com.doruk.presentation.auth.mappers;

import com.doruk.application.app.auth.dto.DeviceInfoObject;
import com.doruk.presentation.auth.dto.DeviceInfoRequest;
import io.micronaut.context.annotation.Mapper;

public interface DeviceInfoMapper {
    @Mapper
    DeviceInfoObject toDeviceInfo(DeviceInfoRequest req);
}
