 package NewImpelmentation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;




//  code to set path of file getting download
public class SeleniumBasic {
	
	static HashMap<Long, WebDriver> map = new HashMap<Long, WebDriver>();
	static WebDriver driver;
	
	public static String toCron(String mins, String hrs) {
		return String.format("%s %s %s %s %s %s %s", 0, mins, hrs, "?", "*", "*", "*");
	}

	public static String getCron() {
		LocalDateTime dateTime = LocalDateTime.now();
		dateTime = dateTime.plusMinutes(5);
		String cronExpression = toCron(String.valueOf(dateTime.getMinute()), String.valueOf(dateTime.getHour()));

		return cronExpression;
	}
	
	// checking if a file got download in a given folder with
	public static boolean isFileDownloaded(String filePath, String fileName, int iterations) throws Exception {
		boolean flag = false;
		File dir = new File(filePath);
		for (int waitForDownload = 0; waitForDownload < iterations; waitForDownload++) {
			File[] dir_contents = dir.listFiles();
			for (int i = 0; i < dir_contents.length; i++) {
				if (dir_contents[i].getName().equals(fileName)) {
					System.out.println("exist.");
					return flag = true;
				}
			}
			Thread.sleep(1000);
		}
		return flag;
	}
  
	// Renaming the File
	public static String renameFile(String file) {
		// original file
		File previousFile = new File(file);
		String[] previousFileArray = file.split(".pdf");
		String newFileName = previousFileArray[0] + "_" + System.currentTimeMillis() + ".pdf";
		File newFile = new File(newFileName);
		boolean flag = previousFile.renameTo(newFile);

		// if renameTo() return true then if block is
		// executed
		if (flag == true) {
			System.out.println("File Successfully Rename");
			return newFileName;
		}
		// if renameTo() return false then else block is
		// executed
		else {
			System.out.println("Operation Failed");
			return "";
		}
	}

	public static String moveFile(String file, String newPath) throws IOException {
		// original path
		Path originalFilePath = Paths.get(file);
		// new path
		Path newFilePath = Paths.get(newPath);
		Files.move(originalFilePath, newFilePath);
		return newPath;

	}
	
	public static WebDriver getWebdriverSession() {
		
		driver = map.get(Thread.currentThread().getId());
		System.out.println("WebDriver in getWebdriverSession: " + driver);
		return driver;
	}

	
	public static void loadWebdriverSession() {
		driver = new ChromeDriver();
		map.put(Thread.currentThread().getId(), driver);
		System.out.println("WebDriver in loadWebdriverSession: " + driver);
	}

	public static void main(String[] args) throws Exception {
		
		System.out.println(getCron());
		
		// Giving the path where we want to download the files
		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("download.default_directory", "G:\\NewImplementation");
		
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.setExperimentalOption("prefs", chromePrefs);
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver(chromeOptions);
		loadWebdriverSession();
		driver = getWebdriverSession();
		// ******* Navigate to the Automation Demo Site *********
		driver.manage().window().maximize();
		driver.get("https://demo.automationtesting.in/FileDownload.html");
		driver.findElement(By.xpath("//a[text()='Download' and @type='button']")).click();
		String originalFileName = "samplefile.pdf";
		System.out.println(isFileDownloaded(originalFileName, 30));
		boolean fileDownloadFlag = isFileDownloaded("G:\\NewImplementation",  originalFileName, 30);
		String originalFilePath = "G:\\NewImplementation\\samplefile.pdf";

		// *************** CALLING RENAMING METHOD ******************** //

		String isFileRenamed = renameFile(originalFilePath);
		if(isFileRenamed.length()>0) {
			System.out.println("RENAMED FILE: "+isFileRenamed);
		}else {
			System.out.println("FILE NOT RENAMED");
		}

		// *************** CALLING RENAMING METHOD - END ******************** //

		if (fileDownloadFlag) {
			try {

				// *************** CALLING MOVING FILE METHOD ******************** //

				moveFile("G:\\NewImplementation\\samplefile.pdf",
						"G:\\NewImplementation\\AutomationDemoSuite\\samplefile" + System.currentTimeMillis() + ".pdf");

			} catch (Exception e) {
				System.out.println("File didn't get moved");
				System.out.println("==========================");
				e.printStackTrace();

			}
		} else {
			System.out.println("File didn't get download");
		}

		driver.quit();

	}
}
