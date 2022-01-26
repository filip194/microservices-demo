package jasypt;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;

public class TestJasypt
{
    public static void main(String[] args)
    {
        final StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        // for this encryptor 3 properties are needed:

        // this will be key to encrypt secrets, so we need to pass this key to application at runtime to be able to decrypt the secrets
        standardPBEStringEncryptor.setPassword("encrypt_key");
        standardPBEStringEncryptor.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        standardPBEStringEncryptor.setIvGenerator(new RandomIvGenerator());

        final String result = standardPBEStringEncryptor.encrypt("test");

        System.out.println(result);
        System.out.println(standardPBEStringEncryptor.decrypt(result));

    }
}

