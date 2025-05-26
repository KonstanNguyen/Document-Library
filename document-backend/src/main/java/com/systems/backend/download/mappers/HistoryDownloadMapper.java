package com.systems.backend.download.mappers;

import com.systems.backend.download.responses.HistoryDownloadResponse;
import com.systems.backend.download.models.HistoryDownload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistoryDownloadMapper {
    @Mapping(source = "historyDownloadId.account.username", target = "username")
    @Mapping(source = "historyDownloadId.document.title", target = "documentName")
    HistoryDownloadResponse toDTO(HistoryDownload historyDownload);
}
