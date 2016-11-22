import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

public class MySign {
    public static void main(String [] args) throws FileNotFoundException {

        //checks the command prompt flags and calls the s or v method or it closes if neither of those flags are called
        if(args[0].equals("s"))
			sign(args[1]);
		else if (args[0].equals("v"))
			verify(args[1]);
		else
			System.out.println("Sorry, incorrect flag chosen.");
    }

    //Method called when the s flag is chosen
    public static void sign(String file) throws FileNotFoundException {
        File test = new File("privkey.rsa");

        //This if statement checks to see if the private key RSA file has been created yet. Exits programs if it hasn't
        if (test.exists()) {
            try {
                // read in the file to hash
                Path path = Paths.get(file);
                byte[] data = Files.readAllBytes(path);

                // create class instance to create SHA-256 hash
                MessageDigest md = MessageDigest.getInstance("SHA-256");

                // process the file
                md.update(data);
                // generate a hash of the file
                byte[] digest = md.digest();

                // convert the bit string to a BitInteger representing the hash
                BigInteger hash = new BigInteger(1, digest);

                //Reads the d and n values from the private key RSA file
                FileInputStream priv = new FileInputStream("privkey.rsa");
                ObjectInputStream privIn = new ObjectInputStream(priv);
                BigInteger d = (BigInteger) privIn.readObject();
                BigInteger n = (BigInteger) privIn.readObject();
                privIn.close();

                //Calculates decrypted value
                BigInteger decrypted = hash.modPow(d, n);

                //Writes out the original file plus the decrypted value to a new file with .signed appended
                String newName = file + ".signed";
                FileOutputStream updatedFile = new FileOutputStream(newName);
                ObjectOutputStream updated = new ObjectOutputStream(updatedFile);
                updated.writeObject(data);
                updated.writeObject(decrypted);
                updated.close();

            }
            catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        //Closes the program if no private key is found at the beginning of the method
        else {
            System.out.println("Private Key is not accessible. Program now closing.");
        }
    }
    public static void verify(String file) throws FileNotFoundException{
        File test = new File("pubkey.rsa");

        //This if statement checks to see if the public key RSA file has been created yet. Exits programs if it hasn't
        if(test.exists()) {
            try {
                // read in the file to hash
                FileInputStream newFile = new FileInputStream(file);
                ObjectInputStream fileIn = new ObjectInputStream(newFile);

                byte[] data = (byte[]) fileIn.readObject();

                // create class instance to create SHA-256 hash
                MessageDigest md = MessageDigest.getInstance("SHA-256");

                // process the file
                md.update(data);
                // generate a has of the file
                byte[] digest = md.digest();

                // convert the bite string to a BigInteger object
                BigInteger result = new BigInteger(1, digest);

				//Grabs e and n from the public key rsa file
                FileInputStream pub = new FileInputStream("pubkey.rsa");
                ObjectInputStream pubIn = new ObjectInputStream(pub);
                BigInteger e = (BigInteger) pubIn.readObject();
                BigInteger n = (BigInteger) pubIn.readObject();
                pubIn.close();

                //result = result.modPow(e, n);
                BigInteger decrypted = (BigInteger) fileIn.readObject();
                BigInteger encrypted = decrypted.modPow(e, n);
                fileIn.close();

				//Checks the signature
                if (encrypted.compareTo(result) == 0)
                    System.out.println("The signature is valid");
                else
                    System.out.println("The signature is not valid");
            }
            catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        else{
            System.out.println("Public Key is not accessible. Program now closing.");
        }
    }
}
