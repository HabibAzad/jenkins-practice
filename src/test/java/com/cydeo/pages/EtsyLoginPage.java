package com.cydeo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Set;

public class EtsyLoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public EtsyLoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void clickSignIn() {
        switchToAvailableWindow();

        By primaryLocator = By.cssSelector("a[href*='signin'], a[href*='sign_in'], button[data-wt-id='signinHeader']");
        By fallbackLocator = By.xpath("//a[normalize-space()='Sign in'] | //button[normalize-space()='Sign in']");

        WebElement signInButton;
        try {
            signInButton = wait.until(ExpectedConditions.elementToBeClickable(primaryLocator));
        } catch (NoSuchWindowException e) {
            switchToAvailableWindow();
            signInButton = wait.until(ExpectedConditions.elementToBeClickable(fallbackLocator));
        } catch (TimeoutException e) {
            switchToAvailableWindow();
            signInButton = wait.until(ExpectedConditions.elementToBeClickable(fallbackLocator));
        }
        signInButton.click();
    }

    private void switchToAvailableWindow() {
        try {
            driver.getTitle();
        } catch (NoSuchWindowException e) {
            Set<String> handles = driver.getWindowHandles();
            if (!handles.isEmpty()) {
                driver.switchTo().window(handles.iterator().next());
            }
        }
    }

    public void enterEmail(String email) {
        WebElement emailField = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[name='email'], input[type='email'], input#email")
        ));
        emailField.clear();
        emailField.sendKeys(email);
    }

    public void enterPassword(String password) {
        WebElement passwordField = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[name='password'], input[type='password'], input#password")
        ));
        passwordField.clear();
        passwordField.sendKeys(password);
    }

    public void clickSubmitSignIn() {
        By primaryLocator = By.name("submit_attempt");
        By fallbackLocator = By.cssSelector("button[type='submit']");

        WebElement submitButton;
        try {
            submitButton = wait.until(ExpectedConditions.elementToBeClickable(primaryLocator));
        } catch (TimeoutException e) {
            submitButton = wait.until(ExpectedConditions.elementToBeClickable(fallbackLocator));
        }
        submitButton.click();
    }
}

