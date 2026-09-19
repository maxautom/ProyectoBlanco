package com.maxautom.stock.reconciliation;
import com.maxautom.stock.product.ProductRepository;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
class ReconciliationServiceTest {
 @Test void appliesValidBatch(){var p=new ProductRepository();var s=new ReconciliationService(p,new ReconciliationStatusRepository());var r=s.reconcile("req-1",List.of(new ReconciliationLine("KB-001",2),new ReconciliationLine("MS-002",-1)));assertThat(r.accepted()).isTrue();assertThat(p.findBySku("KB-001").orElseThrow().available()).isEqualTo(10);}
 @Test void rejectsUnknownSku(){var s=new ReconciliationService(new ProductRepository(),new ReconciliationStatusRepository());assertThat(s.reconcile("req-2",List.of(new ReconciliationLine("UNKNOWN",1))).accepted()).isFalse();}
}
