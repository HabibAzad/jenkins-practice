package com.cydeo.step_definitions;

import com.cydeo.pages.Dashboard;
import com.cydeo.pages.Login;
import com.cydeo.utilities.ConfigurationReader;
import com.cydeo.utilities.Driver;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;

public class Logout_StepDefinitions {

    @When("I click the logout button")
    public void i_click_the_logout_button() {
        WebDriver driver = Driver.getDriver();
        Dashboard dashboardPage = new Dashboard(driver);
        dashboardPage.clickLogoutButton();
    }

    @Then("I should be redirected to the login page")
    public void i_should_be_redirected_to_the_login_page() {
        WebDriver driver = Driver.getDriver();
        String currentUrl = driver.getCurrentUrl();
        String loginUrl = ConfigurationReader.getProperty("url");
        Assertions.assertEquals(
            "Expected redirect to login page URL: " + loginUrl + " but was: " + currentUrl,
            loginUrl,
            currentUrl
        );
    }

    @Then("I should see a logout confirmation message")
    public void i_should_see_a_logout_confirmation_message() {
        WebDriver driver = Driver.getDriver();
        Login loginPage = new Login(driver);
        String message = loginPage.getErrorMessage();
        Assertions.assertTrue(
            message.toLowerCase().contains("logged out") || message.toLowerCase().contains("logout"),
            "Expected logout confirmation message but got: " + message
        );
    }
}
