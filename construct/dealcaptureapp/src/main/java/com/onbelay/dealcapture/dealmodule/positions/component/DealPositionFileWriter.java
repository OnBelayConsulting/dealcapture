package com.onbelay.dealcapture.dealmodule.positions.component;

import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionView;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class DealPositionFileWriter {
    private static final Logger logger = LogManager.getLogger();

    private ByteArrayOutputStream streamOut;


    public DealPositionFileWriter(ByteArrayOutputStream streamOut) {
        this.streamOut = streamOut;
    }

    public void write(List<DealPositionView> dealPositions) throws IOException {

        StringWriter sw = new StringWriter();

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(DealPositionColumnType.getAsArray())
                .build();

        try (final CSVPrinter printer = new CSVPrinter(sw, csvFormat)) {
            dealPositions.forEach( c -> {
                try {
                    DealPositionStreamer streamer = new DealPositionStreamer(c);
                    printer.printRecord(streamer.asAList());
                } catch (IOException e) {
                   logger.error("csv print on positions failed", e);
                   throw new RuntimeException(e);
                }
            });
            printer.flush();
        }
        streamOut.write(sw.toString().getBytes());
    }
}
