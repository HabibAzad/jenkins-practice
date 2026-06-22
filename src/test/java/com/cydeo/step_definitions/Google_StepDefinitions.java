package com.cydeo.step_definitions;

import com.cydeo.utilities.Driver;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Google_StepDefinitions {

    private WebDriver driver;

    @Given("I am on the Google search page")
    public void i_am_on_the_google_search_page() {
        driver = Driver.getDriver();
        driver.get("https://www.google.com/");
    }

    @When("I enter {string} into the search box")
    public void i_enter_into_the_search_box(String searchTerm) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.name("q")));
        searchBox.clear();
        searchBox.sendKeys(searchTerm);
    }

    @And("I click the search button")
    public void i_click_the_search_button() {
        driver.findElement(By.name("q")).sendKeys(Keys.ENTER);
    }

    @Then("I should see results related to {string}")
    public void i_should_see_results_related_to(String searchTerm) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.or(
                ExpectedConditions.titleContains(searchTerm),
                ExpectedConditions.urlContains("search?q=")
        ));

        String title = driver.getTitle();
        String url = driver.getCurrentUrl();
        String encodedTerm = searchTerm.replace(" ", "+");

        Assertions.assertTrue(
                title.contains(searchTerm) || url.contains(encodedTerm) || url.contains(searchTerm),
                "Expected search term in title or URL. Title: [" + title + "] URL: [" + url + "]"
        );
    }
}

