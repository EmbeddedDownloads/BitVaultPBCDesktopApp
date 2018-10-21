package org.pbc.logViewer.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Tail {
    private static final class RingBuffer {
        private final int limit;
        private final String[] data;
        private int counter = 0;

        RingBuffer(final int limit) {
            this.limit = limit;
            this.data = new String[limit];
        }

        void collect(final String line) {
            data[counter++ % limit] = line;
        }

        List<String> contents() {
            return IntStream.range(counter < limit ? 0 : counter - limit, counter)
                    .mapToObj(index -> data[index % limit])
                    .collect(Collectors.toList());
        }

    }

    private static List<String> tailFile(final Path source, final int limit) throws IOException {

        try (Stream<String> stream = Files.lines(source)) {
            final RingBuffer buffer = new RingBuffer(limit);
            stream.forEach(buffer::collect);

            return buffer.contents();
        }

    }

    public static void main(final String[] args) throws IOException {
        tailFile(Paths.get(args[0]), 10).forEach(System.out::println);
    }
}
