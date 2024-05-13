package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.onbelay.core.entity.snapshot.ErrorHoldingSnapshot;

import java.util.List;

public class FileReportResult extends ErrorHoldingSnapshot {

    private byte[] documentInBytes;
    private String fileName;

    public FileReportResult(byte[] documentInBytes, String fileName) {
        this.documentInBytes = documentInBytes;
        this.fileName = fileName;
    }

    public FileReportResult(String errorCode) {
        super(errorCode);
    }

    public FileReportResult(String errorCode, boolean isPermissionException) {
        super(errorCode, isPermissionException);
    }

    public FileReportResult(String errorCode, List<String> parameters) {
        super(errorCode, parameters);
    }

    public byte[] getDocumentInBytes() {
        return documentInBytes;
    }

    public String getFileName() {
        return fileName;
    }
}
