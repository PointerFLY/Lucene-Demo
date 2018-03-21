import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FileParser {

    static ArrayList<Report> readReport(Path path) {
        ArrayList<Report> reports = new ArrayList<>();

        try {
            Document dom = Jsoup.parse(path.toFile(), "UTF-8");
            Elements docs = dom.select("doc");

            for (Element doc : docs) {
                String id = doc.getElementsByTag("docno").text();
                String content = doc.getElementsByTag("text").text();
                Report report = new Report(id, content);
                reports.add(report);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Logger.getGlobal().log(Level.SEVERE, String.format("Read report %s failed.", path.getFileName()));
            System.exit(0);
        }

        return reports;
    }

    static ArrayList<Topic> readTopic(Path path) {
        ArrayList<Topic> topics = new ArrayList<>();

        try {
            String text = String.join(" ", Files.readAllLines(FileUtils.TOPICS_FILE));
            String regex =
                    "<top>.*?<num>" +
                    ".*?(\\d+).*?<title>" +
                    "\\s*(.*?)\\s*<desc>" +
                    ".*?:\\s*(.*?)\\s*<narr>" +
                    ".*?:\\s*(.*?)\\s*</top>";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);

            while (matcher.find()) {
                if (matcher.groupCount() != 4) { throw new Exception("Incorrect format."); }
                Topic topic = new Topic(Integer.valueOf(matcher.group(1)), matcher.group(2), matcher.group(3), matcher.group(4));
                topics.add(topic);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getGlobal().log(Level.SEVERE, String.format("Read Topics %s failed.", path.getFileName()));
            System.exit(0);
        }

        return topics;
    }
}
