package com.cydeo.step_definitions;

import com.cydeo.pages.Login;
import com.cydeo.utilities.ConfigurationReader;
import com.cydeo.utilities.Driver;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

public class Login_StepDefinitions {

    private WebDriver driver;
    private Login loginPage;

    @Given("I navigate to the login page")
    public void i_navigate_to_the_login_page() {
        driver = Driver.getDriver();
        loginPage = new Login(driver);
        driver.get(ConfigurationReader.getProperty("url"));
    }

    @When("I enter username {string} and password {string}")
    public void i_enter_username_and_password(String username, String password) {
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
    }

    @And("I click the login button")
    public void i_click_the_login_button() {
        loginPage.clickLoginButton();
    }

    @Then("I should be redirected away from the login page")
    public void i_should_be_redirected_away_from_the_login_page() {
        String currentUrl = driver.getCurrentUrl();
        Assert.assertFalse(
            "Expected to leave the login page but URL is still: " + currentUrl,
            currentUrl.equals(ConfigurationReader.getProperty("url"))
        );
    }

    @Then("I should see a login error message")
    public void i_should_see_a_login_error_message() {
        Assert.assertTrue(
            "Expected an error message to be displayed after failed login",
            loginPage.isErrorDisplayed()
        );
    }
}
