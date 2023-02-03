import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        //First task
        String scvFileName = "data.csv";
        List<Employee> staffSCV = parseCSV(columnMapping, scvFileName);
        String scvToJson = listToJson(staffSCV);
        String scvToJsonFileName = "data.json";
        writeString(scvToJson, scvToJsonFileName);

        //Second task
        String xmlFileName = "data.xml";
        List<Employee> staffXML = parseXML(xmlFileName);
        String xmlToJson = listToJson(staffXML);
        String xmlToJsonFileName = "data2.json";
        writeString(xmlToJson, xmlToJsonFileName);
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        File file = new File(fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Employee> newList = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            newList = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newList;
    }

    public static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> staff = new ArrayList<>();
        File xmlFile = new File(fileName);
        try {
            xmlFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(fileName);

        Node root = doc.getDocumentElement();
        NodeList list = root.getChildNodes();

        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element element = (Element) node;
                long id = Long.parseLong(getElementValue(element, columnMapping[0]));
                String firstName = getElementValue(element, columnMapping[1]);
                String lastName = getElementValue(element, columnMapping[2]);
                String county = getElementValue(element, columnMapping[3]);
                int age = Integer.parseInt(getElementValue(element, columnMapping[4]));
                Employee emp = new Employee(id, firstName, lastName, county, age);
                staff.add(emp);
            }
        }
        return staff;
    }

    public static String getElementValue(Element element, String nodeName) {
        return element.getElementsByTagName(nodeName).item(0).getTextContent();
    }

    public static String listToJson(List<Employee> list) {
        Type typeToken = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        return gson.toJson(list, typeToken);
    }

    public static void writeString(String s, String fileName) {
        File file = new File(fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
