import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

public class CurrencyConverter {
    // IMPORTANT: Replace this with your actual API key from exchangerate-api.com
    private static final String API_KEY = "a321e2b1dcf670de0c1b17bb";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Welcome to Currency Converter with Live Rates");
        System.out.println("Supported currencies: USD, EUR, GBP, JPY, INR, CAD, AUD, etc.");
        
        try {
            // Get input from user
            System.out.print("Enter amount: ");
            double amount = scanner.nextDouble();
            
            System.out.print("Enter from currency (e.g. USD): ");
            String fromCurrency = scanner.next().toUpperCase();
            
            System.out.print("Enter to currency (e.g. EUR): ");
            String toCurrency = scanner.next().toUpperCase();
            
            // Fetch exchange rates
            JSONObject exchangeRates = fetchExchangeRates(fromCurrency);
            
            // Check API response status
            if (!exchangeRates.getString("result").equals("success")) {
                System.out.println("API Error: " + exchangeRates.getString("error-type"));
                return;
            }
            
            JSONObject rates = exchangeRates.getJSONObject("conversion_rates");
            
            if (!rates.has(toCurrency)) {
                System.out.println("Error: Target currency not found in rates.");
                System.out.println("Available currencies in response: " + rates.keySet());
                return;
            }
            
            // Perform conversion
            double rate = rates.getDouble(toCurrency);
            double convertedAmount = amount * rate;
            
            // Display result
            System.out.printf("%nConversion Result:%n");
            System.out.printf("%.2f %s = %.2f %s%n", 
                            amount, fromCurrency, 
                            convertedAmount, toCurrency);
            System.out.printf("Exchange rate: 1 %s = %.6f %s%n", 
                            fromCurrency, rate, toCurrency);
            
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static JSONObject fetchExchangeRates(String baseCurrency) throws Exception {
        URL url = new URL(BASE_URL + baseCurrency);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        // Set timeout values (in milliseconds)
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("Failed to fetch rates. HTTP error: " + responseCode);
        }
        
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            
            return new JSONObject(response.toString());
        }
    }
}