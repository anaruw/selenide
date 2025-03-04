import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class CardDeliveryTest {

    private String city = "Казань";

    public String generateDate(int days, String pattern) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern(pattern));
    }

    public int generateYear(int days) {
        return LocalDate.now().plusDays(days).getYear();
    }

    public int generateMonth(int days) {
        return LocalDate.now().plusDays(days).getMonthValue();
    }

    public String generateDay(int days) {
        return generateDate(days, "d");
    }

    @Test
    public void shouldDeliveryCardDirectInput() {

        String planningDate = generateDate(3, "dd.MM.yyyy");

        Selenide.open("http://localhost:9999");

        $("[data-test-id='city'] input").setValue(city);
        $("[data-test-id='date'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE)
                .setValue(planningDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("+71234567890");
        $("[data-test-id='agreement']").click();

        $(".button").click();

        $("[data-test-id='notification'] .notification__content")
                .shouldBe(Condition.allOf(Condition.visible, Condition.exactText(
                                "Встреча успешно забронирована на " + planningDate
                        )),
                        Duration.ofSeconds(15));
    }

    @ParameterizedTest
    @ValueSource(ints = {
            7,
            35
    })
    public void shouldDeliveryCardInteractionWithComplexElements(int days) {

        String inputCity = city.substring(0, 2);
        String planningDate = generateDate(days, "dd.MM.yyyy");
        int planningYear = generateYear(days);
        int planningMonth = generateMonth(days);
        String planningDay = generateDay(days);
        int currentYear = generateYear(0);
        int currentMonth = generateMonth(0);

        Selenide.open("http://localhost:9999");

        // city
        $("[data-test-id='city'] input").setValue(inputCity);
        $$(".popup_visible .menu-item__control").findBy(Condition.exactText(city)).click();

        // date
        $("[data-test-id='date'] button").click();

        for (int i = 0; i < planningYear - currentYear; i++) {
            $(".calendar__arrow[data-step='12']").click();
        }
        if (planningMonth > currentMonth) {
            for (int i = 0; i < planningMonth - currentMonth; i++) {
                $(".calendar__arrow[data-step='1']").click();
            }
        }
        if (planningMonth < currentMonth) {
            for (int i = 0; i < currentMonth - planningMonth; i++) {
                $(".calendar__arrow[data-step='-1']").click();
            }
        }
        $$(".calendar__layout .calendar__day").findBy(Condition.exactText(planningDay)).click();

        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("+71234567890");
        $("[data-test-id='agreement']").click();

        $(".button").click();

        $("[data-test-id='notification'] .notification__content")
                .shouldBe(Condition.allOf(Condition.visible, Condition.exactText(
                                "Встреча успешно забронирована на " + planningDate
                        )),
                        Duration.ofSeconds(15));

    }
}