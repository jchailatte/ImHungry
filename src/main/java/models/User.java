package models;

import common.Constants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Date;


public class User {
    private static final Key SIGNING_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    // 1000ms / s * 60s / m * 60m / h * 24h / day -> expires in one day
    public static final long STANDARD_EXPIRY_TIME = 1000 * 60 * 60 * 24;

    private int userID;
    private String username;
    private byte[] salt;
    private byte[] passwrd;

    User(int id, String username, byte[] salt, byte[] password) {
        this.userID = id;
        this.username = username;
        this.salt = salt;
        this.passwrd = password;
    }

    public int getUserID() {
        return this.userID;
    }

    public String getUsername() {
        return this.username;
    }

    private byte[] getSalt() {
        return this.salt;
    }

    private byte[] getPasswrd() {
        return this.passwrd;
    }

    /**
     * Generates a new jwt for the existing user
     *
     * @param expiryMs time to expire in ms
     * @return a jwt that expires expiryMs after time of generation
     */
    public String generateJwt(long expiryMs) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expiryMs);
        return Jwts.builder()
            .setExpiration(expiration)
            .claim("userID", this.getUserID())
            .claim("username", this.getUsername())
            .signWith(SIGNING_KEY)
            .compact();
    }

    /**
     * When user submits login form, the data will be converted into
     * a valid User object. The user will receive the JWT on the Servlet.
     *
     * @param username    from form submission
     * @param rawPassword from form submission
     * @return User if valid, null if invalid
     */
    public static User login(String username, String rawPassword) {
        if (username == null || rawPassword == null || username.isBlank() || rawPassword.isBlank()) {
            return null;
        }

        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        User databaseUser = null;
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM Users WHERE username=?"
            );
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                databaseUser = new User(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getBytes(3),
                        resultSet.getBytes(4)
                );
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (databaseUser == null) {
            return null;
        }

        byte[] attemptedPassword = saltAndHashPassword(databaseUser.getSalt(), rawPassword);
        byte[] actualPassword = databaseUser.getPasswrd();
        if (Arrays.equals(attemptedPassword, actualPassword)) {
            return databaseUser;
        } else {
            return null;
        }
    }

    /**
     * When user submits registration form, the data will be converted
     * into a (new) valid User object. The user will receive the JWT on the Servlet.
     *
     * @param username    from form submission
     * @param rawPassword from form submission
     * @return User if created successfully, null if invalid
     */
    public static User register(String username, String rawPassword) {
        if (username == null || rawPassword == null || username.isBlank() || rawPassword.isBlank() || !passwordIsSecure(rawPassword)) {
            return null;
        }

        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        User databaseUser = null;

        byte[] salt = generateSalt();
        byte[] hashedPassword = saltAndHashPassword(salt, rawPassword);
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO Users (username, salt, passwrd) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            preparedStatement.setString(1, username);
            preparedStatement.setBytes(2, salt);
            preparedStatement.setBytes(3, hashedPassword);

            int affectedRows = preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();
            if (affectedRows != 0 && resultSet.next()) {
                databaseUser = new User(
                        resultSet.getInt(1),
                        username,
                        salt,
                        hashedPassword
                );
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }

        return databaseUser;
    }

    /**
     * For any authenticated request, the user will need to provide an authentication
     * token which will be a JWT. The JWT will then be converted into the user object
     * which will be passed into various database functions
     *
     * @param jws authentication token from frontend
     * @return User if valid, null if invalid or expired
     */
    public static User fromJwt(String jws) {
        User user;
        try {
            int userID = (int)
                Jwts.parser()
                    .setSigningKey(SIGNING_KEY)
                    .parseClaimsJws(jws)
                    .getBody()
                    .get("userID");

            user = new User(userID, null, null, null);
        }
        catch (Exception e) {
            user = null;
        }

        return user;
    }

    /**
     * Salts and hashes the password
     *
     * @param salt        to prepend before hashing
     * @param rawPassword plain text to pass in
     * @return secured password byte array
     */
    static byte[] saltAndHashPassword(byte[] salt, String rawPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            return md.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.toString());
            return new byte[0];
        }
    }

    /**
     * Ensures password is >= 8 characters and has a number
     *
     * @param rawPassword plain text password to validate
     * @return true if secure enough, false otherwise
     */
    static boolean passwordIsSecure(String rawPassword) {
        return rawPassword.length() >= 8 && rawPassword.matches(".*\\d.*");
    }

    /**
     * Generates random byte values to use as salt
     *
     * @return random byte array of length 16
     */
    static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }
}
