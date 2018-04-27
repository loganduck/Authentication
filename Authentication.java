import java.util.Scanner;
import java.io.File;
import java.util.Arrays;

/**
* Zoo authentication system is used to allow appropriate users access to
* data in the computer system. Granted access displays data accessible to
* the user.
* Instructions:
* 1. Enter username and password. Enter "quit" for both to exit login screen. Limited
* to three attempts before notifying user and exiting program.
* 2. Successful login will display the user access information until the user 
* enters "quit" to logout.
*
* @author Logan Duck (logan.duck@snhu.edu)
* @version 1.0
*/
public class Authentication {
	private static String username = "";
	private static String password = "";

	public static final int MAX_ATTEMPTS = 2;

	static File file = new File("credentials.txt");
	static File adminFile = new File("admin.txt");
	static File vetFile= new File("veterinarian.txt");
	static File zookeeperFile = new File("zookeeper.txt");

	/**
	* Access has been granted.
	*
	* @param accessGrantedIndex states the index in which access was granted within
	* valid usernames and passwords read from credentials file.
	*/
	public static void login(int accessGrantedIndex) throws Exception {
		Scanner scanFile = new Scanner(file);
		// storing in an array makes easy access of the role at last index
		String[] credentials = {};
		int index = 0;
		while (scanFile.hasNext()) {
			if (index == accessGrantedIndex) {
				credentials = scanFile.nextLine().split(" "); 
				break;
			}
			scanFile.nextLine();
			index += 1;
		}

		// splitting the array by a (" ") param ensures last index will always be role
		String role = credentials[credentials.length - 1];

		// break statements not neccessary when going to logout screen
		switch(role) {
			case "admin":
				scanFile = new Scanner(adminFile);
				while (scanFile.hasNext()) {
					System.out.println(scanFile.nextLine());
				}
				logoutMessage();

			case "veterinarian":
				scanFile = new Scanner(vetFile);
				while (scanFile.hasNext()) {
					System.out.println(scanFile.nextLine());
				}
				logoutMessage();
			
			case "zookeeper":
				scanFile = new Scanner(zookeeperFile);
				while (scanFile.hasNext()) {
					System.out.println(scanFile.nextLine());
				}
				logoutMessage();

			default: // last index of the credentials is an unauthorized user
				System.out.println("Unauthorized role. Access denied.");
				endProgram();
		}
	}

	/**
	* Continues to display the contents from the file until the user chooses
	* to end the program.
	*/
	public static void logoutMessage() {
		Scanner userInput = new Scanner(System.in);
		System.out.print("\nEnter 'q' to logout: ");
		String input = userInput.nextLine();
		
		while (!(input.equals("q"))) {
			System.out.println("Unknown input. Try again.");
			System.out.print("Enter 'q' to logout: ");
			input = userInput.nextLine();
		}
		System.out.println("\nYou are being logged out...");
		endProgram();
	}

	/**
	* Called whenever a program naturally ends.
	*/
	public static void endProgram() {
		System.out.println("\nProgram ended.");
		System.exit(1);
	}

	public static void main(String[] args) throws Exception {
		String username = "";
		String password = "";
		Scanner userInput = new Scanner(System.in);
		Scanner scanFile = new Scanner(file);

		// initial input for username and password
		System.out.println("Enter 'quit' in username & password to exit.");
		System.out.print("Username: ");
		username = userInput.nextLine();
		System.out.print("Password: ");
		password = userInput.nextLine();

		// exit program when username & password = "q"
		if (username.equals("quit") && password.equals("quit")) {
			endProgram();
		}
		
		// coverts the password using a MD5 hash
		MD5Digest md5 = new MD5Digest();
		password = md5.convertMD5(password);

		// creating separate arrays of valid usernames and passwords
		String[] validPasswords = new String[0];
		String[] validUsernames = new String[0];
		int credentialCount = 0; // used later when iterating over valid credentials
		while (scanFile.hasNext()) {
			String[] credentials = scanFile.nextLine().split(" ");
			
			String validUsername = credentials[0];
			String validPassword = credentials[1];
			
			validUsernames = Arrays.copyOf(validUsernames, validUsernames.length + 1);
			validPasswords = Arrays.copyOf(validPasswords, validPasswords.length + 1);
			
			validUsernames[credentialCount] = validUsername;
			validPasswords[credentialCount] = validPassword;
			
			credentialCount += 1;
		}

		int accessGrantedIndex = 0; // index where access would be granted
		boolean accessGranted = false;
		for (int limit = 0; limit <= MAX_ATTEMPTS; limit++) {
			for (int i = 0; i < credentialCount; i++) {
				if (username.equals(validUsernames[i]) && password.equals(validPasswords[i])) {
					accessGrantedIndex = i;
					accessGranted = true;
					break;
				}
			}

			if (accessGranted) {
				break;
			} else {
				System.out.println("Incorrect username or password. Please try again.\n");
				
				System.out.println("Enter 'quit' in username & password to exit.");
				System.out.print("Username: ");
				username = userInput.nextLine();
				System.out.print("Password: ");
				password = userInput.nextLine();

				if (username.equals("quit") && password.equals("quit")) {
					endProgram();
				}

				password = md5.convertMD5(password);
			}

			// Failing third attempt ends program
			if (limit == MAX_ATTEMPTS - 1) {
				System.out.println("\nToo many failed attempts.");
				endProgram();
			}
		}

		// access has been granted
		login(accessGrantedIndex);
	}
}