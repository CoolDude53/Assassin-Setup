import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.*;

// compile using javac -classpath .:javax.mail.jar Game.java
// run using java -classpath .:javax.mail.jar Game YOUR_EMAIL@GMAIL.COM YOUR_EMAIL_PASSWORD FILE_CONTAINING_PLAYERS_EMAILS.txt

public class Game
{
    public static void main(String[] args)
    {
        if (args.length < 3)
        {
            System.out.println("Please enter your email, password, and a file with your recipients as arguements!");
            return;
        }

        final String USER = args[0];
        final String PASS = args[1];

        // Assuming you are sending email from localhost
        String host = "smtp.gmail.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.user", USER);
        properties.setProperty("mail.smtp.password", PASS);
        properties.setProperty("mail.smtp.port", "587");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties, new Authenticator()
        {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(USER, PASS);
            }
        });

        try
        {
            ArrayList<String> recipients = new ArrayList<>();
            ArrayList<String> recipientsCopy;
            HashMap<String, String> targetPairs = new HashMap<>();
            Scanner scanner = new Scanner(new File(args[2]));

            while (scanner.hasNext())
            {
                recipients.add(scanner.next());
            }

            recipientsCopy = new ArrayList<>(recipients);

            if (recipients.size() == 0)
            {
                System.out.println("Your file has no participants!");
                return;
            }

            if (recipients.size() % 2 != 0)
            {
                System.out.println("You need an even number of participants!");
                return;
            }

            for (String player : recipients)
            {
                String target;

                do
                {
                    Collections.shuffle(recipientsCopy);
                    target = recipientsCopy.get(0);
                } while (target.equals(player));

                recipientsCopy.remove(target);
                targetPairs.put(player, target);
            }

            for (Map.Entry<String, String> entry : targetPairs.entrySet())
            {
                MimeMessage message = new MimeMessage(session);

                message.setFrom(new InternetAddress(USER));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(entry.getKey()));
                message.setSubject("Your game of Assassin has begun!");
                message.setText("Your assignment is: " + entry.getValue());

                Transport.send(message);
            }

            System.out.println("Your players have been assigned!");
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
