package com.maxautom.stock.reconciliation;

import com.maxautom.stock.product.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class ReconciliationService {
 private final ProductRepository products; private final ReconciliationStatusRepository statuses;
 public ReconciliationService(ProductRepository p,ReconciliationStatusRepository s){products=p;statuses=s;}
 public ReconciliationResult reconcile(String requestId,List<ReconciliationLine> lines){
  statuses.find(requestId);
  int applied=0;
  for(ReconciliationLine line:lines){
   if(line.sku()==null||line.sku().isBlank()){var r=ReconciliationResult.rejected(requestId,applied,"blank sku");statuses.save(r);return r;}
   if(products.adjust(line.sku(),line.delta()).isEmpty()){var r=ReconciliationResult.rejected(requestId,applied,"unknown sku or negative final stock");statuses.save(r);return r;}
   applied++;
  }
  var r=ReconciliationResult.accepted(requestId,applied);statuses.save(r);return r;
 }
}
