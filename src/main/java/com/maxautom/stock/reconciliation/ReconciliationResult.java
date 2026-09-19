package com.maxautom.stock.reconciliation;

public record ReconciliationResult(String requestId, boolean accepted, int appliedLines, String reason) {
 public static ReconciliationResult accepted(String id,int n){return new ReconciliationResult(id,true,n,null);}
 public static ReconciliationResult rejected(String id,int n,String reason){return new ReconciliationResult(id,false,n,reason);}
}
