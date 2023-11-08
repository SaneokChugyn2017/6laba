package org.example;

import java.util.Arrays;

public class Main {
    private static final int SIZE = 60;
    static final int half = SIZE / 2;

    public static void main(String[] args) {
        methodOne();
        methodTwo();
        System.out.println();
        methodThree();
    }

    public static void methodOne() {
        float[] array = new float[SIZE];
        Arrays.fill(array, 1.0f);
        long time = System.currentTimeMillis();
        for (int i = 0; i < array.length; i++) {
            array[i] = (float) (array[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
        }
        System.out.println(array[0]);
        System.out.println(array[array.length - 1]);
        System.out.println("Время выполнения первого метода: " + (System.currentTimeMillis() - time));
    }

    public static void methodTwo() {
        float[] array = new float[SIZE];
        float[] firstHalf = new float[half];
        float[] secondHalf = new float[half];
        Arrays.fill(array, 1.0f);
        long time = System.currentTimeMillis();
        System.arraycopy(array, 0, firstHalf, 0, half);
        System.arraycopy(array, half, secondHalf, 0, half);

        Thread threadOne = new Thread(() -> {
            for (int i = 0; i < firstHalf.length; i++) {
                firstHalf[i] = (float) (firstHalf[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
            }
            System.arraycopy(firstHalf, 0, array, 0, firstHalf.length);
        });
        Thread threadTwo = new Thread(() -> {
            for (int i = 0; i < secondHalf.length; i++) {
                secondHalf[i] = (float) (secondHalf[i] * Math.sin(0.2f + (half + i) / 5) * Math.cos(0.2f + (half + i) / 5) * Math.cos(0.4f + (half + i) / 2));
            }
            System.arraycopy(secondHalf, 0, array, half, secondHalf.length);
        });
        threadOne.start();
        threadTwo.start();
        try {
            threadOne.join();
            threadTwo.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(array[0]);
        System.out.println(array[array.length - 1]);
        System.out.println("Время выполнения второго метода: " + (System.currentTimeMillis() - time));
    }

    public static void methodThree() {
        float[] arr = new float[SIZE];
        Arrays.fill(arr, 1.0f);
        int countThreads = 5;
        Thread[] threads = new Thread[countThreads];

        while (SIZE % countThreads != 0) {
            countThreads++;
        }

        float[][] results = new float[countThreads][];
        int partSize = SIZE / countThreads;

        for (int i = 0; i < countThreads; i++) {
            int finalI = i;
            threads[i] = new Thread(() -> {
                float[] part = new float[partSize];
                int startIndex = finalI * partSize;

                System.arraycopy(arr, startIndex, part, 0, partSize);

                for (int j = 0; j < part.length; j++) {
                    part[j] = (float) (part[j] * Math.sin(0.2f + (startIndex + j) / 5) * Math.cos(0.2f + (startIndex + j) / 5) * Math.cos(0.4f + (startIndex + j) / 2));
                }

                results[finalI] = part;
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println();
        for (int i = 0; i < results.length; i++) {
            System.out.println("Поток " + (i + 1) + " " + Arrays.toString(results[i]));
            int startIndex = i * partSize;
            System.arraycopy(results[i], 0, arr, startIndex, partSize);
        }

        System.out.println("первый элемент массива" + arr[0]);
        System.out.println("последний элемент массива" + arr[arr.length - 1]);
    }
}
