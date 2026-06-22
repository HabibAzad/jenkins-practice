package com.cydeo.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

public class DropdownPage {

    private WebDriver driver;

    @FindBy(id = "dropdown")
    private WebElement dropdownElement;

    public DropdownPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void selectByVisibleText(String text) {
        new Select(dropdownElement).selectByVisibleText(text);
    }

    public void selectByIndex(int index) {
        new Select(dropdownElement).selectByIndex(index);
    }

    public void selectByValue(String value) {
        new Select(dropdownElement).selectByValue(value);
    }

    public String getSelectedOptionText() {
        return new Select(dropdownElement).getFirstSelectedOption().getText();
    }
}
