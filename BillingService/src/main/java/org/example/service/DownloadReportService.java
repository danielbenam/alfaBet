package org.example.service;

import org.example.dto.ProcessorDownloadedReportResult;
import org.example.model.ReportResult;
import org.example.repository.ReportResultRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class DownloadReportService {
    private final RestTemplate restTemplate;

    private final ReportResultRepository reportResultRepository;

    public DownloadReportService(RestTemplate restTemplate, ReportResultRepository reportResultRepository) {
        this.restTemplate = restTemplate;
        this.reportResultRepository = reportResultRepository;
    }

    @Scheduled(cron = "${interval-in-download-report-cron}")
    public void downloadReportFromProcessor() {
        final ProcessorDownloadedReportResult[] downloadResults = restTemplate.getForObject("http://localhost:8080/processor/download_report", ProcessorDownloadedReportResult[].class);
        if (downloadResults != null) {
            final List<ReportResult> reportResults = Arrays.stream(downloadResults).map(this::convertDownloadReportResultToEntity).toList();

            //upsert - transactionId as primary key
            reportResultRepository.saveAll(reportResults);
        }
    }

    private ReportResult convertDownloadReportResultToEntity(ProcessorDownloadedReportResult downloadedReportResult) {
        ReportResult reportResult = new ReportResult();
        reportResult.setTransactionId(downloadedReportResult.getTransactionId());
        reportResult.setStatus(downloadedReportResult.getStatus());
        reportResult.setReportedAt(LocalDate.now());
        return reportResult;
    }


}
