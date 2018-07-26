package djordje.webscraper;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author djordje gavrilovic
 * Data scraper from a bookstore web page.
 * It looks for books with 5 star rating and prints data about their:
 * - title
 * - total number
 * - total price
 * - average price
 */
public class WebScraper {
    
    private static double sum = 0;
    private static int bookNum = 0;

    /**
     * Connects to an URL and returns an HTML document.
     */
    private static Document docCon(String url) throws IOException {
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla")
                .get();
        return doc;
    }

    /**
     * Performs scraping for every page provided. 
     * Looking for books with 5 stars rate.
     * ----------------------------------------------------
     * loops container and selects <article>
     * checks if <p> element inside <article> has class="star-rating Five"
     * prints the title attribute from <a> inside <h3>
     * finds <p> with class="price_color" containing price
     * parses it to double and adds to total sum
     * counts number of found books
     * ----------------------------------------------------
     * deals with pagination - finding a link for the next page
     * then calls itself and continues scraping
     */
    private static void doScraping(String url) throws IOException {
        Document page = docCon(url);
        Elements elm = page.select("ol.row");    // container for articles
        for (Element e : elm.select("article")) {            
            if (e.select("p").hasClass("star-rating Five")) {
                System.out.println(e.select("h3 a").attr("title"));
                String price = e.select("p.price_color").text();
                price = price.replace("\u00a3","");    // british pound sign removed from the string
                double bookPrice = Double.parseDouble(price);
                sum = sum + bookPrice;
                bookNum++;
            }
        }
        if (page.select(".pager li").hasClass("next")) {
            String nextUrl = page.select(".pager li.next a").attr("abs:href");
            doScraping(nextUrl);
        }
    }

    public static void main(String[] args) throws IOException {
        doScraping("http://books.toscrape.com/catalogue/category/books_1/index.html");    // bookstore that wants to be scraped ;)
        System.out.println("----------------------------------------");
        System.out.println("sum: " + "\u00a3"+ sum);
        System.out.println("book num: " + bookNum);
        System.out.printf("avg price: \u00a3%.2f", sum/bookNum);
    }

}
