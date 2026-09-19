package com.maxautom.stock.reconciliation;

import org.springframework.stereotype.Component;
import java.util.*;
@Component
public class CsvReconciliationParser {
 public List<ReconciliationLine> parse(String csv){
  List<ReconciliationLine> lines=new ArrayList<>();
  for(String raw:csv.lines().toList()){
   if(raw.isBlank()||raw.startsWith("#")) continue;
   String[] parts=raw.split(",");
   if(parts.length<2) throw new IllegalArgumentException("Expected sku,delta");
   lines.add(new ReconciliationLine(parts[0].trim(),Integer.parseInt(parts[1].trim())));
  }
  if(lines.isEmpty()) throw new IllegalArgumentException("At least one reconciliation line is required");
  return List.copyOf(lines);
 }
}
