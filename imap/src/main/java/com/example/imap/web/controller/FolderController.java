package com.example.imap.web.controller;


import com.example.imap.domain.HttpResponse;
import com.example.imap.service.ImapService;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/folder")
@RequiredArgsConstructor
public class FolderController {
    private final ImapService imapService;
    @PostMapping("/{account}/{folderName}")
    public ResponseEntity<HttpResponse> createFolder(@PathVariable String account,
                                                     @PathVariable String folderName)
            throws Exception {
        imapService.createFolder(folderName, account);
        return ResponseEntity.ok(HttpResponse.builder()
                .httpStatus(HttpStatus.OK)
                .code(200)
                .timeStamp(LocalDateTime.now().toString())
                .data(Map.of("message", "Folder was created successfuly"))
                .build());
    }

    @PostMapping("/{account}/{sourceFolder}/{destinationFolderName}/{msgnum}")
    public ResponseEntity<HttpResponse> moveEmailToFolder(@PathVariable String account,
                                                          @PathVariable String sourceFolder,
                                                          @PathVariable
                                                          String destinationFolderName,
                                                          @PathVariable int msgnum)
            throws Exception {
        imapService.moveEmail(account, sourceFolder, destinationFolderName, msgnum);
        return ResponseEntity.ok(HttpResponse.builder().build());
    }
}
