package com.systems.backend.mapper;

import com.systems.backend.responses.HistoryDownloadResponse;
import com.systems.backend.model.HistoryDownload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistoryDownloadMapper {
    @Mapping(source = "historyDownloadId.account.id", target = "accountId")
    @Mapping(source = "historyDownloadId.document.id", target = "documentId")
    HistoryDownloadResponse toDTO(HistoryDownload historyDownload);
}
