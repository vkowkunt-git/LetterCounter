import com.backblaze.b2.client.B2StorageClient;
import com.backblaze.b2.client.B2StorageClientFactory;
import com.backblaze.b2.client.contentHandlers.B2ContentFileWriter;
import com.backblaze.b2.client.contentHandlers.B2ContentSink;
import com.backblaze.b2.client.exceptions.B2Exception;
import com.backblaze.b2.client.structures.B2Bucket;
import com.backblaze.b2.client.structures.B2DownloadByNameRequest;
import com.backblaze.b2.client.structures.B2FileVersion;
import com.backblaze.b2.util.B2ByteRange;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains countTheLetterOccurrencesInFiles method, downloadFileToLocalPath,
 * and readAndCountFile method. The countTheLetterOccurrencesInFiles method calls the downloadFileToLocalPath
 * and readAndCountFile method.
 */
public class LetterCounter {

    final private Map<String, Integer> letterCountMap; // creating a map to store my filename and letterCount values
    final private B2StorageClient client; // Creating a storage client to connect and get information from the B2 CLoud storage
    final private List<B2Bucket> bucketsList; // list of the buckets that will be retrieved through my client


    LetterCounter(String APP_KEY_ID,String APP_KEY, String USER_AGENT) throws B2Exception {
        this.letterCountMap = new HashMap<>();       //instantiating all the objects created through when this constructor is called
        this.client = B2StorageClientFactory
                .createDefaultFactory()
                .create(APP_KEY_ID, APP_KEY, USER_AGENT);
        this.bucketsList = client.buckets();
    }


    final void countTheLetterOccurrencesInFiles(char letterToCount) {

        try {
            final Path path = Paths.get("files/");
            Files.createDirectories(path);

            // Iterating through every bucket in the B2cloudStorage
            for (B2Bucket bucket : bucketsList) {

                // Iterating through every file in the bucket
                for (B2FileVersion version : client.fileNames(bucket.getBucketId())) {

                    // Downloaded file to my local 'files' folder
                    downloadFileToLocalPath(client, bucket, version, path);

                    // Read the file and count the letter 'a'
                    readAndCountFile(version, path, letterToCount);

                }

            }

            // Sorting by count(value) first and filename(key) second
            letterCountMap.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue()
                            .thenComparing(Map.Entry.comparingByKey()))
                    .forEach(entry -> System.out.println(entry.getValue() + " " + entry.getKey()));

            // Closing my client and exiting gracefully
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /** This method creates a B2DownloadByNameRequest object and B2ContentFileWriter object
     *  to download the files in the bucket and write them to a local folder, reference: B2Sample
     */
    final void downloadFileToLocalPath(B2StorageClient client, B2Bucket bucket, B2FileVersion version, Path path) {

        // The B2DownloadByNameRequest is going to download the file with the given name and mentioned bucket
        final B2DownloadByNameRequest request = B2DownloadByNameRequest
                .builder(bucket.getBucketName(), version.getFileName())
                .setRange(B2ByteRange.between(0,version.getContentLength()))// My request is going to get the bytes from 0 to the end of the file
                .build();
        // The B2ContentFileWriter is going to write the downloaded file when called upon by client.DownloadByName method
        final B2ContentSink handler = B2ContentFileWriter
                .builder(new File(path + File.separator + version.getFileName()))
                .setVerifySha1ByRereadingFromDestination(true)
                .build();
        try {
            client.downloadByName(request, handler);
        } catch (B2Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /** This method reads the file from the local folder and counter the given character and
     *  puts  it into the letterCounterMap
     */
    final void readAndCountFile(B2FileVersion version, Path path, char letterToCount) {
        final File file = new File(path + File.separator + version.getFileName());
        final BufferedReader reader;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file),
                            StandardCharsets.UTF_8));

            int c;
            int count = 0; //variable to count the letter
            while ((c = reader.read()) != -1) { // -1 describes if the file has reached its end

                char character = (char) c;

                if (character == letterToCount) {
                    count++;
                }
            }
            letterCountMap.put(version.getFileName(), count);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

}
