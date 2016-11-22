
import java.io.*;
import java.lang.*;
import java.math.BigInteger;
import java.util.Random;

public class MyKeyGen {
    public static void main(String [] args) throws IOException {
        Random rnd = new Random();//Initializing the random class used for generating primes

        //Important values intialized in the form of BigIntegers
        BigInteger p = BigInteger.probablePrime(512, rnd);
        BigInteger q = BigInteger.probablePrime(513, rnd);
        BigInteger n = p.multiply(q);
        BigInteger e = BigInteger.TEN;

        //Makes sure n is 1024 in bit length, otherwise, creates nes values for p, q, and n
        while(n.bitLength()!=1024){
            p = BigInteger.probablePrime(512, rnd);
            q = BigInteger.probablePrime(513, rnd);
            n = p.multiply(q);
        }

        //Calculates phi of n
        BigInteger phiN = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        //Finds a value of e that is less than phi of n and is co-prime
        while((e.gcd(phiN)).compareTo(BigInteger.ONE)!= 0){
            e = e.add(BigInteger.ONE);
        }

        //calculates d
        BigInteger d = e.modInverse(phiN);

        //writes out e and n to the public key RSA file
        FileOutputStream pub = new FileOutputStream("pubkey.rsa");
        ObjectOutputStream pubOut = new ObjectOutputStream(pub);
        pubOut.writeObject(e);
        pubOut.writeObject(n);
        pubOut.close();

        //writes out d and n to the public key RSA file
        FileOutputStream priv = new FileOutputStream("privkey.rsa");
        ObjectOutputStream privOut = new ObjectOutputStream(priv);
        privOut.writeObject(d);
        privOut.writeObject(n);
        privOut.close();
    }
}
