package com.systems.backend.controller;

import com.systems.backend.mapper.DocumentMapper;
import com.systems.backend.model.Document;
import com.systems.backend.requests.CreateDocumentRequest;
import com.systems.backend.requests.PaginationRequest;
import com.systems.backend.responses.DocumentResponse;
import com.systems.backend.responses.HistoryDownloadResponse;
import com.systems.backend.responses.RatingResponse;
import com.systems.backend.service.DocumentService;
import com.systems.backend.service.HistoryDownloadService;
import com.systems.backend.service.RatingService;
import com.systems.backend.service.UploadService;
import com.systems.backend.utils.UploadResult;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public Page<DocumentResponse> getAllDocuments(@RequestBody(required = false) PaginationRequest pageRequest) {
        Pageable pageable;
        if (pageRequest == null) {
            pageable = PageRequest.of(0, 6, Sort.by("createAt").descending());
        } else {
            int page = pageRequest.getPage() > 0 ? pageRequest.getPage() : 0;
            int size = pageRequest.getSize() > 1 ? pageRequest.getSize() : 6;
            String sortBy = pageRequest.getSortBy() != null ? pageRequest.getSortBy() : "createAt";
            String sortDir = pageRequest.getSortDirection() != null ? pageRequest.getSortDirection() : "desc";

            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            pageable = PageRequest.of(page, size, sort);
        }
        Page<Document> documentPage = documentService.getAllDocuments(pageable);
        
        return documentMapper.toDTOPage(documentPage);
    }


    @PostMapping
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

    @GetMapping("{documentId}/list-account")
    @ResponseStatus(HttpStatus.OK)
    public List<HistoryDownloadResponse> getHistoryByDocumentId(@PathVariable Long documentId) {
        return historyDownloadService.getHistoryByDocumentId(documentId);
    }
}
