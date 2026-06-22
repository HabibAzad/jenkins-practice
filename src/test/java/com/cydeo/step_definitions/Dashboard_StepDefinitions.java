package com.cydeo.step_definitions;

import com.cydeo.pages.Dashboard;
import com.cydeo.utilities.BrowserUtils;
import com.cydeo.utilities.Driver;
import io.cucumber.java.en.Then;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

public class Dashboard_StepDefinitions {

    @Then("the page title should be {string}")
    public void the_page_title_should_be(String expectedTitle) {
        BrowserUtils.verifyTitle(expectedTitle);
    }

    @Then("the current URL should contain {string}")
    public void the_current_url_should_contain(String urlFragment) {
        WebDriver driver = Driver.getDriver();
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(
            "Expected URL to contain '" + urlFragment + "' but was: " + currentUrl,
            currentUrl.contains(urlFragment)
        );
    }

    @Then("I should see a welcome message containing {string}")
    public void i_should_see_a_welcome_message_containing(String expectedText) {
        WebDriver driver = Driver.getDriver();
        Dashboard dashboardPage = new Dashboard(driver);
        String flashText = dashboardPage.getFlashMessageText();
        Assert.assertTrue(
            "Expected flash message to contain '" + expectedText + "' but was: " + flashText,
            flashText.contains(expectedText)
        );
    }

    @Then("the logout button should be visible")
    public void the_logout_button_should_be_visible() {
        WebDriver driver = Driver.getDriver();
        Dashboard dashboardPage = new Dashboard(driver);
        Assert.assertTrue(
            "Expected the logout button to be visible on the dashboard",
            dashboardPage.isLogoutButtonVisible()
        );
    }
}
