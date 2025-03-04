import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.commands.SetValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.$;

public class CardDeliveryTest {

    private String city = "Казань";

    public String generateDate(int days, String pattern) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern(pattern));
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
}