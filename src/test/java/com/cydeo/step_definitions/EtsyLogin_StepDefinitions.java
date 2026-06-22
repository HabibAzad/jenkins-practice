package com.cydeo.step_definitions;

import com.cydeo.pages.EtsyLoginPage;
import com.cydeo.utilities.ConfigurationReader;
import com.cydeo.utilities.Driver;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

public class EtsyLogin_StepDefinitions {

    private WebDriver driver;
    private EtsyLoginPage etsyLoginPage;
    @Given("I navigate to Etsy homepage")
    public void i_navigate_to_etsy_homepage() {
        driver = Driver.getDriver();
        etsyLoginPage = new EtsyLoginPage(driver);
        String etsyUrl = ConfigurationReader.getProperty("etsy_login_url");
        driver.get(etsyUrl != null ? etsyUrl : ConfigurationReader.getProperty("url"));
    }

    @When("I click Etsy Sign in")
    public void i_click_etsy_sign_in() {
        etsyLoginPage.clickSignIn();
    }

    @And("I enter Etsy username and password from config")
    public void i_enter_etsy_username_and_password_from_config() {
        etsyLoginPage.enterEmail(ConfigurationReader.getProperty("etsy_username"));
        etsyLoginPage.enterPassword(ConfigurationReader.getProperty("etsy_password"));
    }

    @And("I enter Etsy username and password from data table")
    public void i_enter_etsy_username_and_password_from_data_table(DataTable dataTable) {
        String username = dataTable.cell(1, 0);
        String password = dataTable.cell(1, 1);
        etsyLoginPage.enterEmail(username);
        etsyLoginPage.enterPassword(password);
    }

    @Then("I submit Etsy sign in")
    public void i_submit_etsy_sign_in() {
        etsyLoginPage.clickSubmitSignIn();
        Assert.assertTrue("Expected to stay on Etsy domain after sign-in attempt",
                driver.getCurrentUrl().contains("etsy.com"));
    }

    @Then("I click on Sign in button")
    public void i_click_on_sign_in_button() {
        etsyLoginPage.clickSubmitSignIn();
        Assert.assertTrue("Expected to stay on Etsy domain after sign-in attempt",
                driver.getCurrentUrl().contains("etsy.com"));
    }

    @When("I enter Etsy username {string} and password {string}")
    public void i_enter_etsy_username_and_password(String username, String password) {
        etsyLoginPage.enterEmail(username);
        etsyLoginPage.enterPassword(password);
    }


}

