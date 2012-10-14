import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;


public class SeleniumJUnit {
	private WebDriver driver;
	private String baseUrl;
	private StringBuffer verificationErrors = new StringBuffer();
	@Before
	public void setUp() throws Exception {
		driver = new FirefoxDriver();
		baseUrl = "http://www.nasubi.co.jp";
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@Test
	public void testSeleniumJUnit() throws Exception {
		driver.get(baseUrl + "/index.html");
		driver.findElement(By.id("Image26")).click();
		driver.findElement(By.linkText("ビジネスソリューション")).click();
		driver.findElement(By.linkText("業種別ソリューション")).click();
		driver.findElement(By.linkText("ソリューションサービス")).click();
		driver.findElement(By.id("Image8")).click();
		driver.findElement(By.linkText("ごあいさつ")).click();
		driver.findElement(By.linkText("会社概要")).click();
		driver.findElement(By.linkText("会社概要")).click();
		driver.findElement(By.linkText("アクセスマップ")).click();
		driver.findElement(By.linkText("▲このページの上部へ")).click();
		driver.findElement(By.linkText("沿革")).click();
		driver.findElement(By.linkText("▲このページの上部へ")).click();
		driver.findElement(By.id("Image9")).click();
		driver.findElement(By.linkText("キャリア（中途・契約）採用")).click();
		driver.findElement(By.linkText("人事からのメッセージ")).click();
		driver.findElement(By.linkText("先輩からのメッセージ")).click();
		driver.findElement(By.linkText("採用お問い合せ")).click();
		driver.findElement(By.id("Image10")).click();
		driver.findElement(By.linkText("ホーム")).click();
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}

	private void fail(String verificationErrorString) {
		// TODO 自動生成されたメソッド・スタブ

	}

	private boolean isElementPresent(By by) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

}
