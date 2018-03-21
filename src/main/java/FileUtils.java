import org.codehaus.plexus.archiver.AbstractUnArchiver;
import org.codehaus.plexus.archiver.gzip.GZipUnArchiver;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;
import org.codehaus.plexus.logging.console.ConsoleLoggerManager;

import java.lang.reflect.Array;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class FileUtils {

    private static final Path TEMP_DIR = Paths.get("temp/");
    private static final Path DOCS_DIR = TEMP_DIR.resolve("docs/");
    private static final Path REPORTS_DIR = DOCS_DIR.resolve("reports");

    static final Path INDEX_DIR = TEMP_DIR.resolve("index/");
    static final Path TOPICS_FILE = DOCS_DIR.resolve("topics");
    static final Path RESULTS_FILE = TEMP_DIR.resolve("results");

    private static final List<String> REPORTS_SUBDIR_NAMES = Arrays.asList("fbis", "fr94", "ft", "latimes");
    private static final List<String> REPORTS_VALID_PREFIXES = Arrays.asList("fb", "fr", "ft", "la");

    private static final URL DOCS_URL;
    private static final URL TOPICS_URL;

    static {
        try {
            DOCS_URL = new URL("https://drive.google.com/a/tcd.ie/uc?export=download&confirm=rjCk&id=1MudJity9Ckh8jxapFx3OS-DLEkcvbYYx");
            TOPICS_URL = new URL("https://www.dropbox.com/s/277vn6l23z2e6ku/CS7IS3-Assignment2-Topics.gz?dl=1");
        } catch (MalformedURLException e) {
            throw new Error(e);
        }
    }

    static ArrayList<Path> getAllReportFiles() {
        ArrayList<Path> paths = new ArrayList<>();

        Stack<File> stack = new Stack<>();
        REPORTS_SUBDIR_NAMES.forEach(item ->
            stack.push(REPORTS_DIR.resolve(item).toFile())
        );

        while (!stack.isEmpty()) {
            File file = stack.pop();

            if (file.isFile()) {
                String fileNamePrefix = file.getName().substring(0, REPORTS_VALID_PREFIXES.get(0).length());
                boolean isReportFile = REPORTS_VALID_PREFIXES.contains(fileNamePrefix);
                if (isReportFile) {
                    paths.add(file.toPath());
                }
            } else {
                File[] subFiles = file.listFiles();
                if (subFiles == null) {
                    Logger.getGlobal().log(Level.SEVERE, "List reports file failed");
                    System.exit(1);
                }

                for (File item: subFiles) {
                    stack.push(item);
                }
            }
        }

        return paths;
    }

    static void initialize() {
        createDirectory(INDEX_DIR);
        createDirectory(DOCS_DIR);
        Path topicsGZip = fetchDocs(TOPICS_URL, TEMP_DIR.resolve("topics.gz"));
        // TODO: Bypass Google virus scan when downloading.
        Path reportZip = fetchDocs(DOCS_URL, TEMP_DIR.resolve("reports.zip"));
        decompress(topicsGZip, new GZipUnArchiver(), DOCS_DIR.resolve("topics"), null);
        decompress(reportZip, new ZipUnArchiver(), null, DOCS_DIR);
    }

    private static void decompress(Path file, AbstractUnArchiver unarchiver, Path destFile, Path destDir) {
        ConsoleLoggerManager manager = new ConsoleLoggerManager();
        manager.initialize();

        if (destFile != null) {
            if (Files.exists(destFile)) { return; }
            unarchiver.setDestFile(destFile.toFile());
        } else {
            if (Files.exists(destDir)) { return; }
            unarchiver.setDestDirectory(destDir.toFile());
        }

        unarchiver.setSourceFile(file.toFile());
        unarchiver.enableLogging(manager.getLoggerForComponent(null));
        unarchiver.extract();
    }

    private static void createDirectory(Path path) {
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    private static Path fetchDocs(URL url, Path path) {
        String urlStr = url.toString();

        try {
            if (Files.notExists(path)) {
                InputStream in = url.openStream();
                Files.copy(in, path);
                in.close();
            }
            return path;
        } catch (IOException e) {
            e.printStackTrace();
            Logger.getGlobal().log(Level.SEVERE, "Fetch documents failed");
            System.exit(1);
        }

        return path;
    }
}