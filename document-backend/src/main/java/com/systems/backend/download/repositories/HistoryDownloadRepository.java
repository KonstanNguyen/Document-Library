package com.systems.backend.download.repositories;

import com.systems.backend.users.models.Account;
import com.systems.backend.documents.models.Document;
import com.systems.backend.download.models.HistoryDownload;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryDownloadRepository extends JpaRepository<HistoryDownload, HistoryDownload.HistoryDownloadId> {
    List<HistoryDownload> findByHistoryDownloadId_Account(Account account);
    List<HistoryDownload> findByHistoryDownloadId_Document(Document document);
}
