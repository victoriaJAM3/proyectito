package accesibilityCheck;


import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.lang.Thread;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Forecast {
    private WebDriver driver;

    public void setUp() throws IOException, InterruptedException {

        Scanner scanner = new Scanner(System.in);

        int selectionBrowser = 1;
        String browsername = "webdriver.chrome.driver";
        String path_browser = "resources/chromedriver";

        System.setProperty(browsername, path_browser);


        String url = "https://id.getharvest.com/forecast/sign_in";


       System.out.println("Write your department name");
        String department = scanner.next();

        driver = new ChromeDriver();


        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get(url);

        Thread.sleep(15000); //tengo 15 segundos para poner usuario y contraseña

        driver.findElement(By.id("log-in")).click();

        Thread.sleep(5000);

       // Busco por "team"
        WebElement teamButton = new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.elementToBeClickable(By.cssSelector(".control-button.test-team-toggle")));
        teamButton.click();


        Thread.sleep(5000);

        // Busco por departamento
        WebElement inputDepartment = new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.elementToBeClickable(By.cssSelector(".test-filter-input.ember-text-field.ember-view")));
        inputDepartment.sendKeys(department);

        Thread.sleep(5000);

        //Agarro todos los elementos de tipo usuario
        List<WebElement> usuarios =driver.findElements(By.cssSelector("div[class*='row user-row ']>div[class*='content-parent-row']>div[class*='row-timeline ember-view']>div[class*='allocation-grid']"));



        System.out.println("La cantidad de personas es: " + usuarios.size());

        double resultado =0;

        for (WebElement z: usuarios) {


            List<WebElement> userTime = new ArrayList<WebElement>();
            userTime = z.findElements(By.cssSelector(".allocation-total"));

            if (userTime.size() != 0){


                //user time contiene todos los allocation total de esa persona

                int value = 0;
            int i = 0;


            String left = userTime.get(i).getCssValue("left");

            //Si está "oculto" que se saltee la primer celda


            String[] horasSintexto3 = left.split("px");
            int leftNumero = Integer.valueOf(horasSintexto3[0]);


            while (leftNumero < 0) {

                i = i + 1;
                left = userTime.get(i).getCssValue("left");
                horasSintexto3 = left.split("px");
                leftNumero = Integer.valueOf(horasSintexto3[0]);
            }

            String[] leftNum = left.split("px");
            double leftNum2 = Double.valueOf(leftNum[0]);

            if (leftNum2 <= 240) {


                Dimension dim = userTime.get(i).getSize();
                int ancho = dim.width;
                value = value + ancho;
                //si en la "primera" celda ya es 240
                if (value == 240) {
                    WebElement alloInfo = userTime.get(i).findElement(By.cssSelector(".allocated-info"));
                    WebElement alloAmount = alloInfo.findElement(By.cssSelector(".allocated-amount"));
                    String horas = alloAmount.getText();


                    //Si horas es igual a Full ó Off no me importan esos valores
                    if ((!horas.equals("Full")) && (!horas.equals("Off"))) {
                        String[] horasSintexto = horas.split(" ");
                        double horasEnt = Double.valueOf(horasSintexto[0]);
                        if (!horasSintexto[1].equals("over")) {
                            resultado = resultado + horasEnt;
                        }
                    }


                } else { //en este caso el width es menor a 240, por lo que el tiempo esta dividido
                    value = 0;
                    left = userTime.get(i).getCssValue("left");
                    String[] leftNum4 = left.split("px");
                    double leftNum3 = Double.valueOf(leftNum4[0]);

                    while ((value < 240) && (leftNum3 < 240)) {

                        left = userTime.get(i).getCssValue("left");
                        leftNum4 = left.split("px");
                        leftNum3 = Double.valueOf(leftNum4[0]);


                        WebElement alloInfo = userTime.get(i).findElement(By.cssSelector(".allocated-info"));
                        WebElement alloAmount = alloInfo.findElement(By.cssSelector(".allocated-amount"));
                        String horas = alloAmount.getText();

                        if ((!horas.equals("Full")) && (!horas.equals("Off"))) {

                            dim = userTime.get(i).getSize();
                            ancho = dim.width;
                            value = value + ancho;

                            if ((value <= 240) && (leftNum3 < 240)) {
                                WebElement alloInfo1 = userTime.get(i).findElement(By.cssSelector(".allocated-info"));
                                WebElement alloAmount1 = alloInfo1.findElement(By.cssSelector(".allocated-amount"));
                                String horas1 = alloAmount1.getText();
                                String[] horasSintexto1 = horas1.split(" ");
                                if (!horasSintexto1[1].equals("over")) {
                                    double horasEnt1 = Double.valueOf(horasSintexto1[0]);
                                    resultado = resultado + horasEnt1;
                                }
                                i = i + 1;
                            }


                        } else { //es full o off de un dia
                            if ((leftNum3 < 240)) {
                                dim = userTime.get(i).getSize();
                                ancho = dim.width;
                                value = value + ancho;
                            }
                            i = i + 1;
                        }


                    }

                }

            }
        }
        }
        System.out.println("El resultado total de horas es : " + Math.round(resultado*100.0)/100.0);

        driver.quit();

    }


    public static void main(String args[]) throws IOException, InterruptedException {

        Forecast test = new Forecast();
        test.setUp();

    }


}
