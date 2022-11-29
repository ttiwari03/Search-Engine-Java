package search;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/*
 *  This program search for given string in data using inverted index (without considering case).
 *  Input file name is given using command line argument succeeded by "--data"
 *
 *  @author   - Trapti Tiwari
 *  @email    - traptit1@yahoo.com
 *  @linkedin - https://www.linkedin.com/in/tiwari-trapti/
 */

public class Main {

    public static final Scanner readIp = new Scanner(System.in);

    public static final ArrayList<String> data = new ArrayList<>();
    public static final Map<String, Set<Integer>> dataInvertedIndex = new HashMap<>();

    public static void main(String[] args) {
        //  Load data from file
        if (args[0].equals("--data")) {
            inputData(args[1]);
        }

        //  Build inverted index from data
        buildInvertedIndex();

        while (true) {
            //  Print available options
            printMenu();
            int command = Integer.parseInt(readIp.nextLine());
            System.out.println();

            if (command == 0) {
                System.out.println("Bye!");
                break;
            }

            switch (command) {
                case 1 -> searchData();
                case 2 -> printData();
                default -> System.out.println("Incorrect option! Try again.");
            }
        }
    }

    /*
     *  Load data into memory from given file
     *  input  -  filename (String)
     */

    private static void inputData(String fileName) {
        File file = new File(fileName);

        try (Scanner readFile = new Scanner(file)) {
            while (readFile.hasNextLine()) {
                data.add(readFile.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }

    private static void printMenu() {
        System.out.println("""
                === Menu ===
                1. Find a person
                2. Print all people
                0. Exit""");
    }

    /*
     *  Build inverted index from data by finding all line numbers in which the word appear
     *  Saved in memory using "word" : "collection of line numbers" format
     */
    private static void buildInvertedIndex() {
        for (int i = 0; i < data.size(); i++) {
            String[] line = data.get(i).split(" ");
            for (String word : line) {
                Set<Integer> lineNumbers = dataInvertedIndex.getOrDefault(word.toLowerCase(), new HashSet<>());
                lineNumbers.add(i);
                dataInvertedIndex.put(word.toLowerCase(), lineNumbers);
            }
        }
    }

    private static void printData() {
        System.out.println("=== List of data ===");

        for (String datum : data) {
            System.out.println(datum);
        }
        System.out.println();
    }

    /*
     *  Search for given string in data based on given strategy
     */
    private static void searchData() {

        System.out.println("Select a matching strategy: ALL, ANY, NONE");
        String strategy = readIp.nextLine();

        System.out.println("Enter a name or email to search all suitable people.");
        String[] searchData = readIp.nextLine().toLowerCase().split(" ");

        Set<Integer> lineNumbers = new HashSet<>();
        switch (strategy) {
            case "ALL" -> lineNumbers = searchAll(searchData);
            case "ANY" -> lineNumbers = searchAny(searchData);
            case "NONE" -> lineNumbers = searchNone(searchData);
        }

        printSearchResult(lineNumbers);
    }

    /*
     * Output all line numbers where none of the data from given string is present
     *  input  -  array of data to be searched
     *  output  - all line where data is not present
     */
    private static Set<Integer> searchNone(String[] searchData) {
        //total lines equals to data size.
        Set<Integer> lineNumbers = new HashSet<>();

        for (int i = 0; i < data.size(); i++) {
            lineNumbers.add(i);
        }

        for (String searchDatum : searchData) {
            lineNumbers.removeAll(dataInvertedIndex.getOrDefault(searchDatum, new HashSet<>()));
        }
        return lineNumbers;
    }

    /*
     * Output all line numbers where any data from given string is present
     *  input  -  array of data to be searched
     *  output  - all line where data is present
     */
    private static Set<Integer> searchAny(String[] searchData) {
        Set<Integer> lineNumbers = new HashSet<>();

        for (String searchDatum : searchData) {
            lineNumbers.addAll(dataInvertedIndex.getOrDefault(searchDatum, new HashSet<>()));
        }

        return lineNumbers;
    }

    /*
     * Output all line numbers where all data from given string is present
     *  input  -  array of data to be searched
     *  output  - all line where complete data is present
     */
    private static Set<Integer> searchAll(String[] searchData) {
        Set<Integer> lineNumbers = new HashSet<>();
        for (int i = 0; i < data.size(); i++) {
            lineNumbers.add(i);
        }

        for (String searchDatum : searchData) {
            lineNumbers.retainAll(dataInvertedIndex.getOrDefault(searchDatum, new HashSet<>()));
        }
        return lineNumbers;
    }


    private static void printSearchResult(Set<Integer> lineNumbers) {
        StringBuilder searchResult = new StringBuilder();

        for (Integer line : lineNumbers) {
            searchResult.append(data.get(line));
            searchResult.append("\n");
        }

        if (searchResult.length() > 0) {
            System.out.println();
            System.out.println(lineNumbers.size() + " persons found:");
            System.out.println(searchResult);
        } else  {
            System.out.println("No matching data found.");
            System.out.println();
        }
    }

}
