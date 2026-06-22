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

    @Then("I should see results containing {string} in the product title")
    public void i_should_see_results_containing_in_the_product_title(String expectedTitle) {
        Assert.assertTrue(
                "No product found with title containing: " + expectedTitle,
                etsySearchPage.isProductTitleVisible(expectedTitle));
    }

    @When("I click on the Categories button")
    public void i_click_on_the_categories_button() {
        driver = Driver.getDriver();
        etsySearchPage = new EtsySearchPage(driver);
        etsySearchPage.clickCategoriesButton();
    }

    @Then("I should see a list of categories displayed")
    public void i_should_see_a_list_of_categories_displayed() {
        Assert.assertTrue("Categories list is not displayed", etsySearchPage.isCategoriesListDisplayed());
    }

    @Then("I should see the {string} category")
    public void i_should_see_the_category(String categoryName) {
        Assert.assertTrue("Category not found: " + categoryName, etsySearchPage.isCategoryVisible(categoryName));
    }
}
