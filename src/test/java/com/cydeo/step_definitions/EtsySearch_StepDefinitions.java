package com.cydeo.step_definitions;

import com.cydeo.pages.EtsySearchPage;
import com.cydeo.utilities.Driver;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

public class EtsySearch_StepDefinitions {

    private WebDriver driver;
    private EtsySearchPage etsySearchPage;

    @When("I search for {string}")
    public void i_search_for(String keyword) {
        driver = Driver.getDriver();
        etsySearchPage = new EtsySearchPage(driver);
        etsySearchPage.searchFor(keyword);
    }

    @Then("I should see a product with title containing {string}")
    public void i_should_see_a_product_with_title_containing(String expectedTitle) {
        Assert.assertTrue(
                "No product found with title containing: " + expectedTitle,
                etsySearchPage.isProductTitleVisible(expectedTitle));
    }
}
