package com.doruk;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;


@OpenAPIDefinition(
        info = @Info(
                title = "Ledger",
                version = "1.0.0"
        )
)
public class Application {

    static void main(String[] args) {
        var ctx = Micronaut.run(Application.class, args);
    }
}
