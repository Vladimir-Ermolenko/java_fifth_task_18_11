import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class Reader {
    public static void main(String[] args) {
        toClasses("/Users/voverm/IdeaProjects/studentManager/data/");
    }

    public static HashMap<Group, ArrayList<Student>> toClasses(String path) {
        HashMap<Group, ArrayList<Student>> data = new HashMap<>();
        final String[] extension = {"csv"};
        Iterator<File> it = FileUtils.iterateFiles(new File(path), extension, false);
        while(it.hasNext()){
            String name = it.next().getName();
            String fileName = "/Users/voverm/IdeaProjects/studentManager/data/" + name;
            String groupName = FilenameUtils.removeExtension(name);
            Group group = new Group(groupName);
            ArrayList<Student> listOfStudents = new ArrayList<>();
            data.put(group, listOfStudents);
            try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
                List<String[]> listOfStrings = reader.readAll();
                for (int i = 0; i < listOfStrings.size(); i++) {
                    String secondName = listOfStrings.get(i)[0];
                    String firstName = listOfStrings.get(i)[1];
                    String lastName = listOfStrings.get(i)[2];
                    String birthdayDate = listOfStrings.get(i)[3];
                    Student student = new Student(groupName, firstName, secondName, lastName, birthdayDate);
                    data.get(group).add(student);
                }
                // r.forEach(x -> System.out.println(Arrays.toString(x)));
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }
        return data;
    }
}
