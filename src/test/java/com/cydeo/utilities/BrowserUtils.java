package com.cydeo.utilities;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BrowserUtils {

    public static void verifyTitle(String expectedTitle) {
        String actualTitle = Driver.getDriver().getTitle();
        if (!actualTitle.equals(expectedTitle)) {
            throw new AssertionError("Expected title: " + expectedTitle + ", but got: " + actualTitle);
        }
    }

    // Waits up to timeoutSeconds for the URL to contain the given fragment
    public static void waitForUrlContains(String urlFragment, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(Driver.getDriver(), Duration.ofSeconds(timeoutSeconds));
        boolean result = wait.until(ExpectedConditions.urlContains(urlFragment));
        if (!result) {
            throw new AssertionError(
                "URL did not contain '" + urlFragment + "' within " + timeoutSeconds + " seconds. " +
                "Actual URL: " + Driver.getDriver().getCurrentUrl()
            );
        }
    }

    public static void selectByText(WebElement selectElement, String visibleText) {
        new Select(selectElement).selectByVisibleText(visibleText);
    }

    public static String getSelectedOptionText(WebElement selectElement) {
        return new Select(selectElement).getFirstSelectedOption().getText();
    }
}
