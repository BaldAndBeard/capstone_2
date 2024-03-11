package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService();
    private final TransferService transferService = new TransferService();
    private final UserService userService = new UserService();


    private final Transfer transfer = new Transfer();


    private AuthenticatedUser currentUser;
    private Account currentUserAccount;

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
            } else if (menuSelection == 0) {
                System.exit(1);
            }else if (menuSelection != 0) {
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

        String token = null;

        while (token == null) {
            try {
                UserCredentials credentials = consoleService.promptForCredentials();

                currentUser = authenticationService.login(credentials);
                currentUserAccount = accountService.getAccountbyUserID(currentUser.getUser().getId());

                // Obtain auth token from current user and assign it to the account service for communication
                token = currentUser.getToken();
            } catch (RestClientResponseException | ResourceAccessException | NullPointerException e) {
                System.out.println("Invalid Login");
            }
        }


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
                menuSelection = -1;
                currentUser = null;
                loginMenu();
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

    private void viewCurrentBalance() {
        // TODO Auto-generated method stub
        System.out.println("**********************");
        System.out.println("Your current balance is: " + currentUserAccount.getBalance());
        System.out.println("**********************");
    }

    private void viewTransferHistory() {
        // TODO Auto-generated method stub
        Transfer[] userTransfers = transferService.getAllTransfersByAccountID(currentUserAccount.getId());

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
        Account selectedUserAccount = null;
        User selectedUser = null;
        Transfer transferToUpdate = null;

        System.out.println("**********************");
        for (Transfer transfer : pendingTransfers) {
            if (transfer.getAccountFrom() == currentUserAccount.getId()) {
                System.out.println("Transfer{" +
                        "id = " + transfer.getTransferId() +
                        ", Requested User = " + userService.getUserByAccountId(transfer.getAccountFrom()).getUsername() +
                        ", Requesting User = " + userService.getUserByAccountId(transfer.getAccountTo()).getUsername() +
                        ", Amount = " + transfer.getAmount() +
                        ", Transfer Type = " + transfer.getTransferTypeId() +
                        ", Transfer Status = " + transfer.getTransferStatusId() +
                        '}');
            }
        }

        System.out.println("**********************");

        // Check for valid selection
        while (selectedUserAccount == null) {

            // Get Transfer by the ID the User types in
            int transferToUpdateID = consoleService.promptForInt("Type the Transfer ID you want to Approve or Reject, or Type 0 to Exit: ");

            if (transferToUpdateID == 0) {
                break;
            }

            transferToUpdate = transferService.getTransfer(transferToUpdateID);

            // Loop through pendingTransfers to check against transferToUpdate
            for (Transfer transfer : pendingTransfers) {

                // Check for transfer in loop against transferToUpdate
                if (transfer.getTransferId() == transferToUpdateID) {

                    // Check for null value
                    try {
                        // Get the Account of the user requested funds2
                        selectedUserAccount = accountService.getAccountByID(transferToUpdate.getAccountTo());


                    } catch (NullPointerException e) {
                        System.out.println("Invalid Selection");

                    }
                }
            }
            // Print Warning to User is Account was not properly selected
            if (selectedUserAccount == null) {
                System.out.println("Invalid Selection");
            }
        }

        if (selectedUserAccount != null) {

            int transferStatusId = -1;

            while (transferStatusId == -1) {

                // Prompt user to approve or reject the transfer
                transferStatusId = consoleService.promptForInt("Type 2 to Approve, Type 3 to Reject, Type 0 to Exit: ");

                if (transferStatusId == 2 || transferStatusId == 3 || transferStatusId == 0) {

                    // If they approve the transfer
                    if (transferStatusId == 2) {

                        // Compare the amount requested against current user's balance and reset loop if insufficient
                        BigDecimal amountToSend = transferToUpdate.getAmount();
                        if (amountToSend.compareTo(currentUserAccount.getBalance()) > 0) {
                            System.out.println("Insufficient Funds");
                            continue;
                        }

                        // Update both user's account balance and update the transfer
                        currentUserAccount.setBalance(currentUserAccount.getBalance().subtract(transferToUpdate.getAmount()));
                        selectedUserAccount.setBalance(selectedUserAccount.getBalance().add(transferToUpdate.getAmount()));

                        // Update accounts in database
                        accountService.updateAccount(currentUserAccount);
                        accountService.updateAccount(selectedUserAccount);
                        // Update Transfer
                        transferToUpdate.setTransferStatusId(transferStatusId);
                        transferService.updateTransfer(transferToUpdate);

                        break;

                        // If they reject the transfer, update the transfer
                    } else if (transferStatusId == 3) {
                        transferToUpdate.setTransferStatusId(transferStatusId);
                        transferService.updateTransfer(transferToUpdate);

                        break;

                    } else if (transferStatusId == 0) {
                        break;
                    }
                }
                System.out.println("Invalid Selection");
                transferStatusId = -1;
            }
        }
    }

    private void sendBucks() {
        // TODO Auto-generated method stub
        Transfer newTransfer = new Transfer();
        Account selectedUserAccount = null;
        User selectedUser = null;

        User[] listOfUsers = userService.getUsers();
        System.out.println("Select user from the following list: ");

        System.out.println("**********************");
        for (User user : listOfUsers) {
            System.out.println("Transfer: " + user.getUsername());
        }
        System.out.println("**********************");

        while (selectedUser == null) {
            String userString = consoleService.promptForString("Type the name of the user you want to send to, or Type 0 to exit: ");

            if (userString.equals("0")) {
                break;
            }

            if (!userString.equals(currentUser.getUser().getUsername())) {
                // Get Username of recipient
                // Get user id based on username
                // Get account id based on user id
                try {

                    selectedUser = userService.getUserByUsername(userString);
                    selectedUserAccount = accountService.getAccountbyUserID(selectedUser.getId());

                } catch (NullPointerException e) {
                    System.out.println("Invalid Selection");
                }
            } else {
                System.out.println("You cannnot send money to yourself");
            }
        }

        if (selectedUserAccount != null) {

            BigDecimal amountToSend = null;

            while (amountToSend == null) {

                amountToSend = consoleService.promptForBigDecimal("Enter amount you want to send: ");

                if (amountToSend.compareTo(new BigDecimal("0")) > 0) {

                    if (amountToSend.compareTo(currentUserAccount.getBalance()) > 0) {
                        System.out.println("Insufficient Funds");
                        amountToSend = null;
                    }

                } else {
                    System.out.println("You must enter an amount greater than 0");
                    amountToSend = null;
                }
            }

            // Assign values to the transfers
            newTransfer.setTransferStatusId(2);
            newTransfer.setTransferTypeId(2);
            newTransfer.setAmount(amountToSend);
            newTransfer.setAccountFrom(currentUserAccount.getId());
            newTransfer.setAccountTo(selectedUserAccount.getId());

            // Create new transfer in database
            transferService.createTransfer(newTransfer);

            // Update Balance in affected Accounts
            currentUserAccount.setBalance(currentUserAccount.getBalance().subtract(newTransfer.getAmount()));
            selectedUserAccount.setBalance(selectedUserAccount.getBalance().add(newTransfer.getAmount()));

            // Update accounts in database
            accountService.updateAccount(currentUserAccount);
            accountService.updateAccount(selectedUserAccount);

        }
    }

    private void requestBucks() {
        // TODO Auto-generated method stub
        Transfer newTransfer = new Transfer();
        Account selectedUserAccount = null;
        User selectedUser = null;

        User[] listOfUsers = userService.getUsers();
        System.out.println("Select user from the following list: ");
        System.out.println("**********************");
        for (User user : listOfUsers) {
            System.out.println("Transfer: " + user.getUsername());
        }
        System.out.println("**********************");
        String userString = null;

        while (selectedUser == null) {

            userString = consoleService.promptForString("Type the user you want to request from or type 0 to exit: ");

            if (userString.equals("0")) {
                break;
            }

            if (!userString.equals(currentUser.getUser().getUsername())) {

                for (User user : listOfUsers) {

                    if (userString.equals(user.getUsername())) {

                        // Get Username of recipient
                        // Get user id based on username
                        // Get account id based on user id
                        selectedUser = userService.getUserByUsername(userString);
                        selectedUserAccount = accountService.getAccountbyUserID(selectedUser.getId());

                        BigDecimal amountToRequest = null;
                        while (amountToRequest == null) {
                            amountToRequest = consoleService.promptForBigDecimal("Enter amount you want to request: ");

                            if (amountToRequest.compareTo(new BigDecimal("0")) <= 0) {
                                System.out.println("You must request an amount greater than 0");
                                amountToRequest = null;
                            }

                        }
                        // Assign values to the transfers
                        // Assign Pending to transfer status id
                        newTransfer.setTransferStatusId(1);
                        // Assign Request to transfer type id
                        newTransfer.setTransferTypeId(1);
                        // Set the amount that you want to request
                        newTransfer.setAmount(amountToRequest);
                        // Set the account that is being requested
                        newTransfer.setAccountFrom(selectedUserAccount.getId());
                        // Set the account that will receive money
                        newTransfer.setAccountTo(currentUserAccount.getId());

                        // Create new transfer in database
                        transferService.createTransfer(newTransfer);

                        break;
                    }
                }
            } else {
                System.out.println("Invalid Selection");
            }
        }
    }
}
