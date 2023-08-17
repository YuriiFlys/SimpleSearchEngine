package search;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String fileName = null;
        for (int i = 0; i < args.length; i++) {
            if ("--data".equals(args[i]) && i + 1 < args.length) {
                fileName = args[i + 1];
                break;
            }
        }
        if (fileName == null) {
            System.out.println("No data file specified!");
            return;
        }

        List<String> data;
        try {
            data = Files.readAllLines(Paths.get(fileName));
        } catch (IOException e) {
            System.out.println("Error reading data from file: " + e.getMessage());
            return;
        }

        Map<String, Set<Integer>> index = new HashMap<>();
        for (int i = 0; i < data.size(); i++) {
            String line = data.get(i);
            String[] words = line.split("\\s+");
            for (String word : words) {
                word = word.toLowerCase();
                Set<Integer> set = index.getOrDefault(word, new HashSet<>());
                set.add(i);
                index.put(word, set);
            }
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("=== Menu ===");
            System.out.println("1. Find a person");
            System.out.println("2. Print all people");
            System.out.println("0. Exit");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) {
                System.out.println("Bye!");
                break;
            } else if (choice == 1) {
                System.out.println("Select a matching strategy: ALL, ANY, NONE");
                String strategy = scanner.nextLine().trim().toUpperCase();
                System.out.println("Enter data to search:");
                String[] queryWords = scanner.nextLine().trim().toLowerCase().split("\\s+");
                Set<Integer> lines = new HashSet<>();
                if ("ALL".equals(strategy)) {
                    lines.addAll(index.getOrDefault(queryWords[0], Collections.emptySet()));
                    for (String word : queryWords) {
                        lines.retainAll(index.getOrDefault(word, Collections.emptySet()));
                    }
                } else if ("ANY".equals(strategy)) {
                    for (String word : queryWords) {
                        lines.addAll(index.getOrDefault(word, Collections.emptySet()));
                    }
                } else if ("NONE".equals(strategy)) {
                    Set<Integer> excludeLines = new HashSet<>();
                    for (String word : queryWords) {
                        excludeLines.addAll(index.getOrDefault(word, Collections.emptySet()));
                    }
                    for (int i = 0; i < data.size(); i++) {
                        if (!excludeLines.contains(i)) {
                            lines.add(i);
                        }
                    }
                }
                for (int line : lines) {
                    System.out.println(data.get(line));
                }
            } else if (choice == 2) {
                System.out.println("=== List of people ===");
                for (String line : data) {
                    System.out.println(line);
                }
            } else {
                System.out.println("Incorrect option! Try again.");
            }
        }
    }
}
