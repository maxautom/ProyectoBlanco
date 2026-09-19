package com.maxautom.stock.reconciliation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/reconciliation/imports")
public class ReconciliationController {
 private final CsvReconciliationParser parser; private final ReconciliationService service; private final ReconciliationStatusRepository statuses;
 public ReconciliationController(CsvReconciliationParser p,ReconciliationService s,ReconciliationStatusRepository st){parser=p;service=s;statuses=st;}
 @PostMapping ResponseEntity<ReconciliationResult> importCsv(@RequestHeader("X-Request-Id") String id,@RequestBody String csv){var r=service.reconcile(id,parser.parse(csv));return ResponseEntity.ok(r);}
 @GetMapping("/{requestId}") ReconciliationResult status(@PathVariable String requestId){return statuses.find(requestId).orElse(null);}
}
