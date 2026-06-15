package com.ust.sdet.examples;

import com.ust.sdet.support.Config;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public final class FirstSession {
    private FirstSession() {
    }

    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();
        try {
            driver.get(Config.catalogUrl());
            System.out.println("Page title: " + driver.getTitle());
        } finally {
            driver.quit();
        }
    }
}
