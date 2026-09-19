package com.maxautom.stock.reconciliation;

import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
@Repository
public class ReconciliationStatusRepository {
 private final Map<String,ReconciliationResult> results=new ConcurrentHashMap<>();
 public Optional<ReconciliationResult> find(String id){return Optional.ofNullable(results.get(id));}
 public void save(ReconciliationResult r){results.put(r.requestId(),r);}
}
