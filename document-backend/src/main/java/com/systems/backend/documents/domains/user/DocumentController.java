package com.systems.backend.documents.domains.user;

import com.systems.backend.documents.mappers.DocumentMapper;
import com.systems.backend.documents.models.Document;
import com.systems.backend.documents.resquests.CreateDocumentRequest;
import com.systems.backend.common.requests.PaginationRequest;
import com.systems.backend.documents.responses.DocumentResponse;
import com.systems.backend.download.responses.HistoryDownloadResponse;
import com.systems.backend.ratings.responses.RatingResponse;
import com.systems.backend.documents.services.DocumentService;
import com.systems.backend.download.services.HistoryDownloadService;
import com.systems.backend.ratings.services.RatingService;
import com.systems.backend.upload.services.UploadService;
import com.systems.backend.common.utils.UploadResult;
import com.systems.backend.common.services.ExportStrategy;
import com.systems.backend.common.services.ExportStrategyFactory;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/documents")
public class DocumentController {
    @Autowired
    private DocumentService documentService;
 
    @Autowired
    private UploadService uploadService;
    
    @Autowired
    private RatingService ratingService;

    @Autowired
    private HistoryDownloadService historyDownloadService;

    @Autowired
    private DocumentMapper documentMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Page<DocumentResponse> getAllDocuments(@ModelAttribute PaginationRequest pageRequest) {
        Pageable pageable;
        if (pageRequest == null) {
            pageable = PageRequest.of(0, 6, Sort.by("createAt").descending());
        } else {
            int page = Math.max(pageRequest.getPage(), 0);
            int size = pageRequest.getSize() > 1 ? pageRequest.getSize() : 6;
            String sortBy = pageRequest.getSortBy() != null ? pageRequest.getSortBy() : "createAt";
            String sortDir = pageRequest.getSortDirection() != null ? pageRequest.getSortDirection() : "desc";

            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            pageable = PageRequest.of(page, size, sort);
        }
        Page<Document> documentPage = documentService.getAllDocuments(pageable);
        
        return documentMapper.toDTOPage(documentPage);
    }


    @PostMapping("create")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Document createDocument(
        @RequestPart("file") MultipartFile file,
        @RequestPart("data") CreateDocumentRequest createDocumentRequest
    ) throws Exception {
        UploadResult uploadResult = uploadService.processFile(file);
        createDocumentRequest.setContent(uploadResult.getOriginalFilePath());
        createDocumentRequest.setThumbnail(uploadResult.getThumbnailFilePath());
        return documentService.createDocument(createDocumentRequest);
    }

    @GetMapping("{documentId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public DocumentResponse getDocument(@PathVariable Long documentId) {
        Document document = documentService.getDocumentById(documentId);
        return documentMapper.toDTO(document);
    }

    @RequestMapping(value = "{documentId}/update", method = {RequestMethod.PUT, RequestMethod.POST, RequestMethod.PATCH})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Document updateDocument(@PathVariable Long documentId, @RequestBody Document document) {
        return documentService.updateDocument(documentId, document);
    }


    @DeleteMapping("{documentId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDocument(@PathVariable Long documentId) {
        documentService.deleteDocument(documentId);
    }

    @GetMapping("{documentId}/ratings")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<RatingResponse> getRatingsByDocument(@PathVariable Long documentId) {
        return ratingService.getRatingByDocumentId(documentId);
    }

    @PreAuthorize("hasAnyAuthority('admin') or hasAnyAuthority('ADMIN')")
    @GetMapping("{documentId}/list-account")
    @ResponseStatus(HttpStatus.OK)
    public List<HistoryDownloadResponse> getHistoryByDocumentId(@PathVariable Long documentId) {
        return historyDownloadService.getHistoryByDocumentId(documentId);
    }

    @GetMapping("/export/{format}")
    @ResponseStatus(HttpStatus.OK)
    public void exportDocuments(
            @PathVariable String format,
            @RequestParam(required = false) String fileName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpServletResponse response) {
        String exportFileName = fileName != null ? fileName : "documents";
        
        List<DocumentResponse> documentResponses;
        
        if (startDate != null && endDate != null) {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<Document> documents = documentService.getDocumentsByDateRange(start, end);
            documentResponses = documents.stream()
                    .map(documentMapper::toDTO)
                    .toList();
        } else {
            documentResponses = documentService.getAllDocuments(Pageable.unpaged())
                    .map(documentMapper::toDTO)
                    .getContent();
        }

        ExportStrategyFactory<DocumentResponse> factory = new ExportStrategyFactory<>();
        ExportStrategy<DocumentResponse> strategy = factory.getExportStrategy(format, response, exportFileName);
        strategy.export(documentResponses);
    }

    @PutMapping("{documentId}/increment-view")
    @ResponseStatus(HttpStatus.OK)
    public void incrementView(@PathVariable Long documentId) {
        Document document = documentService.getDocumentById(documentId);
        document.setViews(document.getViews() + 1);
        documentService.updateDocument(documentId, document);
    }
}
