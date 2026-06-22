package com.cydeo.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Dashboard {

    private final WebDriver driver;
    private static final int WAIT_SECONDS = 10;

    @FindBy(tagName = "h2")
    private WebElement pageHeading;

    @FindBy(id = "flash")
    private WebElement flashMessage;

    @FindBy(xpath = "//a[@href='/logout']")
    private WebElement logoutButton;

    public Dashboard(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public String getHeadingText() {
        return waitUntilVisible(pageHeading).getText();
    }

    public String getFlashMessageText() {
        return waitUntilVisible(flashMessage).getText();
    }

    public boolean isLogoutButtonVisible() {
        return waitUntilVisible(logoutButton).isDisplayed();
    }

    public void clickLogoutButton() {
        waitUntilClickable(logoutButton).click();
    }

    private WebElement waitUntilVisible(WebElement element) {
        return new WebDriverWait(driver, Duration.ofSeconds(WAIT_SECONDS))
                .until(ExpectedConditions.visibilityOf(element));
    }

    private WebElement waitUntilClickable(WebElement element) {
        return new WebDriverWait(driver, Duration.ofSeconds(WAIT_SECONDS))
                .until(ExpectedConditions.elementToBeClickable(element));
    }
}
