package com.systems.backend.repository;

import com.systems.backend.model.Account;
import com.systems.backend.model.Document;
import com.systems.backend.model.HistoryDownload;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryDownloadRepository extends JpaRepository<HistoryDownload, HistoryDownload.HistoryDownloadId> {
    List<HistoryDownload> findByHistoryDownloadId_Account(Account account);
    List<HistoryDownload> findByHistoryDownloadId_Document(Document document);
}
