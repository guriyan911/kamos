import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Selenium2Example {

	@Test
	public void testHelloWorld() {
		WebDriver driver = new FirefoxDriver();
		try {
			driver.get("http://localhost:8080/misekime/index.jsp");
			WebElement element = driver.findElement(By.tagName("h2"));
			assertEquals("Hello World!", element.getText());
		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			driver.quit();
		}
	}
}