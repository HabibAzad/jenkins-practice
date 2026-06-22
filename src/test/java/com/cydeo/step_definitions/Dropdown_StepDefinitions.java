package com.cydeo.step_definitions;

import com.cydeo.pages.DropdownPage;
import com.cydeo.utilities.ConfigurationReader;
import com.cydeo.utilities.Driver;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

public class Dropdown_StepDefinitions {

    private WebDriver driver;
    private DropdownPage dropdownPage;

    @Given("I navigate to the dropdown page")
    public void i_navigate_to_the_dropdown_page() {
        driver = Driver.getDriver();
        dropdownPage = new DropdownPage(driver);
        driver.get(ConfigurationReader.getProperty("dropdown_url"));
    }

    @When("I select the option by visible text {string}")
    public void i_select_the_option_by_visible_text(String text) {
        dropdownPage.selectByVisibleText(text);
    }

    @When("I select the option by index {int}")
    public void i_select_the_option_by_index(int index) {
        dropdownPage.selectByIndex(index);
    }

    @When("I select the option by value {string}")
    public void i_select_the_option_by_value(String value) {
        dropdownPage.selectByValue(value);
    }

    @Then("the selected option text should be {string}")
    public void the_selected_option_text_should_be(String expectedText) {
        String actualText = dropdownPage.getSelectedOptionText();
        Assert.assertEquals(
            "Expected selected option '" + expectedText + "' but got: " + actualText,
            expectedText,
            actualText
        );
    }
}
