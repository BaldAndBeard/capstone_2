package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;

import java.math.BigDecimal;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService();
    private final TransferService transferService = new TransferService();
    private final UserService userService = new UserService();

    Account recipientAccount = null;
    User recipient = null;

    private final Transfer transfer = new Transfer();


    private AuthenticatedUser currentUser;
    private Account userAccount;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {

            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        userAccount = accountService.getAccountbyUserID(currentUser.getUser().getId());



        // Obtain auth token from current user and assign it to the account service for communication
        String token = currentUser.getToken();
        if (token != null) {
            accountService.setAuthToken(token);
            transferService.setAuthToken(token);
            userService.setAuthToken(token);
        }

        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
		// TODO Auto-generated method stub
        System.out.println("**********************");
        System.out.println("Your current balance is: " + userAccount.getBalance());
        System.out.println("**********************");
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
        Transfer[] userTransfers = transferService.getAllTransfersByAccountID(userAccount.getId());

//        transfer.setCurrentUser(currentUser.getUser());
//        transfer.setUserAccount(userAccount);


        System.out.println("**********************");
        for (Transfer transfer : userTransfers) {
            System.out.println("Transfer: " +
                    "id = " + transfer.getTransferId() +
                    ", Sender = " + userService.getUserByAccountId(transfer.getAccountFrom()).getUsername() +
                    ", Receiver = " + userService.getUserByAccountId(transfer.getAccountTo()).getUsername() +
                    ", Amount = " + transfer.getAmount() +
                    ", Transfer Type = " + transfer.getTransferTypeId() +
                    ", Transfer Status = " + transfer.getTransferStatusId());
        }
        System.out.println("**********************");
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		Transfer[] pendingTransfers = transferService.getAllPendingTransfers();

        System.out.println("**********************");
        for (Transfer transfer : pendingTransfers) {
            System.out.println("Transfer{" +
                    "id = " + transfer.getTransferId() +
                    ", Sender = " + userService.getUserByAccountId(transfer.getAccountFrom()).getUsername() +
                    ", Receiver = " + userService.getUserByAccountId(transfer.getAccountTo()).getUsername() +
                    ", Amount = " + transfer.getAmount() +
                    ", Transfer Type = " + transfer.getTransferTypeId() +
                    ", Transfer Status = " + transfer.getTransferStatusId() +
                    '}');
        }

        System.out.println("**********************");

        // Get Transfer by the ID the User types in
        Transfer transferToUpdate = transferService.getTransfer(consoleService.promptForInt("Type the Transfer ID you want to Approve or Reject"));
        // Get the Account of the user that was requested
        recipientAccount = accountService.getAccountByID(transferToUpdate.getAccountFrom());

        // Prompt user to approve or reject the transfer
        int transferStatusId = consoleService.promptForInt("Type 2 to Approve, Type 3 to Reject");

        // If they approve the transfer, update both user's account balance and update the transfer
        if (transferStatusId == 2) {
            userAccount.setBalance(userAccount.getBalance().add(transferToUpdate.getAmount()));
            recipientAccount.setBalance(recipientAccount.getBalance().subtract(transferToUpdate.getAmount()));

            // Update accounts in database
            accountService.updateAccount(userAccount);
            accountService.updateAccount(recipientAccount);
            // Update Transfer
            transferToUpdate.setTransferStatusId(transferStatusId);
            transferService.updateTransfer(transferToUpdate);

        // If they reject the transfer, update the transfer
        } else if (transferStatusId == 3) {
            transferToUpdate.setTransferStatusId(transferStatusId);
            transferService.updateTransfer(transferToUpdate);
        }




        }

	private void sendBucks() {
		// TODO Auto-generated method stub
        Transfer newTransfer = new Transfer();

        User[] listOfUsers = userService.getUsers();
        System.out.println("Select user from the following list");

        System.out.println("**********************");
        for (User user : listOfUsers) {
            System.out.println("Transfer: " + user.getUsername());
        }
        System.out.println("**********************");
        String selectedUser = consoleService.promptForString("Type the user you want to send to: ");

        // Get Username of recipient
        // Get user id based on username
        // Get account id based on user id
        recipient = userService.getUserByUsername(selectedUser);
        transfer.setRecipient(recipient);
        recipientAccount = accountService.getAccountbyUserID(recipient.getId());

        // Assign values to the transfers
        newTransfer.setTransferStatusId(2);
        newTransfer.setTransferTypeId(2);
        newTransfer.setAmount(consoleService.promptForBigDecimal("Enter amount you want to send"));
        newTransfer.setAccountFrom(userAccount.getId());
        newTransfer.setAccountTo(recipientAccount.getId());

        // Create new transfer in database
        transferService.createTransfer(newTransfer);

        // Update Balance in affected Accounts
        userAccount.setBalance(userAccount.getBalance().subtract(newTransfer.getAmount()));
        recipientAccount.setBalance(recipientAccount.getBalance().add(newTransfer.getAmount()));

        // Update accounts in database
        accountService.updateAccount(userAccount);
        accountService.updateAccount(recipientAccount);
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
        Transfer newTransfer = new Transfer();

        User[] listOfUsers = userService.getUsers();
        System.out.println("Select user from the following list");
        System.out.println("**********************");
        for (User user : listOfUsers) {
            System.out.println("Transfer: " + user.getUsername());
        }
        System.out.println("**********************");
        String selectedUser = consoleService.promptForString("Type the user you want to request from: ");

        // Get Username of recipient
        // Get user id based on username
        // Get account id based on user id
        recipient = userService.getUserByUsername(selectedUser);
        transfer.setRecipient(recipient);
        recipientAccount = accountService.getAccountbyUserID(recipient.getId());

        // Assign values to the transfers
        // Assign Pending to transfer status id
        newTransfer.setTransferStatusId(1);
        // Assign Request to transfer type id
        newTransfer.setTransferTypeId(1);
        // Set the amount that you want to request
        newTransfer.setAmount(consoleService.promptForBigDecimal("Enter amount you want to request"));
        // Set the account that is being requested
        newTransfer.setAccountFrom(recipientAccount.getId());
        // Set the account that will receive money
        newTransfer.setAccountTo(userAccount.getId());

        // Create new transfer in database
        transferService.createTransfer(newTransfer);
	}



}
