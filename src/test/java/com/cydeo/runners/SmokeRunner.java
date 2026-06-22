package com.cydeo.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"html:target/smoke-reports.html",
                "json:target/smoke.json",
                "junit:target/smoke.xml"},
        features = "src/test/resources/features",
        glue = "com.cydeo.step_definitions",
        dryRun = false,
        tags = "@smokeTest"
)
public class SmokeRunner {
}
