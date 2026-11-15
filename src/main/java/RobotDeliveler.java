import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class RobotDeliveler {
    private static int MAX_ROUTE = 1000;
    private static int MAX_ROUTE_LENGTH = 100;

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) {
        Thread treadMaxCounter = new Thread(() ->{
            while(!Thread.interrupted()){
                try {
                    // Посчёт команд 'R'
                    synchronized (sizeToFreq){
                        sizeToFreq.wait();
                        // Самое частое количество команд 'R'
                        Optional<Integer> maxFreq = sizeToFreq.values().stream()
                                .max(Integer::compareTo);
                        System.out.println("Максимальное количество команд 'R': " + maxFreq.get());
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("Поток для подсчёта максамлного количества команд 'R' завершён!");
        });
        treadMaxCounter.start();

        for (int i = 0; i < MAX_ROUTE; i++) {
            new Thread(() -> {
                String route = generateRoute("RLRFR", MAX_ROUTE_LENGTH);
                // Посчёт команд 'R'
                int countR = 0;
                for (int j = 0; j < route.length(); j++) {
                    if (route.charAt(j) == 'R') {
                        countR++;
                    }
                }

                // Сохранение к-ва команд 'R' в Map
                synchronized (sizeToFreq) {
                    Integer freq = sizeToFreq.get(countR);
                    if (freq == null) {
                        sizeToFreq.put(countR, 1);
                    } else {
                        sizeToFreq.put(countR, freq + 1);
                    }
                    sizeToFreq.notify();
                }
            }).start();
        }
        // Самое частое количество команд 'R'
        Optional<Integer> maxFreq = sizeToFreq.values().stream()
                .max(Integer::compareTo);
        System.out.println("Максимальное количество команд 'R': " + maxFreq.get());
        // Вывод количества команд 'R'
        for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            System.out.println(" - " + key + " (" + value + " раз)");
        }
        // Завершение потока для подсчёта максамлного количества команд 'R'
        synchronized (sizeToFreq){
            sizeToFreq.notify();
            treadMaxCounter.interrupt();
        }

    }
}
