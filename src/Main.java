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

        // имя файла
        String fileName = args[0];
//        String fileName = "C:\\Users\\Алексей\\Desktop\\input2.txt";

        Path path = Paths.get(fileName);

        // построчное чтение из файла
        List<String> lines = readFile(path);

        // определение счетчиков в файле и первичная валидация
        if (lines == null)
            throw new RuntimeException("It`s not a file .txt");
        int firstCount;
        int secondCount;
        try {
            firstCount = Integer.parseInt(lines.get(0));
            secondCount = Integer.parseInt(lines.get(firstCount + 1));
        } catch (Exception e) {
            throw new RuntimeException("Index-error in the file");
        }

        // первый часть списка
        List<String> firstList = lines.subList(1, firstCount + 1);

        // вторая часть списка
        List<String> secondList = lines.subList(firstCount + 2, lines.size());

        if ((firstList.size() != firstCount) || (secondList.size() != secondCount))
            throw new InvalidPropertiesFormatException("The size(s) of list(s) not match with count(s)");

        // бизнес-логика и подготовка к записи в новый файл
        // первая итерация сопоставления
        List<String> prepareOutput = firstPrepareToWrite(firstList, secondList, firstCount, secondCount);

        // финальная итерация сопоставления (недостающих)
        List<String> finalOutput = finalPrepareToWrite(prepareOutput, secondList, firstCount, secondCount);

        // записть в новый файл необходимый результат
        write(finalOutput, fileName);
    }

    private static List<String> finalPrepareToWrite(List<String> output, List<String> secondList, int firstCount, int secondCount) {

        if (firstCount != secondCount)
            return output;

        List<String> listNotFound = new ArrayList<>();
        List<String> listFound = new ArrayList<>();

        for (String x : output) {
            if (x.contains("?"))
                listNotFound.add(x);
            else
                listFound.add(x);
        }

        // список слов из второго списка, которые не нашли себе пару с первым списком 
        List<String> listNotFoundFromSecond = secondList.stream()
                .filter(s1 -> output.stream()
                        .noneMatch(s2 -> s2.contains(s1) || s1.contains(s2)))
                .toList();
        return addWithoutMatches(listFound, listNotFound, listNotFoundFromSecond);
    }

    private static List<String> addWithoutMatches(List<String> listFound, List<String> listNotFound, List<String> listNotFoundFromSecond) {
        List<String> addMatches = new ArrayList<>(listFound);

        for (int i = 0; i < listNotFound.size(); i++) {
            addMatches.add(listNotFound.get(i).replace("?", listNotFoundFromSecond.get(i)));
        }
        return addMatches;
    }

    private static void write(List<String> output, String fileName) throws IOException {
        String outputFileName = fileName.replace("in", "out");
        Files.write(Paths.get(outputFileName), output, StandardOpenOption.CREATE);
    }

    private static List<String> firstPrepareToWrite(List<String> first, List<String> second, int firstCount, int secondCount) {

        List<String> output = new ArrayList<>(first.stream()
                .map(s -> s.concat(":").concat(second.stream()
                        .filter(s2 -> check(s, s2))
                        .findAny()
                        .orElse("?")))
                .toList());

        if (output.size() < Math.max(firstCount, secondCount)) {
            List<String> addOutput = firstPrepareToWrite(second, first, firstCount, secondCount);

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
            return null;
        }
    }
}