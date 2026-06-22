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
    private final By categoriesButton = By.xpath("//button[normalize-space()='Categories'] | //a[normalize-space()='Categories']");
    private final By categoriesContainer = By.xpath(
        "//a[contains(.,'Jewelry') or contains(.,'Clothing') or contains(.,'Home') or contains(.,'Wedding') or contains(.,'Accessories')]"
    );

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

    public void clickCategoriesButton() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(categoriesButton));
        button.click();
    }

    public boolean isCategoriesListDisplayed() {
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(categoriesContainer, 1));
        return driver.findElements(categoriesContainer).size() > 1;
    }

    public boolean isCategoryVisible(String categoryName) {
        By byText = By.xpath("//*[contains(normalize-space(.), '" + categoryName + "') and (self::a or self::li or self::span or self::button)]");
        return !driver.findElements(byText).isEmpty();
    }
}
