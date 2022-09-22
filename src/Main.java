import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Main {
    public static void main(String[] args) throws IOException {
        // TODO: 20.09.2022 разобраться как запускать программу с аргументами и через консоль
//        args[0] = "C://input2.txt";
//        String fileName = args[0];
        String fileName = "C:\\Users\\Алексей\\Desktop\\input.txt";
        Path path = readPath(fileName);
        System.out.println("path = " + path);
        List<String> lines = readFile(path);
        int firstCount = Integer.parseInt(lines.get(0));
        int secondCount = Integer.parseInt(lines.get(firstCount + 1));
        System.out.println(firstCount + " " + secondCount);
        // TODO: 20.09.2022 валидация на корректронсть чисел и их местонахождение
        List<String> firstList = lines.subList(1, firstCount + 1);
        List<String> secondList = lines.subList(firstCount + 2, lines.size());
        List<String> output = prepareToWrite(lines, firstList, secondList);
        List<String> perOutput = finalPrepareToWrite(output, secondList, firstCount, secondCount);

        System.out.println(perOutput);
        write(output, fileName);
    }

    private static List<String> finalPrepareToWrite(List<String> output, List<String> secondList, int firstCount, int secondCount) {
        if (firstCount == secondCount) {
            List<String> filteredList = output.stream()
                    .filter(str -> str.contains("?") && secondList.stream().noneMatch(str::contains)).collect(Collectors.toList());

            System.out.println(filteredList.size());
            System.out.println(filteredList);

            List<String> specList = output.stream()
                    .peek(str -> str.contains(secondList))
        }
        return null;
    }

    private static void write(List<String> output, String fileName) throws IOException {
        String outputFileName = fileName.replace("in", "out");
//        String outputFileName = "C:\\Users\\Алексей\\Desktop\\output2.txt";
//        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFileName));
//        bufferedWriter.newLine();
        Files.write(Paths.get(outputFileName), output, StandardOpenOption.CREATE);
    }

    private static List<String> prepareToWrite(List<String> lines, List<String> first, List<String> second) {
//        return first.stream()
//                .filter(s -> second.contains(s))
//                .collect(Collectors.toList());

        return first.stream()
//                .map((String s1) -> {s1.concat(":"),
//                                        s1.concat(second.stream().filter(s2 -> s2.contains(s1)).findAny().toString());})
                .map(s -> s.concat(":").concat(second.stream().filter(s2 -> check(s, s2)).findAny().orElse("?")))
                .collect(Collectors.toList());
    }

    private static boolean check(String s, String s2) {
        System.out.println(s2);
        return Arrays.stream(s.split(" "))
//                .map(s1 -> Arrays.stream(s2.split(" "))
                .anyMatch(s1 -> {
                    System.out.println("*****");
//                    System.out.println(s1);
//                    return s1.contains(Arrays.stream(s2.split(" ")).peek(s3 -> System.out.println("s1 = " + s1 + ", s3 = " + s3)).forEach(););
                    return Arrays.stream(s2.split(" ")).anyMatch(s3 -> s1.contains(s3) || s3.contains(s1));
                });
    }

    private static List<String> readFile(Path path) {
        try (Stream<String> lineStream = Files.lines(path)) {
            return lineStream.collect(Collectors.toList());
        } catch (Exception e) {
        }
        return null;
    }

    private static Path readPath(String arg) {
        return Paths.get(arg);
    }
}