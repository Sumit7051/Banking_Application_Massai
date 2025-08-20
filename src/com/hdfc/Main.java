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
            System.out.println("\n\t\t\t\t👋 Thank you for using HDFC Bank. Goodbye! 👋");
        }
    }

    private static void showMainMenu() {
        System.out.println("\n\n\t\t\t\t\t\t\t  📊 Main Menu 📊");
        System.out.println("\t\t\t\t\t\t\t-------------------");
        System.out.println("\t\t\t\t\t\t\t  1. Register New Customer");
        System.out.println("\t\t\t\t\t\t\t  2. Create Account");
        System.out.println("\t\t\t\t\t\t\t  3. Perform Transaction");
        System.out.println("\t\t\t\t\t\t\t  4. View Account Details");
        System.out.println("\t\t\t\t\t\t\t  5. View Transaction History");
        System.out.println("\t\t\t\t\t\t\t  6. Exit");
        System.out.print("\n\t\t\t\t\t\t\t  Enter your choice: ");

        int choice = getInput();
        System.out.println(); // Add a newline for better spacing
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
                System.out.println("\n\t\t\t\t\t\t  Exiting application...");
                System.exit(0);
                break;
            default:
                System.out.println("\t\t\t\t\t\t  ❌ Invalid choice. Please try again.");
        }
    }

    private static int getInput() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("\t\t\t\t\t\t  ⚠️ Please enter a valid number: ");
            }
        }
    }

    private static void registerCustomer() {
        System.out.println("\n\t\t\t\t\t\t\t👤 Customer Registration 👤");
        System.out.println("\t\t\t\t\t\t  ----------------------------------");

        System.out.print("\t\t\t\t\t\t  Enter Customer ID: ");
        String customerId = sc.nextLine().trim();

        if (customers.containsKey(customerId)) {
            System.out.println("\n\t\t\t\t\t\t  ⚠️ Customer with this ID already exists!");
            return;
        }

        System.out.print("\t\t\t\t\t\t  Enter Name: ");
        String name = sc.nextLine().trim();

        String email;
        while (true) {
            System.out.print("\t\t\t\t\t\t  Enter Email: ");
            email = sc.nextLine().trim();
            if (isValidEmail(email)) {
                break;
            } else {
                System.out.println("\t\t\t\t\t\t  ❌ Invalid email format. Please try again.");
            }
        }

        String phone;
        while (true) {
            System.out.print("\t\t\t\t\t\t  Enter Mobile Number: ");
            phone = sc.nextLine().trim();
            if (isValidPhoneNumber(phone)) {
                break;
            } else {
                System.out.println("\t\t\t\t\t\t  ❌ Invalid mobile number format. Please try again.");
            }
        }

        String password;
        while (true) {
            System.out.print("\t\t\t\t\t\t  Enter Password: ");
            password = sc.nextLine().trim();
            if (isValidPassword(password)) {
                break;
            } else {
                System.out.println("\t\t\t\t\t\t  ❌ Invalid password. Password must be 8-20 characters long and include at least one digit, one lowercase letter, one uppercase letter, and one special character (@#$%^&+=).");
            }
        }

        System.out.print("\t\t\t\t\t\t  Enter Date of Birth (yyyy-MM-dd): ");
        String dobStr = sc.nextLine().trim();

        LocalDate dateOfBirth;
        try {
            dateOfBirth = LocalDate.parse(dobStr, dateFormatter);
        } catch (Exception e) {
            System.out.println("\n\t\t\t\t\t\t  ❌ Invalid date format. Please use yyyy-MM-dd.");
            return;
        }

        Customer customer = new Customer(customerId, name, email, phone, password, dateOfBirth);
        customers.put(customerId, customer);

        System.out.println("\n\t\t\t\t\t\t  ✅ Customer Registered Successfully!");
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
        System.out.println("\n\t\t\t\t\t\t\t💳 Create New Account 💳");
        System.out.println("\t\t\t\t\t\t  ----------------------------------");

        System.out.print("\t\t\t\t\t\t  Enter Customer ID: ");
        String customerId = sc.nextLine().trim();
        Customer customer = customers.get(customerId);

        if (customer == null) {
            System.out.println("\n\t\t\t\t\t\t  ❌ Customer not found! Please register first.");
            return;
        }

        System.out.println("\n\t\t\t\t\t\t  Choose Account Type:");
        System.out.println("\t\t\t\t\t\t  1. Savings Account (6% interest, min. balance 1000)");
        System.out.println("\t\t\t\t\t\t  2. Current Account (4% interest, no min. balance)");
        System.out.print("\t\t\t\t\t\t  Enter your choice: ");

        int typeChoice = getInput();
        Account account;

        System.out.print("\t\t\t\t\t\t  Enter initial balance: ");
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
                    System.out.println("\n\t\t\t\t\t\t  ❌ Invalid account type!");
                    return;
            }

            accounts.put(accountNo, account);
            System.out.println("\n\t\t\t\t\t\t  ✅ Account created successfully!");
            System.out.println("\t\t\t\t\t\t  Your new account number is: \uD83D\uDD12 " + accountNo + " \uD83D\uDD12");
        } catch (NumberFormatException e) {
            System.out.println("\n\t\t\t\t\t\t  ❌ Invalid balance amount! Please enter a number.");
        }
    }

    private static String generateAccountNo() {
        return String.format("%010d", System.currentTimeMillis() % 10000000000L);
    }

    private static void performTransaction() {
        System.out.println("\n\t\t\t\t\t\t\t💰 Perform Transaction 💰");
        System.out.println("\t\t\t\t\t\t  ----------------------------------");
        System.out.println("\t\t\t\t\t\t  1. Deposit");
        System.out.println("\t\t\t\t\t\t  2. Withdraw");
        System.out.println("\t\t\t\t\t\t  3. Transfer");
        System.out.print("\t\t\t\t\t\t  Please select transaction type: ");

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
                System.out.println("\t\t\t\t\t\t  ❌ Invalid transaction type!");
        }
    }

    private static void performDeposit() {
        System.out.print("\t\t\t\t\t\t  Enter account number: ");
        String accountNo = sc.nextLine().trim();
        Account account = accounts.get(accountNo);

        if (account == null) {
            System.out.println("\n\t\t\t\t\t\t  ❌ Account not found!");
            return;
        }

        System.out.print("\t\t\t\t\t\t  Enter deposit amount: ");
        String amountStr = sc.nextLine().trim();
        try {
            BigDecimal amount = new BigDecimal(amountStr);
            account.deposit(amount);

            String transactionId = generateTransactionId();
            Transaction transaction = new Transaction(transactionId, amount, accountNo, LocalDateTime.now(), TransactionType.DEPOSIT);
            transactions.add(transaction);
            System.out.println("\n\t\t\t\t\t\t  ✅ Deposit successful! Your new balance is: ₹" + account.getBalance());
        } catch (NumberFormatException e) {
            System.out.println("\n\t\t\t\t\t\t  ❌ Invalid amount! Please enter a number.");
        }
    }

    private static String generateTransactionId() {
        return "HDFC_TXN" + System.currentTimeMillis();
    }

    private static void performWithdraw() {
        System.out.print("\t\t\t\t\t\t  Enter account number: ");
        String accountNo = sc.nextLine().trim();
        Account account = accounts.get(accountNo);

        if (account == null) {
            System.out.println("\n\t\t\t\t\t\t  ❌ Account not found!");
            return;
        }

        System.out.print("\t\t\t\t\t\t  Enter withdrawal amount: ");
        String amountStr = sc.nextLine().trim();
        try {
            BigDecimal amount = new BigDecimal(amountStr);
            account.withdraw(amount);
            String transactionId = generateTransactionId();
            Transaction transaction = new Transaction(transactionId, amount, accountNo, LocalDateTime.now(), TransactionType.WITHDRAW);
            transactions.add(transaction);
            System.out.println("\n\t\t\t\t\t\t  ✅ Withdrawal successful! Your new balance is: ₹" + account.getBalance());
        } catch (NumberFormatException e) {
            System.out.println("\n\t\t\t\t\t\t  ❌ Invalid amount!");
        } catch (InsufficientBalance b) {
            System.out.println("\n\t\t\t\t\t\t  ❌ Error: " + b.getMessage());
        }
    }

    private static void performTransfer() {
        System.out.print("\t\t\t\t\t\t  Enter your account number: ");
        String fromAccountNo = sc.nextLine().trim();
        Account fromAccount = accounts.get(fromAccountNo);
        if (fromAccount == null) {
            System.out.println("\n\t\t\t\t\t\t  ❌ Your account was not found!");
            return;
        }

        System.out.print("\t\t\t\t\t\t  Enter destination account number: ");
        String toAccountNo = sc.nextLine().trim();
        Account toAccount = accounts.get(toAccountNo);
        if (toAccount == null) {
            System.out.println("\n\t\t\t\t\t\t  ❌ Destination account was not found!");
            return;
        }

        System.out.print("\t\t\t\t\t\t  Enter transfer amount: ");
        String amountStr = sc.nextLine().trim();

        try {
            BigDecimal amount = new BigDecimal(amountStr);
            fromAccount.withdraw(amount);
            toAccount.deposit(amount);

            // Log transaction for the sender (Withdrawal to a related account)
            String senderTxnId = generateTransactionId();
            Transaction senderTxn = new Transaction(senderTxnId, amount, fromAccountNo, LocalDateTime.now(), TransactionType.WITHDRAW, toAccountNo);
            transactions.add(senderTxn);

            // Log transaction for the receiver (Deposit from a related account)
            String receiverTxnId = generateTransactionId();
            Transaction receiverTxn = new Transaction(receiverTxnId, amount, toAccountNo, LocalDateTime.now(), TransactionType.DEPOSIT, fromAccountNo);
            transactions.add(receiverTxn);

            System.out.println("\n\t\t\t\t\t\t  ✅ Transfer successful! Your new balance is: ₹" + fromAccount.getBalance());
        } catch (NumberFormatException e) {
            System.out.println("\n\t\t\t\t\t\t  ❌ Invalid amount!");
        } catch (InsufficientBalance e) {
            System.out.println("\n\t\t\t\t\t\t  ❌ Error: " + e.getMessage());
        }
    }

    private static void viewAccount() {
        System.out.println("\n\t\t\t\t\t\t\t  🔍 View Account Details 🔍");
        System.out.println("\t\t\t\t\t\t  -----------------------------------");
        System.out.print("\t\t\t\t\t\t  Enter account number: ");
        String accountNo = sc.nextLine().trim();
        Account account = accounts.get(accountNo);

        if (account == null) {
            System.out.println("\n\t\t\t\t\t\t  ❌ Account not found!");
            return;
        }

        System.out.println("\n\t\t\t\t\t\t  ----------------------------------------");
        System.out.println("\t\t\t\t\t\t  Account Number: " + account.getAccoutNo());
        System.out.println("\t\t\t\t\t\t  Customer ID: " + account.getCustomerId());
        System.out.println("\t\t\t\t\t\t  Account Type: " + (account instanceof SavingAccount ? "Savings Account" : "Current Account"));
        System.out.println("\t\t\t\t\t\t  Current Balance: ₹" + account.getBalance());
        System.out.println("\t\t\t\t\t\t  ----------------------------------------");
    }

    private static void viewHistory() {
        System.out.println("\n\t\t\t\t\t\t\t📜 Transaction History 📜");
        System.out.println("\t\t\t\t\t\t  -----------------------------------");
        System.out.print("\t\t\t\t\t\t  Enter account number: ");
        String accountNo = sc.nextLine().trim();
        Account account = accounts.get(accountNo);

        if (account == null) {
            System.out.println("\n\t\t\t\t\t\t  ❌ Account not found!");
            return;
        }

        List<Transaction> accountTransactions = transactions.stream()
                .filter(t -> t.getAccountNo().equals(accountNo) || (t.getToAccountNo() != null && t.getToAccountNo().equals(accountNo)))
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .toList();

        if (accountTransactions.isEmpty()) {
            System.out.println("\n\t\t\t\t\t\t  ℹ️ No transactions found for this account.");
            return;
        }

        System.out.println("\n\t\t\t\t\t\t  --- Transactions for Account " + accountNo + " ---");
        for (Transaction transaction : accountTransactions) {
            String typeIcon;
            String amountText;
            String fromToInfo = ""; // This variable is new to handle transfer details

            if (transaction.getType() == TransactionType.TRANSFER) {
                // If it's a transfer, we need to determine if it's an outgoing or incoming one
                if (transaction.getAccountNo().equals(accountNo)) {
                    // Outgoing transfer
                    typeIcon = "➡️";
                    amountText = "Transfer Out: -₹" + transaction.getAmount();
                    fromToInfo = "To Account: " + transaction.getToAccountNo();
                } else {
                    // Incoming transfer
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

            System.out.println("\n\t\t\t\t\t\t  " + typeIcon + " " + transaction.getType().getDisplayName().toUpperCase());
            System.out.println("\t\t\t\t\t\t  ----------------------------------");
            System.out.println("\t\t\t\t\t\t  Date/Time: " + transaction.getTimestamp().format(dateTimeFormatter));
            System.out.println("\t\t\t\t\t\t  Transaction ID: " + transaction.getTransactionId());
            System.out.println("\t\t\t\t\t\t  Amount: " + amountText);
            if (!fromToInfo.isEmpty()) {
                System.out.println("\t\t\t\t\t\t  " + fromToInfo);
            }
            System.out.println();
        }

        Map<TransactionType, Long> transactionSummary = accountTransactions.stream()
                .collect(Collectors.groupingBy(Transaction::getType, Collectors.counting()));

        System.out.println("\n\t\t\t\t\t\t  --- Transaction Summary ---");
        transactionSummary.forEach((transactionType, count) ->
                System.out.println("\t\t\t\t\t\t  " + transactionType.getDisplayName() + ": " + count + " transaction(s)"));
        System.out.println("\t\t\t\t\t\t  -----------------------------");
    }
}

