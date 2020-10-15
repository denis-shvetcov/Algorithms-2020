package lesson1;

import kotlin.NotImplementedError;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class JavaTasks {
    /**
     * Сортировка времён
     * <p>
     * Простая
     * (Модифицированная задача с сайта acmp.ru)
     * <p>
     * Во входном файле с именем inputName содержатся моменты времени в формате ЧЧ:ММ:СС AM/PM,
     * каждый на отдельной строке. См. статью википедии "12-часовой формат времени".
     * <p>
     * Пример:
     * <p>
     * 01:15:19 PM
     * 07:26:57 AM
     * 10:00:03 AM
     * 07:56:14 PM
     * 01:15:19 PM
     * 12:40:31 AM
     * <p>
     * Отсортировать моменты времени по возрастанию и вывести их в выходной файл с именем outputName,
     * сохраняя формат ЧЧ:ММ:СС AM/PM. Одинаковые моменты времени выводить друг за другом. Пример:
     * <p>
     * 12:40:31 AM
     * 07:26:57 AM
     * 10:00:03 AM
     * 01:15:19 PM
     * 01:15:19 PM
     * 07:56:14 PM
     * <p>
     * В случае обнаружения неверного формата файла бросить любое исключение.
     */
    static public void sortTimes(String inputName, String outputName) {
        throw new NotImplementedError();

    }

    /**
     * Сортировка адресов
     * <p>
     * Средняя
     * <p>
     * Во входном файле с именем inputName содержатся фамилии и имена жителей города с указанием улицы и номера дома,
     * где они прописаны. Пример:
     * <p>
     * Петров Иван - Железнодорожная 3
     * Сидоров Петр - Садовая 5
     * Иванов Алексей - Железнодорожная 7
     * Сидорова Мария - Садовая 5
     * Иванов Михаил - Железнодорожная 7
     * <p>
     * Людей в городе может быть до миллиона.
     * <p>
     * Вывести записи в выходной файл outputName,
     * упорядоченными по названию улицы (по алфавиту) и номеру дома (по возрастанию).
     * Людей, живущих в одном доме, выводить через запятую по алфавиту (вначале по фамилии, потом по имени). Пример:
     * <p>
     * Железнодорожная 3 - Петров Иван
     * Железнодорожная 7 - Иванов Алексей, Иванов Михаил
     * Садовая 5 - Сидоров Петр, Сидорова Мария
     * <p>
     * В случае обнаружения неверного формата файла бросить любое исключение.
     */
    static public void sortAddresses(String inputName, String outputName) {
        //Трудоемкость - N*logN
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputName))) {
            String formRegex = "(\\w|-)+\\s(\\w|-)+\\s-\\s(\\w|-)+\\s\\d+";
            Pattern pattern = Pattern.compile(formRegex, Pattern.UNICODE_CHARACTER_CLASS);
            Matcher matcher;
            String fullLine;
            while ((fullLine = reader.readLine()) != null) {
                matcher = pattern.matcher(fullLine);
                if (matcher.replaceFirst("").equals(""))
                    lines.add(fullLine);
                else
                    throw new Exception();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Comparator comparator1 = (o1, o2) -> {
            Map.Entry<String, Integer> obj1 = (Map.Entry<String, Integer>) o1;
            Map.Entry<String, Integer> obj2 = (Map.Entry<String, Integer>) o2;
            int compareRes;
            if ((compareRes = obj1.getKey().compareTo(obj2.getKey())) == 0)
                return obj1.getValue().compareTo(obj2.getValue());
            return compareRes;
        };

        Comparator comparator2 = (o1, o2) -> {
            Map.Entry<String, String> obj1 = (Map.Entry<String, String>) o1;
            Map.Entry<String, String> obj2 = (Map.Entry<String, String>) o2;
            int compareRes;
            if ((compareRes = obj1.getKey().compareTo(obj2.getKey())) == 0)
                return obj1.getValue().compareTo(obj2.getValue());
            return compareRes;
        };

        TreeMap<Map.Entry<String, Integer>, TreeSet<Map.Entry<String, String>>> addresses = new TreeMap<>(comparator1);

        lines.stream().forEach(string -> {
            String[] splitted = string.split(" - ");
            Map.Entry<String, Integer> key = new AbstractMap.SimpleEntry<>(
                    splitted[1].trim().split(" ")[0],
                    Integer.parseInt(splitted[1].trim().split(" ")[1]));

            Map.Entry<String, String> value = new AbstractMap.SimpleEntry<>(
                    splitted[0].trim().split(" ")[0], splitted[0].trim().split(" ")[1]);
            addresses.putIfAbsent(key, new TreeSet<Map.Entry<String, String>>(comparator2));
            addresses.get(key).add(value);

        });

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputName))) {
            for (Map.Entry<Map.Entry<String, Integer>, TreeSet<Map.Entry<String, String>>> streetPeople : addresses.entrySet()) {
                List<String> secondsNames = new ArrayList<>();
                streetPeople.getValue().stream().forEach(entry -> {
                    secondsNames.add(entry.getKey() + " " + entry.getValue());
                });
                writer.write(String.format("%s %d - %s", streetPeople.getKey().getKey(),
                        streetPeople.getKey().getValue(), String.join(", ", secondsNames)));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Сортировка температур
     * <p>
     * Средняя
     * (Модифицированная задача с сайта acmp.ru)
     * <p>
     * Во входном файле заданы температуры различных участков абстрактной планеты с точностью до десятых градуса.
     * Температуры могут изменяться в диапазоне от -273.0 до +500.0.
     * Например:
     * <p>
     * 24.7
     * -12.6
     * 121.3
     * -98.4
     * 99.5
     * -12.6
     * 11.0
     * <p>
     * Количество строк в файле может достигать ста миллионов.
     * Вывести строки в выходной файл, отсортировав их по возрастанию температуры.
     * Повторяющиеся строки сохранить. Например:
     * <p>
     * -98.4
     * -12.6
     * -12.6
     * 11.0
     * 24.7
     * 99.5
     * 121.3
     */
    static public void sortTemperatures(String inputName, String outputName) {

        // Трудоемкость - O(n+k)
        // Ресурсоемкость -

        int min = -273;
        int max = 500;

        List<Integer> temps = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputName))) {
            String formRegex = "-?\\d+.\\d";
            String temp;
            while ((temp = reader.readLine()) != null) {
                if (temp.replaceFirst(formRegex, "").equals("")){
                    temps.add(((int) (Double.parseDouble(temp)*10))+2730);}
                else
                    throw new Exception();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int[] tempsArr = new int[temps.size()];
        for (int i = 0; i<temps.size();i++) tempsArr[i] = temps.get(i);
        tempsArr = Sorts.countingSort(tempsArr,(max - min ) *10);

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputName))) {
            for (int num: tempsArr) {
                int decimated = num - 2730;
                writer.write(String.format("%s%d.%d", decimated>=0? "" : "-",Math.abs(decimated/10),Math.abs(decimated)%10));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Сортировка последовательности
     * <p>
     * Средняя
     * (Задача взята с сайта acmp.ru)
     * <p>
     * В файле задана последовательность из n целых положительных чисел, каждое в своей строке, например:
     * <p>
     * 1
     * 2
     * 3
     * 2
     * 3
     * 1
     * 2
     * <p>
     * Необходимо найти число, которое встречается в этой последовательности наибольшее количество раз,
     * а если таких чисел несколько, то найти минимальное из них,
     * и после этого переместить все такие числа в конец заданной последовательности.
     * Порядок расположения остальных чисел должен остаться без изменения.
     * <p>
     * 1
     * 3
     * 3
     * 1
     * 2
     * 2
     * 2
     */
    static public void sortSequence(String inputName, String outputName) {
        throw new NotImplementedError();
    }

    /**
     * Соединить два отсортированных массива в один
     * <p>
     * Простая
     * <p>
     * Задан отсортированный массив first и второй массив second,
     * первые first.size ячеек которого содержат null, а остальные ячейки также отсортированы.
     * Соединить оба массива в массиве second так, чтобы он оказался отсортирован. Пример:
     * <p>
     * first = [4 9 15 20 28]
     * second = [null null null null null 1 3 9 13 18 23]
     * <p>
     * Результат: second = [1 3 4 9 9 13 15 20 23 28]
     */
    static <T extends Comparable<T>> void mergeArrays(T[] first, T[] second) {
        throw new NotImplementedError();
    }
}
