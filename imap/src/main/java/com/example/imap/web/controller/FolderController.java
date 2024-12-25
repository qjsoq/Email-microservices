package com.example.imap.web.controller;


import com.example.imap.common.HttpResponse;
import com.example.imap.service.DraftService;
import com.example.imap.service.ImapService;
import com.example.imap.web.dto.EmailCreationDto;
import com.example.imap.web.mapper.EmailMapper;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/folder")
@RequiredArgsConstructor
public class FolderController {
    private final DraftService draftService;
    private final EmailMapper emailMapper;
    private final ImapService imapService;

    @PostMapping("/create/{account}/{folderName}")
    public ResponseEntity<HttpResponse> createFolder(@PathVariable String account,
                                                     @PathVariable String folderName,
                                                     Principal principal)
            throws Exception {
        imapService.createFolder(folderName, account, principal.getName());
        return ResponseEntity.ok(HttpResponse.builder()
                .httpStatus(HttpStatus.OK)
                .code(200)
                .timeStamp(LocalDateTime.now().toString())
                .data(Map.of("message", "Folder was created successfuly"))
                .build());
    }

    @PostMapping("/move/{account}/{msgnum}")
    public ResponseEntity<HttpResponse> moveEmailToFolder(@PathVariable String account,
                                                          @RequestBody
                                                          Map<String, String> folderNameMap,
                                                          @PathVariable int msgnum,
                                                          Principal principal)
            throws Exception {

        imapService.moveEmail(account, folderNameMap.get("sourceFolder"),
                folderNameMap.get("destinationFolderName"), msgnum,
                principal.getName());
        return ResponseEntity.ok(HttpResponse.builder().build());
    }

    @PostMapping("/add-to-draft")
    public ResponseEntity<HttpResponse> saveEmailAsDraft(@Valid
                                                         @RequestBody
                                                         EmailCreationDto emailCreationDto,
                                                         Principal principal) throws Exception {

        draftService.saveEmailAsDraft(emailMapper.toEmail(emailCreationDto), principal.getName());
        return ResponseEntity.ok(HttpResponse.builder()
                .httpStatus(HttpStatus.OK)
                .code(200)
                .path("/api/v1/read/add-to-draft")
                .timeStamp(LocalDateTime.now().toString())
                .data(Map.of("message", "Email saved as draft"))
                .build());

    }
}
