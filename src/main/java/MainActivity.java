import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.Scanner;

public class MainActivity {
    public static void main(String[] args) throws IOException {

        Scanner userInputScanner = new Scanner(System.in);
        System.out.println("1. URL\n"+"2. Link file\n"+"3. Roam");
        boolean contInput = true;
        while (contInput) {
            String userInput = userInputScanner.nextLine();
            if (userInput.equalsIgnoreCase("abort")) {
                contInput = false;
            }
            //Scraping all links on the webpage
            if (userInput.equalsIgnoreCase("1")) {
                System.out.println("Enter website\n");
                String UrlInput = userInputScanner.nextLine();
                Document doc = Jsoup.connect(UrlInput)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:61.0) Gecko/20100101 Firefox/61.0")
                        .get();
                BufferedWriter writer;
                log(doc.title());
                Elements links = doc.select("a[href]");
                System.out.println(links);
                print("\nLinks: (%d)", links.size());

                for (Element link : links) {
                    print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
                    try {
                        writer = new BufferedWriter(new FileWriter("ScrapeLinks.txt",true));
                        writer.write(link.attr("abs:href"));
                        writer.newLine();
                        System.out.println(link.attr("abs:href"));
                        writer.close();
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                }

                Elements headlines = doc.select("mp-itn b a");
                for (Element headline : headlines) {
                    log("%s\n\t%s", headline.attr("title"), headline.absUrl("href"));
                }
                try {
                    File file = new File("SingleScrape.txt");
                    writer = new BufferedWriter(new FileWriter(file));
                    writer.write(doc.toString());
                    writer.close();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }

            //Scraping all links in the text file.
            if (userInput.equalsIgnoreCase("2")){
                BufferedReader reader;
                File file = new File("ScrapeLinks.txt");
                File file2 = new File("LinksScraped.txt");
                reader = new BufferedReader(new FileReader(file));
                if(file.isFile()){
                    reader = new BufferedReader(new FileReader(file));
                }
                String st;
                while ((st = reader.readLine()) != null){
                    System.out.println(st);
                    Document doc2 = Jsoup.connect(st)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:61.0) Gecko/20100101 Firefox/61.0")
                            .get();
                    BufferedWriter writer;
                    try {
                        writer = new BufferedWriter(new FileWriter(file2,true));
                        writer.write(doc2.toString());
                        writer.close();
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
            //Parses all the scraped
            if (userInput.equalsIgnoreCase("3")){
                BufferedWriter writer = new BufferedWriter(new FileWriter("Cards.txt",true));
                BufferedWriter writer2 = new BufferedWriter(new FileWriter("CardsIMG.txt",true));
                File cards = new File("ExampleScrape.txt");

                Document doc3 = Jsoup.parse(cards,null);
                Elements goldUrls = doc3.select("img.hscard-static");
                Elements cells = doc3.select("td.visual-details-cell");

                for (Element goldurl : goldUrls){
                    System.out.println(goldurl.attr("data-goldurl"));
                    writer2.write("IMG: " + goldurl.attr("data-goldurl"));
                    writer2.newLine();

                }

                for (Element cell : cells){
                    System.out.println("NAME: " + cell.select("h3").text());
                    System.out.println("DESC: " + cell.select("td.visual-details-cell > p").text());
                    System.out.println("INFO " + cell.select("ul").text());
                    System.out.println("FLAVOUR: " + cell.select("div.card-flavor-listing-text").text());
                    writer.write("NAME: " + cell.select("h3").text());
                    writer.newLine();
                    writer.write("DESC: " + cell.select("td.visual-details-cell > p").text());
                    writer.newLine();
                    writer.write("INFO: " + cell.select("ul").text());
                    writer.newLine();
                    writer.write("FLAVOUR: " + cell.select("div.card-flavor-listing-text").text());
                    writer.newLine();
                    writer.newLine();
                }
                writer.close();
                writer2.close();
            }

        }
    }

    private static void print(String msg1, Object... args) {
        System.out.println(String.format(msg1, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }

    private static void log(String msg,Object... vals){
        System.out.println(String.format(msg, vals));
    }
}