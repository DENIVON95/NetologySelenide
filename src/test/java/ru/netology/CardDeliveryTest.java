package ru.netology;

import com.codeborne.selenide.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static java.lang.String.format;

public class CardDeliveryTest {
    private final SelenideElement cityInput = $("[data-test-id='city'] input");
    private final SelenideElement cityValidation = $("[data-test-id='city'] .input__sub");
    private final SelenideElement nameInput = $("[data-test-id='name'] input");
    private final SelenideElement nameValidation = $("[data-test-id='name'] .input__sub");
    private final SelenideElement phoneInput = $("[data-test-id='phone'] input");
    private final SelenideElement phoneValidation = $("[data-test-id='phone'] .input__sub");
    private final SelenideElement agreementCheckbox = $("[data-test-id='agreement'] .checkbox__box");
    private final SelenideElement submitButton = $x("//button//span[text()='Забронировать']");
    private final SelenideElement dateButton = $("[data-test-id='date'] button");
    private final SelenideElement dateInput = $("[data-test-id='date'] input");
    private final SelenideElement dateValidation = $("[data-test-id='date'] .input__sub");
    private final ElementsCollection calendarPopupContent = $$(".calendar-input__calendar-wrapper tbody tr");
    private final SelenideElement calendarCurrentDay = $(".calendar__day.calendar__day_state_current");
    private final SelenideElement notificationPopover = $("[data-test-id='notification']");
    private final SelenideElement notificationContent = notificationPopover.$(".notification__content");
    private final SelenideElement submitButtonIcon = $(".button__icon > span");


    @BeforeAll
    public static void setup() {
        Configuration.baseUrl = "http://localhost:7777";
        Configuration.headless = Boolean.parseBoolean(System.getProperty("selenide.headless"));
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--start-fullscreen", "--start-incognito");
        Configuration.browser = "chrome";
        Configuration.browserCapabilities = options;
        Configuration.startMaximized = true;
    }

    @BeforeEach
    public void openMainPage() {
        Selenide.open("/");
    }

    @Test
    public void shouldSuccessfullyCompleteApplication() {
        String deliveryDate = LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        cityInput
                .shouldBe(visible)
                .sendKeys("Москва");
        dateButton.click();
        calendarPopupContent
                .shouldHave(CollectionCondition.sizeGreaterThan(0));
        calendarCurrentDay.click();
        nameInput.sendKeys("Тест Тестович");
        phoneInput.sendKeys("+79647777777");
        agreementCheckbox.click();
        submitButton.click();
        submitButtonIcon
                .shouldBe(visible)
                .shouldHave(attribute("class", "spin spin_size_m spin_visible spin_theme_alfa-on-color"));
        submitButtonIcon.shouldHave(attribute("class", "spin spin_size_m spin_theme_alfa-on-color"),
                Duration.ofSeconds(15));
        notificationPopover.shouldBe(visible);
        notificationContent.shouldHave(exactTextCaseSensitive(
                format("Встреча успешно забронирована на %s", deliveryDate)));
    }

    @Test
    public void shouldValidateAgreementCheckbox() {
        cityInput
                .shouldBe(visible)
                .sendKeys("Москва");
        nameInput
                .shouldBe(visible)
                .sendKeys("Тест Тестович");
        phoneInput.sendKeys("+79777777777");
        submitButton.click();
        agreementCheckbox
                .parent()
                .shouldHave(attribute("class", "checkbox checkbox_size_m checkbox_theme_alfa-on-white input_invalid"));
    }

    @Test
    public void shouldValidatePhone() {
        cityInput
                .shouldBe(visible)
                .sendKeys("Москва");
        nameInput.sendKeys("Тест Тестович");
        phoneInput.sendKeys("79777777777");
        submitButton.click();
        phoneValidation
                .shouldBe(visible)
                .shouldHave(exactTextCaseSensitive("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    public void shouldValidateCity() {
        cityInput
                .shouldBe(visible)
                .sendKeys("Test");
        submitButton.click();
        cityValidation.shouldHave(exactTextCaseSensitive("Доставка в выбранный город недоступна"));
    }

    @Test
    public void shouldValidateName() {
        cityInput
                .shouldBe(visible)
                .sendKeys("Москва");
        nameInput.shouldBe(visible).sendKeys("Test Testovich");
        submitButton.click();
        nameValidation
                .shouldBe(visible)
                .shouldHave(exactTextCaseSensitive("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    public void shouldValidateDate() {
        cityInput
                .shouldBe(visible)
                .sendKeys("Москва");
        dateInput
                .shouldBe(visible)
                .sendKeys(Keys.CONTROL, Keys.BACK_SPACE);
        submitButton.click();
        dateValidation.shouldHave(exactTextCaseSensitive("Неверно введена дата"));
    }

    @AfterEach
    public void tearDown() {
        Selenide.closeWebDriver();
    }
}
