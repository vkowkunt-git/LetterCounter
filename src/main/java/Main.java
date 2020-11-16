import org.apache.commons.cli.*;

/**
 * This class contains the main() method and getApplicationOptions() method through which
 * we are setting our application keys via letterCounter object, and calling the countTheLetters
 * method of letterCounter class in main class, where the entire logic lies
 */
public class Main {

    /**
     * @param args This is the main method through which we are calling our countTheLettersOccurrencesInFiles
     */
    public static void main(String[] args) {

        try {


            //Creating a CommandLine object and getting it from getApplicationOptions() method
            CommandLine cmd = getApplicationOptions(args);

            //Setting our commandline option values to Strings
            String APP_KEY = cmd.getOptionValue("application_key");
            String APP_KEY_ID = cmd.getOptionValue("application_key_id");
            String USER_AGENT = "B2 Letter Counter";

            //Creating my own storage client by giving the inputs to letterCounter constructor
            LetterCounter letterCounter = new LetterCounter(APP_KEY_ID, APP_KEY, USER_AGENT);

            //Letter to be counted
            char letterToCount = 'a';

            //Counting our letter
            letterCounter.countTheLetterOccurrencesInFiles(letterToCount);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /* This method creates and returns the options for the command line arguments */
    private static CommandLine getApplicationOptions(String[] args) {
        final Options options = new Options();

        //Creating our Application Key Option
        Option key = new Option("application_key", true, "application key value");
        key.setRequired(true); //setting it as a required option
        options.addOption(key);

        //Creating our Application Key Id Option
        Option keyId = new Option("application_key_id", true, "application key id value");
        keyId.setRequired(true); //setting it as a required option
        options.addOption(keyId);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return cmd;
    }
}
