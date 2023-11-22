package pac1;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;



import org.json.JSONObject;


import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class verify {

	private WebDriver driver;
	private List<JSONObject> inputData;

	@BeforeClass
	public void setUp() {
		
		ChromeOptions ops=new ChromeOptions();
		ops.addArguments("--remote-allows-origins=*");
		
		driver = new ChromeDriver(ops);
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver.manage().window().maximize();
	}

	@Test(priority = 1)
	public void navigateToDynamicTablePage() {
		String url = "https://testpages.herokuapp.com/styled/tag/dynamic-table.html";
		driver.get(url);
	}

	@Test(priority = 2)
	public void insertDataAndRefreshTable() throws InterruptedException {


		WebElement tableDataButton = driver.findElement(By.xpath("//div[@class='centered']//details//summary"));
		tableDataButton.click();

		WebElement inputTextBox = driver
				.findElement(By.xpath("//div[@class='centered']//details//p//textarea[@id='jsondata']"));
		inputData = Arrays.asList(new JSONObject().put("name", "Bob").put("age", 20).put("gender", "male"),
				new JSONObject().put("name", "George").put("age", 42).put("gender", "male"),
				new JSONObject().put("name", "Sara").put("age", 42).put("gender", "female"),
				new JSONObject().put("name", "Conor").put("age", 40).put("gender", "male"),
				new JSONObject().put("name", "Jennifer").put("age", 42).put("gender", "female"));

		inputTextBox.clear();

		StringBuilder inputDataAsString = new StringBuilder();
		for (JSONObject json : inputData) {
			String formattedData = "{\"name\": \"" + json.getString("name") + "\", \"age\": " + json.getInt("age")
					+ ", \"gender\": \"" + json.getString("gender") + "\"},";
			inputDataAsString.append(formattedData);
		}
		inputDataAsString.deleteCharAt(inputDataAsString.lastIndexOf(","));
		inputDataAsString.insert(0, "[");
		inputDataAsString.insert(inputDataAsString.length(), "]");

		System.out.println();

		inputTextBox.sendKeys(inputDataAsString.toString());
		Thread.sleep(2000);

		WebElement refreshButton = driver.findElement(By.xpath("//button[text()='Refresh Table']"));
		refreshButton.click();


	}

	@Test(priority = 3)
	public void compareTableWithData() {
		// Fetch the table data after insertion
		List<WebElement> rows = driver.findElements(By.xpath("//div[@id='tablehere']//table//tr"));
		// WebElement a=driver.findElement(By.xpath("//table[@id=\"dynamictable\"]"));
		System.out.println("table rows" + rows.size());
		// Check if the number of rows matches the stored data count
		assert rows.size() - 1 == inputData.size() : "Number of rows in the table doesn't match the stored data count.";

		for (int i = 2; i <= rows.size(); i++) {

			WebElement e = driver.findElement(By.xpath("//div[@id='tablehere']//table//tr[" + i + "]"));
			String actualName = e.findElement(By.xpath("./td[1]")).getText().trim();

			assert inputData.get(i - 2).get("name").equals(actualName) : "names not matched in row " + (i - 2);

			String actualAge = e.findElement(By.xpath("./td[2]")).getText().trim();
			assert String.valueOf(inputData.get(i - 2).getInt("age")).equals(actualAge)
					: "age not matched in row " + (i - 2);

			String actualGender = e.findElement(By.xpath("./td[3]")).getText().trim();
			assert inputData.get(i - 2).get("gender").equals(actualGender) : "gender not matched in row " + (i - 2);



		}

	}

	@AfterClass
	public void tearDown() {
		if (driver != null) {
			driver.quit();
		}
	}
}

