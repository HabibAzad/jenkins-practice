package com.qacopilot.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = "cucumber.glue", value = "com.qacopilot.stepdefs")
@ConfigurationParameter(
        key = "cucumber.plugin",
        value = "pretty,com.qacopilot.support.QaCopilotHtmlReportPlugin:build/reports/qacopilot-api"
)
public class CucumberTestRunner {
}
