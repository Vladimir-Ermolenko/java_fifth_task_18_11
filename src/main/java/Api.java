import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Api {
    public static void main(String[] args) throws IOException {
    }

    public static Map<String, String> searchApi(String arg) throws IOException {
        try {
            String[] splitted = arg.split("\\s+");
            String dateInitial = splitted[0];
            String day = dateInitial.substring(8, 10);
            String month = dateInitial.substring(5, 7);
            String year = dateInitial.substring(0, 4);
            String id = splitted[1];
            String date = day + "/" + month + "/" + year;

            HttpResponse<String> xmlRates = Unirest.get("https://www.cbr.ru/scripts/XML_daily.asp?date_req={date}")
                    .routeParam("date", date).asString();

            try (java.io.FileWriter fw = new java.io.FileWriter("rates.xml")) {
                fw.write(xmlRates.getBody());
            }

            String filename1 = "rates.xml";
            DocumentBuilderFactory dbf1 = DocumentBuilderFactory.newInstance();
            Map<String, String> dataMap = new HashMap<String, String>();
            try {
                dbf1.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                DocumentBuilder db1 = dbf1.newDocumentBuilder();
                Document doc = db1.parse(new File(filename1));
                doc.getDocumentElement().normalize();

                NodeList list = doc.getElementsByTagName("Valute");
                for (int temp = 0; temp < list.getLength(); temp++) {
                    Node node = list.item(temp);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String idXML = element.getElementsByTagName("CharCode").item(0).getTextContent();
                        String rate = element.getElementsByTagName("Value").item(0).getTextContent()
                                .replace(",", ".");
                        dataMap.put(idXML, rate);

                        // try to connect to DB here
                        // put in DB here
                    }
                }
                // dataMap.forEach((key, value) -> System.out.println(key + ": " + value));
                return dataMap;

            } catch (ParserConfigurationException | SAXException | IOException e) {
                System.out.println("Please, provide us with valid data");
            }

        } catch (java.lang.StringIndexOutOfBoundsException e) {
            System.out.println("Please, provide us with valid data");
        }

        return null;
    }

    public static String getInfoFromMap(Map<String, String> dataMap, String arg) {
        try {
            String[] splitted = arg.split("\\s+");
            String id = splitted[1];
            return dataMap.get(id);
        } catch (Exception e) {
            System.out.println("Please, provide us with valid data");
        }
        return null;
    }
}
