package com.maxautom.stock.reconciliation;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class CsvReconciliationParserTest { private final CsvReconciliationParser parser=new CsvReconciliationParser(); @Test void parsesSignedDeltas(){assertThat(parser.parse("# correction\nKB-001,2\nMS-002,-3\n")).hasSize(2);}}
