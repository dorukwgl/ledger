package com.doruk;

import com.doruk.infrastructure.config.AppExecutors;
import com.doruk.infrastructure.persistence.users.UserRepository;
import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;


@OpenAPIDefinition(
        info = @Info(
                title = "microsample",
                version = "1.0.0"
        )
)
public class Application {

    static void main(String[] args) {
        var ctx = Micronaut.run(Application.class, args);
        var client = ctx.getBean(UserRepository.class);
        var executor = ctx.getBean(AppExecutors.class);
    }
}
