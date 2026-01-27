package com.doruk.application.events;

import com.doruk.application.enums.OtpChannel;
import com.doruk.application.enums.TemplateType;
import com.doruk.application.interfaces.EventDto;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;

@Serdeable
@Builder
public record OtpDeliveryEvent(
        OtpChannel channel,
        String to,
        int otp,
        String magicLink,
        TemplateType contentTemplate
) implements EventDto {
    @Override
    public String eventSubject() {
        return "event.otp.delivery";
    }
}
