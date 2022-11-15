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
        // прочитали имя файла
        String fileName = "C:\\Users\\Алексей\\Desktop\\input3.txt";
        // создали объект по имени файла
        Path path = readPath(fileName);
        System.out.println("path = " + path);
        // прочитал в список линии из файла
        List<String> lines = readFile(path);
        // определил счетчики слов одного и второго списка
        if (lines == null)
            throw new RuntimeException();
        int firstCount = Integer.parseInt(lines.get(0));
        int secondCount = Integer.parseInt(lines.get(firstCount + 1));
        System.out.println(firstCount + " " + secondCount);
        // выделил первый лист
        List<String> firstList = lines.subList(1, firstCount + 1);
        // выделил второй лист
        List<String> secondList = lines.subList(firstCount + 2, lines.size());
        // подготовка к записи в новый файл с бизнес-логикой
        // первая итерация сопоставления
        List<String> output = prepareToWrite(firstList, secondList, firstCount, secondCount);
        // финальная итерация сопоставления (недостающих)
        List<String> perOutput = finalPrepareToWrite(output, secondList, firstCount, secondCount);

        System.out.println(perOutput);
        // записть в новый файл необходимый результат
        write(perOutput, fileName);
    }

    private static List<String> finalPrepareToWrite(List<String> output, List<String> secondList, int firstCount, int secondCount) {

        if (firstCount != secondCount)
            return output;

        // список из output со словами, которые не нашли себе пару
        List<String> listNotFound = output.stream()
                .filter(s -> s.contains("?"))
                .toList();

        // список из output со словами, которые нашли себе пару
        List<String> listFound = output.stream()
                .filter(s -> !s.contains("?"))
                .toList();

        // список слов из второго списка, которые не нашли себе пару с первым списком 
        List<String> listNotFoundFromSecond = secondList.stream()
                .filter(s1 -> output.stream()
                        .noneMatch(s2 -> s2.contains(s1) || s1.contains(s2)))
                .toList();

        return addWithoutMatches(listFound, listNotFound, listNotFoundFromSecond);
    }

    private static List<String> addWithoutMatches(List<String> listFound, List<String> listNotFound, List<String> listNotFoundFromSecond) {
        List<String> addMatches = new ArrayList<>(listFound);
        System.out.println("addMatches near cicle = " + addMatches);
        for (int i = 0; i < listNotFound.size(); i++) {
            addMatches.add(listNotFound.get(i).replace("?", listNotFoundFromSecond.get(i)));
        }

        return addMatches;
    }

    private static void write(List<String> output, String fileName) throws IOException {
        String outputFileName = fileName.replace("in", "out");
        Files.write(Paths.get(outputFileName), output, StandardOpenOption.CREATE);
    }

    private static List<String> prepareToWrite(List<String> first, List<String> second, int firstCount, int secondCount) {

        List<String> output = new ArrayList<>(first.stream()
                .map(s -> s.concat(":").concat(second.stream()
                        .filter(s2 -> check(s, s2))
                        .findAny()
                        .orElse("?")))
                .toList());

        if (output.size() < Math.max(firstCount, secondCount)) {
            List<String> addOutput = prepareToWrite(second, first, firstCount, secondCount);

            List<String> addSecOutput = addOutput.stream()
                    .filter(s -> s.contains("?"))
                    .toList();

            output.addAll(addSecOutput);
        }

        return output;
    }

    private static boolean check(String str1, String str2) {
        return Arrays.stream(str1.split(" "))
                .anyMatch(s1 -> Arrays.stream(str2.split(" ")).anyMatch(s2 -> s1.contains(s2) || s2.contains(s1)));
    }

    private static List<String> readFile(Path path) {
        try (Stream<String> lineStream = Files.lines(path)) {
            return lineStream.collect(Collectors.toList());
        } catch (Exception ignored) {
        }
        return null;
    }

    private static Path readPath(String arg) {
        return Paths.get(arg);
    }
}