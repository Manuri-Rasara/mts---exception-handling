package com.UoR_MTS_Backend.mail_tracking_system.controller;

import com.UoR_MTS_Backend.mail_tracking_system.dto.MailRecordDTO;
import com.UoR_MTS_Backend.mail_tracking_system.model.MailRecord;
import com.UoR_MTS_Backend.mail_tracking_system.service.MailRecordService;
import com.UoR_MTS_Backend.mail_tracking_system.utill.response.ResponseBuilder;
import com.UoR_MTS_Backend.mail_tracking_system.utill.response.StandardResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/mailRecord")
public class MailRecordController {
    private static final Logger logger = LoggerFactory.getLogger(MailRecordController.class);

    @Autowired
    private MailRecordService mailRecordService;

    @PostMapping("/transfer")
    public ResponseEntity<StandardResponse<String>>  transferDailyMailsToMainCart() {
        try {
            String message = mailRecordService.transferDailyMailsToMainCart();
            return ResponseBuilder.success("Mail Record Save Successfully!",null);
        }catch(Exception e){

            System.err.println("Error saving Mail Record: " + e.getMessage());
            return ResponseBuilder.error("Error Saving Mail Record",null);

        }
    }

   @GetMapping("/filter/{page}")
   public ResponseEntity<StandardResponse<Page<MailRecordDTO>>> filterMailRecords(
           @RequestParam(required = false) String senderName,
           @RequestParam(required = false) String receiverName,
           @RequestParam(required = false) String mailType,
           @RequestParam(required = false) String trackingNumber,
           @RequestParam(required = false) String branchName,
           @RequestParam(defaultValue = "0") int page) {
       try {
           int pageSize = 10;
           Pageable pageable = PageRequest.of(page, pageSize);
           // Call the service method
           Page<MailRecordDTO> filteredRecords = mailRecordService.filterMailRecords(
                   senderName, receiverName, mailType, trackingNumber, branchName, pageable);
           if(filteredRecords ==null || filteredRecords.isEmpty() ){
               return ResponseBuilder.notFound("No mail activities found with the provided filters.");
           }

           // Return successful response
           return ResponseBuilder.success("Mail activities retrieved successfully", filteredRecords);
       } catch (Exception e) {
           // Log the error for debugging
           System.err.println("Error retrieving mail activities by filter: " + e.getMessage());

           return ResponseBuilder.error("Error retrieving mail activities.", null);
       }

   }

    @GetMapping("/searchByBarcode/{barcodeId}")
    public ResponseEntity<StandardResponse<MailRecord>> searchMailByBarcodeId(@PathVariable String barcodeId) {
        try {
            MailRecord mailRecord = mailRecordService.searchMailByBarcodeId(barcodeId);

            if (mailRecord == null) {
                return ResponseBuilder.notFound("Mail with barcode ID " + barcodeId + " not found.");
            }

            // You can customize the message like this
            String customMessage = "Mail record found successfully with barcode ID " + barcodeId;
            return ResponseBuilder.success(customMessage, mailRecord);
        } catch (Exception e) {
            logger.error("Error occurred while searching for mail by barcode ID: " + barcodeId, e);
            return ResponseBuilder.error("Error occurred while searching by barcode ID.", null);
        }
    }

    @GetMapping("/all-main-mails")
    public ResponseEntity<?> getAllMailRecords(
            @RequestParam(defaultValue = "0") int page) { // Default page size
        try {
            int size = 10;
            Pageable pageable = PageRequest.of(page, size);

            // Call the service method to fetch mail records
            Page<MailRecord> mailRecords = mailRecordService.getAllMailRecords(pageable);

            // Check if the result is empty
            if (mailRecords.isEmpty()) {
                return ResponseBuilder.notFound("No mail records found.");
            }

            // Return successful response
            return ResponseBuilder.success("Mail records retrieved successfully.", mailRecords);
        } catch (Exception e) {
            // Log the exception and return error response
            System.err.println("Error retrieving mail records: " + e.getMessage());
            return ResponseBuilder.error("Error retrieving mail records.", null);
        }
    }
    }




