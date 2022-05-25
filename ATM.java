import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

class MyATM{

    /* authentication methods start*/
    String[] authUserData = new String[6];

    private void createUser(String name, String accountPassword){
        try{
            BufferedWriter out = bufferedWriterObject();
            Random accountNumber = new Random();
            long userAccountNumber = accountNumber.nextLong(111111111111L, 999999999999L);
            String userId = createUserId();
            out.write(userId + " " + name + " " + accountPassword + " " + userAccountNumber + " 0 " + "0" + "\n");
            out.close();
            System.out.println("Account created.\nAccount Number: " + userAccountNumber);
            authUserData[0] = userId;
            authUserData[1] = name;
            authUserData[2] = accountPassword;
            authUserData[3] = String.valueOf(userAccountNumber);
            authUserData[4] = "0";
            authUserData[5] = "0";
            menu();
        }catch(IOException e){
            System.out.println("An error occur! while creating your account please try again later.");
        }
    }

    private boolean validateUser(String[] data){
        File file = bufferedReader();
        try{
            Scanner fileData = new Scanner(file);
            while (fileData.hasNextLine()){
                String userData = fileData.nextLine();
                String[] userCredential = userData.split(" ");
                if (Objects.equals(data[0], userCredential[3]) && Objects.equals(data[1], userCredential[2])){
                    authUserData = userCredential;
                    return false;
                }
            }
        } catch (FileNotFoundException e){
            System.out.println("An error occur! while creating your account please try again later.");
        }
        return true;
    }

    private void authenticate(){
        while (true){
            System.out.println("Enter Your Account number");
            String accountNumber = getStringInput();
            System.out.println("Enter Your Password");
            String accountPassword = getStringInput();
            boolean isAuthenticated = validateUser(new String[] {accountNumber, accountPassword});
            if (isAuthenticated){
                System.out.println("Invalid Account Number or Password");
            } else{
                break;
            }
        }
        menu();
    }

    private void createAccount(){
        System.out.println("Enter Your Name");
        String name = getStringInput().replaceAll(" ", "_");
        System.out.println("Enter a Password");
        String password = getStringInput();  // make another method called checkPassword to check the password criteria.
        createUser(name, password);
    }
    /* authentication methods end */

    /* input methods start*/
    private String getStringInput(){
        return new Scanner(System.in).nextLine();
    }
    /* input methods end */

    /* file methods start*/
    private BufferedWriter bufferedWriterObject() throws IOException {
        return new BufferedWriter(new FileWriter("user_credential.txt", true));
    }

    private File bufferedReader(){
        return new File("user_credential.txt");
    }
    /* file methods end */

    /* helper methods start*/
    private String createUserId() throws FileNotFoundException {
        File file = bufferedReader();
        Scanner sc = new Scanner(file);
        int lastId = 0;
        while (sc.hasNextLine()){
            sc.nextLine();
            lastId++;
        }
        return String.valueOf(lastId );
    }

    private String singleString(String[] authUserData){
        StringBuilder stringUserData = new StringBuilder();
        for(String data: authUserData){
            stringUserData.append(data).append(" ");
        }
        return stringUserData.toString().trim();
    }

    private void changeBalance(int amount, int i, String operation) throws IOException {
        Path path = Paths.get("user_credential.txt");
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        int currentAmount = Integer.parseInt(authUserData[i]);
        if (Objects.equals(operation, "+")){
            authUserData[i] = String.valueOf(currentAmount + amount);
        } else{
            authUserData[i] = String.valueOf(currentAmount - amount);
        }
        String updatedBalance = singleString(authUserData);
        lines.set(Integer.parseInt(authUserData[0]), updatedBalance);
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    private int getAmount(){
        System.out.println("Enter Amount");
        return new Scanner(System.in).nextInt();
    }

    private void showAvailableBalance(int i){
        System.out.println("Your Available balance is: " + authUserData[i]);
    }

    private void menu(){
        String prompt = """
                Select the Account you want to access:
                Press 1: Current Account
                Press 2: Saving Account
                Press 3: Exit""";
        while (true){
            System.out.println(prompt);
            System.out.println("Enter your choice");
            String response = getStringInput();
            if (Objects.equals(response, "1")){
                currentAccount();
            } else if (Objects.equals(response, "2")){
                savingAccount();
            } else if (Objects.equals(response, "3")){
                System.exit(0);
            } else{
                System.out.println("Invalid Input!");
            }
        }

    }
    /* helper methods end */

    /* bank methods start*/
    private void currentAccount(){
        String prompt = """
                Current Account:
                Press 1: Available Balance
                Press 2: Withdraw Amount
                Press 3: Deposit Amount
                Press 4: Exit""";
        while (true){
            System.out.println(prompt);
            System.out.println("Enter your choice");
            String response = getStringInput();
            if (Objects.equals(response, "1")){
                showAvailableBalance(4);
            } else if (Objects.equals(response, "2")){
                int amount = getAmount();
                withdraw(amount, 4);
            } else if (Objects.equals(response, "3")){
                int amount = getAmount();
                deposit(amount, 4);
            } else if (Objects.equals(response, "4")){
                break;
            } else{
                System.out.println("Invalid Input!");
            }
        }

    }

    private void savingAccount(){
        String prompt = """
                Saving Account:
                Press 1: Available Balance
                Press 2: Withdraw Amount
                Press 3: Deposit Amount
                Press 4: Exit""";
        while (true) {
            System.out.println(prompt);
            System.out.println("Enter your choice");
            String response = getStringInput();
            if (Objects.equals(response, "1")) {
                showAvailableBalance(5);
            } else if (Objects.equals(response, "2")) {
                int amount = getAmount();
                withdraw(amount, 5);
            } else if (Objects.equals(response, "3")) {
                int amount = getAmount();
                deposit(amount, 5);
            } else if (Objects.equals(response, "4")){
                break;
            }  else {
                System.out.println("Invalid Input!");
            }
        }

    }

    private void withdraw(int amount, int i){
        try{
            int currentAmount = Integer.parseInt(authUserData[i]);
            if (currentAmount < amount){
                System.out.println("You don't have sufficient balance.");
                showAvailableBalance(i);
            } else{
                changeBalance(amount, i, "-");
                System.out.printf("%d withdraw\n", amount);
            }

        } catch (IOException e){
            System.out.println("An error occur! please try again later.");
        }
    }

    private void deposit(int amount, int i){
        try{
            changeBalance(amount, i, "+");
            System.out.printf("%d deposited\n", amount);

        } catch (IOException e){
            System.out.println("An error occur! please try again later.");
        }
    }
    /* bank methods end */

    /* main method start*/
    void startATM(){
        String prompt = """
                How  I can help you?:
                Press 1: Existing User
                Press 2: Create Account
                Press 3: Exit
                """;
        while (true){
            System.out.println(prompt);
            System.out.println("Enter your choice");
            String response = getStringInput();
            if (Objects.equals(response, "1")){
                authenticate();
            } else if (Objects.equals(response, "2")){
                createAccount();
            } else if (Objects.equals(response, "3")) {
                System.exit(0);
            } else{
                System.out.println("Invalid Input!");
            }
        }
    }
    /* main method end */


}

public class ATM {
    public static void main(String[] args){
        MyATM myATM = new MyATM();
        myATM.startATM();
    }
}
