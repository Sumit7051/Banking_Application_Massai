package com.hdfc;

import com.hdfc.Entities.*;
import com.hdfc.Exception.InsufficientBalance;
import com.hdfc.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    private static final Map<String, Customer> customers = new HashMap<>();
    private static final Map<String, Account> accounts = new HashMap<>();
    private static List<Transaction> transactions = new ArrayList<>();
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final Scanner sc = new Scanner(System.in);
    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);

    // Regex Patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^[+]?[0-9]{10,13}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$");

    public static void main(String[] args) {
        System.out.println("\n\t\t\t\t✨ Welcome to HDFC Banking Application ✨");
        System.out.println("\t\t\t\t---------------------------------------");

        try {
            while (true) {
                showMainMenu();
            }
        } catch (Exception e) {
            System.out.println("\n\t❌ An unexpected error occurred: " + e.getMessage());
        } finally {
            sc.close();
            executorService.shutdown();
            System.out.println("\n\t\t\t\t👋 Thank you for using HDFC Bank. Goodbye! 👋");
        }
    }

    private static void showMainMenu() {
        System.out.println("Main Menu 📊");
        System.out.println("-------------------");
        System.out.println("1. Register New Customer");
        System.out.println("2. Create Account");
        System.out.println("3. Perform Transaction");
        System.out.println("4. View Account Details");
        System.out.println("5. View Transaction History");
        System.out.println("6. Simulate Concurrent Transfers");
        System.out.println("7. Run Demo Mode");
        System.out.println("8. Exit");
        System.out.print("Enter your choice: ");

        int choice = getInput();
        System.out.println();
        switch (choice) {
            case 1:
                registerCustomer();
                break;
            case 2:
                createAccount();
                break;
            case 3:
                performTransaction();
                break;
            case 4:
                viewAccount();
                break;
            case 5:
                viewHistory();
                break;
            case 6:
                simulateConcurrentTransfers();
                break;
            case 7:
                runDemoMode();
                break;
            case 8:
                System.out.println("Exiting application...");
                System.exit(0);
                break;
            default:
                System.out.println(" ❌ Invalid choice. Please try again.");
        }
    }

    private static int getInput() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("⚠️ Please enter a valid number: ");
            }
        }
    }

    private static void registerCustomer() {
        System.out.println("👤 Customer Registration 👤");
        System.out.println("----------------------------------");

        System.out.print("Enter Customer ID: ");
        String customerId = sc.nextLine().trim();

        if (customers.containsKey(customerId)) {
            System.out.println(" ⚠️ Customer with this ID already exists!");
            return;
        }

        System.out.print("Enter Name: ");
        String name = sc.nextLine().trim();

        String email;
        while (true) {
            System.out.print("Enter Email: ");
            email = sc.nextLine().trim();
            if (isValidEmail(email)) {
                break;
            } else {
                System.out.println(" ❌ Invalid email format. Please try again.");
            }
        }

        String phone;
        while (true) {
            System.out.print("Enter Mobile Number: ");
            phone = sc.nextLine().trim();
            if (isValidPhoneNumber(phone)) {
                break;
            } else {
                System.out.println("❌ Invalid mobile number format. Please try again.");
            }
        }

        String password;
        while (true) {
            System.out.print("Enter Password: ");
            password = sc.nextLine().trim();
            if (isValidPassword(password)) {
                break;
            } else {
                System.out.println("❌ Invalid password. Password must be 8-20 characters long and include at least one digit, one lowercase letter, one uppercase letter, and one special character (@#$%^&+=).");
            }
        }

        System.out.print(" Enter Date of Birth (yyyy-MM-dd): ");
        String dobStr = sc.nextLine().trim();

        LocalDate dateOfBirth;
        try {
            dateOfBirth = LocalDate.parse(dobStr, dateFormatter);
        } catch (Exception e) {
            System.out.println("❌ Invalid date format. Please use yyyy-MM-dd.");
            return;
        }

        Customer customer = new Customer(customerId, name, email, phone, password, dateOfBirth);
        customers.put(customerId, customer);

        System.out.println("✅ Customer Registered Successfully!");
    }

    private static boolean isValidEmail(String email) {
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    private static boolean isValidPhoneNumber(String phoneNumber) {
        Matcher matcher = PHONE_NUMBER_PATTERN.matcher(phoneNumber);
        return matcher.matches();
    }

    private static boolean isValidPassword(String password) {
        Matcher matcher = PASSWORD_PATTERN.matcher(password);
        return matcher.matches();
    }

    private static void createAccount() {
        System.out.println("💳 Create New Account 💳");
        System.out.println("----------------------------------");

        System.out.print("Enter Customer ID: ");
        String customerId = sc.nextLine().trim();
        Customer customer = customers.get(customerId);

        if (customer == null) {
            System.out.println("❌ Customer not found! Please register first.");
            return;
        }

        System.out.println("Choose Account Type:");
        System.out.println("1. Savings Account (4.5% interest, min. balance 1000)");
        System.out.println("2. Current Account (0% interest, no min. balance)");
        System.out.print("Enter your choice: ");

        int typeChoice = getInput();
        Account account;

        System.out.print("Enter initial balance: ");
        String balanceStr = sc.nextLine().trim();

        try {
            BigDecimal initialBalance = new BigDecimal(balanceStr);
            String accountNo = generateAccountNo();

            switch (typeChoice) {
                case 1:
                    account = new SavingAccount(accountNo, customerId, initialBalance);
                    break;
                case 2:
                    account = new CurrentAccount(accountNo, customerId, initialBalance);
                    break;
                default:
                    System.out.println("❌ Invalid account type!");
                    return;
            }

            accounts.put(accountNo, account);
            System.out.println("✅ Account created successfully!");
            System.out.println("Your new account number is: \uD83D\uDD12 " + accountNo + " \uD83D\uDD12");
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid balance amount! Please enter a number.");
        }
    }

    private static String generateAccountNo() {
        return String.format("%010d", System.currentTimeMillis() % 10000000000L);
    }

    private static void performTransaction() {
        System.out.println("💰 Perform Transaction 💰");
        System.out.println(" ----------------------------------");
        System.out.println("1. Deposit");
        System.out.println("2. Withdraw");
        System.out.println("3. Transfer");
        System.out.print("Please select transaction type: ");

        int transactionType = getInput();
        System.out.println();

        switch (transactionType) {
            case 1:
                performDeposit();
                break;
            case 2:
                performWithdraw();
                break;
            case 3:
                performTransfer();
                break;
            default:
                System.out.println("❌ Invalid transaction type!");
        }
    }

    private static void performDeposit() {
        System.out.print("Enter account number: ");
        String accountNo = sc.nextLine().trim();
        Account account = accounts.get(accountNo);

        if (account == null) {
            System.out.println("❌ Account not found!");
            return;
        }

        System.out.print("Enter deposit amount: ");
        String amountStr = sc.nextLine().trim();
        try {
            BigDecimal amount = new BigDecimal(amountStr);
            account.deposit(amount);

            String transactionId = generateTransactionId();
            Transaction transaction = new Transaction(transactionId, amount, accountNo, LocalDateTime.now(), TransactionType.DEPOSIT);
            transactions.add(transaction);
            System.out.println("✅ Deposit successful! Your new balance is: ₹" + account.getBalance());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid amount! Please enter a number.");
        }
    }

    private static String generateTransactionId() {
        return "HDFC_TXN" + System.currentTimeMillis();
    }

    private static void performWithdraw() {
        System.out.print("Enter account number: ");
        String accountNo = sc.nextLine().trim();
        Account account = accounts.get(accountNo);

        if (account == null) {
            System.out.println("❌ Account not found!");
            return;
        }

        System.out.print("Enter withdrawal amount: ");
        String amountStr = sc.nextLine().trim();
        try {
            BigDecimal amount = new BigDecimal(amountStr);
            account.withdraw(amount);
            String transactionId = generateTransactionId();
            Transaction transaction = new Transaction(transactionId, amount, accountNo, LocalDateTime.now(), TransactionType.WITHDRAW);
            transactions.add(transaction);
            System.out.println(" ✅ Withdrawal successful! Your new balance is: ₹" + account.getBalance());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid amount!");
        } catch (InsufficientBalance b) {
            System.out.println("❌ Error: " + b.getMessage());
        }
    }

    private static void performTransfer() {
        System.out.print("Enter your account number: ");
        String fromAccountNo = sc.nextLine().trim();
        Account fromAccount = accounts.get(fromAccountNo);
        if (fromAccount == null) {
            System.out.println("❌ Your account was not found!");
            return;
        }

        System.out.print("Enter destination account number: ");
        String toAccountNo = sc.nextLine().trim();
        Account toAccount = accounts.get(toAccountNo);
        if (toAccount == null) {
            System.out.println("❌ Destination account was not found!");
            return;
        }

        System.out.print("Enter transfer amount: ");
        String amountStr = sc.nextLine().trim();

        try {
            BigDecimal amount = new BigDecimal(amountStr);
            fromAccount.withdraw(amount);
            toAccount.deposit(amount);

            String senderTxnId = generateTransactionId();
            Transaction senderTxn = new Transaction(senderTxnId, amount, fromAccountNo, LocalDateTime.now(), TransactionType.WITHDRAW, toAccountNo);
            transactions.add(senderTxn);

            String receiverTxnId = generateTransactionId();
            Transaction receiverTxn = new Transaction(receiverTxnId, amount, toAccountNo, LocalDateTime.now(), TransactionType.DEPOSIT, fromAccountNo);
            transactions.add(receiverTxn);

            System.out.println("✅ Transfer successful! Your new balance is: ₹" + fromAccount.getBalance());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid amount!");
        } catch (InsufficientBalance e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private static void viewAccount() {
        System.out.println("🔍 View Account Details 🔍");
        System.out.println("-----------------------------------");
        System.out.print("Enter account number: ");
        String accountNo = sc.nextLine().trim();
        Account account = accounts.get(accountNo);

        if (account == null) {
            System.out.println("❌ Account not found!");
            return;
        }

        System.out.println("----------------------------------------");
        System.out.println("Account Number: " + account.getAccoutNo());
        System.out.println("Customer ID: " + account.getCustomerId());
        System.out.println("Account Type: " + (account instanceof SavingAccount ? "Savings Account" : "Current Account"));
        System.out.println("Current Balance: ₹" + account.getBalance());
        System.out.println(" ----------------------------------------");
    }

    private static void viewHistory() {
        System.out.println("📜 Transaction History 📜");
        System.out.println("-----------------------------------");
        System.out.print("Enter account number: ");
        String accountNo = sc.nextLine().trim();
        Account account = accounts.get(accountNo);

        if (account == null) {
            System.out.println("❌ Account not found!");
            return;
        }

        List<Transaction> accountTransactions = transactions.stream()
                .filter(t -> t.getAccountNo().equals(accountNo) || (t.getToAccountNo() != null && t.getToAccountNo().equals(accountNo)))
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .toList();

        if (accountTransactions.isEmpty()) {
            System.out.println("ℹ️ No transactions found for this account.");
            return;
        }

        System.out.println("--- Transactions for Account " + accountNo + " ---");
        for (Transaction transaction : accountTransactions) {
            String typeIcon;
            String amountText;
            String fromToInfo = "";

            if (transaction.getType() == TransactionType.TRANSFER) {
                if (transaction.getAccountNo().equals(accountNo)) {
                    typeIcon = "➡️";
                    amountText = "Transfer Out: -₹" + transaction.getAmount();
                    fromToInfo = "To Account: " + transaction.getToAccountNo();
                } else {
                    typeIcon = "⬅️";
                    amountText = "Transfer In: +₹" + transaction.getAmount();
                    fromToInfo = "From Account: " + transaction.getAccountNo();
                }
            } else {
                switch (transaction.getType()) {
                    case DEPOSIT:
                        typeIcon = "✅";
                        amountText = "Deposit: +₹" + transaction.getAmount();
                        break;
                    case WITHDRAW:
                        typeIcon = "➖";
                        amountText = "Withdrawal: -₹" + transaction.getAmount();
                        break;
                    default:
                        typeIcon = "❔";
                        amountText = "Amount: ₹" + transaction.getAmount();
                        break;
                }
            }

            System.out.println("" + typeIcon + " " + transaction.getType().getDisplayName().toUpperCase());
            System.out.println(" ----------------------------------");
            System.out.println("Date/Time: " + transaction.getTimestamp().format(dateTimeFormatter));
            System.out.println("Transaction ID: " + transaction.getTransactionId());
            System.out.println("\t\t\t\t\t\t  Amount: " + amountText);
            if (!fromToInfo.isEmpty()) {
                System.out.println("/t" + fromToInfo);
            }
            System.out.println();
        }

        Map<TransactionType, Long> transactionSummary = accountTransactions.stream()
                .collect(Collectors.groupingBy(Transaction::getType, Collectors.counting()));

        System.out.println("--- Transaction Summary ---");
        transactionSummary.forEach((transactionType, count) ->
                System.out.println("" + transactionType.getDisplayName() + ": " + count + " transaction(s)"));
        System.out.println("-----------------------------");
    }

    private static void simulateConcurrentTransfers() {
        System.out.println("Simulating Concurrent Transfers");
        System.out.println("------------------------------------");

        if (accounts.size() < 2) {
            System.out.println("❌ Need at least two accounts to simulate transfers.");
            return;
        }

        // Get two random accounts to transfer between
        List<Account> availableAccounts = new ArrayList<>(accounts.values());
        Account acc1 = availableAccounts.get(0);
        Account acc2 = availableAccounts.get(1);

        System.out.println("Transferring between accounts " + acc1.getAccoutNo() + " and " + acc2.getAccoutNo());
        System.out.println("Initial Balance 1: ₹" + acc1.getBalance());
        System.out.println("Initial Balance 2: ₹" + acc2.getBalance());

        List<Future<Boolean>> futures = new ArrayList<>();
        int numberOfTransfers = 5;

        for (int i = 0; i < numberOfTransfers; i++) {
            final int transferAmount = 100 + i * 10;
            // Transfer from acc1 to acc2
            futures.add(executorService.submit(() -> {
                try {
                    acc1.withdraw(BigDecimal.valueOf(transferAmount));
                    acc2.deposit(BigDecimal.valueOf(transferAmount));
                    System.out.println("✓ Thread " + Thread.currentThread().getId() + ": Transfer of ₹" + transferAmount + " from " + acc1.getAccoutNo() + " to " + acc2.getAccoutNo() + " successful.");
                    return true;
                } catch (InsufficientBalance | InvalidAccountException e) {
                    System.out.println("❌ Thread " + Thread.currentThread().getId() + ": Transfer failed - " + e.getMessage());
                    return false;
                }
            }));
        }


        System.out.println("Waiting for all transfers to complete...");
        try {
            for (Future<Boolean> future : futures) {
                future.get();
            }
        } catch (Exception e) {
            System.out.println("An error occurred during concurrent transfers: " + e.getMessage());
        }

        System.out.println("✓ All transfers completed.");
        System.out.println("Final Balance 1: ₹" + acc1.getBalance());
        System.out.println("Final Balance 2: ₹" + acc2.getBalance());
    }

    private static void runDemoMode() {
        System.out.println("Demo Mode - Complete Banking Flow 🎮");
        System.out.println("=---------------------------------------");

        System.out.println("1. Registering customers...");
        registerDemoCustomers();
        System.out.println("✓ Customers registered successfully.");

        System.out.println("2. Creating accounts...");
        createDemoAccounts();
        System.out.println("✓ Accounts created successfully.");

        System.out.println("3. Performing transactions...");
        performDemoTransactions();
        System.out.println("✓ Demo transactions completed.");

        System.out.println("4. Viewing account details...");
        displayAccountDetails();
        System.out.println("✓ Account details displayed.");

        System.out.println("5. Viewing transaction history (using Java 8 Streams)...");
        viewDemoHistory();
        System.out.println("✓ Transaction history displayed.");

        System.out.println("6. Demonstrating polymorphism...");
        displayDemo();
        System.out.println("✓ Polymorphism demonstrated.");

        System.out.println("7. Simulating concurrent transfers...");
        simulateConcurrentTransfers();
        System.out.println("✓ Concurrent transfers simulation completed.");

        System.out.println("=== Demo completed successfully! ===");
    }

    private static void registerDemoCustomers() {
        customers.put("CUST001", new Customer("CUST001", "Alok Sharma", "alok@hdfc.com", "9876543210", "Alok@123", LocalDate.of(1990, 5, 15)));
        customers.put("CUST002", new Customer("CUST002", "Priya Singh", "priya@hdfc.com", "9988776655", "Priya#456", LocalDate.of(1985, 8, 20)));
    }

    private static void createDemoAccounts() {
        String accNo1 = "SAVINGS_1";
        String accNo2 = "CURRENT_2";

        Account savingsAccount = new SavingAccount(accNo1, "CUST001", new BigDecimal("2500.00"));
        Account currentAccount = new CurrentAccount(accNo2, "CUST002", new BigDecimal("5000.00"));

        accounts.put(accNo1, savingsAccount);
        accounts.put(accNo2, currentAccount);
    }

    private static void performDemoTransactions() {

        transactions.clear();

        Account savingsAcc = accounts.get("SAVINGS_1");
        Account currentAcc = accounts.get("CURRENT_2");

        try {
            // Deposit
            savingsAcc.deposit(new BigDecimal("1000"));
            transactions.add(new Transaction(generateTransactionId(), new BigDecimal("1000"), savingsAcc.getAccoutNo(), LocalDateTime.now(), TransactionType.DEPOSIT));
            System.out.println("✓ Deposit successful: +₹1000 to " + savingsAcc.getAccoutNo());

            // Withdraw
            currentAcc.withdraw(new BigDecimal("500"));
            transactions.add(new Transaction(generateTransactionId(), new BigDecimal("500"), currentAcc.getAccoutNo(), LocalDateTime.now(), TransactionType.WITHDRAW));
            System.out.println("✓ Withdrawal successful: -₹500 from " + currentAcc.getAccoutNo());

            // Transfer
            BigDecimal transferAmount = new BigDecimal("800");
            savingsAcc.withdraw(transferAmount);
            currentAcc.deposit(transferAmount);
            transactions.add(new Transaction(generateTransactionId(), transferAmount, savingsAcc.getAccoutNo(), LocalDateTime.now(), TransactionType.TRANSFER, currentAcc.getAccoutNo()));
            transactions.add(new Transaction(generateTransactionId(), transferAmount, currentAcc.getAccoutNo(), LocalDateTime.now(), TransactionType.TRANSFER, savingsAcc.getAccoutNo()));
            System.out.println("✓ Transfer successful: ₹" + transferAmount + " from " + savingsAcc.getAccoutNo() + " to " + currentAcc.getAccoutNo());

        } catch (InsufficientBalance | InvalidAccountException e) {
            System.out.println("❌ Demo transaction failed: " + e.getMessage());
        }
    }

    private static void viewDemoHistory() {
        viewHistoryForAccount("SAVINGS_1");
        viewHistoryForAccount("CURRENT_2");
    }

    private static void viewHistoryForAccount(String accountNo) {
        System.out.println("--- Transaction History for Account " + accountNo + " ---");
        List<Transaction> accountTransactions = transactions.stream()
                .filter(t -> t.getAccountNo().equals(accountNo) || (t.getToAccountNo() != null && t.getToAccountNo().equals(accountNo)))
                .sorted(Comparator.comparing(Transaction::getTimestamp))
                .toList();

        if (accountTransactions.isEmpty()) {
            System.out.println("ℹ️ No transactions found.");
            return;
        }

        for (Transaction transaction : accountTransactions) {
            String typeIcon = "";
            String amountText = "";
            String fromToInfo = "";

            if (transaction.getType() == TransactionType.TRANSFER) {
                if (transaction.getAccountNo().equals(accountNo)) {
                    typeIcon = "➡️";
                    amountText = "Transfer Out: -₹" + transaction.getAmount();
                    fromToInfo = "To: " + transaction.getToAccountNo();
                } else {
                    typeIcon = "⬅️";
                    amountText = "Transfer In: +₹" + transaction.getAmount();
                    fromToInfo = "From: " + transaction.getAccountNo();
                }
            } else {
                switch (transaction.getType()) {
                    case DEPOSIT:
                        typeIcon = "✅";
                        amountText = "Deposit: +₹" + transaction.getAmount();
                        break;
                    case WITHDRAW:
                        typeIcon = "➖";
                        amountText = "Withdrawal: -₹" + transaction.getAmount();
                        break;
                    default:
                        typeIcon = "❔";
                        amountText = "Amount: ₹" + transaction.getAmount();
                }
            }

            System.out.println("" + typeIcon + " " + transaction.getType().getDisplayName() + " of " + amountText + " at " + transaction.getTimestamp().format(dateTimeFormatter) + (fromToInfo.isEmpty() ? "" : " (" + fromToInfo + ")"));
        }
    }

    private static void displayAccountDetails() {
        accounts.values().forEach(account -> {
            System.out.println("--- " + (account instanceof SavingAccount ? "Savings Account" : "Current Account") + " ---");
            System.out.println("Account Number: " + account.getAccoutNo());
            System.out.println("Current Balance: ₹" + account.getBalance());
        });
    }

    private static void displayDemo() {
        Account savingsAccount = new SavingAccount("POLY_SAVINGS", "CUST_POLY", new BigDecimal("1000"));
        Account currentAccount = new CurrentAccount("POLY_CURRENT", "CUST_POLY", new BigDecimal("1000"));

        BigDecimal interest1 = savingsAccount.calculateInterest();
        BigDecimal interest2 = currentAccount.calculateInterest();

        System.out.println("Savings Account Interest Rate: 4.5%, Min Balance: ₹1000");
        System.out.println("Current Account Interest Rate: 0%, Min Balance: ₹0");
        System.out.println("Account 'POLY_SAVINGS' (Savings): Calculated Interest: ₹" + interest1);
        System.out.println("Account 'POLY_CURRENT' (Current): Calculated Interest: ₹" + interest2);

        try {
            System.out.println("Attempting to withdraw ₹500 from 'POLY_SAVINGS'...");
            savingsAccount.withdraw(new BigDecimal("500"));
            System.out.println("✓ Withdrawal successful. New balance: ₹" + savingsAccount.getBalance());
        } catch (InsufficientBalance e) {
            System.out.println("❌ Withdrawal failed: " + e.getMessage());
        }

        try {
            System.out.println("Attempting to withdraw ₹500 from 'POLY_CURRENT'...");
            currentAccount.withdraw(new BigDecimal("500"));
            System.out.println("✓ Withdrawal successful. New balance: ₹" + currentAccount.getBalance());
        } catch (InsufficientBalance e) {
            System.out.println("❌ Withdrawal failed: " + e.getMessage());
        }
    }
}
