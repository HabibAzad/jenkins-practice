package com.cydeo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class EtsySearchPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By searchBox = By.cssSelector("input[name='search_query']");
    private final By listingTitles = By.cssSelector("h3[class*='listing-card__title']");

    public EtsySearchPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void searchFor(String keyword) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(searchBox));
        input.clear();
        input.sendKeys(keyword);
        input.sendKeys(Keys.ENTER);
    }

    public boolean isProductTitleVisible(String expectedTitle) {
        wait.until(ExpectedConditions.presenceOfElementLocated(listingTitles));
        List<WebElement> titles = driver.findElements(listingTitles);
        return titles.stream()
                .anyMatch(el -> el.getText().trim().contains(expectedTitle));
    }
}
